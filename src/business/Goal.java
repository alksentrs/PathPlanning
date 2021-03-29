package business;

import util.Point2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Goal extends Ellipse2D {

    private Point2D point;
    private double radius;

    public Goal(Point2D point, double radius) {
        this.point = point;
        this.radius = radius;
    }

    public Point2D getPoint() {
        return point;
    }

    public void setPoint(Point2D point) {
        this.point = point;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double getX() {
        return point.getX();
    }

    @Override
    public double getY() {
        return point.getY();
    }

    @Override
    public double getWidth() {
        return 2*radius;
    }

    @Override
    public double getHeight() {
        return 2*radius;
    }

    @Override
    public boolean isEmpty() {
        return ((point==null)||(radius<=0));
    }

    @Override
    public void setFrame(double x, double y, double width, double height) {
        this.point = new Point2D(x,y);
        this.radius = (width+height)/2;
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle((int)(point.getX()-radius),(int)(point.getY()-radius),(int)(2*radius),(int)(2*radius));
    }

    public boolean contains(Point2D point) {
        return this.point.distance(point) <= radius;
    }
}
