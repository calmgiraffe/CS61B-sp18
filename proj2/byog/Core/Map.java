package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Map object to represent the underlying data type (TETIle[][]),
 * and other invariants like its width, height, numRooms, etc */
public class Map {

    /* Map instance variables */
    private final TETile[][] map;
    private final int width;
    private final int height;
    private final RandomExtra random;
    private ArrayList<Partition> partitions = new ArrayList<>();

    /** Map constructor */
    public Map(int width, int height, String seed) {
        this.map = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.random = new RandomExtra(Long.parseLong(seed));
        this.partitions.add(new Partition(new Position(0, 0), width, height, map, random));
        this.fillWithNothing(); // Initially fill map with Tileset.NOTHING
    }

    /**
     * Fill the map with Tileset.NOTHING */
    private void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Iterate through partitions and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions or result of random generator.
     * Add the result to a new List, then replace partitions */
    public void generatePartitions() {
        boolean canSplit = true;
        while (canSplit) {
            ArrayList<Partition> newList = new ArrayList<>();

            for (Partition p : partitions) {
                newList.add(p); // add current partition

                Partition newPartition = Partition.split(p); // get new partition
                if (newPartition != null) {
                    newList.add(newPartition);
                }
            }
            if (newList.equals(partitions)) { // if old list same as new, no further partitions can be made
                canSplit = false;
            }
            partitions = newList;
        }
    }

    /**
     * Randomly generates some rectangular rooms on the map. */
    public void generateRooms() {
        this.generatePartitions();

        int count = 0;
        int exclude = random.nextInt(3);
        for (Partition p : partitions) {
            if (count % 4 != exclude) {
                p.generateRandomRoom();
                p.drawRoom();
            }
            count += 1;
        }
    }

     /**
      * Returns the underlying TETile[][] object (its pointer) */
     public TETile[][] getMap() {
         return map;
     }
}
