package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.jogamp.opengl.GL.*;


public class BasicFrame implements GLEventListener {
    private FloatBuffer vertexData;
    private FloatBuffer colorData;
    private int indexId;

    private Shader shader=null;
    private final float[] matrix={
            1f,0,0,0,
            0,1f,0,0,
            0,0,1f,0,
            0,0,0,1f
    };

    public void display(GLAutoDrawable drawable) {init(drawable);
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0,0,0,0);


        gl.glUniformMatrix4fv(shader.monitorMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.viewMatrixId, 1, false, matrix, 0);

        gl.glVertexAttribPointer(shader.colorArrayId, 3, GL_FLOAT, false, 0, colorData);
        gl.glVertexAttribPointer(shader.vertexArrayId,2,GL_FLOAT,false,0,vertexData);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);

        gl.glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, 0);
    }

    public void dispose(GLAutoDrawable arg0) {

    }

    public void init(GLAutoDrawable glad) {
        GL2 gl=glad.getGL().getGL2();

        shader=new Shader(gl);


        ShortBuffer indexData = ByteBuffer.allocateDirect(2*3).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexData.put((short) 0).put((short) 1).put((short) 2).position(0);

        vertexData = ByteBuffer.allocateDirect(4*2*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData
                .put(-0.8f).put(-0.8f)
                .put(0).put(0.8f)
                .put(0.8f).put(-0.8f)
                .position(0);

        colorData = ByteBuffer.allocateDirect(4*3*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorData
                .put(0.8f).put(0.8f).put(0)
                .put(0).put(0.8f).put(0)
                .put(0.8f).put(0.3f).put(0)
                .position(0);

        final int[] a=new int[1];
        gl.glGenBuffers(1, a, 0);
        indexId = a[0];

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData.capacity() * 2, indexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }

    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        Animator animator = new Animator(glcanvas);
        animator.start();
        BasicFrame frame = new BasicFrame();
        glcanvas.addGLEventListener(frame);
        glcanvas.setSize(700, 700);

        JPanel content = new JPanel();
        content.add(glcanvas);
        content.add(new ControlPanel(frame, glcanvas));

        final JFrame window = new JFrame("Basic Frame");

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane(content);
        window.pack();
        window.setVisible(true);
    }
}

class ControlPanel extends JPanel {
    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(1, 1));

        /*slider.addChangeListener(e -> {
            final int val = slider.getValue();
            canvas.invoke(false, ee -> {
                frame.points.get(finalI).weigh = val;
                return false;
            });
        });*/

    }
}