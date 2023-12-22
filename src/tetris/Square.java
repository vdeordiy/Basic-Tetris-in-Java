package tetris;

import java.awt.Point;
import java.awt.Color;

public class Square{
    private Color color;
    private Commons.State state;
    private Point rotation;
    private Point location;

    public Square(Point location, Point rotation){
        this.location = location;
        this.rotation = rotation;
    } 

    public Point getRawPoint(){
        return new Point(
            location.x + rotation.x, 
            location.y + rotation.y
        );
    }

    public Point getNormalizedPoint(){
        Point rawPoint = getRawPoint();

        return new Point(
            rawPoint.x * Commons.squareSize,
            rawPoint.y * Commons.squareSize
        );
    }

    public Color getColor(){
        return color;
    }

    public void setColor(Color newColor){
        this.color = newColor;
    }

    public Commons.State getState(){
        return state;
    }

    public void setState(Commons.State newState){
        this.state = newState;
    }

    public void translate(int dx, int dy){
        this.location = new Point(location.x + dx, location.y + dy);
    }

    public void rotate(){
        // rotate by 90 degrees
        this.rotation = new Point(-rotation.y, rotation.x);
    }

    public Point getLocation(){
        return location;
    }

    public Point getRotation(){
        return rotation;
    }
}
