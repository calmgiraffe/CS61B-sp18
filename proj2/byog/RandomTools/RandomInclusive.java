package byog.RandomTools;

import java.util.Random;

/**
 * Made this because the built-in Random class makes the code hard to read */
public class RandomInclusive extends Random {
    private final Random r;

    public RandomInclusive(long seed) {
        r = new Random(seed);
    }

    /** Returns a random positive integer between lower (inclusive) and upper (inclusive) */
    public int nextInt(int lower, int upper) {
        return r.nextInt(upper - lower + 1) + lower;
    }


    /**
     * Returns a random positive integer between 0 (inclusive) and upper (inclusive)
     */
    @Override
    public int nextInt(int upper) {
        return r.nextInt(upper + 1);
    }
}
