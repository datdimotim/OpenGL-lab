package com.dimotim.opengl_lab;


import com.jogamp.opengl.GL4;
import lombok.Data;

@Data
public class NetShader extends Shader{
    public final int vertexLoc;
    public final int colorLoc;
    public final int projMatrixLoc;
    public final int viewMatrixLoc;
    public final int modelMatrixLoc;

    @Override
    public String getVertexShaderPath() {
        return "/shaders_net/vertex.shader.glsl";
    }

    @Override
    public String getGeomShaderPath() {
        return "/shaders_net/geom.shader.glsl";
    }

    @Override
    public String getFragmentShaderPath() {
        return "/shaders_net/fragment.shader.glsl";
    }

    @Override
    public int getTextureLoc() {
        return -1;
    }

    @Override
    public int getNormalLoc() {
        return -1;
    }

    @Override
    public int getTextureUniformLoc() {
        return -1;
    }

    public NetShader(GL4 gl){
        super(gl);


        this.vertexLoc = getAttribLocation("position",gl);
        this.colorLoc = getAttribLocation("color",gl);

        this.viewMatrixLoc=getUniformLocation("viewMatrix",gl);
        this.projMatrixLoc=getUniformLocation("projMatrix",gl);
        this.modelMatrixLoc=getUniformLocation("modelMatrix",gl);
        System.out.println(this);
    }
}
