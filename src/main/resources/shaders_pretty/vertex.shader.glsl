
#version 330

uniform mat4 modelMatrix, viewMatrix, projMatrix;

in vec4 position;
in vec3 color;
in vec2 a_Tex_Coord;
in vec3 norm_in;

out vec3 Color;
out vec2 v_Tex_Coord;
out vec3 normal_v;
out vec4 light;

void main()
{
    vec4 light_source=vec4(0,0,-0.5,1.0);
    Color = color;
    v_Tex_Coord = a_Tex_Coord;

    light=viewMatrix*light_source;
    normal_v=normalize((viewMatrix*modelMatrix*vec4(norm_in,0)).xyz);
    gl_Position =  (viewMatrix*modelMatrix*position) ;
}