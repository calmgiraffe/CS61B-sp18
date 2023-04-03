package byog.Core.Level;

import byog.RandomTools.RandomInclusive;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UnitTests {
    private static final RandomInclusive rand = new RandomInclusive(1);

    @Test
    public void positionToOneDimensional() {
        Level level = new Level(8, 8, rand);
        Position p1 = new Position(7, 7);
        Position p2 = new Position(5, 3);
        Position p3 = new Position(3, 3);
        assertEquals(level.to1D(p1.x, p1.y), 63);
        assertEquals(level.to1D(p2.x, p2.y), 29);
        assertEquals(level.to1D(p3.x, p3.y), 27);
    }

    @Test
    public void OneDimensionalToPosition() {
        Level level = new Level(10, 8, rand);
        int p1 = 32;
        int p2 = 67;
        assertEquals(level.toPosition(p1).x, 2);
        assertEquals(level.toPosition(p1).y, 3);
        assertEquals(level.toPosition(p2).x, 7);
        assertEquals(level.toPosition(p2).y, 6);
    }
}
