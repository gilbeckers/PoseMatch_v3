package org.tensorflow.demo;

/**
 * Created by Gil on 26/11/2017.
 */

public class Keypoint {

    private double x;
    private double y;

    public Keypoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
