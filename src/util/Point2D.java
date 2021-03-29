package util;

public class Point2D extends java.awt.geom.Point2D {

    protected double x = 0;
    protected double y = 0;

    public Point2D() {
    }

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D add(Vector2D vector) {
        return new Point2D(this.x + vector.getX(), this.y + vector.getY());
    }
}
