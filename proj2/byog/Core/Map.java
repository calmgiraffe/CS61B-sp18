package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

 /* Map object to represent the underlying data type (TETIle[][]),
  * and other invariants like its width, height, numRooms, etc */
public class Map {

     /* Inner class to represent an imaginary rectangular partition of the map.
      * Position p is the coordinate of the lower left corner */
     private class Partition {

         /* Partition class and instance variables */
         static final int MIN = 4;
         static final int MAX = 12;
         private final Position p;
         private int width;
         private int height;

         /* Partition constructor */
         Partition(Position p, int width, int height) {
             this.p = p;
             this.width = width;
             this.height = height;
         }

         /* Makes another partition about a point about border,
          * which is approximately in the middle of current partition's width.
          * and adds to list. Then, updates the width of the current partition. */
         public Partition splitHorizontally(int border) {
             Position newPos = new Position(p.X() + border, p.Y());
             Partition newPartition = new Partition(newPos, width - border, height);

             // Update the existing partition and return new Partition
             width = border;
             return newPartition;
         }

         /* Same as above but about the current partition's height */
         public Partition splitVertically(int border) {
             Position newPos = new Position(p.X(), p.Y() + border);
             Partition newPartition = new Partition(newPos, width, height - border);

             // Update the existing partition and return new Partition
             height = border;
             return newPartition;
         }

         /* Draws a room inside the partition whose area is between 4x4 and the exact dimensions of the partition area.
          *  Because a room */
         public void generateRandomRoom() {
             int widthRange = width - 4;
             int heightRange = height - 4;
         }

         /* Draws a rectangular room of width and height that fits exactly within the current partition.
          * Tileset.WALL is used as the wall tile, and the room is filled with Tileset.FLOOR */
         public void generateRoom() {
             int startX = p.X();
             int startY = p.Y();
             int endX = startX + width - 1;
             int endY = startY + height - 1;

             // Draw top and bottom walls
             for (int x = startX; x <= endX; x++) {
                 map[x][startY] = Tileset.WALL;
                 map[x][endY] = Tileset.WALL;
             }
             // Draw left and right walls
             for (int y = startY; y <= endY; y++) {
                 map[startX][y] = Tileset.WALL;
                 map[endX][y] = Tileset.WALL;
             }
             // Draw interior
             for (int x = startX + 1; x <= endX - 1; x++) {
                 for (int y = startY + 1; y <= endY - 1; y++) {
                     map[x][y] = Tileset.FLOOR;
                 }
             }
         }

         /* Pass in random object */
         public Partition split() {
             // if MIN <= width <= MAX and height > MAX, split vertically
             if (width >= MIN && width <= MAX && height > MAX) {
                 int border = random.nextInt(height - (2*MIN - 1)) + MIN;
                 return this.splitVertically(border);
             }
             // if MIN <= height <= MAX and width > MAX, split horizontally
             else if (height >= MIN && height <= MAX && width > MAX) {
                 int border = random.nextInt(width - (2*MIN - 1)) + MIN;
                 return this.splitHorizontally(border);
             }
             // if width and height between MIN and MAX, do nothing
             else if (height >= MIN && height <= MAX && width >= MIN && width <= MAX) {
                 return null;
             }
             // Do either
             else {
                 int choice = random.nextInt(2);
                 if (choice == 0) {
                     int border = random.nextInt(width - 7) + 4;
                     return this.splitHorizontally(border);

                 } else {
                     int border = random.nextInt(height - 7) + 4;
                     return this.splitVertically(border);
                 }
             }
         }

     }
    /* Map instance variables */
    private final TETile[][] map;
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
        this.fillWithNothing(); // Initially fill map with Tileset.NOTHING
    }

    /* Fill the map with Tileset.NOTHING */
    private void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    /* Iterate through partitions and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions or result of random generator.
     * Add the result to a new List, then replace this.partitions */
    public void makePartitions() {
        boolean canSplit = true;
        while (canSplit) {
            ArrayList<Partition> newList = new ArrayList<>();

            for (Partition p : partitions) {
                newList.add(p); // add current partition

                Partition newPartition = p.split(); // get new partition
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

    /* Randomly generates some rectangular rooms on the map. */
    public void generateRooms() {
        this.makePartitions();

        for (Partition p : partitions) {
            p.generateRoom();
        }
    }

     /* Returns the underlying TETile[][] object (its pointer) */
     public TETile[][] getMap() {
         return map;
     }
}
