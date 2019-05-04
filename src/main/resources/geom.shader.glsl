#version 150

layout (triangles) in;
layout (triangle_strip, max_vertices=3) out;

in vec3 Color[3];
in vec2 v_Tex_Coord[3];


out vec3 Color1;
out vec2 v_Tex_Coord1;
out vec3 normal;
out vec3 uVec;
out vec3 vVec;
out vec3 t1Vec;
out vec3 t2Vec;


vec3 crossVec4(vec4 _v1, vec4 _v2){
    vec3 v31 = vec3(_v1[0], _v1[1], _v1[2]);
    vec3 v32 = vec3(_v2[0], _v2[1], _v2[2]);
    vec3 res = cross(v31, v32);
    return vec3(res[0], res[1], res[2]);
}

void main() {
    vec3 u=normalize(gl_in[1].gl_Position.xyz-gl_in[0].gl_Position.xyz);
    vec3 v=normalize(gl_in[2].gl_Position.xyz-gl_in[0].gl_Position.xyz);
    vec3 t1=normalize(vec3(v_Tex_Coord[1]-v_Tex_Coord[0],0));
    vec3 t2=normalize(vec3(v_Tex_Coord[2]-v_Tex_Coord[0],0));
    vec3 n=normalize(cross(u, v));

    uVec=u;
    vVec=v;
    t1Vec=t1;
    t2Vec=t2;
    normal=n;
    Color1=Color[0];
    v_Tex_Coord1=v_Tex_Coord[0];
    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    uVec=u;
    vVec=v;
    t1Vec=t1;
    t2Vec=t2;
    normal=n;
    Color1=Color[1];
    v_Tex_Coord1=v_Tex_Coord[1];
    gl_Position = gl_in[1].gl_Position;
    EmitVertex();

    uVec=u;
    vVec=v;
    t1Vec=t1;
    t2Vec=t2;
    normal=n;
    Color1=Color[2];
    v_Tex_Coord1=v_Tex_Coord[2];
    gl_Position = gl_in[2].gl_Position;
    EmitVertex();
    EndPrimitive();
}


