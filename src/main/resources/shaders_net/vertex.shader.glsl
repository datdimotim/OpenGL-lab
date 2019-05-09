
#version 330

uniform mat4 modelMatrix, viewMatrix, projMatrix;

in vec4 position;
in vec3 color;

out vec3 Color_g;

void main()
{

    float alpha=3.14/6;
    float l=0.1;
    float ll=10;

    mat4 pers=mat4(
    (1/tan(alpha)),    0,    0,      0,
    0,    1/(tan(alpha)),    0,      0,
    0,    0,    (ll+l)/(ll-l),       1,
    0,    0,      -2*ll*l/(ll-l),    0
    );
    Color_g = color;
    gl_Position =  pers*viewMatrix*modelMatrix*position ;
}