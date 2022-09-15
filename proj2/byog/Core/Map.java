package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;


 /* Map object to represent the underlying data type (TETIle[][]),
  * and other invariants like its width, height, numRooms, etc */
public class Map {

    /* Map instance variables */
    private TETile[][] map;
    private final int width;
    private final int height;
    private final Random random;
    private ArrayList<Partition> partitions = new ArrayList<>();

    /* Map constructor */
    public Map(int width, int height, String seed) {
        this.map = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.random = new Random(Long.parseLong(seed));
        this.partitions.add(new Partition(new Position(0, 0), width, height));
    }

    /* Fill the map with Tileset.NOTHING */
    public void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    /* Iterate through the Partitions and apply either their divideHorizontally or divideVertically method,
     * depending on the value of choice. Add the result to a new List, then replace this.partitions */
    public void makePartition() {
        ArrayList<Partition> newList= new ArrayList<>();

        for (Partition p : partitions) {
            newList.add(p);

            int choice = random.nextInt(2);
            if (choice == 0 && p.width() >= 6) {
                int border = random.nextInt(p.width() - 5) + 3;
                newList.add(p.divideHorizontally(border));

            } else if (choice == 1 && p.height() >= 6) {
                int border = random.nextInt(p.height() - 5) + 3;
                newList.add(p.divideVertically(border));
            }
        }
        partitions = newList;
    }

    
    public void makeRooms() {
        for (int i = 0; i < 3; i++) {
            this.makePartition();
        }
        for (Partition p : partitions) {
            p.generateRoom(map);
        }
    }

     /* Returns the underlying TETile[][] object (its pointer) */
     public TETile[][] getMap() {
         return map;
     }
}
