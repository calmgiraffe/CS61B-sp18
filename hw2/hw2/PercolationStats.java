package hw2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    /** Instance variables */
    private final int numExperiments;
    private final PercolationFactory pf;
    private final double[] thresholds;

    /**
     * Constructor
     *
     * @param N percolation experiment side length
     * @param T number of experiements
     * @param pf percolation factor object
     */
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("N and/or T must be greater than 0");
        }
        this.numExperiments = T;
        this.pf = pf;
        this.thresholds = new double[T];

        // perform T independent experiments on an N-by-N grid
        for (int i = 0; i < T; i += 1) {
            Percolation newPercolation = pf.make(N);
            double thresh = calculateThreshold(newPercolation);
            thresholds[i] = thresh;
        }
    }

    /**
     * Repeat the following until the system percolates:
     * Choose a site uniformly at random among all blocked sites.
     * Open the site.
     * The fraction of sites that are opened when the system percolates
     * provides an estimate of the perc threshold.
     */
    private double calculateThreshold(Percolation perc) {
        // Approach: fill arraylist with numbers 0 through N*N - 1
        // Shuffle the arraylist
        // Continuously remove last item from list until percolation

        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < perc.numberOfTotalSites(); i += 1) {
            positions.add(i);
        }
        Collections.shuffle(positions);

        while (!perc.percolates()) {
            int oneDPos = positions.remove(positions.size() - 1);
            int row = perc.oneDtoRow(oneDPos);
            int col = perc.oneDtoCol(oneDPos);
            perc.open(row, col);
        }
        return perc.numberOfOpenSites() / (double) perc.numberOfTotalSites();
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(thresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(thresholds);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        return mean() - (1.96 * stddev() / Math.sqrt(numExperiments));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        return mean() + (1.96 * stddev() / Math.sqrt(numExperiments));
    }

    public static void main(String[] args) {
        PercolationStats ps = new PercolationStats(200, 1000, new PercolationFactory());
        System.out.println("Mean: " + ps.mean());
        System.out.println("95% low: " + ps.confidenceLow());
        System.out.println("95% high: " + ps.confidenceHigh());
    }
}
