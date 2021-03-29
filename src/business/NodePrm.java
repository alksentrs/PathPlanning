package business;

import util.Point2D;

public class NodePrm extends Point2D {

    private boolean visited;

    public NodePrm(Point2D p) {
        super(p.getX(), p.getY());
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
