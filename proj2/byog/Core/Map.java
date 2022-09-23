package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.ArrayList;

/**
 * Map object to represent the underlying data type (TETIle[][]),
 * and other invariants like its width, height, numRooms, etc */
public class Map {

    /**
     * Map instance variables */
    private final TETile[][] map;
    private final int width;
    private final int height;
    private final Partition p;
    private final ArrayList<Partition> partitions = new ArrayList<>();

    /**
     * Map constructor */
    public Map(int width, int height) {
        this.map = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.p = new Partition(new Position(0, 0), width, height, this.map);
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
        Partition.split(p);
    }

    private void addLeafs(Partition p) {
        if (p.partitionA == null && p.partitionB == null) {
            partitions.add(p);
        } else {
            addLeafs(p.partitionA);
            addLeafs(p.partitionB);
        }
    }

    /**
     * Randomly generates some rectangular rooms on the map. */
    public void generateRooms() {
        // make binary tree of partitions
        generatePartitions();

        // traverse partition tree and add leafs to array
        addLeafs(this.p);

        int count = 0;
        int exclude = Game.random.nextIntInclusive(3); // exclude 1/4 of the rooms
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
