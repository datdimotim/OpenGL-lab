package com.dimotim.opengl_lab;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import static com.jogamp.opengl.GL2.*;

public class BasicFrame implements GLEventListener {
    private static final int POINT_SIZE=20;
    private static final float tMin=0;
    private static final float tMax=1;
    private final ArrayList<float[]> pts=new ArrayList<>(){{
        add(new float[]{-0.7f,-0.7f});
        add(new float[]{-0.6f,-0.3f});
        add(new float[]{-0.5f,-0.2f});
        add(new float[]{-0.2f,-0.1f});
        add(new float[]{0.0f,-0.0f});
        add(new float[]{0.1f,0.7f});
        add(new float[]{0.2f,-0.3f});
        //add(new float[]{0.4f,-0.5f});
    }};
    private int draggedPointIndex=-1;

    private static float pixelsToCoords(int pixels, int size){
        return 1.0f*pixels/size;
    }

    private static void drawPoint(float[] pt, int canvasWidth, int canvasHeight, GL2 gl){
        final float ptHeight=pixelsToCoords(POINT_SIZE/2,canvasHeight);
        final float ptWidth=pixelsToCoords(POINT_SIZE/2,canvasWidth);
        gl.glBegin(GL_QUADS);
            gl.glVertex2f(pt[0]-ptWidth,pt[1]-ptHeight);
            gl.glVertex2f(pt[0]-ptWidth,pt[1]+ptHeight);
            gl.glVertex2f(pt[0]+ptWidth,pt[1]+ptHeight);
            gl.glVertex2f(pt[0]+ptWidth,pt[1]-ptHeight);
        gl.glEnd();
    }

    private static boolean isPointOwner(float[] pt, float x, float y, int canvasHeight, int canvasWidth){
        final float ptHeight=pixelsToCoords(POINT_SIZE/2,canvasHeight);
        final float ptWidth=pixelsToCoords(POINT_SIZE/2,canvasWidth);
        return x>=pt[0]-ptWidth && x<=pt[0]+ptWidth && y>=pt[1]-ptHeight && y<= pt[1]+ptHeight;
    }

    private int getOwnerIndex(float x, float y, int canvasHeight, int canvasWidth){
        for (int i=0;i<pts.size();i++)if(isPointOwner(pts.get(i),x,y,canvasHeight,canvasWidth))return i;
        return -1;
    }

    private static float[] genTk(float tMin, float tMax, int count){
        float step=(tMax-tMin)/(count-1);
        float[] tk=new float[count];
        for(int i=0;i<count;i++)tk[i]=tMin+step*i;
        return tk;
    }

    private static Function<Float,Float> genNqk(int q, int k, float[] tk){
        if(q==1)return t->tk[k]<=t&&t<tk[k+1]?1.0f:0.0f;
        else return t-> {
            float first=(t-tk[k])/(tk[k+q-1]-tk[k])*genNqk(q-1,k,tk).apply(t);
            float second=(tk[k+q]-t)/(tk[k+q]-tk[k+1])*genNqk(q-1,k+1,tk).apply(t);
            return first+second;
        };
    }

    private static Function<Float, float[]> genSpline(int q, ArrayList<float[]> pts, float tMin, float tMax){
        final int n=pts.size();
        float[] tk=genTk(tMin,tMax,n+q);
        Function<Float,Float> nqks[]=new Function[n];
        for(int i=0;i<nqks.length;i++){
            nqks[i]=genNqk(q,i,tk);
        }
        return t->{
            float[] f=new float[]{0,0};
            for(int i=0;i<nqks.length;i++){
                f[0]+=nqks[i].apply(t)*pts.get(i)[0];
                f[1]+=nqks[i].apply(t)*pts.get(i)[1];
            }
            return f;
        };
    }

    private void drawSpline(GL2 gl){
        Function<Float,float[]> spline=genSpline(4,pts,tMin,tMax);
        final int count=500;
        gl.glBegin(GL_LINE_STRIP);
            for(int i=100;i<count-100;i++){
                float[] v=spline.apply(tMin+i*(tMax-tMin)/(count-1));
                gl.glVertex2f(v[0],v[1]);
            }
        gl.glEnd();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
    }

    public void dispose(GLAutoDrawable arg0) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl=glAutoDrawable.getGL().getGL2();
        final int height=glAutoDrawable.getSurfaceHeight();
        final int width=glAutoDrawable.getSurfaceWidth();
        final float ptHeight=pixelsToCoords(POINT_SIZE/2,height);
        final float ptWidth=pixelsToCoords(POINT_SIZE/2,width);

        gl.glClear(GL_COLOR_BUFFER_BIT);


        pts.forEach(p->drawPoint(p,width,height,gl));
        drawSpline(gl);


        gl.glFlush();
    }

    public void mousePressed(float x, float y, int width, int height){
        draggedPointIndex=getOwnerIndex(x,y,height,width);
        if(draggedPointIndex==-1);// pts.add(new float[]{x,y});
        System.out.println("click into "+draggedPointIndex);
    }

    public void mouseReleased(float x, float y, int width, int height){
        System.out.println("release");
        draggedPointIndex=-1;
    }

    public void mouseDragged(float x, float y, int width, int height){
        if(draggedPointIndex==-1) draggedPointIndex=getOwnerIndex(x,y,height,width);
        if(draggedPointIndex==-1)return;
        float[] pt=pts.get(draggedPointIndex);
        pt[0]=x;
        pt[1]=y;
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
        glcanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                final float x = mouseEvent.getX();
                final float y = mouseEvent.getY();
                final float w = glcanvas.getSize().width;
                final float h = glcanvas.getSize().height;
                final float glX=2*x/w-1f;
                final float glY=-2*y/h+1f;
                glcanvas.invoke(false, ee -> {
                    frame.mousePressed(glX,glY,(int) w,(int) h);
                    return false;
                });
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                final float x = mouseEvent.getX();
                final float y = mouseEvent.getY();
                final float w = glcanvas.getSize().width;
                final float h = glcanvas.getSize().height;
                final float glX=2*x/w-1f;
                final float glY=-2*y/h+1f;
                glcanvas.invoke(false, ee -> {
                    frame.mouseReleased(glX,glY,(int) w,(int) h);
                    return false;
                });
            }
        });
        glcanvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                final float x = mouseEvent.getX();
                final float y = mouseEvent.getY();
                final float w = glcanvas.getSize().width;
                final float h = glcanvas.getSize().height;
                final float glX=2*x/w-1f;
                final float glY=-2*y/h+1f;
                glcanvas.invoke(false, ee -> {
                    frame.mouseDragged(glX,glY,(int)w, (int)h);
                    return false;
                });
            }
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
}

class ControlPanel extends JPanel {
    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(13, 1));

    }
}