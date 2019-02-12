package com.dimotim.opengl_lab;



import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.util.ArrayList;

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

    private ControlPanel.Primitive primitive= ControlPanel.Primitive.GL_POINTS;

    public void setPrimitive(ControlPanel.Primitive primitive){
        this.primitive=primitive;
    }
    ArrayList<Coords> coords = new ArrayList<>();
    ArrayList<VertexColor> colors = new ArrayList<>();

    public void display(GLAutoDrawable drawable) {
        generateVertexInfo();
        final GL2 gl = drawable.getGL().getGL2();
        gl.glPointSize(10);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glBegin(primitiveToGLConstant(primitive));

        for (int i = 0; i < coords.size(); i=i+3) {
            gl.glColor3f(colors.get((int)(i/3)).r,colors.get((int)(i/3)).g,colors.get((int)(i/3)).b);
            for (int j = i; j < i+3; j++) {
                gl.glVertex3f(coords.get(j).x,coords.get(j).y,0);;
            }
        }
        gl.glEnd();
        gl.glFlush();
    }
    private void generateVertexInfo(){
        colors.clear();
        coords.clear();
        double r = 0.5;
        int n = 9;
        for (int i = 0; i < n; i++) {
            coords.add(new Coords((float) (r*Math.cos((2*Math.PI*i)/n)), (float) (r*Math.sin((2*Math.PI*i)/n))));
        }
        colors.add(new VertexColor(0,0,0.5f));
        colors.add(new VertexColor(0,0.5f,0));
        colors.add(new VertexColor(0.5f,0,0));

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
        content.add(new ControlPanel(frame,glcanvas));

        final JFrame window = new JFrame ("Basic Frame");


        window.setContentPane(content);
        window.setSize(window.getContentPane().getPreferredSize());
        window.setVisible(true);

    }

}

class ControlPanel extends JPanel{
    ControlPanel(BasicFrame frame, GLCanvas canvas){
        JComboBox<Primitive> comboBox=new JComboBox<>(Primitive.values());
        add(comboBox);
        comboBox.addItemListener(v->{
            final Primitive primitive=(Primitive) comboBox.getSelectedItem();
            canvas.invoke(false,(e)->{
                frame.setPrimitive(primitive);
                return false;
            });

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
class Coords{
    float x, y;
    Coords(float x, float y){
        this.x = x;
        this.y=y;
    }
}
class VertexColor{
    float r,g,b;
    VertexColor(float r,float g,float b){
        this.r = r;
        this.g = g;
        this.b = b;
    }
}