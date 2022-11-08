import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class KDTree {

    // Inner class for x-y coordinate
    public static class Point {
        double lon;
        double lat;

        Point(double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
        }
    }

    // Inner class for node of kd tree
    public static class Node {
        Point point;
        Node left;
        Node right;
        boolean leftIsGoodSide;
        int depth;

        Node(Point point, int depth) {
            this.point = point;
            this.depth = depth;
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

        // Generate the KDTree by iterating through nodeIndexes, adding randomly to the tree
        for (int i : nodesIndexes) {
            GraphDB.Node n = nodesHashMap.get(nodes.get(i));
            insert(n);
        }

        //printTree();
    }

    private Node insertHelper(GraphDB.Node n, int depth, Node curr) {

        if (curr == null) { // create a new node at curr from info in n
            return new Node(new Point(n.lon, n.lat), depth);

        } else if (depth % 2 == 0) { // if depth is even, compare longitude
            if (n.lon < curr.point.lon) {
                curr.left = insertHelper(n, depth + 1, curr.left);
            } else {
                curr.right = insertHelper(n, depth + 1, curr.right);
            }

        } else { // if depth is odd, compare latitude
            if (n.lat < curr.point.lat) {
                curr.left = insertHelper(n, depth + 1, curr.left);
            } else {
                curr.right = insertHelper(n, depth + 1, curr.right);
            }
        }
        return curr;
    }

    /** Inserts a node into its proper position in the tree. */
    public void insert(GraphDB.Node n) {
        root = insertHelper(n, 0, root);
    }

    private Node nearestHelper(Node n, Point goal, Node best) {
        return null;
    }

    /**
     * Returns the nodeID of the node that is closest to inputted latitude and longitude.
     * Implementation:
     * for updating best, use great circle distance
     * pruning rule: use straight line distance; greater than great circle, but faster to compute
     *
     * @param lat goal latitude
     * @param lon goal longitude
     * @return id of the closest node
     */
    public long nearest(double lon, double lat) {
        return 0;
    }

    private void printHelper(Node n, String indent, String prefix) {
        if (n == null) { // if leaf
            return;
        }
        System.out.println(prefix + n.depth + indent + " (" + n.point.lon + ", "+  n.point.lat + ")");
        printHelper(n.left, indent + "    ", "L");
        printHelper(n.right, indent + "    ", "R");
    }

    /** Debugging tool to view tree structure */
    public void printTree() {
        printHelper(root, "", "0");
    }
}
