package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

public class BasicFrame implements GLEventListener {
    private double yScissor = 100;
    private double xScissor = 100;
    private double alphaVal = 1.0;
    private int depth = 1;
    private ControlPanel.Primitive primitive = ControlPanel.Primitive.GL_POINTS;
    private ControlPanel.Alpha alpha = ControlPanel.Alpha.GL_NEVER;
    private ControlPanel.Sfactor sfactor= ControlPanel.Sfactor.GL_ZERO;
    private ControlPanel.Dfactor dfactor=ControlPanel.Dfactor.GL_ZERO;
    public ArrayList<Coords> points= new ArrayList();
    public void setSfactor(ControlPanel.Sfactor sfactor) {
        this.sfactor = sfactor;
    }

    public void setDfactor(ControlPanel.Dfactor dfactor) {
        this.dfactor = dfactor;
    }
    public float pointSize = 2;
    public float pointSizeBig = 20;

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

    public void setDepth(int depth) {
        this.depth = depth;
    }
    int n = 6;
    int qmax = 4;
    double []ts = new double[qmax+n+1];
    Coords []ps = new Coords[n];
    public void genTs(){
        for (int i = 0; i < ts.length; i++) {
            ts[i]=i*100;
        }
        System.out.println(ps.length);
       /* for (int i = 0; i < ps.length; i++) {
            ps[i]=new Coords(100f*i,100f,700,700);
        }*/
        points.add(new Coords(0f,100f,700,700));
        points.add(new Coords(100f,200f,700,700));
        points.add(new Coords(200f,100f,700,700));
        points.add(new Coords(300f,300f,700,700));
        points.add(new Coords(400f,100f,700,700));
        points.add(new Coords(500f,500f,700,700));

    }
   int maxPoints=500;
  public void recountTs(){
      float max = -1;
      float min = 100000;
      for (int ktr = 0; ktr < points.size(); ktr++) {
          if (points.get(ktr).JavaX > max) {
              max = points.get(ktr).JavaX;
          }
          if (points.get(ktr).JavaX < min) {
              min = points.get(ktr).JavaX;
          }
      }
      maxPoints = (int)max;
      float dt = (max-min)/(ts.length-1);
      for (int i = 0; i < ts.length; i++) {
          ts[i]=min+i*dt*1.75;
      }
  }
    Function<Double, Double>[][] ns = new Function[qmax][n];
    public void regenerateAll(){
        n++;
        ts = new double[qmax+n+1];
        ns = new Function[qmax][n];
        recountTs();
        Collections.sort(points, new Comparator<Coords>() {
            @Override
            public int compare(Coords o1, Coords o2) {
                return (int)(o1.JavaX - o2.JavaX);
            }
        });
        generateNs(0,0);
    }
    public Double processNs(int q, int k,double t){
        if (q==1) {
                if ((t>=ts[k]&&t<ts[k+1])) return 1.0;
                return 0.0;
        }
        Double pr1,pr2;
        if (abs(ts[k + q - 1] - ts[k])<=1E-4) pr1 = 0.0;
            else pr1 = (t - ts[k]) / (ts[k + q - 1] - ts[k]) *processNs(q - 1,k,t);
        if (abs(ts[k + q] - ts[k + 1])<1E-4) pr2=0.0;
            else pr2 = (ts[q + k] - t) / (ts[k + q] - ts[k + 1]) * processNs(q - 1,k + 1,t);
        return    pr1+pr2;
    }
public void  generateNs(int k,int q){
    if (q==1) {
        ns[q][k] = t -> {
            if ((t>=ts[k]&&t<ts[k+1])) return 1.0;
            return 0.0;
        };
    }
    else {
        ns[q][k] = t -> {
            Double pr1,pr2;
           if (abs(ts[k + q - 1] - ts[k])<=1E-4) pr1 = 0.0;
            else pr1 = (t - ts[k]) / (ts[k + q - 1] - ts[k]) *processNs(q - 1,k,t);
            if (abs(ts[k + q] - ts[k + 1])<1E-4) pr2=0.0;
                else pr2 = (ts[q + k] - t) / (ts[k + q] - ts[k + 1]) * processNs(q - 1,k + 1,t);
            return    pr1+pr2;

        };
    }
    if (q<qmax-1)generateNs(k,q+1);
    if (k<ns[0].length-1&&q<qmax-1)generateNs(k+1,q);
}
    public void display(GLAutoDrawable drawable) {
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL2 gl = drawable.getGL().getGL2();
        gl.glPointSize(pointSizeBig);
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

        gl.glBegin(primitiveToGLConstant(primitive));
        for (int i = 0;i<points.size()-1;i++) {
            gl.glVertex2f(points.get(i).OpenGLX,points.get(i).OpenGLY);
        }
        gl.glEnd();
        gl.glPointSize(pointSize);
        gl.glBegin(primitiveToGLConstant(primitive));
        double sumPrev=0;
        for (int t = 0; t < maxPoints; t++) {
            double sumX=0;
            double sumY=0;
            for (int i = 0; i < ns[0].length-1; i++) {
                double nn = ns[qmax-1][i].apply(t*1.0);
                sumX += nn*points.get(i).JavaX;
                sumY += nn*points.get(i).JavaY;
            }
            //if (sumPrev<=sumX) {
                Coords coords = new Coords((float) sumX, (float) sumY, 700f, 700f);
                gl.glVertex2f(coords.OpenGLX, coords.OpenGLY);
            //}
            sumPrev=sumX;
        }

        gl.glEnd();
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
    float canvasW;
    float canvasH;
    public static void main(String[] args) {

        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(capabilities);


        Animator animator = new Animator(glcanvas);
        animator.start();

        BasicFrame frame = new BasicFrame();
        frame.genTs();
        frame.generateNs(0,0);


        glcanvas.addGLEventListener(frame);
        glcanvas.setSize(700, 700);
        final int[] draggedPointIndex = {-1};
        glcanvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                final float X=mouseEvent.getX();
                final float Y=mouseEvent.getY();
                final float w=glcanvas.getSize().width;
                final float h=glcanvas.getSize().height;
                glcanvas.invoke(false, ee -> {
                    frame.points.add(new Coords(1f * X, 1f * Y, 1f * w, 1f * h));
                    frame.regenerateAll();

                    return false;
                });
            }
            @Override
            public void mousePressed(MouseEvent mouseEvent) {}
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                glcanvas.invoke(false,(t)-> {
                    draggedPointIndex[0] = -1;
                    return false;
                });
            }
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {}
            @Override
            public void mouseExited(MouseEvent mouseEvent) {}
        });
        glcanvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                final float pointSize = frame.pointSize;
                final float pointSizeBig = frame.pointSizeBig;

                final float X=mouseEvent.getX();
                final float Y=mouseEvent.getY();
                glcanvas.invoke(false,(t)->{
                    if (draggedPointIndex[0]!=-1) {
                        frame.points.get(draggedPointIndex[0]).recountCoords(X,Y);
                        return false;
                    }
                    for (int i = 0; i < frame.points.size(); i++) {
                        if (frame.points.get(i).JavaX-pointSizeBig<X&&
                                frame.points.get(i).JavaX+pointSizeBig>X&&
                                frame.points.get(i).JavaY-pointSizeBig<Y&&
                                frame.points.get(i).JavaY+pointSizeBig>Y
                        ){
                            draggedPointIndex[0] = i;
                            break;
                        }
                    }
                    return false;
                });
            }
            @Override
            public void mouseMoved(MouseEvent mouseEvent) { }
        });


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
        setLayout(new GridLayout(13, 1));
        JComboBox<Primitive> comboBox = new JComboBox<>(Primitive.values());
        add(comboBox);
        comboBox.addItemListener(v -> {
            final Primitive primitive = (Primitive) comboBox.getSelectedItem();
            canvas.invoke(false, (e) -> {
                frame.setPrimitive(primitive);
                return false;
            });

        });
        comboBox.setSelectedItem(Primitive.GL_POINTS);



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
            final Alpha alpha = (Alpha) alphaComboBox.getSelectedItem();
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
                final Sfactor sfactor= (Sfactor) getSelectedItem();
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
                final Dfactor dfactor= (Dfactor) getSelectedItem();
                canvas.invoke(false,ee->{
                    frame.setDfactor(dfactor);
                    return false;
                });
            setSelectedItem(Dfactor.GL_ZERO);
            });
        }});
        add(new JPanel(){{
            add(new JLabel("depth"));
            add(new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8}){{
                addItemListener(e->{
                    final int depth= (int) getSelectedItem();
                    canvas.invoke(false,ee->{
                       frame.setDepth(depth);
                       return false;
                    });
                });
                setSelectedItem(1);
            }});
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
    float JavaX, JavaY, OpenGLX, OpenGLY;
    float screenX,  screenY;
    Coords(float JavaX, float JavaY, float screenX, float screenY) {
        this.JavaX = JavaX;
        this.JavaY = JavaY;
        this.screenX = screenX;
        this.screenY = screenY;
        recountCoords(JavaX,JavaY);
    }
    public void recountCoords(float JavaX, float JavaY){
        this.JavaX = JavaX;
        this.JavaY = JavaY;
        OpenGLX = 2*JavaX/screenX-1;
        OpenGLY = -2*JavaY/screenY+1;
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