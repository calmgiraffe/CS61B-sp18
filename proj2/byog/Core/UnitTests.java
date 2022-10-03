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
}
