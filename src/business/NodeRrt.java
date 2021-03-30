package business;

import util.Point2D;

import java.util.ArrayList;
import java.util.List;

public class NodeRrt extends Point2D {

    private static int count;

    private NodeRrt parent;
    private NodeRrt parentOptimized;
    private double distance;
    private List<NodeRrt> childList = new ArrayList<>();
    private int id;

    private double helper = 0;

    public NodeRrt(NodeRrt parent, Point2D point){
        super(point);
        this.parent = parent;
        this.distance = parent.getDistance() + parent.distance(this);
        id = count++;
    }

    public NodeRrt(Point2D point){
       super(point);
        this.parent = null;
        this.distance = 0;
        id = count++;
    }

    public NodeRrt getParentOptimized() {
        return parentOptimized;
    }

    public void setParentOptimized(NodeRrt parentOptimized) {
        this.parentOptimized = parentOptimized;
    }

    public int getId() {
        return id;
    }

    public void removeFromParentChildList() {
        if (null!=parent) parent.removeChild(this);
    }

    public void addToParentChildList() {
        if (null!=parent) parent.addChild(this);
    }

    public NodeRrt getParent() {
        return parent;
    }

    public void setParent(NodeRrt parent) {
        this.parent = parent;
    }

    public void setPoint(Point2D point) {
        setLocation(point);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getHelper() {
        return helper;
    }

    public void setHelper(double helper) {
        this.helper = helper;
    }

    public List<NodeRrt> getChildList() {
        return childList;
    }

    public void addChild(NodeRrt node) {
        childList.add(node);
    }

    public void removeChild(NodeRrt node) {
        childList.remove(node);
    }


    @Override
    public String toString() {

        String childSt = "{";
        for (int i=0; i<childList.size(); i++) childSt = childSt + childList.get(i).id + " ";
        childSt = childSt + "}";

        return "{\"id\" : " + id + ",\"distance\" : " + distance + ",\"x\" : " + getX() + ",\"y\" : " + getY() + ",\"parent\" : " + (parent!=null ? parent.id : "null") + ",\"child\" : " + childSt+ "}";
    }
}

