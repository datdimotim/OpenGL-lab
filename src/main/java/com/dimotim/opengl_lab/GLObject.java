package com.dimotim.opengl_lab;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import static com.dimotim.opengl_lab.ShadeProgram.loadTexture;
import static com.jogamp.opengl.GL4.*;


public abstract class GLObject {
    private int vao=0;
    private int countOfVertexes=0;
    private int textureName=-1;
    private boolean inited=false;

    protected abstract float[] getVertices();
    protected abstract float[] getColorArray(); // RGBA
    protected abstract float[] getTextureArray();
    protected abstract String getTexturePath();
    protected abstract int getPrimitiveType();

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

    private void createBuffers(GL4 gl, int vaoId, float[] verticesArray, float[] colorArray, float[] textureArray, ShadeProgram program) {
        gl.glBindVertexArray(vaoId);
        int vertexBufferId = this.generateBufferId(gl);
        int colorBufferId = this.generateBufferId(gl);
        int textureBufferId = this.generateBufferId(gl);

        this.bindBuffer(gl, vertexBufferId, verticesArray, program.vertexLoc,3);
        this.bindBuffer(gl, colorBufferId, colorArray, program.colorLoc,3);
        this.bindBuffer(gl, textureBufferId, textureArray, program.textureLoc,2);
    }

    private void init(GL4 gl, ShadeProgram program){
        vao=generateVAOId(gl);
        float[] vertices=getVertices();
        countOfVertexes=vertices.length/3;
        createBuffers(gl,vao,vertices,getColorArray(), getTextureArray(),program);

        gl.glActiveTexture(GL_TEXTURE0);

        if(getTexturePath()!=null) {
            textureName = loadTexture(getTexturePath(), gl);
        }else textureName=-1;

        inited=true;
    }

    public void draw(GL4 gl, ShadeProgram program, float[] modelMatrix){
        if(!inited)init(gl, program);
        gl.glUniformMatrix4fv(program.viewMatrixLoc, 1, false, modelMatrix, 0);

        if(textureName!=-1) {
            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, textureName);
            gl.glUniform1i(program.textureUniformLoc, 0);
        }

        gl.glBindVertexArray(this.vao);






        gl.glDrawArrays(getPrimitiveType(), 0, countOfVertexes);
    }
}
