#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;
in vec2 sampleStep;

out vec4 fragColor;

void main() {
	vec4 hl = texture(DiffuseSampler, texCoord);
	// 0.6, 1.0, 0.7

	if (hl.a > 0.005) {
		fragColor = vec4(hl.r, hl.g, hl.b, 0.3);
	} else {
		float a = 0.0;
		float r = 1.0;
		float g = 1.0;
		float b = 1.0;

		for (float i = -2.0; i <= 2.0; i += 1.0) {
			for (float j = -2.0; j <= 2.0; j += 1.0) {
				vec4 c = texture(DiffuseSampler, texCoord + vec2(i, j) * sampleStep);

				if (c.a > 0.005) {
					a = 1.0;
					r = c.r;
					g = c.g;
					b = c.b;
				}
			}
		}

		if (a > 0.0) {
			fragColor = vec4(r, g, b, 1.0);
		} else {
			discard;
		}
	}
}
