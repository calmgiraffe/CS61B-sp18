package byog.Core;

import java.util.Random;

public class RandomExtra extends Random {
    private final Random r;

    public RandomExtra(long seed) {
        r = new Random(seed);
    }

    /**
     * Returns a random positive integer between lower (inclusive) and upper (inclusive) */
    public int nextIntInclusive(int lower, int upper) {
        return r.nextInt(upper - lower + 1) + lower;
    }

    /**
     * Returns a random positive integer between 0 (inclusive) and upper (inclusive) */
    public int nextIntInclusive(int upper) {
        return r.nextInt(upper + 1);
    }
}
