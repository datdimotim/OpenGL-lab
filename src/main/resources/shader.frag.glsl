uniform sampler2D u_texture;
varying vec2 v_Tex_Coord;
varying vec4 ap_Color;
varying vec3 normal;

void main(){
    vec4 textureColor=texture2D(u_texture, v_Tex_Coord);
    const vec4 diffColor = vec4 (0.0, 0.5, 0.0, 1.0);
    const vec3 l2   = vec3(1,1,1);
    vec3 l2n = normalize(l2);
    vec3 n2   = normalize (normal);
    vec4 diff = diffColor * max(dot( n2, l2n),0.0);
    gl_FragColor = diff+vec4(0,0.2,0.5,1);
    //gl_FragColor=vec4(0,0,1,1);
}