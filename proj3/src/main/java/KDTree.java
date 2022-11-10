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
        long id;
        Point point;
        Node left;
        Node right;
        Node goodSide;
        Node badSide;
        int depth;

        Node(Long id, Point point, int depth) {
            this.id = id;
            this.point = point;
            this.depth = depth;
        }
    }

    // kd tree instance variables
    private Node root;
    private final ArrayList<Long> nodeIDs;
    private final HashMap<Long, GraphDB.Node> nodesMap;

    // Constructor
    public KDTree(HashMap<Long, GraphDB.Node> nodesMap) {
        this.nodeIDs = new ArrayList<>();
        this.nodesMap = nodesMap;
        ArrayList<Integer> nodesIndexes = new ArrayList<>();

        // Create a list of all nodeIDs
        nodeIDs.addAll(nodesMap.keySet());

        // Add numbers 0 through nodes.size() - 1 to the array, then shuffle
        for (int i = 0; i < nodeIDs.size(); i += 1) {
            nodesIndexes.add(i);
        }
        Collections.shuffle(nodesIndexes);

        /* Generate the KDTree by iterating through nodeIndexes, getting the
        corresponding GraphDB.Node, adding randomly to the tree */
        for (int i : nodesIndexes) {
            Long id = nodeIDs.get(i);
            GraphDB.Node n = nodesMap.get(id);
            insert(id, n);
        }
        // printTree();
    }

    private Node insertHelper(Long id, GraphDB.Node n, int depth, Node curr) {

        if (curr == null) { // create a new node at curr from info in n
            return new Node(id, new Point(n.lon, n.lat), depth);

        } else if (depth % 2 == 0) { // if depth is even, compare longitude
            if (n.lon < curr.point.lon) {
                curr.left = insertHelper(id, n, depth + 1, curr.left);
            } else {
                curr.right = insertHelper(id, n, depth + 1, curr.right);
            }
        } else { // if depth is odd, compare latitude
            if (n.lat < curr.point.lat) {
                curr.left = insertHelper(id, n, depth + 1, curr.left);
            } else {
                curr.right = insertHelper(id, n, depth + 1, curr.right);
            }
        }
        return curr;
    }

    /** Inserts a node into its proper position in the tree. */
    public void insert(Long id, GraphDB.Node n) {
        root = insertHelper(id, n, 0, root);
    }

    private Node nearestHelper(Node curr, Point goal, Node best) {
        // Base case: return to parent
        if (curr == null) {
            return best;
        }
        // Update Node best and this.bestDistance if curr Node better
        double currDistance = GraphDB.distance(curr.point.lon, curr.point.lat, goal.lon, goal.lat);
        double bestDistance = GraphDB.distance(best.point.lon, best.point.lat, goal.lon, goal.lat);
        if (currDistance < bestDistance) {
            best = curr;
            bestDistance = currDistance;
        }
        // Determine whether to compare lon or lat based on depth
        double currDimension;
        double goalDimension;
        if (curr.depth % 2 == 0) {  // if even depth, compare lon
            currDimension = curr.point.lon;
            goalDimension = goal.lon;
        } else {                    // if odd depth, compare lat
            currDimension = curr.point.lat;
            goalDimension = goal.lat;
        }
        // Determination of "good" side
        if (goalDimension < currDimension) {
            curr.goodSide = curr.left;
            curr.badSide = curr.right;
        } else {
            curr.goodSide = curr.right;
            curr.badSide = curr.left;
        }
        // Search good branch then bad side, if possible closer node could exist there
        // Pruning rule: is straight line distance to split line is less than bestDistance?
        best = nearestHelper(curr.goodSide, goal, best);
        if (Math.pow(goalDimension - currDimension, 2) < Math.pow(bestDistance, 2)) {
            best = nearestHelper(curr.badSide, goal, best);
        }
        return best;
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
        Node sentinel = new Node((long) -1, new Point(0, 0), -1);
        Node nearest = nearestHelper(root, new Point(lon, lat), sentinel);
        return nearest.id;
    }

    /**
     * Naive O(n) approach to determining the nearest node's id, used for testing.
     * Iterate through all nodes and update bestNode and bestDist accordingly.
     */
    public long nearestNaive(double lon, double lat) {
        long bestNode = 0;
        double bestDistance = Double.MAX_VALUE;

        for (long id : nodeIDs) {
            GraphDB.Node n = nodesMap.get(id);
            double currDistance = GraphDB.distance(n.lon, n.lat, lon, lat);
            if (currDistance < bestDistance) {
                bestDistance = currDistance;
                bestNode = id;
            }
        }
        return bestNode;
    }

    private void printHelper(Node curr, String indent, String prefix) {
        if (curr == null) { // if leaf
            return;
        }
        System.out.println(prefix + curr.depth + indent + " (" + curr.point.lon + ", " +
                curr.point.lat + ")");
        printHelper(curr.left, indent + "    ", "L");
        printHelper(curr.right, indent + "    ", "R");
    }

    /** Debugging tool to view tree structure */
    public void printTree() {
        printHelper(root, "", "0");
    }
}
