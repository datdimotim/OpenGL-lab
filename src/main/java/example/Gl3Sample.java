package example;
import javax.swing.JFrame;

import com.dimotim.opengl_lab.BasicFrame;
import com.dimotim.opengl_lab.LinAl;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

public class Gl3Sample implements GLEventListener {

    ShadeProgram program=null;
    GLObject object=new GLObject(){

        @Override
        protected float[] getVertices() {
            return new float[]{
                    0,0,0,
                    0,1,0,
                    1,0,0,
                    1,1,0
            };
        }

        @Override
        protected float[] getColorArray() {
            return new float[]{
                    0,0,0,
                    0,1,0,
                    1,1,0,
                    1,0,0
            };
        }


        @Override
        protected float[] getTextureArray() {
            return new float[]{
                    0,0,
                    0,1,
                    1,1,
                    1,0
            };
        }

        @Override
        protected String getTexturePath() {
            return BasicFrame.texturePath;
        }


    };

    protected void renderScene(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);


        gl.glUseProgram(program.progId);


        object.draw(gl,program, LinAl.translate(0,0,0));

        int error = gl.glGetError();
        if (error != 0) {
            System.err.println("ERROR on render : " + error);
        }
    }



    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);


        program=new ShadeProgram(gl);
        //object=new GLObject();
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        renderScene(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    public static JFrame newJFrame(String name, GLEventListener sample, int x, int y, int width, int height) {
        JFrame frame = new JFrame(name);
        frame.setBounds(x, y, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glp);
        GLCanvas glCanvas = new GLCanvas(glCapabilities);

        glCanvas.addGLEventListener(sample);
        frame.add(glCanvas);

        return frame;
    }

    public static void main(String[] args) {
        Gl3Sample sample = new Gl3Sample();
        JFrame frame = newJFrame("JOGL3 sample with Shader", sample, 10, 10, 300, 200);
        frame.setVisible(true);
    }
}


