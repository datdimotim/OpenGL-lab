package com.dimotim.opengl_lab;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import static com.dimotim.opengl_lab.Shader.loadTexture;
import static com.jogamp.opengl.GL4.*;


public abstract class GLObject {
    private int vao=0;
    private int countOfVertexes=0;
    private int textureName=-1;
    private boolean inited=false;

    protected abstract float[] getVertices();
    protected abstract float[] getColorArray(); // RGBA
    protected abstract float[] getTextureArray();
    protected abstract float[] getNormalArray();
    protected abstract String getTexturePath();
    protected abstract int getPrimitiveType();
    public abstract float getImgNormalRatio();

    private int generateVAOId(GL4 gl) {
        int[] idArray = new int[1];
        gl.glGenVertexArrays(1, idArray, 0);
        return idArray[0];
    }

    private int generateBufferId(GL4 gl) {
        int[] idArray = new int[1];
        gl.glGenBuffers(1, idArray, 0);
        return idArray[0];
    }

    private void bindBuffer(GL4 gl, int bufferId, float[] dataArray, int dataLoc, int dataPerVertex) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        gl.glBufferData(GL_ARRAY_BUFFER, dataArray.length * Float.SIZE / 8, Buffers.newDirectFloatBuffer(dataArray), GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(dataLoc);
        gl.glVertexAttribPointer(dataLoc, dataPerVertex, GL_FLOAT, false, 0, 0);
    }

    private void createBuffers(GL4 gl, int vaoId, float[] verticesArray, float[] colorArray, float[] textureArray, float[] normalArray, Shader program) {
        gl.glBindVertexArray(vaoId);

        if(-1!=program.getVertexLoc()) {
            int vertexBufferId = this.generateBufferId(gl);
            bindBuffer(gl, vertexBufferId, verticesArray, program.getVertexLoc(), 3);
        }

        if(-1!=program.getColorLoc()) {
            int colorBufferId = this.generateBufferId(gl);
            this.bindBuffer(gl, colorBufferId, colorArray, program.getColorLoc(), 3);
        }

        if(-1!=program.getTextureLoc()) {
            int textureBufferId = this.generateBufferId(gl);
            this.bindBuffer(gl, textureBufferId, textureArray, program.getTextureLoc(), 2);
        }

        if(-1!=program.getNormalLoc()) {
            int normalBufferId = this.generateBufferId(gl);
            this.bindBuffer(gl, normalBufferId, normalArray, program.getNormalLoc(), 3);
        }
    }

    private void init(GL4 gl, Shader program){
        vao=generateVAOId(gl);
        float[] vertices=getVertices();
        countOfVertexes=vertices.length/3;
        createBuffers(gl,vao,vertices,getColorArray(), getTextureArray(),getNormalArray(),program);

        gl.glActiveTexture(GL_TEXTURE0);

        if(getTexturePath()!=null) {
            textureName = loadTexture(getTexturePath(), gl);
        }else textureName=-1;

        inited=true;
    }

    public void draw(GL4 gl, Shader program, float[] modelMatrix){
        if(!inited)init(gl, program);
        gl.glUniformMatrix4fv(program.getModelMatrixLoc(), 1, false, modelMatrix, 0);

        if(program instanceof TextureShader) {
            gl.glUniform1f(((TextureShader) program).imgNormalRatioLoc, getImgNormalRatio());
        }

        if(textureName!=-1) {
            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, textureName);
            gl.glUniform1i(program.getTextureUniformLoc(), 0);
        }

        gl.glBindVertexArray(this.vao);
        gl.glDrawArrays(getPrimitiveType(), 0, countOfVertexes);
    }
}
