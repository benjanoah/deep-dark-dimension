#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    // TEST: maak alles rood om te controleren of de shader werkt
    fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
