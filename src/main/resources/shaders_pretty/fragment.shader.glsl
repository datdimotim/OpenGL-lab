
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

    vec3 ptTolight=normalize((position-light_m).xyz);
    vec3 ptToEye=normalize(position.xyz-observer);

    vec4 textureColor=texture(u_texture, v_Tex_Coord1);

    float dx=texture(u_texture, v_Tex_Coord1+vec2(onePixel,0)).x-texture(u_texture, v_Tex_Coord1).x;
    float dy=texture(u_texture, v_Tex_Coord1+vec2(0,onePixel)).x-texture(u_texture, v_Tex_Coord1).x;
    vec3 uvt=normalize(vec3(dx,dy,0.7));

    mat3 mtex=mat3(t1Vec,t2Vec,vec3(0,0,1.0));
    mat3 mvert=mat3(uVec,vVec,normal);
    vec3 exactNormal = mvert * inverse(mtex)*uvt;

    vec3 resNormal=normal*(1-imgNormalRatio)+exactNormal;

    float specIsPresent=0;
    if(dot(ptToEye,ptTolight)>0)specIsPresent=1;

    vec4 ambientColor=vec4(Color1,1)+textureColor*(1-imgNormalRatio);
    vec4 diffuseColor=(vec4(Color1,1)+textureColor*(1-imgNormalRatio))*max(dot(-ptTolight,resNormal),0);
    vec4 specularColor=specIsPresent*vec4(1,1,1,1)*pow(max(dot(ptToEye,-normal),0),100);

    outColor=ambientColor*0.2+0.6*diffuseColor+specularColor*0.2;
}