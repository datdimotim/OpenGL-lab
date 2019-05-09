
#version 330

uniform mat4 modelMatrix, viewMatrix, projMatrix;
uniform vec3 light_pos;

in vec4 position;
in vec3 color;
in vec2 a_Tex_Coord;
in vec3 norm_in;


out vec3 Color_g;
out vec2 v_Tex_Coord;
out vec3 normal_v;
out vec4 light;
out vec4 posg;

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

    vec4 light_source=vec4(light_pos,1.0);
    Color_g = color;
    v_Tex_Coord = a_Tex_Coord;

    light=viewMatrix*light_source;
    normal_v=normalize((viewMatrix*modelMatrix*vec4(norm_in,0)).xyz);
    posg=viewMatrix*modelMatrix*position;
    gl_Position =pers* (viewMatrix*modelMatrix*position) ;
}