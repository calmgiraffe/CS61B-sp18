package lab11.graphs;


import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;
    private Queue<Integer> fringe = new ArrayDeque<>();

    /**
     * Constructor
     * @param m maze object
     * @param sourceX x coordinate of the start
     * @param sourceY y coordinate of the start
     * @param targetX x coordinate of the target
     * @param targetY y coordinate of the target
     */
    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;

    }

    /** Conducts a breadth first search of the maze starting at the source. */
    // Recall: is not a recursive algorithm
    private void bfs() {
        // Don't forget to update distTo, edgeTo, and marked, as well as call announce()
        fringe.add(s);
        marked[s] = true;
        distTo[s] = 0;

        while (fringe.size() > 0 && !targetFound) {
            int v = fringe.remove();

            for (int neighbour: maze.adj(v)) {
                if (!marked[neighbour]) {
                    marked[neighbour] = true;
                    edgeTo[neighbour] = v;
                    distTo[neighbour] = distTo[v] + 1;
                    announce(); // call whenever want drawing to be updated

                    if (neighbour == t) {
                        targetFound = true;
                    } else {
                        fringe.add(neighbour);
                    }
                }
            }
        }
    }

    @Override
    public void solve() {
        bfs();
    }
}

