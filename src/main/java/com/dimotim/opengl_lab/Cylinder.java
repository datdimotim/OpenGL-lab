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

public class Cylinder {
    private static final double R=0.07;
    private static final double L=0.5;
    private static final int count=100;

    private ShortBuffer indexData;
    private FloatBuffer vertexData;
    private FloatBuffer normalData;
    private FloatBuffer materialData;

    //private FloatBuffer colorData;
    private FloatBuffer textureData;

    private int textureId=0;
    private boolean inited=false;

    private static double[][] genQuad(int rNum){
        return new double[][]{
                genPoint(rNum,0),
                genPoint(rNum,1),
                genPoint(rNum+1,1),
                genPoint(rNum+1,0)
        };
    }

    private static double[][] genTextureQuad(int rNum){
        return new double[][]{
                genPointTexture(rNum,0),
                genPointTexture(rNum,1),
                genPointTexture(rNum+1,1),
                genPointTexture(rNum+1,0)
        };
    }

    private static double[] genPoint(int rNum, int l){
        final double a=2*Math.PI/count*rNum;

        final double y=R*Math.sin(a);
        final double x=R*Math.cos(a);
        final double z=(l-0.5)*L;

        return new double[]{x,y,z};
    }

    private static double[] genPointTexture(int rNum, int l){
        final double a=rNum*1.0/count;
        return new double[]{a,l*L};
    }

    public Cylinder(){
        vertexData = ByteBuffer.allocateDirect(4*3*4*count).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalData= ByteBuffer.allocateDirect(4*3*4*count).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureData = ByteBuffer.allocateDirect(4*2*4*count).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for(int r=0;r<count;r++){
            double[][] gran=genQuad(r);
            double[][] granTex=genTextureQuad(r);
            for(double[] d:gran) {
                vertexData.put((float) d[0]).put((float) d[1]).put((float) d[2]);
            }
            for(double[] d:granTex) {
                textureData.put((float) d[0]).put((float) d[1]);
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

        indexData = ByteBuffer.allocateDirect(2*4*count).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i=0;i<4*count;i++)indexData.put((short) i);
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
        //textureId = loadTexture(texturePath).getTextureObject();
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

        gl.glDrawElements(GL_QUADS, 4*count, GL_UNSIGNED_SHORT, indexData.rewind());
    }
}

