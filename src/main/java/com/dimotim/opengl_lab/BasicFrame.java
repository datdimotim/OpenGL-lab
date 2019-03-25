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

    private LystraHead lystraHead;
    private TorHead torHead;
    private TorHead torFull;
    private final Axis axis = new Axis();
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
    private boolean isAxisShow = false;
    @Setter
    int melkost = 5;
    @Setter
    private float red = 0;
    @Setter
    private float green = 0;
    @Setter
    private float blue = 0;

    private float dtx=0.01f;
    private float dty=0.01f;
    private float dtz = 0.01f;
    private float scaleX = 1;
    private float scaleY = 1;
    private float scaleZ = 1;
    public void increaseScaleX(){
        scaleX+=0.1;
    }
    public void increaseScaleY(){
        scaleY+=0.1;
    }
    public void increaseScaleZ(){
        scaleZ+=0.1;
    }
    public void decreaseScaleX(){
        if (scaleX<=0.2) return;
        scaleX-=0.1;
    }
    public void decreaseScaleY(){
        if (scaleY<=0.5) return;
        scaleY-=0.1;
    }
    public void decreaseScaleZ(){
        if (scaleZ<=0.5)return;
        scaleZ-=0.1;
    }
    public void increaseDtx(){
        dtx+=0.01;
    }
    public void increaseDty(){
        dty+=0.01;
    }
    public void increaseDtz(){
        dtz+=0.01;
    }

    public void decreaseDtx(){
        if (dtx<=-0.9) return;
        dtx-=0.01;
    }
    public void decreaseDty(){
        if (dty<=-0.9) return;
        dty-=0.01;
    }
    public void decreaseDtz(){
        if (dty<=-0.9) return;
        dtz-=0.01;
    }
    private static float[] translate(double dx, double dy, double dz) {
        return new float[]{
                1f, 0, 0, 0,
                0, 1f, 0, 0,
                0, 0, 1f, 0,
                (float) dx, (float) dy, (float) dz, 1f
        };
    }

    private static float[] translateFull(double dx, double dy, double dz) {
        return new float[]{
                1f, 0, 0, 0,
                0, 1f, 0, 0,
                0, 0, 1f, 0,
                (float) dx, (float) dy, (float) dz, 1f
        };
    }
    private static float[] scaleFull(double dx, double dy, double dz) {
        return new float[]{
                (float) dx, 0, 0, 0,
                0, (float) dy, 0, 0,
                0, 0,(float) dz, 0,
                0, 0, 0, 1f
        };
    }

    private double angleX = 0;
    private double angleY = 0;
    private double angleZ = 0;

    private final double  dangle = 2 * Math.PI / 60 / 5;

    public void increaseAngleX(){
        angleX+=dangle;
    }
    public void increaseAngleY(){
        angleY+=dangle;
    }
    public void increaseAngleZ(){
        angleZ+=dangle;
    }
    private float[] getXAnimMatrix() {
        return new float[]{
                1f, 0, 0, 0,
                0, (float) Math.cos(angleX), -(float) Math.sin(angleX), 0,
                0, (float) Math.sin(angleX), (float) Math.cos(angleX), 0,
                0, 0, 0, 1f
        };
    }
    private float[] getYAnimMatrix() {
        return new float[]{
                (float) Math.cos(angleY),  -(float) Math.sin(angleY), 0, 0,
                (float) Math.sin(angleY), (float) Math.cos(angleY),0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1f
        };
    }
    private float[] getZAnimMatrix() {
        return new float[]{
                (float) Math.cos(angleZ),  0, -(float) Math.sin(angleZ), 0,
                0, 1,0, 0,
                -(float) Math.sin(angleZ), 0, (float) Math.cos(angleZ), 0,
                0, 0, 0, 1f
        };
    }

    public void display(GLAutoDrawable drawable) {
        init(drawable);
        lystraHead = new LystraHead(melkost);
        torHead = new TorHead(-Math.PI / 2, 2 * Math.PI / 3, 0, 2 * Math.PI, melkost);
        torFull = new TorHead(0, 2 * Math.PI, 0, 2 * Math.PI, melkost);
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
        gl.glUniformMatrix4fv(shader.viewMatrixId, 1, false,
                LinAl.matrixMul(scaleFull(scaleX,scaleY,scaleZ),LinAl.matrixMul(LinAl.matrixMul(getXAnimMatrix(),getYAnimMatrix()),getZAnimMatrix())), 0);
        gl.glUniform1f(shader.intensivnost_blue_Id, blue);
        gl.glUniform1f(shader.intensivnost_red_Id, red);
        gl.glUniform1f(shader.intensivnost_green_Id, green);

        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, translateFull(dtx,dty,dtz), 0);
        torHead.draw(gl, shader);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, LinAl.matrixMul(translate(0, 0, 0.1),translateFull(dtx,dty,dtz)), 0);
        lystraHead.draw(gl, shader);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, LinAl.matrixMul(translate(0, 0, -0.3),translateFull(dtx,dty,dtz)), 0);
        torFull.draw(gl, shader);

        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false,matrix, 0);
        if (isAxisShow) axis.draw(gl, shader);

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

        setLayout(new GridLayout(15, 1));

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
        add(new JComboBox<Integer>() {{
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

        add(new JPanel() {{
            add(new JLabel("Red"));
            add(new JSlider(0, 100,0) {{
                this.addChangeListener(e -> {
                    float red = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setRed(red / 100);
                        return false;
                    });
                });
            }});
        }});

        add(new JPanel() {{

            add(new JLabel("Green"));
            add(new JSlider(0, 100,0) {{
                this.addChangeListener(e -> {
                    float green = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setGreen(green / 100);
                        return false;
                    });
                });
            }});
        }});

        add(new JPanel() {{

            add(new JLabel("Blue"));
            add(new JSlider(0, 100,0) {{
                this.addChangeListener(e -> {
                    float blue = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setBlue(blue / 100);
                        return false;
                    });
                });
            }});
        }});
        add(new JPanel() {{
            add(new JButton("←"){{
                 this.addChangeListener(e->{
                     canvas.invoke(false, ee -> {
                         frame.decreaseDtx();
                         return false;
                     });
                 });
            }});
            add(new JButton("→"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseDtx();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("↓"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseDty();
                        return false;
                    });
                });
            }});
            add(new JButton("↑"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseDty();
                        return false;
                    });
                });
            }});

            add (new JLabel("      "));
            add(new JButton("\u2BBF"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseDtz();
                        return false;
                    });
                });
            }});
            add(new JButton("⬝"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseDtz();
                        return false;
                    });
                });
            }});
        }});

        add(new JPanel() {{
            add(new JButton("Rotate X"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseAngleX();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("Rotate Y"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseAngleY();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("Rotate Z"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseAngleZ();
                        return false;
                    });
                });
            }});

        }});

        add(new JPanel() {{
            add(new JButton("x+"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseScaleX();
                        return false;
                    });
                });
            }});
            add(new JButton("x-"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseScaleX();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("y+"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseScaleY();
                        return false;
                    });
                });
            }});
            add(new JButton("y-"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseScaleY();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("z+"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseScaleZ();
                        return false;
                    });
                });
            }});
            add(new JButton("z-"){{
                this.addChangeListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseScaleZ();
                        return false;
                    });
                });
            }});
        }});


    }
}