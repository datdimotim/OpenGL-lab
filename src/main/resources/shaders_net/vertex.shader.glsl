
#version 330

uniform mat4 modelMatrix, viewMatrix, projMatrix;

in vec4 position;
in vec3 color;

out vec3 Color_g;

void main()
{
    float a=3.14/180*80;
    float s=sin(a);
    float c=cos(a);

    mat4 pers=mat4(
        1,0,0,0,
        0,1,0,0,
        0,0,1,0.7,
        0,0,0,1
    );
    Color_g = color;
    gl_Position =  pers*viewMatrix*modelMatrix*position ;
}