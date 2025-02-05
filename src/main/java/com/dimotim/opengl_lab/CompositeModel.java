package com.dimotim.opengl_lab;


import com.jogamp.opengl.GL4;


public class CompositeModel {
    private final double d=0.2;
    private final Sphere sphere=new Sphere();
    private final Cylinder cylinder=new Cylinder();

    public void draw(GL4 gl, Shader shader, float[] modelMatrix){

        float[] mat;

        mat=LinAl.matrixMul(LinAl.translate(-d,-d,-d),modelMatrix);
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(LinAl.translate(-d,-d,d), modelMatrix);
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(LinAl.translate(-d,d,d), modelMatrix);
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(LinAl.translate(-d,d,-d), modelMatrix);
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(LinAl.translate(d,-d,-d), modelMatrix);
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(LinAl.translate(d,-d,d), modelMatrix);
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(LinAl.translate(d,d,d),modelMatrix);
        sphere.draw(gl, shader, mat);
        mat=LinAl.matrixMul(LinAl.translate(d,d,-d),modelMatrix);
        sphere.draw(gl, shader, mat);


        mat=LinAl.matrixMul(LinAl.translate(-d,-d,0),modelMatrix);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.translate(-d,d,0),modelMatrix);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.translate(d,d,0),modelMatrix);
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(LinAl.translate(d,-d,0),modelMatrix);
        cylinder.draw(gl,shader,mat);


        float[] rotate=LinAl.rotate(Math.PI/2,1,0,0);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(-d,0,-d),modelMatrix));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(-d,0,d),modelMatrix));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(d,0,d),modelMatrix));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(d,0,-d),modelMatrix));
        cylinder.draw(gl,shader,mat);


        rotate=LinAl.rotate(Math.PI/2,0,1,0);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(0,-d,-d),modelMatrix));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(0,-d,d),modelMatrix));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(0,d,d),modelMatrix));
        cylinder.draw(gl,shader,mat);
        mat=LinAl.matrixMul(rotate,LinAl.matrixMul(LinAl.translate(0,d,-d),modelMatrix));
        cylinder.draw(gl,shader,mat);


    }
}
