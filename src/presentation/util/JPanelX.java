package presentation.util;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

public class JPanelX extends JPanel {

    public JPanelX() {
    }

    private Vector<JPaintListener> paintListenerVector = new Vector<>();

    public void addPaintListener(JPaintListener paintListener) {
        paintListenerVector.add(paintListener);
    }

    public void removePaintListener(JPaintListener paintListener) {
        paintListenerVector.remove(paintListener);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if (null!=paintListenerVector) {
            Iterator<JPaintListener> it = paintListenerVector.iterator();
            while (it.hasNext()) it.next().paintComponent(g);
        }
    }
}
