package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL4;
import lombok.Data;

@Data
public class TextureShader extends Shader{
    public final int vertexLoc;
    public final int colorLoc;
    public final int textureLoc;
    public final int normalLoc;
    public final int projMatrixLoc;
	public final int viewMatrixLoc;
	public final int modelMatrixLoc;
	public final int textureUniformLoc;
	public final int imgNormalRatioLoc;

    @Override
    public String getVertexShaderPath() {
        return "/shaders_pretty/vertex.shader.glsl";
    }

    @Override
    public String getGeomShaderPath() {
        return "/shaders_pretty/geom.shader.glsl";
    }

    @Override
    public String getFragmentShaderPath() {
        return "/shaders_pretty/fragment.shader.glsl";
    }

    public TextureShader(GL4 gl){
        super(gl);


        this.vertexLoc = getAttribLocation("position",gl);
        this.colorLoc = getAttribLocation("color",gl);
        this.textureLoc = getAttribLocation("a_Tex_Coord",gl);
        this.normalLoc=getAttribLocation("norm_in", gl);

        this.viewMatrixLoc=getUniformLocation("viewMatrix",gl);
        this.projMatrixLoc=getUniformLocation("projMatrix",gl);
        this.modelMatrixLoc=getUniformLocation("modelMatrix",gl);
        this.textureUniformLoc=getUniformLocation("u_texture",gl);
        this.imgNormalRatioLoc=getUniformLocation("imgNormalRatio",gl);
        System.out.println(this);
    }
}
