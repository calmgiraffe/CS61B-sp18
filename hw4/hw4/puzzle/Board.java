package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState {

    private static final int BLANK = 0;
    private final int maxTile;
    private final int[][] tiles;

    /**
     * Constructs a board from an N-by-N array of tiles where tiles[i][j] = tile at row i, column j
     */
    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i += 1) {
            System.arraycopy(tiles[i], 0, this.tiles[i], 0, tiles.length);
        }
        this.maxTile = tiles.length * tiles.length;
    }

    /**
     * Returns value of tile at row i, column j (or 0 if blank)
     * @param i row
     * @param j column
     * @return value of tile at (i, j)
     */
    public int tileAt(int i, int j) {
        return tiles[i][j];
    }

    /**
     * Returns the board size N
     * @return board size
     */
    public int size() {
        return tiles.length;
    }

    /**
     * Estimated distance to goal. This method should simply return the results of manhattan()
     * when submitted to Gradescope.
     * @return estimated distance to goal
     */
    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    /**
     * Returns neighbors of this board.
     * SPOILERZ: This is the answer.
     */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == BLANK) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = BLANK;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = BLANK;
                }
            }
        }
        return neighbors;
    }

    /**
     * Hamming estimate
     */
    public int hamming() {
        int estimate = 0;
        int n = 0;

        for (int row = 0; row < size(); row += 1) {
            for (int col = 0; col < size(); col += 1) {
                n += 1;
                if (tileAt(row, col) == BLANK) {
                    continue;
                }
                if (tileAt(row, col) != n % maxTile) {
                    estimate += 1;
                }

            }
        }
        return estimate;
    }

    /**
     * Manhattan estimate
     */
    public int manhattan() {
        int estimate = 0;
        int n = 0;

        for (int row = 0; row < size(); row += 1) {
            for (int col = 0; col < size(); col += 1) {
                n += 1;
                // check all 9 squares, looking for values 1-8 but excluding 0
                if (tileAt(row, col) == BLANK) {
                    continue;
                }
                if (tileAt(row, col) != n % maxTile) {
                    estimate += calcManhattanDistance(col, row, tileAt(row, col));
                }
            }
        }
        return estimate;
    }

    /**
     * Given the value at the current location, convert to x and y of correct position
     */
    private int calcManhattanDistance(int x, int y, int actual) {
        int correctX = (actual - 1) % size();
        int correctY = (actual - 1) / size();
        return Math.abs(correctX - x) + Math.abs(correctY - y);
    }

    /**
     * Returns true if this board's tile values are the same position as y's
     * @param y board
     */
    @Override
    public boolean equals(Object y) {
        if (!(y instanceof Board b)) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (this.tileAt(i, j) != b.tileAt(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the string representation of the board.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N).append("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i,j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }
}
