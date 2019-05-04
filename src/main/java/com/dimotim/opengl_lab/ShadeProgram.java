package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.sun.prism.impl.BufferUtil;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

import static com.dimotim.opengl_lab.BasicFrame.checkErr;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL2ES2.GL_RED;
import static com.jogamp.opengl.GL2ES3.*;
import static com.jogamp.opengl.GL2GL3.GL_TEXTURE_SWIZZLE_RGBA;


@Data
public class ShadeProgram{
    public final int vertexLoc;
    public final int colorLoc;
    public final int textureLoc;
    public final int progId;
    public final int projMatrixLoc;
	public final int viewMatrixLoc;
	public final int textureUniformLoc;
    public ShadeProgram(GL4 gl){
        int v = this.newShaderFromCurrentClass(gl, "/vertex.shader.glsl", ShaderType.VertexShader);
        int f = this.newShaderFromCurrentClass(gl, "/fragment.shader.glsl", ShaderType.FragmentShader);
        int g = this.newShaderFromCurrentClass(gl,"/geom.shader.glsl",ShaderType.GeomShader);
        System.out.println(g);
        int p = this.createProgram(gl, v, f, g);

        gl.glBindFragDataLocation(p, 0, "outColor");
        printProgramInfoLog(gl, p);

        this.progId=p;checkErr(gl);
        this.vertexLoc = gl.glGetAttribLocation(p, "position");checkErr(gl);
        this.colorLoc = gl.glGetAttribLocation(p, "color");
        this.textureLoc = gl.glGetAttribLocation(p, "a_Tex_Coord");

        this.viewMatrixLoc=gl.glGetUniformLocation(p,"viewMatrix");
        this.projMatrixLoc=gl.glGetUniformLocation(p,"projMatrix");
        this.textureUniformLoc=gl.glGetUniformLocation(p,"u_texture");
        System.out.println(this);
    }

    public static int loadTexture(String file, GL4 gl) {
        try {
            URL texture= BasicFrame.class.getClassLoader().getResource(file);
            TextureData data= TextureIO.newTextureData(gl.getGLProfile(),texture,false,TextureIO.PNG);
            int level=0;
            int[] a=new int[1];
            gl.glGenTextures(1,a,0);
            final int textureName=a[0];

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
            return textureName;

        } catch (IOException e) {
            throw new RuntimeException("error load texture: file=" + file);
        }
    }

    private void checkLinkAndValidationErrors(GL4 gl, int id) {
        IntBuffer status = BufferUtil.newIntBuffer(1);
        gl.glGetProgramiv(id, GL4.GL_LINK_STATUS, status);

        if (status.get() == GL.GL_FALSE) {
            getInfoLog(gl, id);
        } else {
            status.rewind();
            gl.glValidateProgram(id);
            gl.glGetProgramiv(id, GL4.GL_VALIDATE_STATUS, status);
            if (status.get() == GL.GL_FALSE) {
                getInfoLog(gl,id);
            } else {
                System.out.println("Successfully linked program " + id);
            }
        }
    }

    private void getInfoLog(GL4 gl, int id) {
        IntBuffer infoLogLength = BufferUtil.newIntBuffer(1);
        gl.glGetProgramiv(id, GL4.GL_INFO_LOG_LENGTH, infoLogLength);

        ByteBuffer infoLog = BufferUtil.newByteBuffer(infoLogLength.get(0));
        gl.glGetProgramInfoLog(id, infoLogLength.get(0), null, infoLog);

        String infoLogString =
                Charset.forName("US-ASCII").decode(infoLog).toString();
        throw new Error("Program compile error\n" + infoLogString);
    }

    public String getShaderInfoLog(GL4 gl, int obj) {
        final int logLen = getShaderParameter(gl, obj, GL4.GL_INFO_LOG_LENGTH);
        if (logLen <= 0)
            return "";

        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return String.format("ShaderLog: %s", logMessage);
    }

    private int getShaderParameter(GL4 gl, int obj, int paramName) {
        final int params[] = new int[1];
        gl.glGetShaderiv(obj, paramName, params, 0);
        return params[0];
    }

    public String printProgramInfoLog(GL4 gl, int obj) {
        final int logLen = getProgramParameter(gl, obj, GL4.GL_INFO_LOG_LENGTH);
        if (logLen <= 0)
            return "";

        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return logMessage;
    }

    private int getProgramParameter(GL4 gl, int obj, int paramName) {
        final int params[] = new int[1];
        gl.glGetProgramiv(obj, paramName, params, 0);
        return params[0];
    }

    private String loadStringFileFromCurrentPackage(String fileName) {
        InputStream stream = this.getClass().getResourceAsStream(fileName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder strBuilder = new StringBuilder();

        try {
            String line = reader.readLine();
            while (line != null) {
                strBuilder.append(line + "\n");
                line = reader.readLine();
            }
            reader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strBuilder.toString();
    }

    private int createProgram(GL4 gl, int vertexShaderId, int fragmentShaderId, int geomShaderId) {
        int programId = gl.glCreateProgram();
        gl.glAttachShader(programId, vertexShaderId);
        gl.glAttachShader(programId, geomShaderId);
        gl.glAttachShader(programId, fragmentShaderId);
        checkErr(gl);
        gl.glLinkProgram(programId);
        checkLinkAndValidationErrors(gl,programId);

        int[] status=new int[1];
        gl.glGetProgramiv(programId, GL_LINK_STATUS,status,0);
        System.out.println("link status: "+(status[0]==GL_TRUE));
        checkErr(gl);
        return programId;
    }

    private int newShaderFromCurrentClass(GL4 gl, String fileName, ShaderType type) {
        String shaderSource = this.loadStringFileFromCurrentPackage(fileName);
        int shaderType = type.getGlType();
        int id = gl.glCreateShader(shaderType);
        gl.glShaderSource(id, 1, new String[]{shaderSource}, null);
        gl.glCompileShader(id);
        System.err.println(getShaderInfoLog(gl,id));

        return id;
    }

    enum ShaderType {
        VertexShader(GL4.GL_VERTEX_SHADER),
        FragmentShader(GL4.GL_FRAGMENT_SHADER),
        GeomShader(GL4.GL_GEOMETRY_SHADER);

        public int getGlType(){
            return glType;
        }
        private final int glType;
        ShaderType(int glType){
            this.glType=glType;
        }
    }
}
