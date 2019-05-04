package com.dimotim.opengl_lab;

import example.GLObject;

public class Sphere extends GLObject {
    private static final double R=0.1;
    private static final double am=Math.PI/2;
    private static final int betaCount=30;
    private static final int alphaCount=30;

    private static float[][] genQuad(int na, int nb){
        return new float[][]{
                genPoint(na,nb),
                genPoint(na,nb+1),
                genPoint(na+1,nb),
                genPoint(na+1,nb+1)
        };
    }

    private static float[][] genTextureQuad(int na, int nb){
        return new float[][]{
                genPointTexture(na,nb),
                genPointTexture(na,nb+1),
                genPointTexture(na+1,nb),
                genPointTexture(na+1,nb+1)
        };
    }

    private static float[] genPoint(int na, int nb){
        final double a=Math.PI/alphaCount*na-Math.PI/2;
        final double b=Math.PI*2/betaCount*nb;

        final double y=R*Math.sin(a);
        final double xProj=-R*Math.cos(a);
        final double x=xProj*Math.cos(b);
        final double z=xProj*Math.sin(b);

        return new float[]{(float) x, (float) y, (float) z};
    }

    private static float[] genPointTexture(int na, int nb){
        final double a=Math.PI/alphaCount*na-Math.PI/2;
        final double b=Math.PI*2/betaCount*nb;

        return new float[]{(float) (b/2/Math.PI), (float) (Math.sin(a)/Math.sin(am)/2+1.0/2)};
    }



    @Override
    protected float[] getVertices() {
        float[] v=new float[3*4*betaCount*alphaCount];
        int i=0;
        for(int a=0;a<alphaCount;a++){
            for(int b=0;b<betaCount;b++){
                float[][] g=genQuad(a,b);
                for(float[] vert:g){
                    for(float f:vert){
                        v[i++]=f;
                    }
                }
            }
        }
        return v;
    }

    @Override
    protected float[] getTextureArray() {
        float[] v=new float[2*4*betaCount*alphaCount];
        int i=0;
        for(int a=0;a<alphaCount;a++){
            for(int b=0;b<betaCount;b++){
                float[][] g=genTextureQuad(a,b);
                for(float[] vert:g){
                    for(float f:vert){
                        v[i++]=f;
                    }
                }
            }
        }
        return v;
    }

    @Override
    protected String getTexturePath() {
        return BasicFrame.texturePath;
    }

    @Override
    protected float[] getColorArray() {
        return new float[0];
    }
}
