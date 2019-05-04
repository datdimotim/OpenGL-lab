package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;

import static com.jogamp.opengl.GL.*;

public class Axis{
    private ShortBuffer indexData;
    private FloatBuffer vertexData;


    private static List<Integer> genIdx(){
        return Arrays.asList(0,1,2,3,4,5);
    }
    public Axis(){
        vertexData = ByteBuffer.allocateDirect(4*3*6).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(0).put(0).put(0);
        vertexData.put(1).put(0).put(0);
        vertexData.put(0).put(0).put(0);
        vertexData.put(0).put(1).put(0);
        vertexData.put(0).put(0).put(0);
        vertexData.put(0).put(0).put(1);
        vertexData.position(0);

        indexData = ByteBuffer.allocateDirect(2*6).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i:genIdx())indexData.put((short) i);
        indexData.position(0);

    }
    //public void draw( GL2 gl, Shader shader ){
    //    gl.glVertexAttribPointer(shader.vertexArrayId,3,GL_FLOAT,false,0,vertexData.rewind());
    //    gl.glDrawElements(GL_LINES, 6, GL_UNSIGNED_SHORT, indexData.rewind());
   // }
}
