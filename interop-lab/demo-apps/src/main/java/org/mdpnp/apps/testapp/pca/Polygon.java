package org.mdpnp.apps.testapp.pca;

import java.util.Arrays;

import javafx.scene.canvas.GraphicsContext;

public class Polygon {
    private double[] x_points, y_points;
    private int count;
    
    Polygon(int N) {
        x_points = new double[N];
        y_points = new double[N];
    }
    void addPoint(double x, double y) {
        if(count >= x_points.length) {
            x_points = Arrays.copyOf(x_points, 2 * x_points.length + 1);
            y_points = Arrays.copyOf(y_points, 2 * y_points.length + 1);
        }
        x_points[count] = x;
        y_points[count] = y;
        count++;
    }
    void fill(GraphicsContext g) {
        g.fillPolygon(x_points, y_points, count);
    }
    void stroke(GraphicsContext g) {
        g.strokePolygon(x_points, y_points, count);
    }
    void clear() {
        count = 0;
    }
    public int getCount() {
        return count;
    }
    public double[] getXPoints() {
        return x_points;
    }
    public double[] getYPoints() {
        return y_points;
    }

}