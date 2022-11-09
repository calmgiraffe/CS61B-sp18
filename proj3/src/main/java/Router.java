import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {

    /** Private class to represent a vertex-distance pair in the pQueue. */
    private static class Node {
        long nodeID;
        double distance;

        Node(long nodeID, double distance) {
            this.nodeID = nodeID;
            this.distance = distance;
        }
    }

    private static class DistanceComparator implements Comparator<Node> {
        @Override
        public int compare(Node a, Node b) {
            if (a.distance - b.distance < 0) {
                return -1;
            } else if (a.distance - b.distance > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /** Return a Comparator for comparing priority of two Nodes in the PQ */
    private static Comparator<Node> getDistanceComparator() {
        return new DistanceComparator();
    }

    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     *
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {

        // Below is the implementation of a*
        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());
        HashMap<Long, Long> edgeTo = new HashMap<>(g.size());
        HashMap<Long, Double> distTo = new HashMap<>(g.size());

        // Find the closest nodes to start and dest coordinates
        long startID = g.closest(stlon, stlat);
        long destID = g.closest(destlon, destlat);

        // Fill distTo with infinity for all nodes except start node
        for (long nodeID : g.vertices()) {
            distTo.put(nodeID, Double.MAX_VALUE);
        }
        distTo.put(startID, 0.0);

        // Initially add start to PQ, then remove node until target found or fringe.size = 0
        fringe.add(new Node(startID, 0.0));
        boolean targetFound = false;
        while (fringe.size() > 0 && !targetFound) {
            long p = fringe.poll().nodeID;
            for (long q : g.adjacent(p)) {

                // If new distance < old distance, update distTo and edgeTo
                double weight = g.distance(p, q);
                if (distTo.get(p) + weight < distTo.get(q)) {
                    distTo.put(q, distTo.get(p) + weight);
                    edgeTo.put(p, q);

                    // Add neighbour q to fringe, with heuristic distance factored in
                    Node n = new Node(q, distTo.get(q) + g.distance(q, destID));
                    fringe.add(n);
                }
                if (q == destID) {
                    targetFound = true;
                }
            }
        }

        // Generate list of nodes corresponding to the shortest path
        ArrayList<Long> path = new ArrayList<>();
        Long curr = startID;
        while (curr != null) {
            path.add(curr);
            curr = edgeTo.get(curr);
        }
        return path;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     *
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        return null; // FIXME
    }

    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         *
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
