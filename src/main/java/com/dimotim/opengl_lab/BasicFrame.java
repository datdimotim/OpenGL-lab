package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;

public class BasicFrame implements GLEventListener {
    private double yScissor = 100;
    private double xScissor = 100;
    private double alphaVal = 1.0;
    private ControlPanel.Primitive primitive = ControlPanel.Primitive.GL_POINTS;
    private ControlPanel.Alpha alpha = ControlPanel.Alpha.GL_NEVER;
    private ControlPanel.Sfactor sfactor= ControlPanel.Sfactor.GL_ZERO;
    private ControlPanel.Dfactor dfactor=ControlPanel.Dfactor.GL_ZERO;

    public void setSfactor(ControlPanel.Sfactor sfactor) {
        this.sfactor = sfactor;
    }

    public void setDfactor(ControlPanel.Dfactor dfactor) {
        this.dfactor = dfactor;
    }

    private ArrayList<Coords> coords = new ArrayList<>();
    private ArrayList<VertexColor> colors = new ArrayList<>();


    public void setXScissor(double xScissor) {
        this.xScissor = xScissor;
    }

    public void setYScissor(double yScissor) {
        this.yScissor = yScissor;
    }

    public void setPrimitive(ControlPanel.Primitive primitive) {
        this.primitive = primitive;
    }

    public void setAlpha(ControlPanel.Alpha alpha) {
        this.alpha = alpha;
    }

    public void setAlphaVal(double alphaVal) {
        this.alphaVal = alphaVal;
    }

    public void display(GLAutoDrawable drawable) {
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL2 gl = drawable.getGL().getGL2();
        gl.glPointSize(10);
        gl.glDisable(GL_SCISSOR_TEST);
        gl.glDisable(GL_ALPHA_TEST);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);


        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(sFactorToGLConstant(sfactor),dFactorToGLConstant(dfactor));
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(alphaToGLConstant(alpha), (float) alphaVal);

        gl.glEnable(GL_SCISSOR_TEST);
        gl.glViewport(0, 0, width, heght);
        gl.glScissor(0, 0, ((int) (width * xScissor / 100.0)), ((int) (heght * yScissor / 100.0)));


