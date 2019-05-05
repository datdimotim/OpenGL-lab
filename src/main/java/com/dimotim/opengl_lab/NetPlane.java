package com.dimotim.opengl_lab;

import static com.jogamp.opengl.GL.GL_LINES;

public class NetPlane extends GLObject{

    @Override
    protected float[] getVertices() {
        return new float[]{
                -0.8f,0,-0.8f,
                -0.8f,0, 0.8f,
                -0.4f,0,-0.8f,
                -0.4f,0, 0.8f,
                -0.0f,0,-0.8f,
                 0.0f,0, 0.8f,
                 0.8f,0,-0.8f,
                 0.8f,0, 0.8f,
                 0.4f,0,-0.8f,
                 0.4f,0, 0.8f,

                -0.8f,0,-0.8f,
                 0.8f,0,-0.8f,
                -0.8f,0,-0.4f,
                 0.8f,0,-0.4f,
                -0.8f,0,-0.0f,
                 0.8f,0, 0.0f,
                -0.8f,0, 0.4f,
                 0.8f,0, 0.4f,
                -0.8f,0, 0.8f,
                 0.8f,0, 0.8f
        };
    }

    @Override
    protected float[] getColorArray() {
        return new float[]{
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f,
                0.5f,0.5f,0.5f
        };
    }

    @Override
    protected float[] getTextureArray() {
        return new float[0];
    }

    @Override
    protected float[] getNormalArray() {
        return new float[0];
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
