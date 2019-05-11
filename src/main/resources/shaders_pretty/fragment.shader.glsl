
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
in vec4 light_m;
in vec4 position;
in vec3 observer;
out vec4 outColor;



void main(){
    float onePixel =1.0 / 512;

    vec4 light_mToPosition=position-light_m;
    vec3 light=normalize(vec3(light_mToPosition[0],light_mToPosition[1],light_mToPosition[2]));

    vec4 textureColor=texture(u_texture, v_Tex_Coord1);

    vec3 uvt=vec3(0,0,1.0);
    float dx=texture(u_texture, v_Tex_Coord1+vec2(onePixel,0)).x-texture(u_texture, v_Tex_Coord1).x;
    float dy=texture(u_texture, v_Tex_Coord1+vec2(0,onePixel)).x-texture(u_texture, v_Tex_Coord1).x;
    uvt=normalize(vec3(dx,dy,0.7));

    mat3 mtex=mat3(t1Vec,t2Vec,vec3(0,0,1.0));
    mat3 mvert=mat3(uVec,vVec,normal);
    vec3 exactNormal = mvert * inverse(mtex)*uvt;



    vec4 texturedColor=0.6*textureColor+  0.6*textureColor*dot(-light,normal)               + 0.5*vec4(1,1,1,1)*pow(dot(normalize(position.xyz-observer),-normal),100);
    vec4 bumpedColor=vec4(0.3,0.3,0.3,1)+vec4(0.5,0.5,0.5,1)*(dot(-light,exactNormal)) + 0.1*vec4(1,1,1,1)*pow(dot(normalize(position.xyz-observer),-exactNormal),100);;
    vec4 resColor=(texturedColor*(1-imgNormalRatio) + bumpedColor*imgNormalRatio);
    outColor=vec4(Color1,1.0)  +  resColor;
    //outColor=vec4(1,1,1,1);
}