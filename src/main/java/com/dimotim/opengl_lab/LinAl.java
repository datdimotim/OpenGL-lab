package com.dimotim.opengl_lab;

import java.util.Arrays;

public class LinAl {
    public static double[] vecMul(double[] o,double[] a, double[] b){
        double[] oa={a[0]-o[0],a[1]-o[1],a[2]-o[2]};
        double[] ob={b[0]-o[0],b[1]-o[1],b[2]-o[2]};

        return new double[]{oa[1]*ob[2]-oa[2]*ob[1], -(oa[0]*ob[2]-oa[2]*ob[0]), oa[0]*ob[1]-oa[1]*ob[0]};
    }
    public static float[] matrixMul(float [] m1, float[] m2){
        float c [] = new float[16];
        for (int i = 0; i<4; i++) {
            for (int j = 0; j<4; j++) {
                c[4*i+j] = 0;
                for (int k = 0; k<4; k++)
                    c[4*i+j] += m1[4*i+k] * m2[4*k+j];
            }
        }
        return c;
    }
}
