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

import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

public class TorHead{
    private int nt = 15*10;
    private int np = 15*10;
    private static final double scale=3;
    private static final double R=0.1*scale;
    private static final double r=0.05*scale;
    private FloatBuffer vertexData;
    private FloatBuffer normalData;
    private ShortBuffer indexData;

    private static List<Integer> genTorIdx(final int np,final int nt){
        return Stream.iterate(0, i->i+1).takeWhile(i->i<np*nt*4).collect(Collectors.toList());
    }
    private static double[] genPt(double p, double t){
        return new double[]{
                Math.cos(t)*(R - r*Math.cos(p)),
                Math.sin(t)*(R-r*Math.cos(p)),
                r*Math.sin(p)
        };
    }

    private static ArrayList<double[]> genGran(double p,double t, int np, int nt){
        final double dp=2*Math.PI/np;
        final double dt=2*Math.PI/nt;

        ArrayList<double[]> list=new ArrayList<>();
        list.add(genPt(p,t));
        list.add(genPt(p,t+dt));
        list.add(genPt(p+dp,t+dt));
        list.add(genPt(p+dp,t));
        return list;
    }

    private static ArrayList<ArrayList<double[]>> genTor(final int np, final int nt) {
        final double dp=2*Math.PI/np;
        final double dt=2*Math.PI/nt;
        ArrayList<ArrayList<double[]>> lists = new ArrayList<>();
        for (int i = 0; i < np; i++) {
            for (int j = 0; j < nt; j++) {
                lists.add(genGran(dp*i,dt*j,np,nt));
            }
        }
        return lists;
    }
    TorHead(){
        List<ArrayList<double[]>> fig=genTor(np,nt);
        List<Integer> figIdx=genTorIdx(np,nt);

        vertexData = ByteBuffer.allocateDirect(4*3*4*np*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalData = ByteBuffer.allocateDirect(4*3*4*np*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(ArrayList<double[]> gran:fig){
            double[] n=LinAl.vecMul(gran.get(0),gran.get(1),gran.get(2));
            for(double[] pt:gran){
                vertexData.put((float) pt[0]).put((float) pt[1]).put((float)pt[2]);
                normalData.put((float) n[0]).put((float) n[1]).put((float) n[2]);
            }
        }
        vertexData.position(0);
        normalData.position(0);

        indexData = ByteBuffer.allocateDirect(2*4*np*nt).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i:figIdx)indexData.put((short) i);
        indexData.position(0);

    }
    public void draw(GL2 gl, Shader shader ){
        gl.glVertexAttribPointer(shader.vertexArrayId,3,GL_FLOAT,false,0,vertexData.rewind());
        gl.glVertexAttribPointer(shader.normalId,3,GL_FLOAT,false,0,normalData.rewind());
        gl.glDrawElements(GL_QUADS, 4*np*nt, GL_UNSIGNED_SHORT, indexData.rewind());
    }
}