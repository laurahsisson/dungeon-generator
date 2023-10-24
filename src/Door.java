package dungeon;
import java.awt.Point;
import java.awt.Color;
import java.util.Random;
/**
 *
 * @author Ben
 */
public class Door { //Connects room1 to room2 through the rectangle of point1+point2
    private Room room1;
    private Point point1;
    private Room room2;
    private Point point2;
    public Color color;
    public Door(Room room1, Point point1, Room room2){
        this.room1=room1;
        this.point1=point1;
        this.room2=room2;
        Random rand = Dungeon.rand;
        color = new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
    }
    public void setPos(Point point2){
        this.point2=point2;
    }
    public Room getRoom1(){
        return room1;
    }
    public Room getRoom2(){
        return room2;
    }
    public Point getPoint1(){
        return point1;
    }
    public Point getPoint2(){
        return point2;
    }
    public boolean contains(Room r){
        return room1==r||room2==r; //Reference equals works fine here
    }
    public boolean contains(Point p){
        return point1.equals(p)||point2.equals(p); 
    }
}
