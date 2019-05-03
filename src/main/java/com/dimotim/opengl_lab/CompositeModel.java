package com.dimotim.opengl_lab;

import com.jogamp.opengl.GL2;


public class CompositeModel {
    private final double d=0.2;
    private final Sphere sphere=new Sphere();
    private final Cylinder cylinder=new Cylinder();

    public void draw(GL2 gl, Shader shader, float[] modelMatrix){
        gl.glUniformMatrix4fv(shader.modelMatrixId, 1, false, modelMatrix, 0);

        float[] mat;

        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(-d,-d,-d));
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(-d,-d,d));
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(-d,d,d));
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(-d,d,-d));
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(d,-d,-d));
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(d,-d,d));
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(d,d,d));
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(d,d,-d));
        sphere.draw(gl, shader, mat);


        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(-d,-d,0));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(-d,d,0));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(d,d,0));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(modelMatrix,LinAl.translate(d,-d,0));
        cylinder.draw(gl,shader,mat);


        float[] rotate=LinAl.rotate(Math.PI/2,1,0,0);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(-d,-d,0)),rotate);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(-d,d,0)),rotate);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(d,d,0)),rotate);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(d,-d,0)),rotate);
        cylinder.draw(gl,shader,mat);

        rotate=LinAl.rotate(Math.PI/2,0,1,0);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(-d,-d,0)),rotate);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(-d,d,0)),rotate);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(d,d,0)),rotate);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.matrixMul(modelMatrix,LinAl.translate(d,-d,0)),rotate);
        cylinder.draw(gl,shader,mat);


    }
}
