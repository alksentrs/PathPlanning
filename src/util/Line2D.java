package util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Line2D extends java.awt.geom.Line2D {

    private util.Point2D p1;
    private util.Point2D p2;

    public Line2D(double x1, double y1, double x2, double y2) {
        p1 = new util.Point2D(x1,y1);
        p2 = new util.Point2D(x2,y2);
    }

    public Line2D(util.Point2D p1, util.Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public double getX1() {
        return p1.getX();
    }

    @Override
    public double getY1() {
        return p1.getY();
    }

    @Override
    public Point2D getP1() {
        return p1;
    }

    @Override
    public double getX2() {
        return p2.getX();
    }

    @Override
    public double getY2() {
        return p2.getY();
    }

    @Override
    public Point2D getP2() {
        return p2;
    }

    @Override
    public void setLine(double x1, double y1, double x2, double y2) {
        p1 = new util.Point2D(x1,y1);
        p2 = new util.Point2D(x2,y2);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return null;
    }
}
