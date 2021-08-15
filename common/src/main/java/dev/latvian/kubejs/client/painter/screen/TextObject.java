package dev.latvian.kubejs.client.painter.screen;

import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.kubejs.text.Text;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.util.FormattedCharSequence;

public class TextObject extends ScreenPainterObject {
	private FormattedCharSequence text = FormattedCharSequence.EMPTY;
	private boolean shadow = false;
	private float scale = 1F;
	private Unit color = PainterObjectProperties.WHITE_COLOR;
	private boolean centered = false;

	private float textWidth;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		text = Text.componentOf(properties.tag.get("text")).getVisualOrderText();
		shadow = properties.getBoolean("shadow", shadow);
		scale = properties.getFloat("scale", scale);
		color = properties.getColor("color", color);
		centered = properties.getBoolean("centered", centered);
	}

	@Override
	public void preDraw(ScreenPaintEventJS event) {
		textWidth = event.font.getSplitter().stringWidth(text);
		w = FixedUnit.of(textWidth * scale);
		h = FixedUnit.of(9F * scale);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		float aw = w.get();
		float ah = h.get();
		float ax = event.alignX(x.get(), aw, alignX);
		float ay = event.alignY(y.get(), ah, alignY);
		float az = z.get();

		if (scale == 1F && az == 0F) {
			if (centered) {
				event.rawText(text, ax - textWidth / 2F, ay, color.getAsInt(), shadow);
			} else {
				event.rawText(text, ax, ay, color.getAsInt(), shadow);
			}
		} else {
			event.push();
			event.translate(ax, ay, az);
			event.scale(scale);

			if (centered) {
				event.rawText(text, -textWidth / 2F, 0, color.getAsInt(), shadow);
			} else {
				event.rawText(text, 0, 0, color.getAsInt(), shadow);
			}

			event.pop();
		}
	}
}
