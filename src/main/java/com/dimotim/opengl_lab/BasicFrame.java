package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;


import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import static com.jogamp.opengl.GL.*;


public class BasicFrame implements GLEventListener {
    private final Axis axis = new Axis();
    private final NetPlane netPlane=new NetPlane();
    private final Marker marker=new Marker();
    private final CompositeModel model=new CompositeModel();
    private TextureShader shader = null;
    private Shader netShader=null;

    private float[] viewMatrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 1, 1
    };

    private final float[] lightPos=new float[3];

    private float[] prevViewMatrix=viewMatrix;

    public void changeViewMatrixByMouse(double dx,double dy){
        if(Math.abs(dx)<0.00001)return;
        viewMatrix=LinAl.matrixMul(prevViewMatrix,LinAl.rotate(Math.abs(dx)*10,0,dx,0));
        if(Math.abs(dy)<0.00001)return;
        viewMatrix=LinAl.matrixMul(viewMatrix,LinAl.rotate(Math.abs(dy)*10,dy,0,0));
    }

    public void commitViewMatrixByMouse(){
        prevViewMatrix=viewMatrix;
    }


    public void setLightPos(float x, float y, float z){
        lightPos[0]= x;
        lightPos[1]= y;
        lightPos[2]= z;
    }

    public void translateObserver(float[] dir){
        viewMatrix=LinAl.matrixMul(viewMatrix,LinAl.translate(-dir[0],-dir[1],-dir[2]));
        prevViewMatrix=viewMatrix;
    }

    public void rotate(int dir){
        viewMatrix=LinAl.matrixMul(viewMatrix,LinAl.rotate(0.05,0,0,dir));
        prevViewMatrix=viewMatrix;
    }

    public void display(GLAutoDrawable drawable) {
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();

        float[] inv=LinAl.inverse(viewMatrix);
        float[] observerPos=new float[]{inv[12],inv[13],inv[14]};
        System.out.println("ObserverPos: "+Arrays.toString(observerPos));

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
        gl.glUniform3f(shader.getLightPosLoc(),lightPos[0],lightPos[1],lightPos[2]);
        gl.glUniformMatrix4fv(shader.getViewMatrixLoc(), 1, false, viewMatrix, 0);
        model.draw(gl,shader,LinAl.identity());

        netShader.use(gl);
        gl.glUniformMatrix4fv(netShader.getViewMatrixLoc(), 1, false,viewMatrix, 0);
        axis.draw(gl,netShader,LinAl.identity());
        netPlane.draw(gl,netShader,LinAl.identity());
        marker.draw(gl,netShader,LinAl.translate(lightPos[0],lightPos[1],lightPos[2]));

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
        final GLCanvas glcanvas = new GLCanvas(capabilities){};
        BasicFrame frame = new BasicFrame();
        glcanvas.addGLEventListener(frame);
        glcanvas.setSize(1000, 1000);

        JPanel content = new JPanel();
        content.add(glcanvas);
        content.add(new ControlPanel(frame, glcanvas));

        final JFrame window = new JFrame("Basic Frame");
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                glcanvas.requestFocus();
                Timer timer=new Timer(100,ee->{
                    glcanvas.requestFocus();
                });
                timer.setRepeats(true);
                timer.start();
            }
        });


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

