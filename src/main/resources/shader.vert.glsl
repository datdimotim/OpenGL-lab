uniform mat4 u_Matrix;
uniform mat4 u_Model_Matrix;
uniform mat4 u_Monitor_Matrix;
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_Tex_Coord;
attribute vec3 a_normal;
varying vec2 v_Tex_Coord;
varying vec4 ap_Color;
varying vec3 normal;
void main(){
    ap_Color=a_Color;
    v_Tex_Coord=a_Tex_Coord;
    vec4 n4=vec4(a_normal,0);
    vec4 nn=u_Matrix*u_Model_Matrix*n4;
    normal = vec3(nn[0],nn[1],nn[2]);
    //normal=vec3(1,1,0);
    gl_Position =u_Monitor_Matrix*u_Matrix*u_Model_Matrix*a_Position;
}