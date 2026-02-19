#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    // Simpele blit: kopieer pixels zonder aanpassing
    fragColor = texture(DiffuseSampler, texCoord);
}
