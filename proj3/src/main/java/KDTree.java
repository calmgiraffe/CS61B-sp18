import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class KDTree {

    // Inner class for x-y coordinate
    public static class Point {
        double lat;
        double lon;

        Point(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    // Inner class for node of kd tree
    public static class Node {
        Point point;
        Node left;
        Node right;
        boolean leftIsGoodSide;
        int depth;

        Node(Point point) {
            this.point = point;
        }
    }

    // kd tree instance variables
    private Node root;
    private int size;
    private final ArrayList<Long> nodes;
    private final ArrayList<Integer> nodesIndexes;

    public KDTree(Iterable<Long> nodeIDs, HashMap<Long, GraphDB.Node> nodesHashMap) {
        this.nodes = new ArrayList<>();
        this.nodesIndexes = new ArrayList<>();

        // Create a list of all nodeIDs
        for (Long nodeID : nodeIDs) {
            nodes.add(nodeID);
        }
        // Add numbers 0 through nodes.size() - 1 to the array, then shuffle
        for (int i = 0; i < nodes.size(); i += 1) {
            nodesIndexes.add(i);
        }
        Collections.shuffle(nodesIndexes);

        // Generate the KDTree by iterating through nodeIndexes, adding to the tree
        for (int i : nodesIndexes) {
            GraphDB.Node n = nodesHashMap.get(nodes.get(i));
            Node newNode = new Node(new Point(n.latitude, n.longitude));
            insert(newNode);
        }
    }

    // implementation:
    // for updating best, use great circle distance
    // pruning rule: use straight line distance instead; this is greater than great circle, but faster to compute

    // insertion: do randomly
    // If N distinct keys are inserted in random order, expected tree height is ~ 4.311 ln N
    private void insertHelper(Node n, int depth, Node curr) {

        if (curr == null) { // create a new node at curr from info in n
            curr = n;
        } else if (depth % 2 == 0) { // if depth is even, compare longitude
            if (n.point.lon < curr.point.lon) {
                insertHelper(n, depth + 1, curr.left);
            } else {
                insertHelper(n, depth + 1, curr.right);
            }
        } else { // if depth is odd, compare latitude
            if (n.point.lat < curr.point.lat) {
                insertHelper(n, depth + 1, curr.left);
            } else {
                insertHelper(n, depth + 1, curr.right);
            }
        }
    }

    public void insert(Node n) {
        insertHelper(n, 0, root);
    }

    private Node nearestHelper(Node n, Point goal, Node best) {
        return null;
    }

    /**
     * Returns the nodeID of the node that is closest to inputted latitude and longitude.
     *
     * @param lat goal latitude
     * @param lon goal longitude
     * @return id of the closest node
     */
    public long nearest(double lat, double lon) {
        return 0;
    }
}
