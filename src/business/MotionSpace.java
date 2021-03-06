package business;

import org.apache.commons.math3.linear.*;
import util.Graph;
import util.Line2D;
import util.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotionSpace {

    private int numOfConnections = 5;

    private int RRTMultiplier = 10;
    private int optimiseDistance = 40;

    private int rRTStarFnMaximumLength = 5000;

    private Goal goal;
    private double goalRadius = 10;

    private List<NodeRrt> rrtPoints = new ArrayList<>();
    private List<NodePrm> prmPoints = new ArrayList<>();
    private Graph<NodePrm> prmGraph = new Graph<>();

    private List<Point2D> pathPrm = new ArrayList<>();
    private NodeRrt pathRrt;

    private List<Rectangle> obstacles = new ArrayList<>();

    private List<List<Rectangle>> obstacleSets = new ArrayList<>();

    private double minDistance = 8 * RRTMultiplier / 10;

    private int width;
    private int height;

    public MotionSpace(int width, int height) {
        this.width = width;
        this.height = height;

        List<Rectangle> r1 = new ArrayList<>();
        r1.add(new Rectangle(width / 4, 0, 20, height / 4 - 20));
        r1.add(new Rectangle(width / 4, height / 4 + 20, 20, 3 * height / 4 - 20));
        r1.add(new Rectangle(3 * width / 4, 0, 20, 3 * height / 4 - 20));
        r1.add(new Rectangle(3 * width / 4, 3 * height / 4 + 20, 20, height / 4 - 20));

        List<Rectangle> r2 = new ArrayList<>();
        r2.add(new Rectangle(width / 4, height / 8, 20, 2 * height / 8 - 20));
        r2.add(new Rectangle(width / 4, 3 * height / 8 + 20, 20, 4 * height / 8 - 20));
        r2.add(new Rectangle(3 * width / 4, height / 8, 20, 4 * height / 8 - 20));
        r2.add(new Rectangle(3 * width / 4, 5 * height / 8 + 20, 20, 2 * height / 8 - 20));
        r2.add(new Rectangle(width / 4 + 20, height / 8, 2 * width / 4 - 20, 20));
        r2.add(new Rectangle(width / 4 + 20, 7 * height / 8 - 20, 2 * width / 4 - 20, 20));

        List<Rectangle> r3 = new ArrayList<>();
        r3.add(new Rectangle(width / 4, height / 8, 20, 2 * height / 8 - 20));
        r3.add(new Rectangle(width / 4, 3 * height / 8 + 20, 20, 4 * height / 8 - 20));
        r3.add(new Rectangle(3 * width / 4, height / 8, 20, 4 * height / 8 - 20));
        r3.add(new Rectangle(3 * width / 4, 5 * height / 8 + 20, 20, 2 * height / 8 - 20));
        r3.add(new Rectangle(width / 4 + 20, height / 8, 2 * width / 4 - 20, 20));
        r3.add(new Rectangle(width / 4 + 20, 7 * height / 8 - 20, 2 * width / 4 - 20, 20));
        r3.add(new Rectangle(width / 4 - 70, height / 8, 20, 6 * height / 8));
        r3.add(new Rectangle(3 * width / 4 + 50, height / 8, 20, 6 * height / 8));

        List<Rectangle> r4 = new ArrayList<>();
        r4.add(new Rectangle(width / 4 + 20, height / 8, 2 * width / 4 - 20, 20));
        r4.add(new Rectangle(width / 4 + 20, 7 * height / 8 - 20, 2 * width / 4 - 20, 20));
        r4.add(new Rectangle(3 * width / 4, height / 8, 20, 6 * height / 8));
        r4.add(new Rectangle(width / 4, height / 8, 20, 3 * height / 8 -20));
        r4.add(new Rectangle(width / 4, 4 * height / 8 + 20, 20, 3 * height / 8 -20));

        obstacleSets.add(r1);
        obstacleSets.add(r2);
        obstacleSets.add(r3);
        obstacleSets.add(r4);

        reset();
    }

    public void reset() {
        rrtPoints.clear();
        prmPoints.clear();
        pathPrm.clear();
        pathRrt = null;
        prmGraph.clear();
        Point2D initialPoint = new Point2D(width / 2, height / 2);
        rrtPoints.add(new NodeRrt(initialPoint));
        prmPoints.add(new NodePrm(initialPoint));
        generateRandomGoal();
        prmPoints.add(new NodePrm(goal.getPoint()));
    }

    private void generateRandomGoal() {
        Random r = new Random();
        Point2D goalPoint = new Point2D(r.nextInt(width), r.nextInt(height));
        while (hasCollision(goalPoint)) goalPoint = new Point2D(r.nextInt(width), r.nextInt(height));
        goal = new Goal(goalPoint, goalRadius);
    }

    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public List<NodeRrt> getNodes() {
        return rrtPoints;
    }

    public List<NodePrm> getPoints() {
        return prmPoints;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }

    public List<Point2D> getPathPrm() {
        return pathPrm;
    }

    public NodeRrt getPathRrt() {
        return pathRrt;
    }

    public Graph<NodePrm> getPrmGraph() {
        return prmGraph;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setIncrement(int value) {
    }

    private boolean hasCollision(Point2D point) {
        boolean collision = false;
        for (int j = 0; j < obstacles.size(); j++) {
            Rectangle rect = obstacles.get(j);
            if (rect.contains(point)) {
                collision = true;
                break;
            }
        }
        return collision;
    }

    public void addPRM(int n) {
        for (int i = 0; i < n; i++) {
            Random r = new Random();
            Point2D point = new Point2D(r.nextInt(width), r.nextInt(height));
            while (hasCollision(point)) point = new Point2D(r.nextInt(width), r.nextInt(height));
            prmPoints.add(new NodePrm(point));
        }
        prmGraph = connect();
        pathPrm = findPrmPath(prmPoints.get(0), prmPoints.get(1));
    }

    private List<Point2D> findPrmPath(NodePrm source, NodePrm destination) {
        for (int i = 0; i < prmPoints.size(); i++) prmPoints.get(i).setVisited(false);
        List<Point2D> path = findPrmPathDFSHelper(source, destination);
        return path;
    }

    private List<Point2D> findPrmPathDFSHelper(NodePrm source, NodePrm destination) {
        if (!source.isVisited()) {
            source.setVisited(true);
            if (source.equals(destination)) {
                List<Point2D> ret = new ArrayList<>();
                ret.add(source);
                return ret;
            } else {
                List<NodePrm> neighbors = prmGraph.getNeighbors(source);
                for (int i = 0; i < neighbors.size(); i++) {
                    List<Point2D> ret = findPrmPathDFSHelper(neighbors.get(i), destination);
                    if (null != ret) {
                        ret.add(source);
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    public void addRRT(int n) {
        Random rand = new Random();
        for (int j = 0; j < n; j++) {

            //Sample
            Point2D sample;
            if ((n > 1) && (j == 0) && (null != goal)) {
                sample = goal.getPoint();
            } else {
                sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
            }

            //Nearest node
            double closestDistance = Double.MAX_VALUE;
            NodeRrt closestNodeRrt = null;

            boolean tooClose = false;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(sample);

                if (dist < minDistance) {
                    tooClose = true;
                }

                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestNodeRrt = nodeRrt;
                }
            }

            if (tooClose || closestNodeRrt == null) {
                continue;
            }

            //Steer
            double delX = RRTMultiplier * ((sample.getX() - closestNodeRrt.getX()) / closestDistance);
            double delY = RRTMultiplier * ((sample.getY() - closestNodeRrt.getY()) / closestDistance);

            Point2D newPoint = new Point2D(closestNodeRrt.getX() + delX, closestNodeRrt.getY() + delY);

            //ChooseParent
            boolean collision = false;

            Line2D line = new Line2D(closestNodeRrt, newPoint);
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle r = obstacles.get(i);
                if (line.intersects(r)) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                collision = false;

                newPoint.setLocation(delX / 2 + closestNodeRrt.getX(), delY / 2 + closestNodeRrt.getY());
                line = new Line2D(closestNodeRrt, newPoint);

                for (int i = 0; i < obstacles.size(); i++) {
                    Rectangle r = obstacles.get(i);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    NodeRrt node = new NodeRrt(closestNodeRrt, newPoint);
                    rrtPoints.add(node);
                    node.addToParentChildList();
                    checkGoal(node);
                }
            } else {
                NodeRrt node = new NodeRrt(closestNodeRrt, newPoint);
                rrtPoints.add(node);
                node.addToParentChildList();
                checkGoal(node);
            }
        }
    }

    public void addRRTStar(int n) {
        Random rand = new Random();

        for (int j = 0; j < n; j++) {

            //Sample
            Point2D sample;
            if ((n > 1) && (j == 0) && (null != goal)) {
                sample = goal.getPoint();
            } else {
                sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
            }

            //Nearest node
            double closestDistance = Double.MAX_VALUE;
            boolean tooClose = false;

            NodeRrt closestNodeRrt = null;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(sample);

                if (dist < minDistance) {
                    tooClose = true;
                }

                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestNodeRrt = nodeRrt;
                }

            }

            if (tooClose || closestNodeRrt == null) {
                continue;
            }

            //Steer
            double delX = RRTMultiplier * ((sample.getX() - closestNodeRrt.getX()) / closestDistance);
            double delY = RRTMultiplier * ((sample.getY() - closestNodeRrt.getY()) / closestDistance);

            Point2D newPoint = new Point2D(closestNodeRrt.getX() + delX, closestNodeRrt.getY() + delY);

            //ChooseParent
            List<NodeRrt> closeNodeRrts = new ArrayList<>();
            int maxDist = optimiseDistance;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(newPoint);

                if (dist < maxDist) {
                    nodeRrt.setHelper(dist);
                    closeNodeRrts.add(nodeRrt);
                }
            }

            closestNodeRrt = null;
            double smallestDist = Double.MAX_VALUE;

            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                boolean collision = false;
                Line2D line = new Line2D(nodeRrt, newPoint);
                for (int k = 0; k < obstacles.size(); k++) {
                    Rectangle r = obstacles.get(k);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    if (nodeRrt.getDistance() + nodeRrt.getHelper() < smallestDist) {
                        smallestDist = nodeRrt.getDistance() + nodeRrt.getHelper();
                        closestNodeRrt = nodeRrt;
                    }
                }
            }

            if (closestNodeRrt == null) {
                continue;
            }

            NodeRrt toAdd = new NodeRrt(closestNodeRrt, newPoint);
            rrtPoints.add(toAdd);
            toAdd.addToParentChildList();

            //Check goal
            checkGoal(toAdd);

            //ReWire
            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                if (nodeRrt.getHelper() + toAdd.getDistance() < nodeRrt.getDistance()) {

                    boolean canConnect = true;
                    Line2D line = new Line2D(nodeRrt, newPoint);

                    for (int k = 0; k < obstacles.size(); k++) {
                        Rectangle rect = obstacles.get(k);
                        if (line.intersects(rect)) {
                            canConnect = false;
                            break;
                        }
                    }

                    //Reconnect
                    if (canConnect) {
                        nodeRrt.removeFromParentChildList();
                        nodeRrt.setParent(toAdd);
                        nodeRrt.addToParentChildList();
                        nodeRrt.setDistance(toAdd.getDistance() + nodeRrt.getHelper());
                    }
                }
            }
        }
    }

    public void addRRTStarSmart(int n) {
        Random rand = new Random();

        for (int j = 0; j < n; j++) {

            //Sample
            Point2D sample;
            if ((n > 1) && (j == 0) && (null != goal)) {
                sample = goal.getPoint();
            } else {
                sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
            }

            //Nearest node
            double closestDistance = Double.MAX_VALUE;
            boolean tooClose = false;

            NodeRrt closestNodeRrt = null;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(sample);

                if (dist < minDistance) {
                    tooClose = true;
                }

                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestNodeRrt = nodeRrt;
                }

            }

            if (tooClose || closestNodeRrt == null) {
                continue;
            }

            //Steer
            double delX = RRTMultiplier * ((sample.getX() - closestNodeRrt.getX()) / closestDistance);
            double delY = RRTMultiplier * ((sample.getY() - closestNodeRrt.getY()) / closestDistance);

            Point2D newPoint = new Point2D(closestNodeRrt.getX() + delX, closestNodeRrt.getY() + delY);

            //ChooseParent
            List<NodeRrt> closeNodeRrts = new ArrayList<>();
            int maxDist = optimiseDistance;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(newPoint);

                if (dist < maxDist) {
                    nodeRrt.setHelper(dist);
                    closeNodeRrts.add(nodeRrt);
                }
            }

            closestNodeRrt = null;
            double smallestDist = Double.MAX_VALUE;

            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                boolean collision = false;
                Line2D line = new Line2D(nodeRrt, newPoint);
                for (int k = 0; k < obstacles.size(); k++) {
                    Rectangle r = obstacles.get(k);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    if (nodeRrt.getDistance() + nodeRrt.getHelper() < smallestDist) {
                        smallestDist = nodeRrt.getDistance() + nodeRrt.getHelper();
                        closestNodeRrt = nodeRrt;
                    }
                }
            }

            if (closestNodeRrt == null) {
                continue;
            }

            NodeRrt toAdd = new NodeRrt(closestNodeRrt, newPoint);
            rrtPoints.add(toAdd);
            toAdd.addToParentChildList();

            //Check goal
            checkGoal(toAdd);

            //ReWire
            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                if (nodeRrt.getHelper() + toAdd.getDistance() < nodeRrt.getDistance()) {

                    boolean canConnect = true;
                    Line2D line = new Line2D(nodeRrt, newPoint);

                    for (int k = 0; k < obstacles.size(); k++) {
                        Rectangle rect = obstacles.get(k);
                        if (line.intersects(rect)) {
                            canConnect = false;
                            break;
                        }
                    }

                    //Reconnect
                    if (canConnect) {
                        nodeRrt.removeFromParentChildList();
                        nodeRrt.setParent(toAdd);
                        nodeRrt.addToParentChildList();
                        nodeRrt.setDistance(toAdd.getDistance() + nodeRrt.getHelper());
                    }
                }
            }

            //Optimize the path
            if (null != pathRrt) {
                NodeRrt node1 = pathRrt;
                NodeRrt node2 = null;
                if (null != node1.getParent()) node2 = node1.getParent().getParent();

                while (null != node2) {
                    Line2D line = new Line2D(node1, node2);
                    boolean canConnect = true;
                    for (int k = 0; k < obstacles.size(); k++) {
                        Rectangle rect = obstacles.get(k);
                        if (line.intersects(rect)) {
                            canConnect = false;
                            break;
                        }
                    }
                    if (canConnect) {
                        node1.setParentOptimized(node2);
                        node2 = node2.getParent();
                    } else {
                        node1 = node1.getParent();
                        if (null != node1.getParent()) {
                            node2 = node1.getParent().getParent();
                        } else {
                            node2 = null;
                        }
                    }
                }
            }
        }
    }

    public void addRRTStarFN(int n) {
        Random rand = new Random();

        List<NodeRrt> newNodeRrts = new ArrayList<>();

        for (int j = 0; j < n; j++) {

            //Sample
            Point2D sample;
            if ((n > 1) && (j == 0) && (null != goal)) {
                sample = goal.getPoint();
            } else {
                sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
            }

            //Nearest node
            double closestDistance = Double.MAX_VALUE;
            boolean tooClose = false;

            NodeRrt closestNodeRrt = null;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(sample);

                if (dist < minDistance) {
                    tooClose = true;
                }

                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestNodeRrt = nodeRrt;
                }

            }

            if (tooClose || closestNodeRrt == null) {
                continue;
            }

            //Steer
            double delX = RRTMultiplier * ((sample.getX() - closestNodeRrt.getX()) / closestDistance);
            double delY = RRTMultiplier * ((sample.getY() - closestNodeRrt.getY()) / closestDistance);

            Point2D newPoint = new Point2D(closestNodeRrt.getX() + delX, closestNodeRrt.getY() + delY);

            //ChooseParent
            List<NodeRrt> closeNodeRrts = new ArrayList<>();
            int maxDist = optimiseDistance;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(newPoint);

                if (dist < maxDist) {
                    nodeRrt.setHelper(dist);
                    closeNodeRrts.add(nodeRrt);
                }
            }

            closestNodeRrt = null;
            double smallestDist = Double.MAX_VALUE;

            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                boolean collision = false;
                Line2D line = new Line2D(nodeRrt, newPoint);
                for (int k = 0; k < obstacles.size(); k++) {
                    Rectangle r = obstacles.get(k);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    if (nodeRrt.getDistance() + nodeRrt.getHelper() < smallestDist) {
                        smallestDist = nodeRrt.getDistance() + nodeRrt.getHelper();
                        closestNodeRrt = nodeRrt;
                    }
                }
            }

            if (closestNodeRrt == null) {
                continue;
            }

            NodeRrt toAdd = new NodeRrt(closestNodeRrt, newPoint);
            rrtPoints.add(toAdd);
            toAdd.addToParentChildList();

            //Check goal
            checkGoal(toAdd);

            //ReWire
            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                if (nodeRrt.getHelper() + toAdd.getDistance() < nodeRrt.getDistance()) {

                    boolean canConnect = true;
                    Line2D line = new Line2D(nodeRrt, newPoint);

                    for (int k = 0; k < obstacles.size(); k++) {
                        Rectangle rect = obstacles.get(k);
                        if (line.intersects(rect)) {
                            canConnect = false;
                            break;
                        }
                    }

                    //Reconnect
                    if (canConnect) {
                        nodeRrt.removeFromParentChildList();
                        nodeRrt.setParent(toAdd);
                        nodeRrt.addToParentChildList();
                        nodeRrt.setDistance(toAdd.getDistance() + nodeRrt.getHelper());
                    }
                }
            }
        }

        if (rrtPoints.size() > rRTStarFnMaximumLength) {
            //ForcedRemoval
            List<NodeRrt> childlessNodeRrts = new ArrayList<>();

            int lenToRemove = rrtPoints.size() - rRTStarFnMaximumLength;
            int k = 0;
            while (childlessNodeRrts.size() < lenToRemove) {
                for (int i = 0; i < rrtPoints.size(); i++) {
                    NodeRrt node = rrtPoints.get(i);
                    if (node.getChildList().size() == k) childlessNodeRrts.add(node);
                }
                k++;
            }

            NodeRrt node = pathRrt;
            while (null != node) {
                if (childlessNodeRrts.contains(node)) childlessNodeRrts.remove(node);
                node = node.getParent();
            }
            for (int i = 0; i < newNodeRrts.size(); i++) {
                NodeRrt newNode = newNodeRrts.get(i);
                if (childlessNodeRrts.contains(newNode)) childlessNodeRrts.remove(newNode);
            }

            lenToRemove = Math.min(lenToRemove, childlessNodeRrts.size());

            if (lenToRemove <= childlessNodeRrts.size()) {
                while (lenToRemove > 0) {
                    int toRemove = rand.nextInt(childlessNodeRrts.size());
                    NodeRrt nodeToRemove = childlessNodeRrts.get(toRemove);
                    childlessNodeRrts.remove(nodeToRemove);
                    lenToRemove--;
                    removeNodeRrt(nodeToRemove);
                }
            }
        }
    }

    public void addRRTStarInformed(int n) {
        Random rand = new Random();

        for (int j = 0; j < n; j++) {

            //Sample
            Point2D sample;
            if ((n > 1) && (j == 0) && (null != goal)) {
                sample = goal.getPoint();
            } else {
                if (null!=pathRrt) {
                    Point2D start = rrtPoints.get(0);
                    double c_min = start.distance(goal.getPoint());
                    double c_best = pathRrt.getDistance();
                    if (c_best>c_min) {
                        Point2D center = new Point2D((start.getX() + goal.getPoint().getX()) / 2, (start.getY() + goal.getPoint().getY()) / 2);
                        RealMatrix C = rotationToWorldFrame(start, goal.getPoint());
                        sample = sample(c_best, c_min, center, C);
                    } else {
                        sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
                    }
                } else {
                    sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
                }
            }

            //Nearest node
            double closestDistance = Double.MAX_VALUE;
            boolean tooClose = false;

            NodeRrt closestNodeRrt = null;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(sample);

                if (dist < minDistance) {
                    tooClose = true;
                }

                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestNodeRrt = nodeRrt;
                }

            }

            if (tooClose || closestNodeRrt == null) {
                continue;
            }

            //Steer
            double delX = RRTMultiplier * ((sample.getX() - closestNodeRrt.getX()) / closestDistance);
            double delY = RRTMultiplier * ((sample.getY() - closestNodeRrt.getY()) / closestDistance);

            Point2D newPoint = new Point2D(closestNodeRrt.getX() + delX, closestNodeRrt.getY() + delY);

            //ChooseParent
            List<NodeRrt> closeNodeRrts = new ArrayList<>();
            int maxDist = optimiseDistance;

            for (int i = 0; i < rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(newPoint);

                if (dist < maxDist) {
                    nodeRrt.setHelper(dist);
                    closeNodeRrts.add(nodeRrt);
                }
            }

            closestNodeRrt = null;
            double smallestDist = Double.MAX_VALUE;

            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                boolean collision = false;
                Line2D line = new Line2D(nodeRrt, newPoint);
                for (int k = 0; k < obstacles.size(); k++) {
                    Rectangle r = obstacles.get(k);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    if (nodeRrt.getDistance() + nodeRrt.getHelper() < smallestDist) {
                        smallestDist = nodeRrt.getDistance() + nodeRrt.getHelper();
                        closestNodeRrt = nodeRrt;
                    }
                }
            }

            if (closestNodeRrt == null) {
                continue;
            }

            NodeRrt toAdd = new NodeRrt(closestNodeRrt, newPoint);
            rrtPoints.add(toAdd);
            toAdd.addToParentChildList();

            //Check goal
            checkGoal(toAdd);

            //ReWire
            for (int i = 0; i < closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                if (nodeRrt.getHelper() + toAdd.getDistance() < nodeRrt.getDistance()) {

                    boolean canConnect = true;
                    Line2D line = new Line2D(nodeRrt, newPoint);

                    for (int k = 0; k < obstacles.size(); k++) {
                        Rectangle rect = obstacles.get(k);
                        if (line.intersects(rect)) {
                            canConnect = false;
                            break;
                        }
                    }

                    //Reconnect
                    if (canConnect) {
                        nodeRrt.removeFromParentChildList();
                        nodeRrt.setParent(toAdd);
                        nodeRrt.addToParentChildList();
                        nodeRrt.setDistance(toAdd.getDistance() + nodeRrt.getHelper());
                    }
                }
            }
        }
    }

    private RealMatrix rotationToWorldFrame(Point2D start, Point2D goal) {
        double dist = start.distance(goal);
        RealVector a = MatrixUtils.createRealVector(new double [] {(goal.getX()-start.getX())/dist,(goal.getY()-start.getY())/dist,0});
        RealVector b = MatrixUtils.createRealVector(new double [] {1,0,0});
        RealMatrix M = a.outerProduct(b);
        SingularValueDecomposition svd = new SingularValueDecomposition(M);
        RealMatrix U = svd.getU();
        RealMatrix VT = svd.getVT();

        double dd = (new LUDecomposition(U)).getDeterminant() * (new LUDecomposition(VT.transpose())).getDeterminant();
        RealMatrix D = MatrixUtils.createRealDiagonalMatrix(new double [] {1.0, 1.0, dd});
        RealMatrix C = U.multiply(D).multiply(VT);
        return C;
    }

    private Point2D sample(double c_max, double c_min, Point2D x_center, RealMatrix C) {
        Point2D x_rand;
        double cc = Math.sqrt(c_max*c_max - c_min*c_min);
        double [] r = {c_max/2,  cc/2, cc/2};
        RealMatrix L = MatrixUtils.createRealMatrix(new double [][] {{r[0],0,0},{0,r[1],0},{0,0,r[2]}});

        while (true) {
            RealVector x_ball = MatrixUtils.createRealVector(sampleUnitBall());
            RealVector m_ = C.multiply(L).operate(x_ball);
            x_rand =  new Point2D(m_.getEntry(0) + x_center.getX(),m_.getEntry(1) + x_center.getY());
            if ((x_rand.getX()>=0)&&(x_rand.getX()<width)&&(x_rand.getY()>=0)&&(x_rand.getY()<height)) {
                break;
            }
        }
        return x_rand;
    }

    private double [] sampleUnitBall() {
        Random rand = new Random();
        while (true) {
            double x = 2*rand.nextDouble()-1;
            double y = 2*rand.nextDouble()-1;
            if (x*x + y*y < 1) return new double [] {x,y,0};
        }
    }

    public void addRRTStarSmartFNInformed(int n){
        Random rand = new Random();

        List<NodeRrt> newNodeRrts = new ArrayList<>();

        for(int j = 0; j < n; j++) {

            //Sample
            Point2D sample;
            if ((n > 1) && (j == 0) && (null != goal)) {
                sample = goal.getPoint();
            } else {
                if (null!=pathRrt) {
                    Point2D start = rrtPoints.get(0);
                    double c_min = start.distance(goal.getPoint());
                    double c_best = pathRrt.getDistance();
                    if (c_best>c_min) {
                        Point2D center = new Point2D((start.getX() + goal.getPoint().getX()) / 2, (start.getY() + goal.getPoint().getY()) / 2);
                        RealMatrix C = rotationToWorldFrame(start, goal.getPoint());
                        sample = sample(c_best, c_min, center, C);
                    } else {
                        sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
                    }
                } else {
                    sample = new Point2D(rand.nextInt(width), rand.nextInt(height));
                }
            }

            //Nearest node
            double closestDistance = Double.MAX_VALUE;
            boolean tooClose = false;

            NodeRrt closestNodeRrt = null;

            for (int i = 0; i< rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(sample);

                if (dist < minDistance) {
                    tooClose = true;
                }

                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestNodeRrt = nodeRrt;
                }

            }

            if (tooClose || closestNodeRrt == null) {
                continue;
            }

            //Steer
            double delX = RRTMultiplier * ((sample.getX() - closestNodeRrt.getX()) / closestDistance);
            double delY = RRTMultiplier * ((sample.getY() - closestNodeRrt.getY()) / closestDistance);

            Point2D newPoint = new Point2D(closestNodeRrt.getX() + delX,closestNodeRrt.getY() + delY);

            //ChooseParent
            List<NodeRrt> closeNodeRrts = new ArrayList<>();
            int maxDist = optimiseDistance;

            for (int i = 0; i< rrtPoints.size(); i++) {
                NodeRrt nodeRrt = rrtPoints.get(i);
                double dist = nodeRrt.distance(newPoint);

                if (dist < maxDist) {
                    nodeRrt.setHelper(dist);
                    closeNodeRrts.add(nodeRrt);
                }
            }

            closestNodeRrt = null;
            double smallestDist = Double.MAX_VALUE;

            for (int i = 0; i< closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                boolean collision = false;
                Line2D line = new Line2D(nodeRrt, newPoint);
                for(int k=0; k<obstacles.size(); k++) {
                    Rectangle r = obstacles.get(k);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    if (nodeRrt.getDistance() + nodeRrt.getHelper() < smallestDist) {
                        smallestDist = nodeRrt.getDistance() + nodeRrt.getHelper();
                        closestNodeRrt = nodeRrt;
                    }
                }
            }

            if (closestNodeRrt == null) {
                continue;
            }

            NodeRrt toAdd = new NodeRrt(closestNodeRrt, newPoint);
            rrtPoints.add(toAdd);
            toAdd.addToParentChildList();

            //Check goal
            checkGoal(toAdd);

            //ReWire
            for (int i = 0; i< closeNodeRrts.size(); i++) {
                NodeRrt nodeRrt = closeNodeRrts.get(i);

                if (nodeRrt.getHelper() + toAdd.getDistance() < nodeRrt.getDistance()) {

                    boolean canConnect = true;
                    Line2D line = new Line2D(nodeRrt, newPoint);

                    for(int k=0; k<obstacles.size(); k++) {
                        Rectangle rect = obstacles.get(k);
                        if (line.intersects(rect)) {
                            canConnect = false;
                            break;
                        }
                    }

                    //Reconnect
                    if (canConnect) {
                        nodeRrt.removeFromParentChildList();
                        nodeRrt.setParent(toAdd);
                        nodeRrt.addToParentChildList();
                        nodeRrt.setDistance(toAdd.getDistance() + nodeRrt.getHelper());
                    }
                }
            }
        }

        if (rrtPoints.size() > rRTStarFnMaximumLength) {
            //ForcedRemoval
            List<NodeRrt> childlessNodeRrts = new ArrayList<>();

            int lenToRemove = rrtPoints.size()-rRTStarFnMaximumLength;
            int k = 0;
            while (childlessNodeRrts.size() < lenToRemove) {
                for (int i = 0; i < rrtPoints.size(); i++) {
                    NodeRrt node = rrtPoints.get(i);
                    if (node.getChildList().size() == k) childlessNodeRrts.add(node);
                }
                k++;
            }

            NodeRrt node = pathRrt;
            while (null!=node) {
                if (childlessNodeRrts.contains(node)) childlessNodeRrts.remove(node);
                node = node.getParent();
            }
            for (int i=0; i<newNodeRrts.size(); i++) {
                NodeRrt newNode = newNodeRrts.get(i);
                if (childlessNodeRrts.contains(newNode)) childlessNodeRrts.remove(newNode);
            }

            lenToRemove = Math.min(lenToRemove,childlessNodeRrts.size());

            if (lenToRemove <= childlessNodeRrts.size()) {
                while (lenToRemove>0) {
                    int toRemove = rand.nextInt(childlessNodeRrts.size());
                    NodeRrt nodeToRemove = childlessNodeRrts.get(toRemove);
                    childlessNodeRrts.remove(nodeToRemove);
                    lenToRemove--;
                    removeNodeRrt(nodeToRemove);
                }
            }
        }

        //Optimize the path
        if (null!=pathRrt) {
            NodeRrt node1 = pathRrt;
            NodeRrt node2 = null;
            if (null!=node1.getParent()) node2 = node1.getParent().getParent();

            while (null!=node2) {
                Line2D line = new Line2D(node1, node2);
                boolean canConnect = true;
                for(int k=0; k<obstacles.size(); k++) {
                    Rectangle rect = obstacles.get(k);
                    if (line.intersects(rect)) {
                        canConnect = false;
                        break;
                    }
                }
                if (canConnect) {
                    node1.setParentOptimized(node2);
                    node2 = node2.getParent();
                } else {
                    node1 = node1.getParent();
                    if (null!=node1.getParent()) {
                        node2 = node1.getParent().getParent();
                    } else {
                        node2 = null;
                    }
                }
            }
        }
    }

    private void checkGoal(NodeRrt node) {
        if (goal.contains(node)) {
            pathRrt = node;
        }
    }

    private void removeNodeRrt(NodeRrt node) {
        rrtPoints.remove(node);
        node.removeFromParentChildList();

        List<NodeRrt> childList = node.getChildList();
        for (int i=childList.size()-1; i>=0; i--) {
            NodeRrt child = childList.get(i);

            List<NodeRrt> closeNodeList = new ArrayList<>();
            for (int j = 0; j< rrtPoints.size(); j++) {
                NodeRrt nodeRrt = rrtPoints.get(j);
                double dist = nodeRrt.distance(child);
                if (dist < optimiseDistance) {
                    nodeRrt.setHelper(dist);
                    closeNodeList.add(nodeRrt);
                }
            }

            NodeRrt closestNodeRrt = null;
            double smallestDist = Double.MAX_VALUE;
            for (int j = 0; j < closeNodeList.size(); j++) {
                NodeRrt nodeRrt = closeNodeList.get(i);

                boolean collision = false;
                Line2D line = new Line2D(nodeRrt, child);
                for (int k = 0; k < obstacles.size(); k++) {
                    Rectangle r = obstacles.get(k);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    if (nodeRrt.getDistance() + nodeRrt.getHelper() < smallestDist) {
                        smallestDist = nodeRrt.getDistance() + nodeRrt.getHelper();
                        closestNodeRrt = nodeRrt;
                    }
                }
            }

            if (null!=closestNodeRrt) {
                child.setParent(closestNodeRrt);
                closestNodeRrt.addChild(child);
            } else {
                removeNodeRrt(child);
            }
        }
    }

    public void setObstacles(int n){
        obstacles = obstacleSets.get(n);
        reset();
    }

    public void setNoObstacles() {
        obstacles = new ArrayList<>();
        reset();
    }

    public int getRRTMultiplier() {
        return RRTMultiplier;
    }

    public void setRRTMultiplier(int m){
        RRTMultiplier = m;
        minDistance = 8*m/10;
    }

    public void setGoalRadius(double radius) {
        goalRadius = radius;
        goal.setRadius(radius);
    }

    private Graph<NodePrm> connect(){

        Graph<NodePrm> graph = new Graph<>();

        for(int j = 0; j < prmPoints.size(); j++){
            NodePrm point = prmPoints.get(j);

            List<NodePrm> closest = new ArrayList<>();
            List<Double> closestDist = new ArrayList<>();

            for(int i = 0; i < numOfConnections; i++) {
                closest.add(null);
                closestDist.add(Double.MAX_VALUE);
            }


            for(int k = 0; k < prmPoints.size(); k++){
                NodePrm p = prmPoints.get(k);
                if(p != point){

                    boolean intersects = false;
                    Line2D line = new Line2D(p.getX(), p.getY(), point.getX(), point.getY());
                    for(int m=0; m<obstacles.size(); m++) {
                        Rectangle r = obstacles.get(m);
                        if(line.intersects(r)){
                            intersects = true;
                        }
                    }

                    if(intersects){
                        continue;
                    }

                    double dist = Math.sqrt((p.getX()-point.getX())*(p.getX()-point.getX()) + (p.getY()-point.getY())*(p.getY()-point.getY()));

                    for(int i = 0; i < numOfConnections; i++){

                        if(closestDist.get(i) > dist){
                            closestDist.add(i, dist);
                            closest.add(i, p);
                            break;
                        }
                    }
                }
            }

            List<NodePrm> edges = new ArrayList<>();
            for(int i = 0; i < numOfConnections; i++) {
                NodePrm close = closest.get(i);
                if(close != null){
                    edges.add(close);
                    graph.addEdge(close,point,true);
                }
            }
        }
        return graph;
    }

    public void removeRoot() {
        removeNodeRrt(rrtPoints.get(0));
    }
}
