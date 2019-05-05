#version 150

layout (lines) in;
layout (line_strip, max_vertices=2) out;

in vec3 Color_g[2];
out vec3 Color1;

void main() {
    Color1=Color_g[0];
    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    Color1=Color_g[1];
    gl_Position = gl_in[1].gl_Position;
    EmitVertex();
    EndPrimitive();
}


