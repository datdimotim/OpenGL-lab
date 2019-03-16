package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jogamp.opengl.GL.*;


public class BasicFrame implements GLEventListener {
    public static final String texturePath="/texture.png";

    private FloatBuffer vertexData;
    private FloatBuffer colorData;
    private FloatBuffer textureData;
    private FloatBuffer kernel;
    private int indexId;
    private int textureId;
    private int size=21;
    private float sigm=100;

    private Shader shader=null;
    private final float[] matrix={
            1f,0,0,0,
            0,1f,0,0,
            0,0,1f,0,
            0,0,0,1f
    };

    public void display(GLAutoDrawable drawable) {init(drawable);
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0,0,0,0);


        gl.glUniformMatrix4fv(shader.monitorMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.viewMatrixId, 1, false, matrix, 0);



        gl.glUniform1fv(shader.kernelId,4*kernel.capacity(),kernel);
        gl.glUniform1i(shader.kernelSizeId,size);

        gl.glVertexAttribPointer(shader.colorArrayId, 3, GL_FLOAT, false, 0, colorData);
        gl.glVertexAttribPointer(shader.vertexArrayId,2,GL_FLOAT,false,0,vertexData);
        gl.glVertexAttribPointer(shader.textureArrayId,2,GL_FLOAT,false,0,textureData);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);

        gl.glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, 0);
    }

    public void dispose(GLAutoDrawable arg0) {

    }

    public void init(GLAutoDrawable glad) {
        GL2 gl=glad.getGL().getGL2();

        shader=new Shader(gl);


        ShortBuffer indexData = ByteBuffer.allocateDirect(2*3).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexData.put((short) 0).put((short) 1).put((short) 2).position(0);

        vertexData = ByteBuffer.allocateDirect(4*2*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData
                .put(-0.8f).put(-0.8f)
                .put(0).put(0.8f)
                .put(0.8f).put(-0.8f)
                .position(0);

        textureData = ByteBuffer.allocateDirect(4*2*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureData
                .put(0).put(0)
                .put(0).put(0.3f)
                .put(0.3f).put(0.3f)
                .position(0);

        colorData = ByteBuffer.allocateDirect(4*3*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorData
                .put(0.8f).put(0.8f).put(0)
                .put(0).put(0.8f).put(0)
                .put(0.8f).put(0.3f).put(0)
                .position(0);

        makeKernel(sigm,size);

        final int[] a=new int[1];
        gl.glGenBuffers(1, a, 0);
        indexId = a[0];

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData.capacity() * 2, indexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);


        gl.glActiveTexture(GL_TEXTURE0);
        textureId = loadTexture(texturePath).getTextureObject();
        gl.glBindTexture(GL_TEXTURE_2D, textureId);
        gl.glUniform1i(shader.textureId, 0);// 0- индекс текстурного блока
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }

    public int getSize(){
        return size;
    }

    public float getSigm(){
        return (float) sigm;
    }

    public void makeKernel(float sigm,int size){
        this.sigm=sigm;
        this.size=size;
        List<Float> coeff=Stream.iterate(-(size-1)/2, i->i+1).takeWhile(i->i<=(size-1)/2)
                .map(i->(float)(Math.pow(2*Math.PI*sigm,-0.25)*Math.exp(-i*i/2.0/sigm/sigm)))
                .collect(Collectors.toList());
        double sum=0;
        for (double i:coeff){
            for (double j:coeff){
                sum+=i*j;
            }
        }
        final double norm=sum;
        kernel=ByteBuffer.allocateDirect(size*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        coeff.forEach(f->kernel.put((float)(f/Math.sqrt(norm))));
        kernel.position(0);
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
}

class ControlPanel extends JPanel {
    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(2, 1));

        add(new JPanel(){{
            add(new Label("size"));
            add(new JComboBox<>(Stream.iterate(1, i -> i + 2).takeWhile(i -> i < 100).toArray(Integer[]::new)){{
                setSelectedItem(1);
                addItemListener(e->{
                    canvas.invoke(false, ee->{
                        frame.makeKernel(frame.getSigm(),(int)getSelectedItem());
                        return false;
                    });
                });
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