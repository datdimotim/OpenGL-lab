package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

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

    public final ShaderProgram shaderProgram;
    public final int monitorMatrixId;
    public final int viewMatrixId;
    public final int modelMatrixId;
    public final int colorArrayId;
    public final int vertexArrayId;
    public final int textureArrayId;
    public final int textureId;

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
        textureId = gl.glGetUniformLocation(shaderProgram.id(), TEXTURE);
        modelMatrixId =gl.glGetUniformLocation(shaderProgram.id(), MODEL_MATRIX);
        colorArrayId = gl.glGetAttribLocation(shaderProgram.id(), COLOR_ARRAY);
        vertexArrayId = gl.glGetAttribLocation(shaderProgram.id(), VERTEX_ARRAY);
        textureArrayId=gl.glGetAttribLocation(shaderProgram.id(), TEXTURE_ARRAY);
        gl.glEnableVertexAttribArray(colorArrayId);
        gl.glEnableVertexAttribArray(textureArrayId);
        gl.glEnableVertexAttribArray(vertexArrayId);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);           // enable vertex arrays
    }
}
