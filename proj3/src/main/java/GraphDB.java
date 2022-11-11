import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {

    // Inner class for object representing the information associated with a nodeID
    public static class Node {
        double lon;
        double lat;
        String name;
        boolean isLocation;
        ArrayList<Long> adjacent;
        long way;

        Node(double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
            this.adjacent = new ArrayList<>();
        }
    }

    // Inner class for object representing the information associated with a wayID
    public static class Way {
        String name;
        int numNodes;

        Way(String name, int numNodes) {
            this.name = name;
            this.numNodes = numNodes;
        }
    }

    // Instance variables for storing the graph
    HashMap<Long, Node> nodes;
    HashMap<Long, Node> locations;
    HashMap<Long, Way> ways;
    HashSet<Long> uncleanedNodes;
    KDTree kdTree;
    protected Trie trie;

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        this.nodes = new HashMap<>();
        this.locations = new HashMap<>();
        this.ways = new HashMap<>();
        this.uncleanedNodes = new HashSet<>();

        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            // Graph building logic is within below GraphBuildingHandler class.
            // GraphBuildingHandler has an instance of a GraphDB object,
            // which is the underlying implementation of the graph.
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        // final step is to destroy all disconnected nodes or those that are not a location
        clean();

        // After cleaning, make KDTree of nodes for nearest node searching
        this.kdTree = new KDTree(nodes);
        this.trie = buildTrie();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /** Builds and returns a trie for the location names of this graph. */
    private Trie buildTrie() {
        Trie tr = new Trie();
        for (long id : locations.keySet()) {
            String name = locations.get(id).name;
            tr.add(name);
        }
        return tr;
    }


    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     *  Runs in theta(n) time, n is number of nodes currently stored.
     */
    private void clean() {
        for (Long nodeID : uncleanedNodes) { // uncleanedNodes is correct size
            Node nodeObj = nodes.get(nodeID);
            if (nodeObj.isLocation) {
                locations.put(nodeID, nodeObj);
                // System.out.println(cleanString(locations.get(nodeID).name));
            }
            if (nodeObj.adjacent.isEmpty()) {
                nodes.remove(nodeID);
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return nodes.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        return nodes.get(v).adjacent;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     *
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        return kdTree.nearest(lon, lat);
    }

    /**
     * Gets the longitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return nodes.get(v).lon;
    }

    /**
     * Gets the latitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return nodes.get(v).lat;
    }

    /**
     * Returns the number of vertices in the graph.
     */
    int size() {
        int N = 0;
        for (Long id : nodes.keySet()) {
            N += 1;
        }
        return N;
    }

    /**
     * Return euclidian distance between two nodes.
     * @param v node1
     * @param w node2
     * @return euclidian distance
     */
    double euclidian(long v, long w) {
        double londiff = lon(v) - lon(w);
        double latdiff = lat(v) - lat(w);
        return Math.sqrt(Math.pow(londiff, 2) + Math.pow(latdiff, 2));
    }
}
