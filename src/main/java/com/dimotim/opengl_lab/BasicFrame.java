package com.dimotim.opengl_lab;



import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;

public class BasicFrame implements GLEventListener {
    private static int primitiveToGLConstant(ControlPanel.Primitive primitive){
        switch (primitive){
            case GL_LINES: return GL2.GL_LINES;
            case GL_QUADS: return GL2.GL_QUADS;
            case GL_LINE_LOOP: return GL2.GL_LINE_LOOP;
            case GL_POINTS: return GL2.GL_POINTS;
            case GL_POLYGON: return GL2.GL_POLYGON;
            case GL_TRIANGLES: return GL2.GL_TRIANGLES;
            case GL_LINE_STRIP: return GL2.GL_LINE_STRIP;
            case GL_QUAD_STRIP:return GL2.GL_QUAD_STRIP;
            case GL_TRIANGLE_FAN: return GL2.GL_TRIANGLE_FAN;
            case GL_TRIANGLE_STRIP:return GL2.GL_TRIANGLE_STRIP;
            default:throw new RuntimeException();
        }
    }

    private ControlPanel.Primitive primitive= ControlPanel.Primitive.GL_LINES;

    public void setPrimitive(ControlPanel.Primitive primitive){
        this.primitive=primitive;
    }

    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glBegin(primitiveToGLConstant(primitive));
            gl.glVertex3f(-0.50f, -0.50f, 0);
            gl.glVertex3f(0.50f, -0.50f, 0);
            gl.glVertex3f(0f, 0.50f, 0);
            gl.glVertex3f(-0.50f, -0.50f, 0);
            gl.glVertex3f(0f, 0.50f, 0);
            gl.glVertex3f(0.50f, -0.50f, 0);

        gl.glEnd();
        gl.glFlush();
    }

    public void dispose(GLAutoDrawable arg0) {

    }

    public void init(GLAutoDrawable arg0) {

    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }

    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(capabilities);

        Animator animator=new Animator(glcanvas);
        animator.start();

        BasicFrame frame = new BasicFrame();
        glcanvas.addGLEventListener(frame);
        glcanvas.setSize(700, 700);

        JPanel content=new JPanel();
        content.add(glcanvas);
        content.add(new ControlPanel(frame,content));

        final JFrame window = new JFrame ("Basic Frame");


        window.setContentPane(content);
        window.setSize(window.getContentPane().getPreferredSize());
        window.setVisible(true);

    }

}

class ControlPanel extends JPanel{
    ControlPanel(BasicFrame frame, JPanel parent){
        JComboBox<Primitive> comboBox=new JComboBox<>(Primitive.values());
        add(comboBox);
        comboBox.addItemListener(v->{
            frame.setPrimitive((Primitive) comboBox.getSelectedItem());
            parent.updateUI();
        });
    }
    enum Primitive{
        GL_POINTS,
        GL_LINES,
        GL_LINE_STRIP,
        GL_LINE_LOOP,
        GL_TRIANGLES,
        GL_TRIANGLE_STRIP,
        GL_TRIANGLE_FAN,
        GL_QUADS,
        GL_QUAD_STRIP,
        GL_POLYGON
    }
}