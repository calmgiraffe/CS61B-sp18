package lab11.graphs;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    public boolean hasCycle = false;
    public int[] parent;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        parent = new int[maze.V()];
    }

    @Override
    public void solve() {
        int s = maze.xyTo1D(1, 1);
        checkCycles(s);
    }

    // when cycle detected, algorithm should connect the cycles by setting
    // values in edgeTo array and calling announce()
    private void checkCycles(int curr) {
        marked[curr] = true;
        announce();

        for (int neighbour : maze.adj(curr)) {
            if (marked[neighbour] && parent[curr] != neighbour) {
                // mark neighbour's parent as curr so it forms a cycle
                parent[neighbour] = curr;
                drawEdges(curr);
                hasCycle = true;
                return;

            } else if (!marked[neighbour] && !hasCycle) {
                marked[neighbour] = true;
                parent[neighbour] = curr;
                checkCycles(neighbour);
            }
        }
    }

    /* Given a node v that is part of a cycle, draws the edges of the cycle and displays them. */
    private void drawEdges(int v) {
        edgeTo[v] = parent[v];
        announce();

        int curr = parent[v];
        int next = parent[curr];

        while (curr != v) {
            edgeTo[curr] = next;
            next = parent[next];
            curr = parent[curr];
            announce();
        }
    }
}

