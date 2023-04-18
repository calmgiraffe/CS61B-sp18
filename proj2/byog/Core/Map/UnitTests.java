package byog.Core.Map;

import byog.Core.Position;
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
        assertEquals(map.to1D(p1.floorX(), p1.floorY()), 63);
        assertEquals(map.to1D(p2.floorX(), p2.floorY()), 29);
        assertEquals(map.to1D(p3.floorX(), p3.floorY()), 27);
    }

    @Test
    public void OneDimensionalToPosition() {
        Map map = new Map(10, 8, rand);
        int p1 = 32;
        int p2 = 67;
        assertEquals(map.toPosition(p1).floorX(), 2);
        assertEquals(map.toPosition(p1).floorY(), 3);
        assertEquals(map.toPosition(p2).floorX(), 7);
        assertEquals(map.toPosition(p2).floorY(), 6);
    }
}
