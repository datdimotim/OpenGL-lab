uniform sampler2D u_texture;
varying vec2 v_Tex_Coord;
varying vec4 ap_Color;
void main(){
    vec4 textureColor=texture2D(u_texture, v_Tex_Coord);
    gl_FragColor =textureColor;
}