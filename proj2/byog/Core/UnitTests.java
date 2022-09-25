package byog.Core;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UnitTests {

    @Test
    public void calculateDistanceTesting() {
        Position p1 = new Position(4, 34);
        Position p2 = new Position(20, 19);
        assertEquals(Position.calculateDistance(p1, p2), 21.9, 0.1);
        System.out.println(Position.calculateDistance(p1, p2));
    }
}