package com.dimotim.opengl_lab.ngran;

public class Hexaeder extends TrianglesMesh {

    private static final double[] a = {1,1,-1};
    private static final double[] b = {1,1,1};
    private static final double[] c = {-1,1,1};
    private static final double[] d = {-1,1,-1};
    private static final double[] a1 = {1,-1,-1};
    private static final double[] b1 = {1,-1,1};
    private static final double[] c1 = {-1,-1,1};
    private static final double[] d1 = {-1,-1,-1};

    protected double[][][] getTriangles() {

        return new double[][][]{
                {a1, c1, b1}, {a1, d1, c1},
                {a, b, c}, {a, c, d},
                {b, b1, c1}, {b, c1, c},
                {a, d1, a1}, {a, d, d1},
                {b,a,a1}, {b,a1,b1},
                {d,c,d1}, {c,c1,d1}
        };
    }
}
