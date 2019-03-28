package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private float ox=1,oy=0, oz=0, oux=0, ouy=1,ouz=0;

    public void setOx(float ox) {
        this.ox = ox;
        changeObserverMatrix(new double[]{ox,oy,oz}, new double[]{oux,ouy, ouz});
    }

    public void setOy(float oy) {
        this.oy = oy;
        changeObserverMatrix(new double[]{ox,oy,oz}, new double[]{oux,ouy, ouz});
    }

    public void setOz(float oz) {
        this.oz = oz;
        changeObserverMatrix(new double[]{ox,oy,oz}, new double[]{oux,ouy, ouz});
    }

    public void setOux(float oux) {
        this.oux = oux;
        changeObserverMatrix(new double[]{ox,oy,oz}, new double[]{oux,ouy, ouz});
    }

    public void setOuy(float ouy) {
        this.ouy = ouy;
        changeObserverMatrix(new double[]{ox,oy,oz}, new double[]{oux,ouy, ouz});
    }

    public void setOuz(float ouz) {
        this.ouz = ouz;
        changeObserverMatrix(new double[]{ox,oy,oz}, new double[]{oux,ouy, ouz});
    }

    // Первый-куда, второй - координата верха
    private final float[] matrix = {
            1f, 0, 0, 0,
            0, 1f, 0, 0,
            0, 0, 1f, 0,
            0, 0, 0, 1f
    };
    public void changeObserverMatrix(double[] where, double [] up){
        float normWhere = (float) Math.sqrt(Math.pow(where[0],2)+Math.pow(where[1],2)+Math.pow(where[2],2));
        float normUp = (float) Math.sqrt(Math.pow(up[0],2)+Math.pow(up[1],2)+Math.pow(up[2],2));
        for (int i = 0; i < up.length; i++) {
            up[i]=up[i]/normUp;
            where[i]=where[i]/normWhere;
        }
        for (int i = 0; i < where.length; i++) {
            matrix[i*4] = (float) where[i];
        }
        matrix[12]=0;
        for (int i = 0; i < up.length; i++) {
            matrix[i*4+1] = (float) up[i];
        }
        matrix[13]=0;
        double[] vecMul = LinAl.vecMulNormal(where,up);
        for (int i = 0; i < vecMul.length; i++) {
            matrix[i*4+2] = (float)vecMul[i];
        }
        matrix[14]=0;
        for (int i = 0; i < 3; i++) {
            matrix[i*4+3]=0;
        }
        matrix[15]=1;
/*
        for (int i = 0; i < where.length; i++) {
            matrix[i] = (float)where[i];
        }
        matrix[3]=0;
        for (int i = 0; i < up.length; i++) {
            matrix[i+4] = (float) up[i];
        }
        matrix[7]=0;
        double[] vecMul = LinAl.vecMulNormal(where,up);
        for (int i = 0; i < vecMul.length; i++) {
            matrix[i+8] = (float)vecMul[i];
        }
        for (int i = 0; i < 4; i++) {
            matrix[i+12]=0;
        }
        matrix[15]=1;*/
    }
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
        scaleZ-=0.3;
    }
    public void increaseDtx(){
        dtx+=0.03;
    }
    public void increaseDty(){
        dty+=0.03;
    }
    public void increaseDtz(){
        dtz+=0.03;
    }

    public void decreaseDtx(){
        if (dtx<=-0.9) return;
        dtx-=0.03;
    }
    public void decreaseDty(){
        if (dty<=-0.9) return;
        dty-=0.03;
    }
    public void decreaseDtz(){
        if (dty<=-0.9) return;
        dtz-=0.03;
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
    float[] mouseMatrix = new float[]{
            (float) 1, 0, 0, 0,
            0, (float) 1, 0, 0,
            0, 0,(float) 1, 0,
            0, 0, 0, 1f
    };
    float[] mouseMatrixSaved = new float[]{
            (float) 1, 0, 0, 0,
            0, (float) 1, 0, 0,
            0, 0,(float) 1, 0,
            0, 0, 0, 1f
    };
    private   float [] getMouseMatrixToMul(float angleMatrix,float x, float y){
       float []m= new float[]{
               (float) (Math.cos(angleMatrix)+(1-Math.cos(angleMatrix))*x*x),(float) (1-Math.cos(angleMatrix))*x*y,(float) (y*Math.sin(angleMatrix)), 0,
               (float) (1-Math.cos(angleMatrix))*y*x,(float) (Math.cos(angleMatrix)+(1-Math.cos(angleMatrix))*y*y),(float)-Math.sin(angleMatrix)*x,0,
                -(float)Math.sin(angleMatrix)*y,(float) Math.sin(angleMatrix)*x,(float) Math.cos(angleMatrix),0,
                0,0,0,1
        };
        return m;
    }
    public void changeMouseMatrix(float angleMatrix,float x, float y){
        mouseMatrix=LinAl.matrixMul(mouseMatrixSaved,getMouseMatrixToMul(angleMatrix,x,y));
    }
    public void setMatrixAfterDrop(){
        for (int i = 0; i < mouseMatrix.length; i++) {
           mouseMatrixSaved[i]=mouseMatrix[i];
        }
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
                LinAl.matrixMul(LinAl.matrixMul(scaleFull(scaleX,scaleY,scaleZ),LinAl.matrixMul(LinAl.matrixMul(getXAnimMatrix(),getYAnimMatrix()),getZAnimMatrix())),
                        mouseMatrix), 0);
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
    private float [] getXYNormalised(float x, float y){
        float norma = (float) Math.sqrt(x*x+y*y);
        x=x/norma;
        y=y/norma;

        float t=x;
        x=y;
        y=t;
        return new float[]{x,y};
    }
    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(15, 1));

        MouseAdapter adapter=new MouseAdapter() {
            private int xStart;
            private int yStart;
            @Override
            public void mousePressed(MouseEvent e){
                System.out.println("p");
                xStart=e.getX();
                yStart=e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                float xD = e.getX()-xStart;
                float yD = e.getY()-yStart;
                float vectorSize = (float) Math.sqrt(xD*xD+yD*yD);
                float[] xy  = getXYNormalised(xD,yD);

                canvas.invoke(false, ee -> {
                    frame.changeMouseMatrix((float) Math.PI*vectorSize/300
                            ,xy[0],xy[1]);
                    return false;
                });
            }
            @Override
            public void mouseReleased(MouseEvent e){
                canvas.invoke(false, ee -> {
                    frame.setMatrixAfterDrop();
                    return false;
                });
            }
        };

        canvas.addMouseListener(adapter);
        canvas.addMouseMotionListener(adapter);

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
                 this.addActionListener(e->{
                     canvas.invoke(false, ee -> {
                         frame.decreaseDtx();
                         return false;
                     });
                 });
            }});
            add(new JButton("→"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseDtx();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("↓"){{
                   this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseDty();
                        return false;
                    });
                });
            }});
            add(new JButton("↑"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseDty();
                        return false;
                    });
                });
            }});

            add (new JLabel("      "));
            add(new JButton("\u2BBF"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseDtz();
                        return false;
                    });
                });
            }});
            add(new JButton("⬝"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseDtz();
                        return false;
                    });
                });
            }});
        }});

        add(new JPanel() {{
            add(new JButton("Rotate X"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseAngleX();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("Rotate Y"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseAngleY();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("Rotate Z"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseAngleZ();
                        return false;
                    });
                });
            }});

        }});

        add(new JPanel() {{
            add(new JButton("x+"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseScaleX();
                        return false;
                    });
                });
            }});
            add(new JButton("x-"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseScaleX();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("y+"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseScaleY();
                        return false;
                    });
                });
            }});
            add(new JButton("y-"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseScaleY();
                        return false;
                    });
                });
            }});
            add (new JLabel("      "));
            add(new JButton("z+"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.increaseScaleZ();
                        return false;
                    });
                });
            }});
            add(new JButton("z-"){{
                this.addActionListener(e->{
                    canvas.invoke(false, ee -> {
                        frame.decreaseScaleZ();
                        return false;
                    });
                });
            }});
        }});

        add(new JLabel("Наблюдатель смотрит:"){{ }});

        add(new JPanel() {{

            add(new JLabel("x"));
            add(new JSlider(-100, 100,100) {{
                this.addChangeListener(e -> {
                    float value = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setOx(value / 100);
                        return false;
                    });
                });
                setValue(100);
            }});

            add(new JLabel("y"));
            add(new JSlider(-100, 100,0) {{
                this.addChangeListener(e -> {
                    float value = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setOy(value / 100);
                        return false;
                    });
                });
                setValue(0);
            }});

            add(new JLabel("z"));
            add(new JSlider(-100, 100,0) {{
                this.addChangeListener(e -> {
                    float value = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setOz(value / 100);
                        return false;
                    });
                });
                setValue(0);
            }});
        }});

        add(new JLabel("Вверх наблюдателя:"){{ }});

        add(new JPanel() {{

            add(new JLabel("x"));
            add(new JSlider(-100, 100,0) {{
                this.addChangeListener(e -> {
                    float value = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setOux(value / 100);
                        return false;
                    });
                });
                setValue(0);
            }});

            add(new JLabel("y"));
            add(new JSlider(-100, 100,100) {{
                this.addChangeListener(e -> {
                    float value = this.getValue();
                    canvas.invoke(false, ee -> {
                        frame.setOuy(value / 100);
                        return false;
                    });
                });
                setValue(100);
            }});

        add(new JLabel("z"));
        add(new JSlider(-100, 100,0) {{
            this.addChangeListener(e -> {
                float value = this.getValue();
                canvas.invoke(false, ee -> {
                    frame.setOuz(value / 100);
                    return false;
                });
            });
            setValue(0);
        }});

    }});

    }
}