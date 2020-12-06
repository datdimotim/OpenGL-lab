package com.dimotim.opengl_lab;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static java.lang.Math.*;

public class Zont extends GLObject {
    private static final String texturePath = "blue.png";

    private final int n;
    private final int m;
    private final double h;

    public Zont(int n, int m) {
        this.n = n;
        this.m = m;
        h = sqrt(pow(sin(PI / n), 2) - pow(cos(PI / n) / tan(PI / m), 2));
    }

    private double[][][] getTriangles() {
        return IntStream.range(0, m)
                .mapToObj(i -> new double[][]{
                        {0, 0, -h},
                        {cos(2 * PI / m * i), sin(2 * PI / m * i), 0},
                        {cos(2 * PI / m * (i + 1)), sin(2 * PI / m * (i + 1)), 0}
                })
                .toArray(double[][][]::new);
    }

    @Override
    protected float[] getVertices() {
        return toFloatArray(Arrays.stream(getTriangles())
                .flatMapToDouble(tr->Arrays.stream(tr).flatMapToDouble(Arrays::stream))
                .toArray());
    }

    private static float[] toFloatArray(double[] d){
        float[] res = new float[d.length];
        for(int i = 0; i<res.length;i++){
            res[i] = (float) d[i];
        }
        return res;
    }

    @Override
    protected float[] getTextureArray() {
        return toFloatArray(Arrays.stream(getTriangles())
                .flatMapToDouble(tr -> DoubleStream.of(
                        0, 0,
                        0, 1,
                        1, 1
                ))
                .toArray());
    }

    @Override
    protected float[] getNormalArray() {
        return toFloatArray(
                Arrays.stream(getTriangles())
                    .flatMapToDouble(tr -> {
                        double[] n = LinAl.normalize(LinAl.vecMul(tr[1],tr[0],tr[2]));
                        return DoubleStream.of(
                                n[0],n[1],n[2],
                                n[0],n[1],n[2],
                                n[0],n[1],n[2]
                        );
                    }).toArray()
        );
    }

    @Override
    protected String getTexturePath() {
        return texturePath;
    }

    @Override
    protected int getPrimitiveType() {
        return GL_TRIANGLES;
    }

    @Override
    public float getImgNormalRatio() {
        return 0;
    }

    @Override
    protected float[] getColorArray() {
        return new float[0];
    }
}
