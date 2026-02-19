#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    // Invert all colors: flip R, G, B but keep alpha
    fragColor = vec4(1.0 - color.r, 1.0 - color.g, 1.0 - color.b, color.a);
}
