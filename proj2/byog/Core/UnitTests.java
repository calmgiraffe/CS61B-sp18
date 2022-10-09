package byog.Core;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UnitTests {

    @Test
    public void calculateEuclidean() {
        Position p1 = new Position(4, 34);
        Position p2 = new Position(20, 19);
        assertEquals(Position.euclidean(p1, p2), 481);
        System.out.println(Position.euclidean(p1, p2));
    }

    @Test
    public void positionToOneDimensional() {
        Map map = new Map(8, 8, 2130, false);
        Position p1 = new Position(7, 7);
        Position p2 = new Position(5, 3);
        Position p3 = new Position(3, 3);
        assertEquals(map.positionToOneD(p1), 63);
        assertEquals(map.positionToOneD(p2), 29);
        assertEquals(map.positionToOneD(p3), 27);
    }

    @Test
    public void OneDimensionalToPosition() {
        Map map = new Map(10, 8, 2130, false);
        int p1 = 32;
        int p2 = 67;
        assertEquals(map.oneDToPosition(p1).x, 2);
        assertEquals(map.oneDToPosition(p1).y, 3);
        assertEquals(map.oneDToPosition(p2).x, 7);
        assertEquals(map.oneDToPosition(p2).y, 6);
    }

    @Test
    public void adjacentTest() {
        Map map = new Map(8, 8, 999, false);
        int p1 = map.positionToOneD(new Position(7, 7)); // corner
        int p2 = map.positionToOneD(new Position(7, 4)); // edge
        int p3 = map.positionToOneD(new Position(4, 4)); // middle

        for (Integer p : map.adjacent(p3)) {
            System.out.println(p);
        }
    }

}