        //gl.glBegin(primitiveToGLConstant(primitive));
        gl.glLoadIdentity();
        gl.glScalef(2,2,2);
        fractal(gl,4);
        //gl.glEnd();
        gl.glFlush();
    }

    private void fractal(GL2 gl,final int depth){

        gl.glColor3f(1,1,1);

        if(depth==1) {
            segmentBig(gl);
            return;
        }
        segmentSmall(gl);

        gl.glPushMatrix();
            gl.glTranslatef(0.25f,0.015f,0);
            gl.glRotatef(-30,0,0,1);
            gl.glScalef(0.3f,0.3f,0.3f);
            fractal(gl,depth-1);
        gl.glPopMatrix();


        gl.glPushMatrix();
            gl.glTranslatef(0.25f,0.1f,0);
            gl.glRotatef(30,0,0,1);
            gl.glScalef(0.5f,0.5f,0.5f);
             //gl.glTranslatef(0.0f,0.1f,0);

            fractal(gl,depth-1);
        gl.glPopMatrix();
    }

    private void segmentBig(GL2 gl){
        gl.glBegin(primitiveToGLConstant(primitive));
            gl.glVertex2f(0,0.5f);
            gl.glVertex2f(-0.25f,0.0f);
            gl.glVertex2f(0.25f,0.0f);
        gl.glEnd();

    }
    private void segmentSmall(GL2 gl){
        gl.glPushMatrix();
        gl.glRotatef(10,0,0,1f);
        gl.glBegin(primitiveToGLConstant(primitive));
        gl.glVertex2f(0,0.02f);
        gl.glVertex2f(-0.2f,0.0f);
        gl.glVertex2f(0.2f,0.0f);
        gl.glEnd();
        gl.glPopMatrix();

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

    private static int primitiveToGLConstant(ControlPanel.Primitive primitive) {
        switch (primitive) {
            case GL_LINES: return GL2.GL_LINES;
            case GL_QUADS: return GL2.GL_QUADS;
            case GL_LINE_LOOP: return GL2.GL_LINE_LOOP;
            case GL_POINTS: return GL2.GL_POINTS;
            case GL_POLYGON: return GL2.GL_POLYGON;
            case GL_TRIANGLES: return GL2.GL_TRIANGLES;
            case GL_LINE_STRIP: return GL2.GL_LINE_STRIP;
            case GL_QUAD_STRIP: return GL2.GL_QUAD_STRIP;
            case GL_TRIANGLE_FAN: return GL2.GL_TRIANGLE_FAN;
            case GL_TRIANGLE_STRIP: return GL2.GL_TRIANGLE_STRIP;
            default: throw new RuntimeException();
        }
    }

    private static int alphaToGLConstant(ControlPanel.Alpha alpha) {
        switch (alpha) {
            case GL_NEVER: return GL2.GL_NEVER;
            case GL_LESS: return GL_LESS;
            case GL_EQUAL: return GL2.GL_EQUAL;
            case GL_LEQUAL: return GL2.GL_LEQUAL;
            case GL_GREATER: return GL2.GL_GREATER;
            case GL_NOTEQUAL: return GL2.GL_NOTEQUAL;
            case GL_GEQUAL: return GL2.GL_GEQUAL;
            case GL_ALWAYS: return GL2.GL_ALWAYS;
            default: throw new RuntimeException();
        }
    }
    private static int sFactorToGLConstant(ControlPanel.Sfactor sfactor){
        switch (sfactor) {
            case GL_ZERO: return GL_ZERO;
            case GL_ONE:return GL_ONE;
            case GL_DST_COLOR: return GL_DST_COLOR;
            case GL_ONE_MINUS_DST_COLOR: return GL_ONE_MINUS_DST_ALPHA;
            case GL_SRC_ALPHA: return GL_SRC_ALPHA;
            case GL_ONE_MINUS_SRC_ALPHA: return GL_ONE_MINUS_SRC_ALPHA;
            case GL_DST_ALPHA: return GL_DST_ALPHA;
            case GL_ONE_MINUS_DST_ALPHA: return GL_ONE_MINUS_DST_ALPHA;
            case GL_SRC_ALPHA_SATURATE: return GL_SRC_ALPHA_SATURATE;
            default:throw new RuntimeException();
        }
    }
    private static int dFactorToGLConstant(ControlPanel.Dfactor dfactor){
        switch (dfactor) {
            case GL_ZERO: return GL_ZERO;
            case GL_ONE: return GL_ONE;
            case GL_SRC_COLOR: return GL_SRC_COLOR;
            case GL_ONE_MINUS_SRC_COLOR: return GL_ONE_MINUS_SRC_COLOR;
            case GL_SRC_ALPHA: return GL_SRC_ALPHA;
            case GL_ONE_MINUS_SRC_ALPHA: return GL_ONE_MINUS_SRC_ALPHA;
            case GL_DST_ALPHA: return GL_DST_ALPHA;
            case GL_ONE_MINUS_DST_ALPHA: return GL_ONE_MINUS_DST_ALPHA;
            default:throw new RuntimeException();
        }
    }
}

