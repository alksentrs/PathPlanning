package presentation;

import business.MotionSpace;
import presentation.util.JPanelX;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewMain {

    private JFrame jFrame;
    private JPanel jPanelMain;

    private JButton buttonAdd1Point;
    private JButton buttonAdd50Points;
    private JButton buttonAdd500Points;
    private JButton buttonClear;
    private JComboBox comboBoxSearchStrategy;
    private JComboBox comboBoxObstacleSet;
    private JSlider sliderIncrement;
    private JPanelX jPanelDraw;
    private JLabel textTotalNodes;

    private Strategy strategy;
    private MotionSpace space;

    private ViewHelper viewHelper;

    private void createUIComponents() {
        jPanelDraw = new JPanelX();
    }

    private enum Strategy {
        PRM,
        RRT,
        RRTstar,
        RRTstarSmart,
        RRTstarFN,
        RRTstarFNSmart;
    }

    public void open(MotionSpace space){
        this.space = space;

        jFrame = new JFrame("Path Planning");
        jFrame.setPreferredSize(new Dimension(1280, 880));
        jFrame.add(jPanelMain);

        buttonAdd1Point.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                add(1);
                update();
            }
        });

        buttonAdd50Points.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                add(50);
                update();
            }
        });

        buttonAdd500Points.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                add(500);
                update();
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                space.reset();
                update();
            }
        });

        comboBoxSearchStrategy.addItem("Probabilistic Road Map");
        comboBoxSearchStrategy.addItem("Rapidly Expanding Random Tree");
        comboBoxSearchStrategy.addItem("Rapidly Expanding Random Tree Star");
        comboBoxSearchStrategy.addItem("Rapidly Expanding Random Tree Star Smart");
        comboBoxSearchStrategy.addItem("Rapidly Expanding Random Tree Star FN");
        comboBoxSearchStrategy.addItem("Rapidly Expanding Random Tree Star FN Smart");

        comboBoxSearchStrategy.setSelectedIndex(5);
        strategy = Strategy.RRTstarFNSmart;

        comboBoxSearchStrategy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox comboBox = (JComboBox) actionEvent.getSource();
                String selected = (String) comboBox.getSelectedItem();

                if(selected.equals("Probabilistic Road Map")) strategy = Strategy.PRM;
                else if(selected.equals("Rapidly Expanding Random Tree")) strategy = Strategy.RRT;
                else if(selected.equals("Rapidly Expanding Random Tree Star")) strategy = Strategy.RRTstar;
                else if(selected.equals("Rapidly Expanding Random Tree Star Smart")) strategy = Strategy.RRTstarSmart;
                else if(selected.equals("Rapidly Expanding Random Tree Star FN")) strategy = Strategy.RRTstarFN;
                else if(selected.equals("Rapidly Expanding Random Tree Star FN Smart")) strategy = Strategy.RRTstarFNSmart;

                space.reset();
                update();
            }
        });

        comboBoxObstacleSet.addItem("No Obstacles");
        comboBoxObstacleSet.addItem("Set 1");
        comboBoxObstacleSet.addItem("Set 2");
        comboBoxObstacleSet.addItem("Set 3");
        comboBoxObstacleSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox comboBox = (JComboBox) actionEvent.getSource();
                String selected = (String) comboBox.getSelectedItem();

                if(selected.equals("Set 1")) space.setObstacles(0);
                else if(selected.equals("Set 2")) space.setObstacles(1);
                else if(selected.equals("Set 3")) space.setObstacles(2);
                else if(selected.equals("No Obstacles")) space.setNoObstacles();

                update();
            }
        });


        sliderIncrement.setMinimum(0);
        sliderIncrement.setMaximum(40);
        sliderIncrement.setValue(space.getRRTMultiplier());
        space.setGoalRadius(sliderIncrement.getValue());

        sliderIncrement.setMinorTickSpacing(5);
        sliderIncrement.setMajorTickSpacing(10);
        sliderIncrement.setPaintTicks(true);
        sliderIncrement.setPaintLabels(true);

        sliderIncrement.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JSlider slider = (JSlider) changeEvent.getSource();
                space.setRRTMultiplier(slider.getValue());
                space.setGoalRadius(slider.getValue());
                update();
            }
        });

        viewHelper = new ViewHelper();
        jPanelDraw.addPaintListener(viewHelper);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

        update();

    }

    public void updateScreen() {
        jPanelDraw.repaint();
    }

    public void update() {
        boolean connect = false;
        if(strategy == Strategy.PRM) connect = true;
        viewHelper.setSpace(space,connect);
        if(strategy == Strategy.PRM) {
            textTotalNodes.setText(String.valueOf(space.getPoints().size()));
        } else {
            textTotalNodes.setText(String.valueOf(space.getNodes().size()));
        }
        updateScreen();
    }

    private void add(int n) {
        if(strategy == Strategy.PRM) space.addPRM(n);
        else if(strategy == Strategy.RRT) space.addRRT(n);
        else if(strategy == Strategy.RRTstar) space.addRRTStar(n);
        else if(strategy == Strategy.RRTstarFN) space.addRRTStarFN(n);
        else if(strategy == Strategy.RRTstarSmart) space.addRRTStarSmart(n);
        else if(strategy == Strategy.RRTstarFNSmart) space.addRRTStarFNSmart(n);
    }
}
