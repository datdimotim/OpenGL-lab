package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import example.ShadeProgram;


import javax.swing.*;

import static com.jogamp.opengl.GL.*;


public class BasicFrame implements GLEventListener {
    private int textureId;

    private final Axis axis = new Axis();
    private final CompositeModel model=new CompositeModel();
    private ShadeProgram shader = null;

    // Первый-куда, второй - координата верха
    private final float[] projectionMatrix = {
            1.5f, 0, 0, 0,
            0, 1.5f, 0, 0,
            0, 0, 1.5f, 0,
            0, 0, 0, 1f
    };

    private float[] viewMatrix = {
            1f, 0, 0, 0,
            0, 1f, 0, 0,
            0, 0, 1f, 0,
            0, 0, 0, 1f
    };

    private float[] modelMatrix = {
            1f, 0, 0, 0,
            0, 1f, 0, 0,
            0, 0, 1f, 0,
            0, 0, 0, 1f
    };

    private float[] prevViewMatrix=viewMatrix;

    public void changeViewMatrixByMouse(double angle, double x, double y){
        viewMatrix=LinAl.matrixMul(prevViewMatrix,LinAl.rotate(angle,x,y,0));
    }

    public void commitViewMatrixByMouse(){
        prevViewMatrix=viewMatrix;
    }

    public void display(GLAutoDrawable drawable) {
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL3 gl = drawable.getGL().getGL3();

        gl.glUseProgram(shader.progId);
        gl.glClearColor(0, 0, 0, 0);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL2GL3.GL_FILL);



        //gl.glUniformMatrix4fv(shader.monitorMatrixId, 1, false, projectionMatrix, 0);
        gl.glUniformMatrix4fv(shader.viewMatrixLoc, 1, false, viewMatrix, 0);
        //gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, modelMatrix, 0);
        //axis.draw(gl, shader);
        //sphere.draw(gl,shader, projectionMatrix);
        model.draw(gl,shader,viewMatrix);

        int error = gl.glGetError();
        if (error != 0) {
            System.err.println("ERROR on render : " + error);
        }
    }

    public void dispose(GLAutoDrawable arg0) {

    }

    public void init(GLAutoDrawable glad) {
        System.out.println("init");
        GL3 gl = glad.getGL().getGL3();
        System.out.println("GL version:" + gl.glGetString(GL.GL_VERSION));
        shader = new ShadeProgram(gl);
    }


    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }


    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities capabilities = new GLCapabilities(profile);
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        BasicFrame frame = new BasicFrame();
        glcanvas.addGLEventListener(frame);
        glcanvas.setSize(700, 700);

        JPanel content = new JPanel();
        content.add(glcanvas);
        content.add(new ControlPanel(frame, glcanvas));

        final JFrame window = new JFrame("Basic Frame");

        new FPSAnimator(glcanvas, 60).start();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane(content);
        window.pack();
        window.setVisible(true);
    }
}

