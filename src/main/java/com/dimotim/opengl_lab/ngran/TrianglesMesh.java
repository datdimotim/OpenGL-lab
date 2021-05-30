package com.dimotim.opengl_lab.ngran;

import com.dimotim.opengl_lab.GLObject;
import com.dimotim.opengl_lab.LinAl;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import static com.jogamp.opengl.GL.GL_TRIANGLES;

public abstract class TrianglesMesh extends GLObject {
    private static final String texturePath = "blue.png";

    protected abstract double[][][] getTriangles();

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
