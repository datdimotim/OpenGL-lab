package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

public class LystraHead{
    private static final double scale=3;
    private static final double R=0.2*scale;
    private static final double r=0.1*scale;
    private static final double H = 0.2*scale;
    private int nt = 15*10;

    private ShortBuffer indexData;
    private FloatBuffer vertexData;
    private FloatBuffer normalData;
    //private FloatBuffer colorData;
    //private FloatBuffer textureData;
    private static double[] genLystraUpPt(double t){
        return new double[]{
                Math.sin(t)*r,
                Math.cos(t)*r,
                H
        };
    }
    private static double[] genLystraLowPt(double t){
        return new double[]{
                Math.sin(t)*R,
                Math.cos(t)*R,
                0
        };
    }

    private static ArrayList<double[]> genLystraGran(double t, int nl){
        final double d=2*Math.PI/nl;

        ArrayList<double[]> list=new ArrayList<>();
        list.add(genLystraLowPt(t));
        list.add(genLystraLowPt(t+d));
        list.add(genLystraUpPt(t+d));
        list.add(genLystraUpPt(t));
        return list;
    }
    private static ArrayList<ArrayList<double[]>> genLystra(final int nt) {
        final double dt=2*Math.PI/nt;
        ArrayList<ArrayList<double[]>> lists = new ArrayList<>();
        for (int j = 0; j < nt; j++) {
            lists.add(genLystraGran(dt*j,nt));
        }
        return lists;
    }
    private static List<Integer> genTorIdx(final int nt){
        return Stream.iterate(0, i->i+1).takeWhile(i->i<nt*4).collect(Collectors.toList());
    }
    LystraHead(){
        List<ArrayList<double[]>> fig=genLystra(nt);
        List<Integer> figIdx=genTorIdx(nt);

        vertexData = ByteBuffer.allocateDirect(4*3*4*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalData= ByteBuffer.allocateDirect(4*3*4*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(ArrayList<double[]> gran:fig){
            double[] n=LinAl.vecMul(gran.get(0),gran.get(1),gran.get(2));
            for(double[] pt:gran){
                vertexData.put((float) pt[0]).put((float) pt[1]).put((float)pt[2]);
                normalData.put((float)n[0]).put((float)n[1]).put((float)n[2]);
            }
        }
        vertexData.position(0);
        normalData.position(0);

        indexData = ByteBuffer.allocateDirect(2*4*nt).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i:figIdx)indexData.put((short) i);
        indexData.position(0);

        //textureData = ByteBuffer.allocateDirect(4*2*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //textureData
        //        .put(0).put(0)
        //        .put(0).put(0.3f)
        //        .put(0.3f).put(0.3f)
        //        .position(0);

        //colorData = ByteBuffer.allocateDirect(4*3*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //colorData
        //        .put(0.8f).put(0.8f).put(0)
        //        .put(0).put(0.8f).put(0)
        //        .put(0.8f).put(0.3f).put(0)
        //        .position(0);


    }
    public void draw( GL2 gl, Shader shader ){
        gl.glVertexAttribPointer(shader.vertexArrayId,3,GL_FLOAT,false,0,vertexData.rewind());
        gl.glVertexAttribPointer(shader.normalId,3,GL_FLOAT,false,0,normalData.rewind());

        gl.glDrawElements(GL_QUADS, 4*nt, GL_UNSIGNED_SHORT, indexData.rewind());
    }
}
