
#version 330

uniform mat4 viewMatrix, projMatrix;

in vec4 position;
in vec3 color;
in vec2 a_Tex_Coord;
in vec3 norm_in;

out vec3 Color;
out vec2 v_Tex_Coord;
out vec3 normal_v;

void main()
{
    Color = color;
    v_Tex_Coord = a_Tex_Coord;

    normal_v=normalize((viewMatrix*vec4(norm_in,0)).xyz);
    gl_Position =  (viewMatrix*position) ;
}