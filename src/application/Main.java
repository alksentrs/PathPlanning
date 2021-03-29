package application;

import business.MotionSpace;
import presentation.ViewMain;

public class Main {

    public static void main(String[] args) {
        MotionSpace space = new MotionSpace(800,800);
        ViewMain viewMain = new ViewMain();
        viewMain.open(space);
    }
}
