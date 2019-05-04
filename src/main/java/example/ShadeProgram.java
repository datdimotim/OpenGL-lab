package example;

import com.jogamp.opengl.GL3;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Data
public class ShadeProgram{
    public final int vertexLoc;
    public final int colorLoc;
    public final int textureLoc;
    public final int progId;
    public final int projMatrixLoc;
	public final int viewMatrixLoc;
	public final int textureUniformLoc;
    public ShadeProgram(GL3 gl){
        int v = this.newShaderFromCurrentClass(gl, "/vertex.shader", ShaderType.VertexShader);
        int f = this.newShaderFromCurrentClass(gl, "/fragment.shader", ShaderType.FragmentShader);

        int p = this.createProgram(gl, v, f);

        gl.glBindFragDataLocation(p, 0, "outColor");
        printProgramInfoLog(gl, p);

        this.vertexLoc = gl.glGetAttribLocation(p, "position");
        this.colorLoc = gl.glGetAttribLocation(p, "color");
        this.textureLoc = gl.glGetAttribLocation(p, "a_Tex_Coord");
        this.progId=p;
        this.viewMatrixLoc=gl.glGetUniformLocation(p,"viewMatrix");
        this.projMatrixLoc=gl.glGetUniformLocation(p,"projMatrix");
        this.textureUniformLoc=gl.glGetUniformLocation(p,"u_texture");
        System.out.println(this);
    }


    public String getShaderInfoLog(GL3 gl, int obj) {
        final int logLen = getShaderParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
        if (logLen <= 0)
            return "";

        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return String.format("ShaderLog: %s", logMessage);
    }

    private int getShaderParameter(GL3 gl, int obj, int paramName) {
        final int params[] = new int[1];
        gl.glGetShaderiv(obj, paramName, params, 0);
        return params[0];
    }

    public String printProgramInfoLog(GL3 gl, int obj) {
        final int logLen = getProgramParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
        if (logLen <= 0)
            return "";

        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return logMessage;
    }

    private int getProgramParameter(GL3 gl, int obj, int paramName) {
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

    private int createProgram(GL3 gl, int vertexShaderId, int fragmentShaderId) {
        int programId = gl.glCreateProgram();
        gl.glAttachShader(programId, vertexShaderId);
        gl.glAttachShader(programId, fragmentShaderId);
        gl.glLinkProgram(programId);

        return programId;
    }

    private int newShaderFromCurrentClass(GL3 gl, String fileName, ShaderType type) {
        String shaderSource = this.loadStringFileFromCurrentPackage(fileName);
        int shaderType = type.getGlType();
        int id = gl.glCreateShader(shaderType);
        gl.glShaderSource(id, 1, new String[]{shaderSource}, null);
        gl.glCompileShader(id);

        return id;
    }

    enum ShaderType {
        VertexShader(GL3.GL_VERTEX_SHADER),
        FragmentShader(GL3.GL_FRAGMENT_SHADER);

        public int getGlType(){
            return glType;
        }
        private final int glType;
        ShaderType(int glType){
            this.glType=glType;
        }
    }
}
