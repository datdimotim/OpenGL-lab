package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.dimotim.opengl_lab.Shader.loadTexture;
import static com.dimotim.opengl_lab.BasicFrame.texturePath;
import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

public class Sphere {
    private static final double R=0.1;
    private static final double am=Math.PI/2;
    private static final int betaCount=100;
    private static final int alphaCount=100;

    private ShortBuffer indexData;
    private FloatBuffer vertexData;
    private FloatBuffer normalData;
    private FloatBuffer materialData;

    //private FloatBuffer colorData;
    private FloatBuffer textureData;

    private int textureId=0;
    private boolean inited=false;

    private static double[][] genQuad(int na, int nb){
        return new double[][]{
                genPoint(na,nb),
                genPoint(na,nb+1),
                genPoint(na+1,nb+1),
                genPoint(na+1,nb)
        };
    }

    private static double[][] genTextureQuad(int na, int nb){
        return new double[][]{
                genPointTexture(na,nb),
                genPointTexture(na,nb+1),
                genPointTexture(na+1,nb+1),
                genPointTexture(na+1,nb)
        };
    }

    private static double[] genPoint(int na, int nb){
        final double a=Math.PI/alphaCount*na-Math.PI/2;
        final double b=Math.PI*2/betaCount*nb;

        final double y=R*Math.sin(a);
        final double xProj=-R*Math.cos(a);
        final double x=xProj*Math.cos(b);
        final double z=xProj*Math.sin(b);

        return new double[]{x,y,z};
    }

    private static double[] genPointTexture(int na, int nb){
        final double a=Math.PI/alphaCount*na-Math.PI/2;
        final double b=Math.PI*2/betaCount*nb;

        return new double[]{b/2/Math.PI,Math.sin(a)/Math.sin(am)/2+1.0/2};
    }

    public Sphere(){
        vertexData = ByteBuffer.allocateDirect(4*3*4*alphaCount*betaCount).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalData= ByteBuffer.allocateDirect(4*3*4*alphaCount*betaCount).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureData = ByteBuffer.allocateDirect(4*2*4*alphaCount*betaCount).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for(int na=0;na<alphaCount;na++){
            for(int nb=0;nb<betaCount;nb++){
                double[][] gran=genQuad(na,nb);
                double[][] granTex=genTextureQuad(na,nb);
                for(double[] d:gran) {
                    vertexData.put((float) d[0]).put((float) d[1]).put((float) d[2]);
                }
                for(double[] d:granTex) {
                    textureData.put((float) d[0]).put((float) d[1]);
                }
            }
        }
        //materialData = ByteBuffer.allocateDirect(4*3*4*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //for(ArrayList<double[]> gran:fig){
        //    for(double[] ignored :gran){
        //        materialData.put(rm).put(gm).put(bm);
        //    }
        //}
        vertexData.position(0);
        normalData.position(0);
        textureData.position(0);

        indexData = ByteBuffer.allocateDirect(2*4*alphaCount*betaCount).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i=0;i<4*alphaCount*betaCount;i++)indexData.put((short) i);
        indexData.position(0);


        //colorData = ByteBuffer.allocateDirect(4*3*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //colorData
        //        .put(0.8f).put(0.8f).put(0)
        //        .put(0).put(0.8f).put(0)
        //        .put(0.8f).put(0.3f).put(0)
        //        .position(0);


    }

    private void init(GL2 gl){
        gl.glActiveTexture(GL_TEXTURE0);
        textureId = loadTexture(texturePath).getTextureObject();
        inited=true;
    }

    public void draw(GL2 gl, Shader shader, float[] modelMatrix){
        if(!inited)init(gl);

        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, modelMatrix, 0);

        gl.glBindTexture(GL_TEXTURE_2D, textureId);
        gl.glUniform1i(shader.textureId, 0);

        gl.glVertexAttribPointer(shader.vertexArrayId,3,GL_FLOAT,false,0,vertexData.rewind());
        gl.glVertexAttribPointer(shader.textureArrayId,2,GL_FLOAT,false,0,textureData.rewind());
        //gl.glVertexAttribPointer(shader.normalId,3,GL_FLOAT,false,0,normalData.rewind());
        //gl.glVertexAttribPointer(shader.colorArrayId,3,GL_FLOAT,false,0,materialData.rewind());

        gl.glDrawElements(GL_QUADS, 4*alphaCount*betaCount, GL_UNSIGNED_SHORT, indexData.rewind());
    }
}
