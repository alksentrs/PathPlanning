package presentation;

import business.Goal;
import business.MotionSpace;
import business.NodePrm;
import business.NodeRrt;
import presentation.util.JPaintListener;
import util.Graph;
import util.Point2D;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewHelper implements JPaintListener {

    private int width, height;
    private List<Rectangle> obstacles;
    private List<NodeRrt> nodeRrts;
    private List<NodePrm> nodePrms;
    private Graph<NodePrm> graph;
    private boolean connect;
    private Goal goal;
    private List<Point2D> pathPrm;
    private NodeRrt pathRrt;

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.WHITE);
        g.fillRect(0,0,width,height);

        int pointWidth = 2;

        if (null!=obstacles) {
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle r = obstacles.get(i);

                g.setColor(Color.GRAY);
                g.fillRect((int)(r.getMinX()), (int)(r.getMinY()), (int)(r.getWidth()), (int)(r.getHeight()));

                g.setColor(Color.BLACK);
                g.drawRect((int)r.getMinX(), (int)r.getMinY(), (int)r.getWidth(), (int)r.getHeight());
            }
        }

        if (null!=goal) {
            double x = goal.getPoint().getX();
            double y = goal.getPoint().getY();
            g.setColor(Color.YELLOW);
            g.fillOval((int)(x-goal.getRadius()), (int)(y-goal.getRadius()), (int)(2*goal.getRadius()), (int)(2*goal.getRadius()));
            g.setColor(Color.GREEN);
            g.fillOval((int)(x-pointWidth), (int)(y-pointWidth), 2*pointWidth, 2*pointWidth);
        }

        if (null!= nodeRrts) {
            if(nodeRrts.size()>0) {
                NodeRrt n = nodeRrts.get(0);
                g.setColor(Color.CYAN);
                g.fillOval((int) (n.getX() - 3 * pointWidth), (int) (n.getY() - 3 * pointWidth), 6 * pointWidth, 6 * pointWidth);
                for (int i = 0; i < nodeRrts.size(); i++) {
                    n = nodeRrts.get(i);
                    g.setColor(Color.BLUE);
                    g.fillOval((int) (n.getX() - pointWidth), (int) (n.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                    if (n.getParent() != null) {
                        g.setColor(Color.BLACK);
                        g.drawLine((int) n.getX(), (int) n.getY(), (int) n.getParent().getX(), (int) n.getParent().getY());
                    }
                }
            }
        }

        if (null!= nodePrms) {
            if (null!=graph) {
                g.setColor(Color.BLACK);
                Map<NodePrm,List<NodePrm>> edges = graph.getEdgeList();
                Set<NodePrm> vertexList = edges.keySet();
                Iterator<NodePrm> it = vertexList.iterator();
                while (it.hasNext()) {
                    NodePrm edgeSource = it.next();
                    List<NodePrm> edgeDestinationList = edges.get(edgeSource);
                    for (int j=0; j<edgeDestinationList.size(); j++) {
                        Point2D edgeDestination = edgeDestinationList.get(j);
                        g.drawLine((int)edgeSource.getX(), (int)edgeSource.getY(), (int)edgeDestination.getX(), (int)edgeDestination.getY());
                    }
                }
            }
            g.setColor(Color.BLUE);
            for(int i = 0; i < nodePrms.size(); i++){
                double x = nodePrms.get(i).getX();
                double y = nodePrms.get(i).getY();
                g.fillOval((int)(x-pointWidth), (int)(y-pointWidth), 2*pointWidth, 2*pointWidth);
            }
        }

        if (null!=pathPrm) {
            for (int i = 0; i < pathPrm.size() - 1; i++) {
                Point2D p1 = pathPrm.get(i);
                Point2D p2 = pathPrm.get(i + 1);
                g.setColor(Color.BLUE);
                g.fillOval((int) (p1.getX() - pointWidth), (int) (p1.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                g.fillOval((int) (p2.getX() - pointWidth), (int) (p2.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                g.setColor(Color.RED);
                Stroke strokeAux = g2d.getStroke();
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                g2d.setStroke(strokeAux);
            }
        }

        if (null!=pathRrt) {
            NodeRrt node = pathRrt;
            while (null!=node.getParent()) {
                Point2D p1 = node;
                Point2D p2 = node.getParent();
                g.setColor(Color.BLUE);
                g.fillOval((int) (p1.getX() - pointWidth), (int) (p1.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                g.fillOval((int) (p2.getX() - pointWidth), (int) (p2.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                g.setColor(Color.RED);
                Stroke strokeAux = g2d.getStroke();
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                g2d.setStroke(strokeAux);
                node = node.getParent();
            }
        }
    }

    public void setSpace(MotionSpace space, boolean connect) {
        obstacles = space.getObstacles();
        width = space.getWidth();
        height = space.getHeight();
        nodeRrts = space.getNodes();
        nodePrms = space.getPoints();
        goal = space.getGoal();
        pathPrm = space.getPathPrm();
        pathRrt = space.getPathRrt();
        graph = space.getPrmGraph();
        this.connect = connect;
    }
}
