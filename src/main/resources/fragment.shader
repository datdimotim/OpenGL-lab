
#version 330
uniform sampler2D u_texture;
in vec3 Color;
in vec2 v_Tex_Coord;
out vec4 outColor;



void main()
{
    vec4 textureColor=texture(u_texture, v_Tex_Coord);
    //outColor=vec4(v_Tex_Coord,0.0,1.0);
    outColor=textureColor+vec4(Color,1.0);
}