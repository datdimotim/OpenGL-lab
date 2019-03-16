uniform sampler2D u_texture;
varying vec2 v_Tex_Coord;
varying vec4 ap_Color;
uniform int u_kern_size;
uniform float u_kern[100];

void main(){
    vec4 textureColor=texture2D(u_texture, v_Tex_Coord);
    vec4 sum = vec4(0.0);

    vec2 Hor=vec2(0.0005,0);
    vec2 Ver=vec2(0,0.0005);
    vec2 startDir = -float(0.5)*(Hor+Ver)*float(u_kern_size-1);
    for(int i=0;i<u_kern_size;++i){
        for(int j=0;j<u_kern_size;++j){

            vec2 p=v_Tex_Coord+startDir+Hor*float(i)+Ver*float(j);
            vec4 c = texture2D(u_texture, p);
            vec4 r=c*u_kern[i]*u_kern[j];
            sum = sum+r;
        }
    }

    gl_FragColor = sum;
    //gl_FragColor= textureColor;
}