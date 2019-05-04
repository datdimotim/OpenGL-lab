
#version 330
uniform sampler2D u_texture;
in vec3 Color1;
in vec2 v_Tex_Coord1;
in vec3 normal;
out vec4 outColor;



void main(){
    vec3 light=vec3(0,0,1);
    vec4 textureColor=texture(u_texture, v_Tex_Coord1);
    //outColor=vec4(v_Tex_Coord,0.0,1.0);
    outColor=(textureColor+vec4(Color1,1.0))*abs((dot(light,normal)));
}