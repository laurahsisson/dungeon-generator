package dungeon;

import java.awt.Rectangle;
import java.awt.Point;
//import java.awt.line;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

/**
 *
 * @author Ben
 */
public class Room {

    private int x, y, length, height; //x and y are top left corner, length is distance to the right, height is distance down
    private Rectangle dim;
    private Rectangle dimWide; //Length+1
    private Rectangle dimLong; //Height+1

    private ArrayList<Room> rooms;
    private ArrayList<Door> doors;
    public ArrayList<Rectangle> rects;
    private int id = -1;
    private Random rand;
    public Color col;

    public static enum Position {

        CENTER {
                    public String toString() {
                        return " ";
                    }
                },
        LEFT {
                    public String toString() {
                        return "[";
                    }
                },
        RIGHT {
                    public String toString() {
                        return "]";
                    }
                },
        TOP {
                    public String toString() {
                        return "¯";
                    }
                },
        BOTTOM {
                    public String toString() {
                        return "_";
                    }
                },
        TOPLEFT {
                    public String toString() {
                        return "⎡";
                    }
                },
        TOPRIGHT {
                    public String toString() {
                        return "⎤";
                    }
                },
        BOTTOMLEFT {
                    public String toString() {
                        return "⎣";
                    }
                },
        BOTTOMRIGHT {
                    public String toString() {
                        return "⎦";
                    }
                },
        NIL {
                    public String toString() {
                        return "?";
                    }
                }
    };

    public Room(int x, int y, int length, int height, Random rand) { //Setup the room
        rooms = new ArrayList<>();
        rects = new ArrayList<>();
        doors = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.length = length;
        this.height = height;
        this.rand = rand;
        dim = new Rectangle(x, y, length, height);
        dimWide = new Rectangle(x - 1, y, length + 2, height);
        dimLong = new Rectangle(x, y - 1, length, height + 2);
        col = Color.GRAY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public boolean contains(int posX, int posY) {
        return dim.contains(new Point(posX, posY));
    }

    public boolean contains(Point p) {
        return dim.contains(p);
    }

    //public boolean containsExt()
    public boolean intersects(Room r) {
        return dim.intersects(r.dim);
    }

    public boolean tangent(Room r) {
        return dimLong.intersects(r.dim) || dimWide.intersects(r.dim);
        //return new Rectangle(x-1,y-1,length+2,height+2).intersects(r.dim);

    }

    public void addNeighbor(Room r) {
        if (!rooms.contains(r)) {
            rooms.add(r);
            r.rooms.add(this);
        }
    }

    public int getId() {
        return id;
    }

    public void kill(ArrayList<Room> toKill) { //Recursively remove this and all neighbouring rooms
        toKill.remove(this);
        for (Room r : rooms) {
            if (toKill.contains(r)) {
                r.kill(toKill);
            }
        }
        rooms.clear();
    }

    public void setId(int id) {
        this.id = id;
        for (Room r : rooms) {
            if (r.getId() == -1) {
                r.setId(id);
            }
        }
    }

    public void setupDoors() {
        for (Room r : rooms) {
            doorPoint(r);
        }
    }

    private void doorPoint(Room r) { //Find a valid point to place the door
        boolean found = false;
        Door toCheck = null;
        for (Door d : r.doors) { //Find the 
            if (d.contains(this)) {
                found = true;
                toCheck = d;
            }
        }
        if (found) {
            //System.out.println("A");
            Point p = toCheck.getPoint1();
            Point north = new Point(p.x - 1, p.y);
            Point south = new Point(p.x + 1, p.y);
            Point east = new Point(p.x, p.y - 1);
            Point west = new Point(p.x, p.y + 1);
            if (dim.contains(north)) { //Find our adjacent location in the connected room
                doors.add(toCheck);
                toCheck.setPos(north);
            } else if (dim.contains(south)) {
                doors.add(toCheck);
                toCheck.setPos(south);
            } else if (dim.contains(east)) {
                doors.add(toCheck);
                toCheck.setPos(east);
            } else if (dim.contains(west)) {
                doors.add(toCheck);
                toCheck.setPos(west);
            } else {
                System.out.println("Was this misplaced?");
            }
        } else { //Attempt to collide with the other room to find a range of positions to randomly place our new door
            Rectangle rWide = r.dimWide.intersection(dim);
            Rectangle rLong = r.dimLong.intersection(dim);
            int dX = 0, dY = 0;
            Point toAdd = new Point(-1, -1);
            if (!rWide.isEmpty()) {
                rects.add(rWide);
                dX = rand.nextInt(rWide.width) + rWide.x;
                dY = rand.nextInt(rWide.height) + rWide.y;
                toAdd = new Point(dX, dY);

            }
            if (!rLong.isEmpty()) {
                rects.add(rLong);
                dX = rand.nextInt(rLong.width) + rLong.x;
                dY = rand.nextInt(rLong.height) + rLong.y;
                toAdd = new Point(dX, dY);
            }
            doors.add(new Door(this, toAdd, r));
        }
        //System.out.println(rooms.contains(this));
    }

    public ArrayList<Door> doors() {
        return doors;
    }

    public Position getPos(int posX, int posY) { //Returns from position enum. Long method to find if the relative position of this point in the room (corner, wall, center etc.)
        Point p = new Point(posX, posY);
        Point north = new Point(p.x - 1, p.y);
        Point south = new Point(p.x + 1, p.y);
        Point east = new Point(p.x, p.y - 1);
        Point west = new Point(p.x, p.y + 1);
        if (contains(north)) {
            if (contains(south)) {
                if (contains(west)) {
                    if (contains(east)) {
                        return Position.CENTER;
                    }
                    else {
                        return Position.LEFT;
                    }
                } else {
                    if (contains(east)){
                        return Position.RIGHT;
                    }
                }
            }
        }
        for (Door d : doors) {
            if (d.getPoint1().equals(p)) {
                return Position.CENTER;
            }
            if (d.getPoint2().equals(p)) {
                return Position.CENTER;
            }
        }
        if (contains(north)) {
            if (contains(west)) {
                if (contains (east)){
                    return Position.BOTTOM;
                }
                return Position.TOPLEFT;
            } else if (contains(east)) {
                return Position.TOPRIGHT;
            } else {
                return Position.TOP;
            }
        }
        if (contains(south)) {
            if (contains(west)) {
                if (contains(east)){
                    return Position.TOP;
                }
                return Position.BOTTOMLEFT;
            } else if (contains(east)) {
                return Position.BOTTOMRIGHT;
            } else {
                return Position.TOP;
            }
        }
        return Position.NIL;
    }
}
