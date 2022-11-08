public class KDTree {

    public static class Point {
        double lat;
        double lon;

        Point(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    public static class Node {
        Point point;
        boolean leftIsGoodSide;
        int depth;

        Node(Point point) {
            this.point = point;
        }
    }

    /* kd tree instance variables */
    private Node root;
    private int size;

    public KDTree() {

    }

    // inner static Node class
    // each node has:
    // lon and lat value (equivalent to x & y)
    // "left" and "right" branches that point to other Nodes
    // "good" and "bad" pointers that point to left & right branches
    // optional: depth (for knowing which dimension to compare to)

    // implementation:
    // for updating best, use great circle distance
    // pruning rule: use straight line distance instead; this is greater than great circle, but
    // faster to compute

    // insertion: do randomly
    // If N distinct keys are inserted in random order, expected tree height is ~ 4.311 ln N

    public void insert(Node n) {

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
        Point goalPoint = new Point(lat, lon);
        nearestHelper(root, goalPoint, null);
        return 0;
    }
}
