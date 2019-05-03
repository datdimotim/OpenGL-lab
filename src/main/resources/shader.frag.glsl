uniform sampler2D u_texture;
uniform float u_iblue;
uniform float u_igreen;
uniform float u_ired;

varying vec2 v_Tex_Coord;
varying vec4 ap_Color;
varying vec3 normal;

void main(){
    vec4 textureColor=texture2D(u_texture, v_Tex_Coord);
    vec4 diffColor = vec4 (0.5,0.5,0.5, 0.5);
    const vec3 l2   = vec3(1,1,1);
    vec3 l2n = normalize(l2);
    vec3 n2   = normalize (normal);
    vec4 diff = diffColor * abs(dot( n2, l2n));
    //gl_FragColor = (diff+vec4 (u_ired, u_igreen, u_iblue, 1.0))+ap_Color;
    gl_FragColor=textureColor;
}