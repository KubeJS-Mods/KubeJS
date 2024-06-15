#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
	vec4 color = texture(Sampler0, texCoord0);
	if (color.a < 0.005) {
		discard;
	}
	fragColor = vertexColor;
}
