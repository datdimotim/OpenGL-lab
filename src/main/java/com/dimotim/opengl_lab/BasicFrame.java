package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;


import javax.swing.*;

import static com.jogamp.opengl.GL.*;


public class BasicFrame implements GLEventListener {
    private int textureId;

    private final Axis axis = new Axis();
    private final NetPlane netPlane=new NetPlane();
    private final Marker marker=new Marker();
    private final CompositeModel model=new CompositeModel();
    private Shader shader = null;
    private Shader netShader=null;

    // Первый-куда, второй - координата верха
    private float[] projectionMatrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    private float[] viewMatrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    private float[] modelMatrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    private float[] prevViewMatrix=viewMatrix;

    public void changeViewMatrixByMouse(double angle, double x, double y){
        viewMatrix=LinAl.matrixMul(prevViewMatrix,LinAl.rotate(angle,x,y,0));
    }

    public void commitViewMatrixByMouse(){
        prevViewMatrix=viewMatrix;
    }

    public void scale(int dir){
        projectionMatrix=LinAl.matrixMul(projectionMatrix,LinAl.scale((float) ((100.0+dir)/100)));
    }

    public void display(GLAutoDrawable drawable) {
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();

        final GL4 gl = drawable.getGL().getGL4();

        int error2 = gl.glGetError();
        if (error2 != 0) {
            System.err.println("ERROR on render2 : " + error2);
        }



        gl.glClearColor(0, 0, 0, 0);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);




        gl.glPolygonMode(GL_FRONT_AND_BACK, GL2GL3.GL_FILL);


        shader.use(gl);
        gl.glUniformMatrix4fv(shader.getViewMatrixLoc(), 1, false, LinAl.matrixMul(projectionMatrix,viewMatrix), 0);
        model.draw(gl,shader,modelMatrix);

        netShader.use(gl);
        gl.glUniformMatrix4fv(netShader.getViewMatrixLoc(), 1, false, LinAl.matrixMul(projectionMatrix,viewMatrix), 0);
        axis.draw(gl,netShader,modelMatrix);
        netPlane.draw(gl,netShader,modelMatrix);
        marker.draw(gl,netShader,LinAl.matrixMul(LinAl.translate(0,0,-0.4),modelMatrix));

        checkErr(gl);
    }

    public void dispose(GLAutoDrawable arg0) {

    }

    public void init(GLAutoDrawable glad) {
        System.out.println("init");
        GL4 gl = glad.getGL().getGL4();
        System.out.println("GL version:" + gl.glGetString(GL.GL_VERSION));
        shader = new TextureShader(gl);
        netShader=new NetShader(gl);
        checkErr(gl);
    }


    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }


    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        BasicFrame frame = new BasicFrame();
        glcanvas.addGLEventListener(frame);
        glcanvas.setSize(1000, 1000);

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

    public static void checkErr(GL4 gl){
        int error = gl.glGetError();
        if (error != 0) {
            throw new RuntimeException("ERROR on render : " + error);
        }
    }
}

