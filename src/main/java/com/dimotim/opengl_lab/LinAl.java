package com.dimotim.opengl_lab;
import java.util.Arrays;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static java.lang.Math.*;


public class LinAl {

    public static float[] identity(){
        return translate(0,0,0);
    }

    public static float[] translate(double dx, double dy, double dz) {
        return new float[]{
                1f, 0, 0, 0,
                0, 1f, 0, 0,
                0, 0, 1f, 0,
                (float) dx, (float) dy, (float) dz, 1f
        };
    }

    public static float[] scale(float r) {
        return new float[]{
                r, 0, 0, 0,
                0, r, 0, 0,
                0, 0, r, 0,
                0, 0, 0, 1f
        };
    }

    public static float[] rotate(double angle, double x, double y, double z){
        double[] n=normalize(new double[]{x,y,z});
        x=n[0];
        y=n[1];
        z=n[2];
        double[] m= new double[]{
                cos(angle)+(1-cos(angle))*x*x, (1-cos(angle))*x*y - sin(angle)*z, (1-cos(angle))*x*z+sin(angle)*y,0,
                (1-cos(angle))*y*x+sin(angle)*z, cos(angle)+(1-cos(angle))*y*y, (1-cos(angle))*y*z-sin(angle)*x,0,
                (1-cos(angle))*z*x-sin(angle)*y, (1-cos(angle))*z*y+sin(angle)*x, cos(angle)+(1-cos(angle))*z*z,0,
                0,0,0,1
        };

        float[] mf=new float[16];
        for (int i = 0; i < 16; i++) {
            mf[i]= (float) m[i];
        }
        return mf;
    }

    public static double[] normalize(double[] v){
        final double norm= Math.pow(DoubleStream.of(v).map(d->d*d).sum(),0.5);
        return DoubleStream.of(v).map(d->d/norm).toArray();
    }

    public static double[] vecMul(double[] o,double[] a, double[] b){
        double[] oa={a[0]-o[0],a[1]-o[1],a[2]-o[2]};
        double[] ob={b[0]-o[0],b[1]-o[1],b[2]-o[2]};

        return new double[]{oa[1]*ob[2]-oa[2]*ob[1], -(oa[0]*ob[2]-oa[2]*ob[0]), oa[0]*ob[1]-oa[1]*ob[0]};
    }
    public static double[] vecMulNormal(double [] x1, double x2[]){
        return vecMul(new double[]{0,0,0},x1,x2);
    }
    public static float [] transpose(float [] m){
       float [] m_copy = new float[16];
       float [] m1 = new float[16];
        for (int i = 0; i < m.length; i++) {
            m_copy[i]=m[i];
            m1[i]=m[i];
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m1[4*i+j] = m_copy[4*j+i];
            }
        }
        return m1;
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
