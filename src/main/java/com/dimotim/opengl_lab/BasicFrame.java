package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;


public class BasicFrame implements GLEventListener {
    public static final String texturePath="/texture.png";

    private FloatBuffer vertexData;
    //private FloatBuffer colorData;
    //private FloatBuffer textureData;
    private ShortBuffer indexData;
    private int textureId;

    private Shader shader=null;
    private final float[] matrix={
            1f,0,0,0,
            0,1f,0,0,
            0,0,1f,0,
            0,0,0,1f
    };

    private final double angleX=Math.PI/2;
    private final float[] matrix1={
            1f,0,0,0,
            0,(float) Math.cos(angleX),-(float)Math.sin(angleX),0,
            0,(float) Math.sin(angleX), (float)Math.cos(angleX),0,
            0,0,0,1f
    };


    private final int np=10;
    private final int nt=10;
    private static final double scale=3;
    private static final double R=0.1*scale;
    private static final double r=0.2*scale;

    public void display(GLAutoDrawable drawable) {
        init(drawable);
        System.out.println("display");
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL2 gl = drawable.getGL().getGL2();

        gl.glUseProgram(shader.shaderProgram.id());
        gl.glClearColor(0,0,0,0);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        //gl.glPolygonMode(GL_FRONT_AND_BACK,GL2GL3.GL_LINE);


        gl.glUniformMatrix4fv(shader.monitorMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.viewMatrixId, 1, false, matrix1, 0);

        //gl.glVertexAttribPointer(shader.colorArrayId, 3, GL_FLOAT, false, 0, colorData.rewind());
        gl.glVertexAttribPointer(shader.vertexArrayId,3,GL_FLOAT,false,0,vertexData.rewind());
        //gl.glVertexAttribPointer(shader.textureArrayId,2,GL_FLOAT,false,0,textureData.rewind());

        gl.glDrawElements(GL_QUADS, 4*np*nt, GL_UNSIGNED_SHORT, indexData.rewind());

    }

    public void dispose(GLAutoDrawable arg0) {

    }

    int c=0;
    public void init(GLAutoDrawable glad) {
        if(c++>2)return;
        System.out.println("init");
        GL2 gl=glad.getGL().getGL2();
        System.out.println("GL version:" + gl.glGetString(GL.GL_VERSION));

        shader=new Shader(gl);

        List<ArrayList<double[]>> fig=genTor(np,nt);
        List<Integer> figIdx=genTorIdx(np,nt);

        vertexData = ByteBuffer.allocateDirect(4*3*4*np*nt).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(ArrayList<double[]> gran:fig){
            for(double[] pt:gran){
                vertexData.put((float) pt[0]).put((float) pt[1]).put((float)pt[2]);
            }
        }
        vertexData.position(0);

        indexData = ByteBuffer.allocateDirect(2*4*np*nt).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i:figIdx)indexData.put((short) i);
        indexData.position(0);

        //textureData = ByteBuffer.allocateDirect(4*2*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //textureData
        //        .put(0).put(0)
        //        .put(0).put(0.3f)
        //        .put(0.3f).put(0.3f)
        //        .position(0);

        //colorData = ByteBuffer.allocateDirect(4*3*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //colorData
        //        .put(0.8f).put(0.8f).put(0)
        //        .put(0).put(0.8f).put(0)
        //        .put(0.8f).put(0.3f).put(0)
        //        .position(0);


        gl.glActiveTexture(GL_TEXTURE0);
        textureId = loadTexture(texturePath).getTextureObject();
        gl.glBindTexture(GL_TEXTURE_2D, textureId);
        gl.glUniform1i(shader.textureId, 0);// 0- индекс текстурного блока

    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }


    private static Texture loadTexture(String file){
        try {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(ImageIO.read(BasicFrame.class.getResourceAsStream(file)), "png", os);
            InputStream fis = new ByteArrayInputStream(os.toByteArray());
            return TextureIO.newTexture(fis, true, TextureIO.PNG);
        } catch (IOException e) {
            throw new RuntimeException("error load texture: file="+file);
        }
    }

    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        final GLCanvas glcanvas = new GLCanvas(capabilities);
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

    private static double[] genPt(double p, double t){
        return new double[]{
                Math.cos(t)*r*Math.cos(p)-Math.sin(t)*R,
                Math.sin(t)*r*Math.cos(p)+Math.cos(t)*R,
                r*Math.sin(p)
        };
    }

    private static ArrayList<double[]> genGran(double p,double t, int np, int nt){
        final double dp=2*Math.PI/np;
        final double dt=2*Math.PI/nt;

        ArrayList<double[]> list=new ArrayList<>();
        list.add(genPt(p,t));
        list.add(genPt(p,t+dt));
        list.add(genPt(p+dp,t+dt));
        list.add(genPt(p+dp,t));
        return list;
    }

    private static ArrayList<ArrayList<double[]>> genTor(final int np, final int nt) {
        final double dp=2*Math.PI/np;
        final double dt=2*Math.PI/nt;
        ArrayList<ArrayList<double[]>> lists = new ArrayList<>();
        for (int i = 0; i < np; i++) {
            for (int j = 0; j < nt; j++) {
                lists.add(genGran(dp*i,dt*j,np,nt));
            }
        }
        return lists;
    }

    private static List<Integer> genTorIdx(final int np,final int nt){
        return Stream.iterate(0, i->i+1).takeWhile(i->i<np*nt*4).collect(Collectors.toList());
    }
}

class ControlPanel extends JPanel {
    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(2, 1));

        add(new JPanel(){{
            add(new Label("size"));
            add(new JComboBox<>(Stream.iterate(1, i -> i + 2).takeWhile(i -> i < 100).toArray(Integer[]::new)){{
                addItemListener(e->{
                    canvas.invoke(false, ee->{
                        //frame.makeKernel(frame.getSigm(),(int)getSelectedItem());
                        return false;
                    });
                });
                setSelectedItem(11);
            }});
        }});

        add(new JPanel(){{
            add(new Label("disp"));
            add(new JSlider(1,100,1){{
                addChangeListener(e->{
                    canvas.invoke(false,ee->{
                        //frame.makeKernel(BasicFrame.MAX_SIGM*getValue()/getMaximum(),frame.getSize());
                        return false;
                    });
                });
                setValue(10);
            }});
        }});
        /*slider.addChangeListener(e -> {
            final int val = slider.getValue();
            canvas.invoke(false, ee -> {
                frame.points.get(finalI).weigh = val;
                return false;
            });
        });*/

    }
}