class ControlPanel extends JPanel {
    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(12, 1));
        JComboBox<Primitive> comboBox = new JComboBox<>(Primitive.values());
        add(comboBox);
        comboBox.addItemListener(v -> {
            final Primitive primitive = (Primitive) comboBox.getSelectedItem();
            canvas.invoke(false, (e) -> {
                frame.setPrimitive(primitive);
                return false;
            });

        });
        comboBox.setSelectedItem(Primitive.GL_LINE_LOOP);



        add(new JLabel("Отсечение"));
        add(new JPanel() {{
            add(new Label("X"));
            JSlider xSlider = new JSlider(0, 100, 100);
            xSlider.addChangeListener(v -> {
                final int val = xSlider.getValue();
                canvas.invoke(false, (e) -> {
                    frame.setXScissor(val);
                    return false;
                });

            });
            add(xSlider);
        }});
        add(new JPanel() {{
            add(new Label("Y"));
            JSlider ySlider = new JSlider(0, 100, 100);
            ySlider.addChangeListener(v -> {
                final int val = ySlider.getValue();
                canvas.invoke(false, (e) -> {
                    frame.setYScissor(val);
                    return false;
                });

            });
            add(ySlider);
        }});

        add(new JLabel("Прозрачность"));
        JComboBox<Alpha> alphaComboBox = new JComboBox<>(Alpha.values());
        alphaComboBox.addItemListener(e -> {
            Alpha alpha = (Alpha) alphaComboBox.getSelectedItem();
            canvas.invoke(false, ee -> {
                frame.setAlpha(alpha);
                return false;
            });
        });
        alphaComboBox.setSelectedItem(Alpha.GL_ALWAYS);
        add(alphaComboBox);
        add(new JPanel() {{
            add(new Label(""));
            JSlider slider = new JSlider(0, 100, 100);
            slider.addChangeListener(e -> {
                final int val = slider.getValue();
                canvas.invoke(false, ee -> {
                    frame.setAlphaVal(val / 100.0);
                    return false;
                });
            });
            add(slider);
        }});


        add(new JLabel("Смешение"));
        add(new JLabel("sfactor"));
        add(new JComboBox<Sfactor>(Sfactor.values()){{
            addItemListener(e->{
                Sfactor sfactor= (Sfactor) getSelectedItem();
                canvas.invoke(false,ee->{
                    frame.setSfactor(sfactor);
                    return false;
                });
            });
            setSelectedItem(Sfactor.GL_ONE);
        }});
        add(new JLabel("dfactor"));
        add(new JComboBox<Dfactor>(Dfactor.values()){{
            addItemListener(e->{
                Dfactor dfactor= (Dfactor) getSelectedItem();
                canvas.invoke(false,ee->{
                    frame.setDfactor(dfactor);
                    return false;
                });
            setSelectedItem(Dfactor.GL_ZERO);
            });
        }});


    }

    enum Primitive {
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

    enum Alpha {
        GL_NEVER,        //– никогда не пропускает
        GL_LESS,         //– пропускает, если входное значение альфа меньше,   чем        значение ref
        GL_EQUAL,        //– пропускает, если входное значение альфа равно  значению ref
        GL_LEQUAL,       //– пропускает, если входное значение альфа меньше или равно значения ref
        GL_GREATER,      //– пропускает, если входное значение альфа больше, чем    значение ref
        GL_NOTEQUAL,     //– пропускает, если входное значение альфа не равно значению ref
        GL_GEQUAL,         //– пропускает, если входное значение альфа больше или равно значения ref
        GL_ALWAYS,         //
    }

    enum Sfactor {
        GL_ZERO,
        GL_ONE,
        GL_DST_COLOR,
        GL_ONE_MINUS_DST_COLOR,
        GL_SRC_ALPHA,
        GL_ONE_MINUS_SRC_ALPHA,
        GL_DST_ALPHA,
        GL_ONE_MINUS_DST_ALPHA,
        GL_SRC_ALPHA_SATURATE
    }
    enum Dfactor{
        GL_ZERO,
        GL_ONE,
        GL_SRC_COLOR,
        GL_ONE_MINUS_SRC_COLOR,
        GL_SRC_ALPHA,
        GL_ONE_MINUS_SRC_ALPHA,
        GL_DST_ALPHA,
        GL_ONE_MINUS_DST_ALPHA
    }
}


class Coords {
    float x, y;

    Coords(float x, float y) {
        this.x = x;
        this.y = y;
    }
}

class VertexColor {
    float r, g, b;

    VertexColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}