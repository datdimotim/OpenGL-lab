package com.dimotim.opengl_lab;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.jogamp.opengl.GL.*;


class Shader{
    public static final String vertexShaderPath="/shader.vert.glsl";
    public static final String fragmentShaderPath="/shader.frag.glsl";

    public static final String MONITOR_MATRIX="u_Monitor_Matrix";
    public static final String VIEW_MATRIX="u_Matrix";
    public static final String MODEL_MATRIX="u_Model_Matrix";
    public static final String COLOR_ARRAY="a_Color";
    public static final String VERTEX_ARRAY="a_Position";
    //public static final String TEXTURE_ARRAY="a_Tex_Coord";
    //public static final String TEXTURE="u_texture";

    public final ShaderProgram shaderProgram;
    public final int monitorMatrixId;
    public final int viewMatrixId;
    public final int modelMatrixId;
    public final int colorArrayId;
    public final int vertexArrayId;
   //public final int textureArrayId;
    //public final int textureId;

    public Shader(GL2 gl){
        final String vertexShaderText=FileUtils.readTextFromRaw(vertexShaderPath);
        final ShaderCode vertexShader=new ShaderCode(GL2ES2.GL_VERTEX_SHADER,1,new CharSequence[][]{{vertexShaderText}});
        vertexShader.compile(gl);
        if(!vertexShader.isValid())throw new RuntimeException("vertex shader is not valid, source=\n"+vertexShaderText);

        final String fragmentShaderText=FileUtils.readTextFromRaw(fragmentShaderPath);
        final ShaderCode fragmentShader=new ShaderCode(GL2ES2.GL_FRAGMENT_SHADER,1,new CharSequence[][]{{fragmentShaderText}});
        fragmentShader.compile(gl);
        if(!fragmentShader.isValid())throw new RuntimeException("fragment shader is not valid, source=\n"+fragmentShaderText);

        shaderProgram=new ShaderProgram();
        shaderProgram.add(gl,vertexShader,System.out);
        shaderProgram.add(gl,fragmentShader,System.out);
        shaderProgram.link(gl,System.out);
        if(!shaderProgram.linked())throw new RuntimeException("ShaderProgram is not linked");
        shaderProgram.useProgram(gl,true);

        //////////////////////////////////////////
        //////////////////////////////////////////


        viewMatrixId = gl.glGetUniformLocation(shaderProgram.id(), VIEW_MATRIX);
        monitorMatrixId =gl.glGetUniformLocation(shaderProgram.id(), MONITOR_MATRIX);
        //textureId = gl.glGetUniformLocation(shaderProgramId, TEXTURE);
        modelMatrixId =gl.glGetUniformLocation(shaderProgram.id(), MODEL_MATRIX);
        colorArrayId = gl.glGetAttribLocation(shaderProgram.id(), COLOR_ARRAY);
        vertexArrayId = gl.glGetAttribLocation(shaderProgram.id(), VERTEX_ARRAY);
        //textureArrayId=gl.glGetAttribLocation(shaderProgramId, TEXTURE_ARRAY);
        gl.glEnableVertexAttribArray(colorArrayId);
        //gl.glEnableVertexAttribArray(textureArrayId);
        gl.glEnableVertexAttribArray(vertexArrayId);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);           // enable vertex arrays
    }
}

public class BasicFrame implements GLEventListener {
    private FloatBuffer vertexData;
    private FloatBuffer colorData;
    private int indexId;

    private Shader shader=null;
    private final float[] matrix={
            10,0,0,0,
            0,10,0,0,
            0,0,10,0,
            0,0,0,1
    };

    public void display(GLAutoDrawable drawable) {
        final int width = drawable.getSurfaceWidth();
        final int heght = drawable.getSurfaceHeight();
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        gl.glUniformMatrix4fv(shader.monitorMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, matrix, 0);
        gl.glUniformMatrix4fv(shader.viewMatrixId, 1, false, matrix, 0);

        gl.glVertexAttribPointer(shader.colorArrayId, 3, GL_FLOAT, false, 0, colorData);
        gl.glVertexPointer(2, GL_FLOAT,0, vertexData);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);

        gl.glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, 0);


        gl.glFlush();
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

        colorData = ByteBuffer.allocateDirect(4*3*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorData
                .put(0.8f).put(0.8f).put(0)
                .put(0).put(0.8f).put(0)
                .put(0.8f).put(0.8f).put(0)
                .position(0);

        final int[] a=new int[1];
        gl.glGenBuffers(1, a, 0);
        indexId = a[0];

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData.capacity() * 2, indexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
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
        final int[] draggedPointIndex = {-1};

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
        setLayout(new GridLayout(1, 1));

        /*slider.addChangeListener(e -> {
            final int val = slider.getValue();
            canvas.invoke(false, ee -> {
                frame.points.get(finalI).weigh = val;
                return false;
            });
        });*/

    }
}