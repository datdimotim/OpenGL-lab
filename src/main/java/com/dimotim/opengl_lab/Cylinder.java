package com.dimotim.opengl_lab;


import static com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;

public class Cylinder extends GLObject {
    private static final String texturePath = "bump24.png";
    private static final double R=0.07;
    private static final double L=0.5;
    private static final int count=30;

    private static float[][] genQuad(int rNum){
        return new float[][]{
                genPoint(rNum,0),
                genPoint(rNum,1),
                genPoint(rNum+1,1),
                genPoint(rNum+1,1),
                genPoint(rNum,0),
                genPoint(rNum+1,0)
        };
    }

    private static float[][] genTextureQuad(int rNum){
        return new float[][]{
                genPointTexture(rNum,0),
                genPointTexture(rNum,1),
                genPointTexture(rNum+1,1),
                genPointTexture(rNum+1,1),
                genPointTexture(rNum,0),
                genPointTexture(rNum+1,0)
        };
    }

    private static float[] genPoint(int rNum, int l){
        final double a=2*Math.PI/count*rNum;

        final double y=R*Math.sin(a);
        final double x=R*Math.cos(a);
        final double z=(l-0.5)*L;

        return new float[]{(float) x, (float) y, (float) z};
    }

    private static float[] genPointTexture(int rNum, int l){
        final double a=rNum*1.0/count;
        return new float[]{(float) a, (float) (l*L)};
    }

    @Override
    protected float[] getVertices() {
        float[] v=new float[3*6*count];
        int i=0;
        for(int q=0;q<count;q++){
            float[][] g=genQuad(q);
            for(float[] vert:g){
                for(float c:vert)v[i++]=c;
            }
        }
        return v;
    }

    @Override
    protected float[] getColorArray() {
        return new float[0];
    }

    @Override
    protected float[] getTextureArray() {
        float[] v=new float[2*6*count];
        int i=0;
        for(int q=0;q<count;q++){
            float[][] g=genTextureQuad(q);
            for(float[] vert:g){
                for(float c:vert)v[i++]=c;
            }
        }
        return v;
    }

    @Override
    protected float[] getNormalArray() {
        float[] vert=getVertices();
        for(int i=2;i<vert.length;i+=3)vert[i]=0;
        return vert;
    }

    @Override
    protected String getTexturePath() {
        return texturePath;
    }

    @Override
    protected int getPrimitiveType() {
        return GL_TRIANGLE_STRIP;
    }
}

