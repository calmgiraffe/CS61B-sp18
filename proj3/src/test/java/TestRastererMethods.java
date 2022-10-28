import org.junit.Assert;
import org.junit.Test;

public class TestRastererMethods {

    @Test
    public void testBinarySearch() {
        Rasterer r = new Rasterer();
        double ullon = -122.25;
        double ullat = 37.85;

        // Testing depth 3
        int x2 = r.binarySearch(r.lonCache[3], 0, 8, ullon); // 4
        int y2 = r.binarySearch(r.latCache[3], 0, 8, -1*ullat); // 4
        Assert.assertEquals(x2, 4);
        Assert.assertEquals(y2, 4);

        // Testing out of range, lon
        int x3 = r.binarySearch(r.lonCache[6], 0, 64, -125); // 0
        int x4 = r.binarySearch(r.lonCache[6], 0, 64, -120); // 63
        Assert.assertEquals(x3, 0);
        Assert.assertEquals(x4, 63);

        // Testing out of range, lat
        int y3 = r.binarySearch(r.latCache[6], 0, 64, -40); // 0
        int y4 = r.binarySearch(r.latCache[6], 0, 64, -30); // 63
        Assert.assertEquals(y3, 0);
        Assert.assertEquals(y4, 63);
    }


}
