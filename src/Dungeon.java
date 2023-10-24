package dungeon;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.util.Random;

import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Ben
 */
public class Dungeon extends JComponent {

    static final int PIX_SIZE = 5;
    static final int DUNGEON_LENGTH = 100;
    static final int DUNGEON_HEIGHT = 100;
    static final int DUNGEON_ATTEMPS = 10000;
    static final int DUNGEON_MIN_ROOMS = 150;
    static final int ROOM_MIN_DIM = 3; //Smallest a room's length/height can be
    static final int ROOM_MAX_DIM = 10; //Largest a room's length/height can be
    static ArrayList<Room> rooms;
    public static final Random rand = new Random(10231996);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        rooms = new ArrayList<>();
        int lastTry = 0;
        while (lastTry < DUNGEON_MIN_ROOMS) { //Keep trying to make the actual rooms in the dungeon until we have a sufficient number of rooms
            makeRects();
            lastTry = cullRects();
        }
        for (Room r : rooms) {
            r.setupDoors();
        }
        int startRoom = rand.nextInt(rooms.size());
        int endRoom = rand.nextInt(rooms.size());
        while (endRoom==startRoom){
            System.out.println("Same");
            endRoom = rand.nextInt(rooms.size());
        }
        System.out.println(startRoom + "," + endRoom);
        JFrame frame = new JFrame();
        Dungeon dun = new Dungeon();
        frame.add(dun);
        frame.setVisible(true);
        frame.toFront();
        frame.setSize(PIX_SIZE * DUNGEON_LENGTH, PIX_SIZE * DUNGEON_HEIGHT + 22);
        frame.repaint(); //Set up graphics

        for (int x=0; x<DUNGEON_LENGTH; x++){ //Print out the map as a string
            for (int y = 0; y < DUNGEON_HEIGHT; y++) {
                String s="*";
                for (Room r : rooms){
                    if (r.contains(x, y)){ 
                        s=r.getPos(x, y).toString();
                    }
                }
                System.out.print(s);
            }
            System.out.println();
        }
    }

    static private void makeRects() {
        int x = DUNGEON_ATTEMPS;
        rooms.clear();
        while (x > 0) { //Make a ton of rectangles until we fail to place a room enough times
            int length = rand.nextInt(ROOM_MAX_DIM - ROOM_MIN_DIM) + ROOM_MIN_DIM;
            int height = rand.nextInt(ROOM_MAX_DIM - ROOM_MIN_DIM) + ROOM_MIN_DIM;
            Room newRoom = new Room(rand.nextInt(DUNGEON_LENGTH - length), rand.nextInt(DUNGEON_HEIGHT - height), length, height, rand);
            boolean valid = true;
            for (Room r : rooms) {
                if (r.intersects(newRoom)) { //Make sure they dont collide
                    valid = false;
                }
            }
            if (valid) {
                rooms.add(newRoom);
            } else {
                x--;
            }
        }
        for (Room r : rooms) {
            for (Room xR : rooms) {
                if (r.tangent(xR) && r != xR) {//One time == will actually work
                    r.addNeighbor(xR);
                }
            }
        }
    }

    static private int cullRects() { //Find the largest island
        int curID = 0;
        ArrayList<Room> headRooms = new ArrayList<>();
        for (Room r : rooms) {
            if (r.getId() == -1) {
                r.setId(curID);
                headRooms.add(r);
                curID++;
            }
        }
        int[] newSize = new int[curID];
        for (int i = 0; i < rooms.size(); i++) {
            newSize[rooms.get(i).getId()]++;
        }
        int largestIsland = 0;
        int largestIndex = -1;
        for (int i = 0; i < newSize.length; i++) {
            if (newSize[i] > largestIsland) {
                largestIsland = newSize[i];
                largestIndex = i;
            }
        }
        for (int i = 0; i < newSize.length; i++) { //Kill of all the smaller islands
            if (i != largestIndex) {
                headRooms.get(i).kill(rooms);
            }
        }
        return largestIsland;
    } //Returns largest island size

    public void paintComponent(Graphics g) {

        Random rand = new Random();
        g.setColor(Color.BLACK);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, PIX_SIZE * DUNGEON_LENGTH, PIX_SIZE * DUNGEON_HEIGHT);

        for (Room r : rooms) {
            g.setColor(Color.BLACK);
            g.drawRect(r.getX() * PIX_SIZE, r.getY() * PIX_SIZE, r.getLength() * PIX_SIZE, r.getHeight() * PIX_SIZE);
            g.setColor(Color.GRAY);
            g.drawRect(r.getX() * PIX_SIZE + 1, r.getY() * PIX_SIZE + 1, r.getLength() * PIX_SIZE - 2, r.getHeight() * PIX_SIZE - 2);

            g.setColor(Color.red);
            for (Door d : r.doors()) {
                g.setColor(Color.blue);
                g.fillRect(d.getPoint1().x * PIX_SIZE, d.getPoint1().y * PIX_SIZE, PIX_SIZE, PIX_SIZE);
                g.fillRect(d.getPoint2().x * PIX_SIZE, d.getPoint2().y * PIX_SIZE, PIX_SIZE, PIX_SIZE);
            }
        }
    }
}
