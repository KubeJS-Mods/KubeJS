#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D MCDepthSampler;
uniform float OutlineSize;

in vec2 texCoord;
in vec2 sampleStep;
in vec2 ScreenPosition;

out vec4 fragColor;

void main() {
	vec4 hl = texture(DiffuseSampler, texCoord);

	if (hl.a > 0.005) {
		if (texture(DiffuseDepthSampler, texCoord).r - texture(MCDepthSampler, texCoord).r > 0.0001) {
			vec2 Pos = floor(ScreenPosition / 3.0);

			float y = mod(Pos.y, 2.0);

			if (y >= 1.0) {
				discard;
			} else {
				float x = mod(Pos.x + mod(Pos.y / 2.0, 2.0), 2.0);

				if (x < 0.5) {
					discard;
				} else {
					fragColor = vec4(hl.r, hl.g, hl.b, 0.3);
				}
			}
		} else {
			fragColor = vec4(hl.r, hl.g, hl.b, 0.1);
		}
	} else {
		float a = 0.0;
		float r = 1.0;
		float g = 1.0;
		float b = 1.0;

		for (float i = -OutlineSize; i <= OutlineSize; i += 1.0) {
			for (float j = -OutlineSize; j <= OutlineSize; j += 1.0) {
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
