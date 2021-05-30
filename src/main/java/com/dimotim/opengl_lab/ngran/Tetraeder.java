package com.dimotim.opengl_lab.ngran;

public class Tetraeder extends TrianglesMesh {

    private static final double[][] kubVertexes = {
            {1, -1, -1},  // 0
            {1, -1, 1},   // 1
            {1, 1, -1},   // 2
            {1, 1, 1},    // 3
            {-1, -1, -1},   // 4
            {-1, -1, 1},    // 5
            {-1, 1, -1},    // 6
            {-1, 1, 1},     // 7
    };

    protected double[][][] getTriangles() {
        double[] a = kubVertexes[7];
        double[] d = kubVertexes[2];
        double[] c1 = kubVertexes[1];
        double[] b1 = kubVertexes[4];
        return new double[][][]{
                {a, d, c1},
                {a, b1, d},
                {a, c1, b1},
                {b1, c1, d}
        };
    }
}
