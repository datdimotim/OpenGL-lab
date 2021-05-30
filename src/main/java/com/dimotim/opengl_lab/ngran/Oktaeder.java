package com.dimotim.opengl_lab.ngran;

public class Oktaeder extends TrianglesMesh {

    private static final double[][] kubCenters = {
            {1, 0, 0},     // 0
            {-1, 0, 0},    // 1
            {0, 0, 1},     // 2
            {0, 0, -1},    // 3
            {0, -1, 0},    // 4
            {0, 1, 0}      // 5
    };

    protected double[][][] getTriangles() {
        double[] u = kubCenters[5];
        double[] d = kubCenters[4];
        double[] f = kubCenters[3];
        double[] b = kubCenters[2];
        double[] l = kubCenters[1];
        double[] r = kubCenters[0];
        return new double[][][]{
                {u, l, f},
                {u, b, l},
                {u, r, b},
                {u, f, r},
                {d, f, l},
                {d, l, b},
                {d, b, r},
                {d, r, f}
        };
    }
}
