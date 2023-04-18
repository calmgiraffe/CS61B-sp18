package byog.Core.Level.Map;

import byog.RandomTools.RandomInclusive;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UnitTests {
    private static final RandomInclusive rand = new RandomInclusive(1);

    @Test
    public void positionToOneDimensional() {
        Map map = new Map(8, 8, rand);
        Position p1 = new Position(7, 7);
        Position p2 = new Position(5, 3);
        Position p3 = new Position(3, 3);
        assertEquals(map.to1D(p1.x, p1.y), 63);
        assertEquals(map.to1D(p2.x, p2.y), 29);
        assertEquals(map.to1D(p3.x, p3.y), 27);
    }

    @Test
    public void OneDimensionalToPosition() {
        Map map = new Map(10, 8, rand);
        int p1 = 32;
        int p2 = 67;
        assertEquals(map.toPosition(p1).x, 2);
        assertEquals(map.toPosition(p1).y, 3);
        assertEquals(map.toPosition(p2).x, 7);
        assertEquals(map.toPosition(p2).y, 6);
    }
}
