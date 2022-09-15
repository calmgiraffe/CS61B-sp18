package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


 /* Map object to represent the underlying data type (TETIle[][]),
  * and other invariants like its width, height, numRooms, etc */
public class Map {

    /* Inner class to represent an imaginary rectangular partition of the map.
     * Position p is the coordinate of the lower left corner */
    private class Partition {
        /* Partition instance variables */
        private Position p;
        private int width;
        private int height;

        /* Partition constructor */
        Partition(Position p, int width, int height) {
            this.p = p;
            this.width = width;
            this.height = height;
        }

        /* Makes another partition about a point approximately in the middle of current partition's width
         * and adds to list. Then, updates the width of the current partition. */
        public Partition divideHorizontally() {
            // Smallest width for partitioning is 6, because the smallest possible room is 3x3
            if (width < 6) {
                return null;
            }
            // ex: if width 7, can return (0 + 3) or (1 + 3)
            int border = random.nextInt(width - 5) + 3;
            int currentY = position().Y();
            Partition newPartition = new Partition(new Position(border, currentY), width() - border, height());

            // Update the existing partition and return new Partition
            this.updateWidth(border);
            return newPartition;
        }

        /* Same as above but about the current partition's height */
        public Partition divideVertically() {
            if (height < 6) {
                return null;
            }
            // ex: if width 7, can return (0 + 3) or (1 + 3)
            int border = random.nextInt(height - 5) + 3;
            int currentX = position().X();
            Partition newPartition = new Partition(new Position(currentX, border), width(), height() - border);

            // Update the existing partition and return new Partition
            this.updateHeight(border);
            return newPartition;
        }

        /* Draws a rectangular room of width and height, and places its bottom corner at position.
         * Tileset.WALL is used as the wall tile, and the room is filled with Tileset.FLOOR */
        public void generateRooms(int width, int height, Position p) {
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

        public void generateRoom() {
            int startX = this.p.X();
            int startY = this.p.Y();
            int endX = startX + this.width - 1;
            int endY = startY + this.height - 1;

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

        public void updateWidth(int newWidth) {
            width = newWidth;
        }

        public void updateHeight(int newHeight) {
            height = newHeight;
        }

        public Position position() {
            return p;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }
    }

    /* Map instance variables */
    private TETile[][] map;
    private int width;
    private int height;
    private Random random;
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

    public void makePartition() {
        ArrayList<Partition> newList= new ArrayList<>();
        for (Partition p : partitions) {
            newList.add(p);
            newList.add(p.divideHorizontally());
        }
        this.partitions = newList;
    }

    public void makeRoom() {
        this.makePartition();
        for (Partition p : partitions) {
            p.generateRoom();
        }
    }

     /* Returns the underlying TETile[][] object (its pointer) */
     public TETile[][] getMap() {
         return map;
     }
}
