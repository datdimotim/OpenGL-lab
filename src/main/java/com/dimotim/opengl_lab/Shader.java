package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import lombok.Data;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL2ES2.GL_RED;
import static com.jogamp.opengl.GL2ES3.*;
import static com.jogamp.opengl.GL2GL3.GL_TEXTURE_SWIZZLE_RGBA;

@Data
public class Shader{
    public static final String vertexShaderPath="/shader.vert.glsl";
    public static final String fragmentShaderPath="/shader.frag.glsl";

    public static final String MONITOR_MATRIX="u_Monitor_Matrix";
    public static final String VIEW_MATRIX="u_Matrix";
    public static final String MODEL_MATRIX="u_Model_Matrix";
    public static final String COLOR_ARRAY="a_Color";
    public static final String VERTEX_ARRAY="a_Position";
    public static final String TEXTURE_ARRAY="a_Tex_Coord";
    public static final String TEXTURE="u_texture";
    public static final String NORMAL="a_normal";
    public static final String INTENSIVNOST_BLUE = "u_iblue";
    public static final String INTENSIVNOST_RED = "u_ired";
    public static final String INTENSIVNOST_GREEN = "u_igreen";

    public final ShaderProgram shaderProgram;
    public final int monitorMatrixId;
    public final int viewMatrixId;
    public final int modelMatrixId;
    public final int colorArrayId;
    public final int vertexArrayId;
    public final int textureArrayId;
    public final int textureId;
    public final int normalId;
    public final int intensivnost_blue_Id;
    public final int intensivnost_green_Id;
    public final int intensivnost_red_Id;

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
        shaderProgram.init(gl);
        shaderProgram.link(gl,System.out);
        if(!shaderProgram.linked())throw new RuntimeException("ShaderProgram is not linked");
        //shaderProgram.useProgram(gl,true);
        //////////////////////////////////////////
        //////////////////////////////////////////

        viewMatrixId = gl.glGetUniformLocation(shaderProgram.id(), VIEW_MATRIX);
        monitorMatrixId =gl.glGetUniformLocation(shaderProgram.id(), MONITOR_MATRIX);
        textureId = gl.glGetUniformLocation(shaderProgram.id(), TEXTURE);
        modelMatrixId =gl.glGetUniformLocation(shaderProgram.id(), MODEL_MATRIX);
        colorArrayId = gl.glGetAttribLocation(shaderProgram.id(), COLOR_ARRAY);
        vertexArrayId = gl.glGetAttribLocation(shaderProgram.id(), VERTEX_ARRAY);
        textureArrayId=gl.glGetAttribLocation(shaderProgram.id(), TEXTURE_ARRAY);
        normalId=gl.glGetAttribLocation(shaderProgram.id(),NORMAL);
        intensivnost_blue_Id = gl.glGetUniformLocation(shaderProgram.id(),INTENSIVNOST_BLUE);
        intensivnost_green_Id = gl.glGetUniformLocation(shaderProgram.id(),INTENSIVNOST_GREEN);
        intensivnost_red_Id = gl.glGetUniformLocation(shaderProgram.id(),INTENSIVNOST_RED);

        gl.glEnableVertexAttribArray(colorArrayId);
        gl.glEnableVertexAttribArray(textureArrayId);
        gl.glEnableVertexAttribArray(vertexArrayId);
        gl.glEnableVertexAttribArray(normalId);

        System.out.println(this);
    }

    public static void loadTexture(String file, GL3 gl) {
        try {
            URL texture=BasicFrame.class.getClassLoader().getResource(file);
            TextureData data=TextureIO.newTextureData(gl.getGLProfile(),texture,false,TextureIO.PNG);
            int level=0;
            int[] a=new int[1];
            gl.glGenTextures(1,a,0);
            final int textureName=a[0];
            System.out.println(textureName);
            gl.glBindTexture(GL_TEXTURE_2D,textureName);
            {
                gl.glTexImage2D(GL_TEXTURE_2D,
                        level,
                        data.getInternalFormat(),
                        data.getWidth(), data.getHeight(),
                        data.getBorder(),
                        data.getPixelFormat(), data.getPixelType(),
                        data.getBuffer());

                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, level);

                IntBuffer swizzle = GLBuffers.newDirectIntBuffer(new int[]{GL_RED, GL_GREEN, GL_BLUE, GL_ONE});
                gl.glTexParameterIiv(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_RGBA, swizzle);

            }
            gl.glBindTexture(GL_TEXTURE_2D, textureName);

        } catch (IOException e) {
            throw new RuntimeException("error load texture: file=" + file);
        }
    }
}
