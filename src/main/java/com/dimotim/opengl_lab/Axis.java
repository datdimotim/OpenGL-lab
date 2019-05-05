package com.dimotim.opengl_lab;

import static com.jogamp.opengl.GL.GL_LINES;

public class Axis extends GLObject{
    @Override
    protected float[] getVertices() {
        return new float[]{
                0,0,0,
                1,0,0,
                0,0,0,
                0,1,0,
                0,0,0,
                0,0,1
        };
    }

    @Override
    protected float[] getColorArray() {
        return new float[]{
                1,0,0,
                1,0,0,
                0,1,0,
                0,1,0,
                0,0,1,
                0,0,1
        };
    }

    @Override
    protected float[] getTextureArray() {
        return getColorArray();
    }

    @Override
    protected float[] getNormalArray() {
        return getColorArray();
    }

    @Override
    protected String getTexturePath() {
        return null;
    }

    @Override
    protected int getPrimitiveType() {
        return GL_LINES;
    }

    @Override
    public float getImgNormalRatio() {
        return 0;
    }
}
