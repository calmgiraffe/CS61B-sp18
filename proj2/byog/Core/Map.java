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
        Partition.addRooms(rooms, partition); // traverse partition tree and add leafs to rooms array
        //addToPartitionsMaxHeap(partition.pQueue());

        for (Room r : rooms) {
            r.drawRoom();
        }

        PriorityQueue<Partition> pq = partition.pQueue();
        int size = pq.size();

        /*
        for (int i = 0; i < 3*size / 4; i++) {
            pq.poll();
        }
        for (int i = 0; i < size / 8; i++) {
            Partition p1 = pq.poll();
            Partition p2 = pq.poll();
            Room.drawPath(p1.room(), p2.room());
        }
        */

    }

    public void addToPartitionsMaxHeap(PriorityQueue<Partition> pq) {
        partitionsMaxHeap = new PriorityQueue<>(Partition.getInverseDistanceComparator());
        partitionsMaxHeap.addAll(pq);
    }
}
