#version 300 es
precision highp float;
uniform sampler2D samplerBackground;
uniform sampler2D samplerRipples;
uniform vec2 delta;
uniform float perturbance;
in vec2 ripplesCoord;
in vec2 backgroundCoord;
out vec4 outColor;
void main() {
    float height = texture(samplerRipples,ripplesCoord).r;
    float heightX = texture(samplerRipples,vec2(ripplesCoord.x + delta.x,ripplesCoord.y)).r;
    float heightY = texture(samplerRipples,vec2(ripplesCoord.x,ripplesCoord.y + delta.y)).r;
    vec3 dx = vec3(delta.x,heightX - height,0.0);
    vec3 dy = vec3(0.0,heightY - height,delta.y);
    vec2 offset = -normalize(cross(dy,dx)).xz;
    float specular = pow(max(0.0,dot(offset,normalize(vec2(-0.6,1.0)))),4.0);
    outColor = texture(samplerBackground,backgroundCoord + offset * perturbance) + specular;
}



