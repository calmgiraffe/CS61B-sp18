package lab11.graphs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *  @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {

    /* Private inner class to represent a vertex-distance pair */
    private class Node {
        private int vertex;
        private int distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;

        }
    }

    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;
    private PriorityQueue<Node> fringe;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
        fringe = new PriorityQueue<>(getDistanceComparator());
    }

    /** Estimate of the distance from v to the target. */
    private int manhattan(int v) {
        // Manhattan distance
        return Math.abs(maze.toX(v) - maze.toX(t)) + Math.abs(maze.toY(v) - maze.toY(t));
    }

    private int euclidian(int v) {
        double a2 = Math.pow(maze.toX(v) - maze.toX(t), 2);
        double b2 = Math.pow(maze.toY(v) - maze.toY(t), 2);
        return (int) (a2 + b2);
    }

    /** Performs an A star search from vertex s. */
    private void astar(int s) {
        fringe.add(new Node(s, 0));
        marked[s] = true; // don't need marked for alg, but need it here to display nodes on maze
        distTo[s] = 0;

        while (fringe.size() > 0 && !targetFound) {
            Node v = fringe.remove();

            // Relax all edges v -> neighbours
            for (int neighbour : maze.adj(v.vertex)) {

                // if current cumulative distance + 1 < previous stored distance to neighbour
                if (distTo[v.vertex] + 1 < distTo[neighbour])  {
                    marked[neighbour] = true;
                    distTo[neighbour] = distTo[v.vertex] + 1;
                    edgeTo[neighbour] = v.vertex;
                    fringe.add(new Node(neighbour, distTo[neighbour] + euclidian(neighbour)));
                    announce();
                }

                if (neighbour == t) {
                    targetFound = true;
                }
            }
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

    private static class DistanceComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            return o1.distance - o2.distance;
        }
    }

    public Comparator<Node> getDistanceComparator() {
        return new DistanceComparator();
    }

}

