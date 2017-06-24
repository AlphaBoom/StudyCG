#version 300 es
precision highp float;
uniform sampler2D tex;
uniform vec2 delta;
in vec2 coord;
out vec4 outColor;
void main() {
    vec4 info = texture(tex,coord);
    vec2 dx = vec2(delta.x,0.0);
    vec2 dy = vec2(0.0,delta.y);

    float average = (
        texture(tex,coord - dx).r +
        texture(tex,coord - dy).r +
        texture(tex,coord + dx).r +
        texture(tex,coord + dy).r
    ) * 0.25;
    info.g += (average - info.r) * 2.0;
    info.g *= 0.995;
    info.r += info.g;
    outColor = info;
}
