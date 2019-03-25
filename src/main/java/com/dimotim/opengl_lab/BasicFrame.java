package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

import static com.jogamp.opengl.GL.*;


public class BasicFrame implements GLEventListener {
    public static final String texturePath = "/texture.png";

    private int textureId;

    private  LystraHead lystraHead;
    private  TorHead torHead;
    private  TorHead torFull;
    private final Axis axis=new Axis();
    private Shader shader = null;
    private final float[] matrix = {
            1f, 0, 0, 0,
            0, 1f, 0, 0,
            0, 0, 1f, 0,
            0, 0, 0, 1f
    };

    public void setNetShow(boolean isLine) {
        if (isLine) this.isShowNet = GL2GL3.GL_LINE;
        else this.isShowNet = GL2GL3.GL_FILL;
    }


    private int isShowNet = GL2GL3.GL_LINE;

    @Setter
    private boolean isAxisShow=false;
    @Setter
    int melkost=5;
    private static float[] translate(double dx, double dy, double dz) {
        return new float[]{
                1f, 0, 0, 0,
                0, 1f, 0, 0,
                0, 0, 1f, 0,
                (float) dx, (float) dy, (float) dz, 1f
        };
    }

    private double angleX = 0;
    private double dx = 2 * Math.PI / 60 / 5;


    private float[] getAnimMatrix() {
        angleX += dx;
        return new float[]{
                1f, 0, 0, 0,
                0, (float) Math.cos(angleX), -(float) Math.sin(angleX), 0,
                0, (float) Math.sin(angleX), (float) Math.cos(angleX), 0,
                0, 0, 0, 1f
        };
    }

    public void display(GLAutoDrawable drawable) {
        init(drawable);
        System.out.println("display");
        lystraHead = new LystraHead(melkost);
        torHead = new TorHead(-Math.PI / 2, 2 * Math.PI / 3, 0, 2 * Math.PI,melkost);
        torFull = new TorHead(0, 2 * Math.PI, 0, 2 * Math.PI,melkost);
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL2 gl = drawable.getGL().getGL2();

        gl.glUseProgram(shader.shaderProgram.id());
        gl.glClearColor(0, 0, 0, 0);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        gl.glPolygonMode(GL_FRONT_AND_BACK, isShowNet);


        gl.glUniformMatrix4fv(shader.monitorMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.viewMatrixId, 1, false, getAnimMatrix(), 0);

        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, matrix, 0);
        torHead.draw(gl, shader);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, translate(0, 0, 0.1), 0);
        lystraHead.draw(gl, shader);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, translate(0, 0, -0.3), 0);
        torFull.draw(gl, shader);

        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, translate(0, 0, 0), 0);
        if(isAxisShow)axis.draw(gl,shader);

    }

    public void dispose(GLAutoDrawable arg0) {

    }

    private int c = 0;

    public void init(GLAutoDrawable glad) {
        if (c++ > 2) return;
        System.out.println("init");
        GL2 gl = glad.getGL().getGL2();
        System.out.println("GL version:" + gl.glGetString(GL.GL_VERSION));

        shader = new Shader(gl);
    }


    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }


    private static Texture loadTexture(String file) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(ImageIO.read(BasicFrame.class.getResourceAsStream(file)), "png", os);
            InputStream fis = new ByteArrayInputStream(os.toByteArray());
            return TextureIO.newTexture(fis, true, TextureIO.PNG);
        } catch (IOException e) {
            throw new RuntimeException("error load texture: file=" + file);
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

        new FPSAnimator(glcanvas, 60).start();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane(content);
        window.pack();
        window.setVisible(true);
    }


}

class ControlPanel extends JPanel {
    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(2, 1));

        add(new Checkbox("Net") {{
            addItemListener(e -> {
                final boolean state = this.getState();
                canvas.invoke(false, ee -> {
                    frame.setNetShow(state);
                    return false;
                });
            });
            this.setState(true);
        }});

        add(new Checkbox("Axes") {{
            addItemListener(e -> {
                final boolean state = this.getState();
                canvas.invoke(false, ee -> {
                    frame.setAxisShow(state);
                    return false;
                });
            });
            this.setState(false);
        }});
        add(new JComboBox <Integer>() {{
            this.addItemListener(e -> {
                final int state = Integer.parseInt(this.getSelectedItem().toString());
                canvas.invoke(false, ee -> {
                    frame.setMelkost(state);
                    return false;
                });
            });
            for (int i = 5; i <= 100; i++) {
                addItem(i);
            }
        }});
    }
}