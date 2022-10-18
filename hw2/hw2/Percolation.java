package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.ArrayList;

// all methods should take constant time plus a number of constant calls to union-find methods
// void union(int p, int q): Unions two items.
// int find(int p): Returns set number (parent, i.e., root) of a given item.
// boolean connected(int p, int q): Returns true if items are connected.
// int count(): Returns the num of sets.

public class Percolation {

    /* Instance variables */
    boolean[][] grid; // When grid[row][col] is true, the corresponding site is open
    int sideLength;
    int numOpenSites;
    WeightedQuickUnionUF sets;
    int top; // n*n is top
    int bottom; // n*n + 1 is bottom

    // create N-by-N grid, with all sites initially blocked
    public Percolation(int N) {
        this.grid = new boolean[N][N];
        this.sideLength = N;
        this.numOpenSites = 0;
        this.sets = new WeightedQuickUnionUF(N * N + 1);
        this.top = N * N;
        this.bottom = N * N + 1;
    }

    // open the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!isOpen(row, col)) {
            grid[row][col] = true;
            numOpenSites += 1;
            connectAdjacents(row, col);
        }
    }

    // Check for open adjacent sites that are open
    public void connectAdjacents(int row, int col) {
        int curr = xyTo1D(row, col);

        // If not on left edge, union with left
        if (curr % sideLength > 0) {
            join(curr - 1, curr);
        }
        // If not on right edge, union with right
        if (curr % sideLength < (sideLength - 1)) {
            join(curr + 1, curr);
        }
        // If not on top edge, union with up
        // Else, union with top (source)
        int up = curr - sideLength;
        if (up < 0) {
            join(top, curr);
        } else {
            join(up, curr);
        }
        // If not on bottom edge, union with down
        // Else, union with bottom (sink)
        int down = curr + sideLength;
        if (down >= sideLength * sideLength) {
            join(bottom, curr);
        } else {
            join(down, curr);
        }
    }

    // if adj is open, add current site to set of adjacent site
    public void join(int adj, int curr) {
        if (adj == top || adj == bottom || isOpen(oneDtoRow(adj), oneDtoCol(adj))) {
            sets.union(adj, curr);
        }
    }

    // is the site (row, col) open (has been excavated)?
    public boolean isOpen(int row, int col) {
         return grid[row][col];
    }

    // is the site (row, col) full (has water reached it)?
    // Approach: see if site is part of the same set as top site
    public boolean isFull(int row, int col) {
        return sets.connected(top, xyTo1D(row, col));
    }

    /**
     * Converts a row, col pair to a 1D coordinate, where the top left corner is 0, increasing
     * from left to right
     */
    private int xyTo1D(int row, int col) {
        return sideLength * row + col;
    }

    /**
     * Converts a 1D coordinate to row
     */
    private int oneDtoRow(int position) {
        return position / sideLength;
    }

    /**
     * Converts a 1D coordinate to col
     */
    private int oneDtoCol(int position) {
        return position % sideLength;
    }

    // number of open sites
    public int numberOfOpenSites() {
        return numOpenSites;
    }

    // does the system percolate?
    // Approach: see if top and bottom are in the same set
    public boolean percolates() {
        return sets.connected(top, bottom);
    }

    // use for unit testing (not required)
    public static void main(String[] args) {
        return; // do nothing
    }
}
