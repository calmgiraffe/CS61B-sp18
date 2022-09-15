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
        int border;
        ArrayList<Partition> newList= new ArrayList<>();

        for (Partition p : partitions) {
            newList.add(p);

            // Split vertically
            if (p.width() < Partition.MIN_WIDTH && p.height() >= Partition.MIN_HEIGHT) {
                border = random.nextInt(p.height() - 7) + 4;
                newList.add(p.splitVertically(border));
            }
            // Split horizontally
            else if (p.width() >= Partition.MIN_WIDTH && p.height() < Partition.MIN_HEIGHT) {
                border = random.nextInt(p.width() - 7) + 4;
                newList.add(p.splitHorizontally(border));
            }
            // Do either
            else if (p.width() >= Partition.MIN_WIDTH && p.height() >= Partition.MIN_HEIGHT) {
                int choice = random.nextInt(2);

                if (choice == 0) {
                    border = random.nextInt(p.width() - 7) + 4;
                    newList.add(p.splitHorizontally(border));

                } else {
                    border = random.nextInt(p.height() - 7) + 4;
                    newList.add(p.splitVertically(border));
                }
            }
        }
        partitions = newList;
    }

    /* Randomly generates some rectangular rooms on the map. */
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
