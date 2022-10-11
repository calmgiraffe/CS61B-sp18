package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Solver {

    private static class SearchNode {
        WorldState state;
        int moves;
        int estDist;
        SearchNode prevNode;

        SearchNode(WorldState state, int moves, int estDist, SearchNode prevNode) {
            this.state = state;
            this.moves = moves;
            this.estDist = estDist;
            this.prevNode = prevNode;
        }
    }

    /** Instance variables of Solver */
    private final int moves;
    private final ArrayList<WorldState> solution = new ArrayList<>();
    private HashMap<WorldState, Integer> cache = new HashMap<>();

    /**
     * Constructor which solves the puzzle, computing
     * everything necessary for moves() and solution() to
     * not have to solve the problem again. Solves the
     * puzzle using the A* algorithm. Assumes a solution exists.
     */
    public Solver(WorldState initial) {
        MinPQ<SearchNode> fringe = new MinPQ<>(new NodeComparator());
        SearchNode sentinel = new SearchNode(null, -1, -1, null);
        fringe.insert(new SearchNode(initial, 0, 0, sentinel));

        SearchNode curr;
        while (true) {
            curr = fringe.delMin();
            if (curr.state.isGoal()) {
                this.moves = curr.moves;
                break;
            }
            for (WorldState nextState : curr.state.neighbors()) {
                if (nextState.equals(curr.prevNode.state)) {
                    continue;
                }

                int estDist = getEstimatedDistance(nextState);
                SearchNode newNode = new SearchNode(nextState, curr.moves + 1, estDist, curr);
                fringe.insert(newNode);
            }
        }
        while (curr.prevNode != null) {
            solution.add(curr.state);
            curr = curr.prevNode;
        }
    }

    private int getEstimatedDistance(WorldState word) {
        if (!cache.containsKey(word)) {
            cache.put(word, word.estimatedDistanceToGoal());
        }
        return cache.get(word);
    }

    /**
     * Returns the minimum number of moves to solve the puzzle starting
     * at the initial WorldState.
     */
    public int moves() {
        return moves;
    }

    /**
     * Returns a sequence of WorldStates from the initial WorldState
     * to the solution.
     */
    public Iterable<WorldState> solution() {
        Collections.reverse(solution);
        return solution;
    }

    private static class NodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode n1, SearchNode n2) {
            return (n1.moves + n1.estDist) - (n2.moves + n2.estDist);
        }
    }

}
