
#version 330

uniform mat4 viewMatrix, projMatrix;

in vec4 position;
in vec3 color;
in vec2 a_Tex_Coord;


out vec3 Color;
out vec2 v_Tex_Coord;

void main()
{
    Color = color;
    v_Tex_Coord = a_Tex_Coord;
    gl_Position =  (viewMatrix*position) ;
}