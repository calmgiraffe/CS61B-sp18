package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Map object to represent the underlying data type (TETIle[][]),
 * and other invariants like its width, height, numRooms, etc
 */
public class Map {

    /**
     * Map instance variables
     */
    protected static TETile[][] map;
    private final int width;
    private final int height;
    private final Partition partition;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private PriorityQueue<Partition> partitionsMaxHeap;

    /**
     * Map constructor
     */
    public Map(int width, int height) {
        Map.map = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.partition = new Partition(new Position(0, 0), width, height);
        this.fillWithNothing(); // Initially fill map with Tileset.NOTHING
    }

    /**
     * Fill the map with NOTHING Tileset.
     */
    private void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Randomly generates some rectangular rooms on the map.
     */
    public void generateRooms() {
        Partition.splitAndConnect(partition); // make binary tree of partitions and draw hallways, making a connected graph
        // Partition.addRooms(rooms, partition); // traverse partition tree and add leafs to rooms array
        addToPartitionsMaxHeap(partition.pQueue());

        int count = 0;
        int exclude = Game.random.nextIntInclusive(3);
        for (Partition p : partition.pQueue()) {
            if (count % 4 != -1) {
                p.room().drawRoom();

            }
            count += 1;
        }

        for (int i = 0; i < 0; i++) {
            Partition p1 = partitionsMaxHeap.poll();
            Partition p2 = partitionsMaxHeap.poll();
            Room.drawPath(p1.room(), p2.room());
        }
    }

    public void addToPartitionsMaxHeap(PriorityQueue<Partition> pq) {
        partitionsMaxHeap = new PriorityQueue<>(Partition.getInverseDistanceComparator());
        partitionsMaxHeap.addAll(pq);
    }
}
