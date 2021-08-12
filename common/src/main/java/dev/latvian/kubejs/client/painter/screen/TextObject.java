package dev.latvian.kubejs.client.painter.screen;

import dev.latvian.kubejs.client.painter.RenderObjectProperties;
import dev.latvian.kubejs.text.Text;
import net.minecraft.util.FormattedCharSequence;

public class TextObject extends ScreenPainterObject {
	private FormattedCharSequence text = FormattedCharSequence.EMPTY;
	private boolean shadow = false;
	private float scale = 1F;
	private int color = 0xFFFFFFFF;
	private boolean centered = false;

	private float textWidth;

	@Override
	protected void load(RenderObjectProperties properties) {
		super.load(properties);

		text = Text.componentOf(properties.tag.get("text")).getVisualOrderText();
		shadow = properties.getBoolean("shadow", shadow);
		scale = properties.getFloat("scale", scale);
		color = properties.getARGB("color", color);
		centered = properties.getBoolean("centered", centered);
	}

	@Override
	public void preDraw(ScreenPaintEventJS event) {
		textWidth = event.font.getSplitter().stringWidth(text);
		w = textWidth * scale;
		h = 9F * scale;
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		float ax = event.alignX(x, w, alignX);
		float ay = event.alignY(y, h, alignY);

		if (scale == 1F && z == 0) {
			if (centered) {
				event.rawText(text, ax - textWidth / 2F, ay, color, shadow);
			} else {
				event.rawText(text, ax, ay, color, shadow);
			}
		} else {
			event.push();
			event.translate(ax, ay, z);
			event.scale(scale);

			if (centered) {
				event.rawText(text, -textWidth / 2F, 0, color, shadow);
			} else {
				event.rawText(text, 0, 0, color, shadow);
			}

			event.pop();
		}
	}
}
