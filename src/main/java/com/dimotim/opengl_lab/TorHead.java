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
    private int nt = 15;
    private int np = 15;
    private static final double scale=3;
    private static final double R=0.1*scale;
    private static final double r=0.05*scale;

    private final double pStart;
    private final double pFinish;
    private final double tStart;
    private final double tFinish;

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

    private ArrayList<double[]> genGran(double p,double t, int np, int nt){
        final double dp=(pFinish-pStart)/np;
        final double dt=(tFinish-tStart)/nt;

        ArrayList<double[]> list=new ArrayList<>();
        list.add(genPt(p,t));
        list.add(genPt(p,t+dt));
        list.add(genPt(p+dp,t+dt));
        list.add(genPt(p+dp,t));
        return list;
    }

    private ArrayList<ArrayList<double[]>> genTor(final int np, final int nt) {
        final double dp=(pFinish-pStart)/np;
        final double dt=(tFinish-tStart)/nt;
        ArrayList<ArrayList<double[]>> lists = new ArrayList<>();
        for (int i = 0; i < np; i++) {
            for (int j = 0; j < nt; j++) {
                lists.add(genGran(pStart+dp*i,tStart+dt*j,np,nt));
            }
        }
        return lists;
    }

    private ArrayList<ArrayList<double[]>> genTorNormals(final  int np, final int nt){
        final double dp=(pFinish-pStart)/np;
        final double dt=(tFinish-tStart)/nt;
        ArrayList<ArrayList<double[]>> listGrans=new ArrayList<>();
        for (int i = 0; i < np; i++) {
            for (int j = 0; j < nt; j++) {
                ArrayList<double[]> gran=new ArrayList<>();
                gran.add(genNormal(pStart+dp*i,tStart+dt*j));
                gran.add(genNormal(pStart+dp*i,tStart+dt*(j+1)));
                gran.add(genNormal(pStart+dp*(i+1),tStart+dt*(j+1)));
                gran.add(genNormal(pStart+dp*(i+1),tStart+dt*j));
                listGrans.add(gran);
            }
        }
        return listGrans;
    }

    private double[] genNormal(double p, double t){
        return new double[]{
                Math.cos(t)*(-1)*Math.cos(p),
                Math.sin(t)*(-1)*Math.cos(p),
                Math.sin(p)
        };
    }

    public TorHead(double pStart, double pFinish, double tStart, double tFinish, int melkost){
        this.pStart=pStart;
        this.pFinish=pFinish;
        this.tStart=tStart;
        this.tFinish=tFinish;
        this.nt = melkost;
        this.np = melkost;
        List<ArrayList<double[]>> fig=genTor(np,nt);
        List<ArrayList<double[]>> normals=genTorNormals(np,nt);
        List<Integer> figIdx=genTorIdx(np,nt);

        vertexData = ByteBuffer.allocateDirect(4*3*4*np*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalData = ByteBuffer.allocateDirect(4*3*4*np*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(ArrayList<double[]> gran:fig){
            for(double[] pt:gran){
                vertexData.put((float) pt[0]).put((float) pt[1]).put((float)pt[2]);
            }
        }
        for(ArrayList<double[]> gran:normals){
            for(double[] pt:gran){
                normalData.put((float) pt[0]).put((float) pt[1]).put((float)pt[2]);
            }
        }
        vertexData.position(0);
        normalData.position(0);

        indexData = ByteBuffer.allocateDirect(2*4*np*nt).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i:figIdx)indexData.put((short) i);
        indexData.position(0);
    }
    public void draw(GL2 gl, Shader shader){


        gl.glVertexAttribPointer(shader.vertexArrayId,3,GL_FLOAT,false,0,vertexData.rewind());
        gl.glVertexAttribPointer(shader.normalId,3,GL_FLOAT,false,0,normalData.rewind());
        gl.glDrawElements(GL_QUADS, 4*np*nt, GL_UNSIGNED_SHORT, indexData.rewind());
    }
}