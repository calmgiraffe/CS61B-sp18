import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 *  Parses OSM XML files using an XML SAX parser. Used to construct the graph of roads for
 *  pathfinding, under some constraints.
 *
 *  See OSM documentation on
 *  <a href="http://wiki.openstreetmap.org/wiki/Key:highway">the highway tag</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Way">the way XML element</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Node">the node XML element</a>,
 *  and the java
 *  <a href="https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html">SAX parser tutorial</a>.
 *
 *  You may find the CSCourseGraphDB and CSCourseGraphDBHandler examples useful.
 *
 *  The idea here is that some external library is going to walk through the XML
 *  file, and your override method tells Java what to do every time it gets to the next
 *  element in the file. This is a very common but strange-when-you-first-see it pattern.
 *  It is similar to the Visitor pattern we discussed for graphs.
 *
 *  @author Alan Yao, Maurice Lee
 */
public class GraphBuildingHandler extends DefaultHandler {
    /**
     * Only allow for non-service roads; this prevents going on pedestrian streets as much as
     * possible. Note that in Berkeley, many of the campus roads are tagged as motor vehicle
     * roads, but in practice we walk all over them with such impunity that we forget cars can
     * actually drive on them.
     */
    private static final Set<String> ALLOWED_HIGHWAY_TYPES = new HashSet<>(Arrays.asList
            ("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                    "residential", "living_street", "motorway_link", "trunk_link", "primary_link",
                    "secondary_link", "tertiary_link"));

    //  flags and GraphDB info that will be added at the </way> tag
    private String activeState = "";
    private Long nodeID;
    private final Queue<Long> nodeStaging;

    private Long wayID;
    private String wayName;

    private boolean isValidWay;
    private final GraphDB g;

    /**
     * Create a new GraphBuildingHandler.
     * @param g The graph to populate with the XML data.
     */
    public GraphBuildingHandler(GraphDB g) {
        this.g = g;
        this.nodeStaging = new LinkedList<>();
    }

    /**
     * Called at the beginning of an element. Typically, you will want to handle each element in
     * here, and you may want to track the parent element.
     *
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available. This tells us which element we're looking at.
     * @param attributes The attributes attached to the element. If there are no attributes, it
     *                   shall be an empty Attributes object.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see Attributes
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equals("node")) {
            /* Encountered a new <node...> tag. */
            activeState = "node";
            nodeID = Long.parseLong(attributes.getValue("id"));

            /* Add the node to the HashMap of nodes found in GraphDB g. */
            double lon = Double.parseDouble(attributes.getValue("lon"));
            double lat = Double.parseDouble(attributes.getValue("lat"));
            g.nodes.put(nodeID, new GraphDB.Node(lon, lat));
            g.uncleanedNodes.add(nodeID);
        }
        else if (qName.equals("way")) {
            /* Encountered a new <way...> tag, which is found at the beginning of a way block. */
            activeState = "way";
            wayID = Long.parseLong(attributes.getValue("id"));
        }
        else if (activeState.equals("way") && qName.equals("nd")) {
            /* Found a node within a way block.
            Add node to g.nodeStaging, a queue of nodes that is kept track of in the case
            that the way is highway AND is one of the valid types, as will be determined later. */
            Long id = Long.parseLong(attributes.getValue("ref"));
            nodeStaging.add(id);
        }
        else if (activeState.equals("way") && qName.equals("tag")) {
            /* <tag> represents important information about the way like whether it is a valid way
            in the context of this program, speed, name, etc. k is the key, v is the value. */

            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            if (k.equals("highway") && ALLOWED_HIGHWAY_TYPES.contains(v)) {
                // Set isValidWay flag for edge drawing & queue clearing at end.
                isValidWay = true;

            } else if (k.equals("name")) {
                // Note: not every way has a name
                wayName = v;
            }
        }
        else if (activeState.equals("node") && qName.equals("tag") && attributes.getValue("k")
                .equals("name")) {
            /* While looking at a node, we found a <tag...> with k="name".
            Set the node's isLocation flag to true and update its name. */
            g.nodes.get(nodeID).isLocation = true;
            g.nodes.get(nodeID).name = attributes.getValue("v");
        }
    }

    /**
     * Receive notification of the end of an element. You may want to take specific terminating
     * actions here, like finalizing vertices or edges found.
     *
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available.
     * @throws SAXException  Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way")) {
            if (isValidWay) {
                int numNodes = nodeStaging.size();

                /* Connect current and next node both ways, updating curr and next pointers
                each iteration, until staging queue is empty. */
                Long currNode = nodeStaging.poll();
                g.nodes.get(currNode).way = wayID;
                while (nodeStaging.peek() != null) {
                    Long nextNode = nodeStaging.peek();
                    g.nodes.get(currNode).adjacent.add(nextNode);
                    g.nodes.get(nextNode).adjacent.add(currNode);

                    // Set new value for currNode for next iteration
                    currNode = nodeStaging.poll();
                    g.nodes.get(currNode).way = wayID;
                }
                // Add way to g.way HashMap
                GraphDB.Way newWay = new GraphDB.Way(wayName, numNodes);
                g.ways.put(wayID, newWay);

                // Reset isValidWay flag
                isValidWay = false;
            } else {
                nodeStaging.clear();
            }
        }
    }

}
