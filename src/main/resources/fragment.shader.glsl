
#version 330
uniform sampler2D u_texture;
uniform float imgNormalRatio;
in vec3 Color1;
in vec2 v_Tex_Coord1;
in vec3 normal;
in vec3 uVec;
in vec3 vVec;
in vec3 t1Vec;
in vec3 t2Vec;

out vec4 outColor;



void main(){
    float onePixel =1.0 / 512;

    vec3 light=vec3(0,0,1.0);
    vec4 textureColor=texture(u_texture, v_Tex_Coord1);

    vec3 uvt=vec3(0,0,1.0);
    float dx=texture(u_texture, v_Tex_Coord1+vec2(onePixel,0)).x-texture(u_texture, v_Tex_Coord1).x;
    float dy=texture(u_texture, v_Tex_Coord1+vec2(0,onePixel)).x-texture(u_texture, v_Tex_Coord1).x;
    uvt=normalize(vec3(dx,dy,0.7));

    mat3 mtex=mat3(t1Vec,t2Vec,vec3(0,0,1.0));
    mat3 mvert=mat3(uVec,vVec,normal);

    vec3 exactNormal = mvert * inverse(mtex)*uvt;


    //outColor=vec4(v_Tex_Coord,0.0,1.0);
    vec4 texturedColor=textureColor*abs(dot(light,normal));
    vec4 bumpedColor=vec4(0.7,0.7,0.7,1)*abs(dot(light,exactNormal));
    outColor=(texturedColor*(1-imgNormalRatio) + bumpedColor*imgNormalRatio)+vec4(Color1,1.0);
}