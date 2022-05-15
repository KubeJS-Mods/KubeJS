package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.text.Text;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.FormattedCharSequence;

public class TextObject extends ScreenPainterObject {
	private FormattedCharSequence[] text = new FormattedCharSequence[0];
	private boolean shadow = false;
	private float scale = 1F;
	private Unit color = PainterObjectProperties.WHITE_COLOR;
	private boolean centered = false;
	private float lineSpacing = 10F;

	private float maxTextWidth = 0F;
	private float[] textWidth = new float[0];

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		if (properties.tag.get("textLines") instanceof ListTag list) {
			text = new FormattedCharSequence[list.size()];

			for (int i = 0; i < list.size(); i++) {
				text[i] = Text.componentOf(list.get(i)).getVisualOrderText();
			}
		} else {
			text = new FormattedCharSequence[]{
					Text.componentOf(properties.tag.get("text")).getVisualOrderText()
			};
		}

		shadow = properties.getBoolean("shadow", shadow);
		scale = properties.getFloat("scale", scale);
		color = properties.getColor("color", color);
		centered = properties.getBoolean("centered", centered);
		lineSpacing = properties.getFloat("lineSpacing", lineSpacing);

		textWidth = new float[text.length];
	}

	@Override
	public void preDraw(ScreenPaintEventJS event) {
		maxTextWidth = 0F;

		for (int i = 0; i < text.length; i++) {
			textWidth[i] = event.font.getSplitter().stringWidth(text[i]);
			maxTextWidth = Math.max(maxTextWidth, textWidth[i]);
		}

		w = FixedUnit.of(maxTextWidth * scale);
		h = FixedUnit.of(9F * scale);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		var ax = event.alignX(x.get(), maxTextWidth, alignX);
		var ay = event.alignY(y.get(), text.length * lineSpacing, alignY);
		var az = z.get();

		event.push();
		event.translate(ax, ay, az);
		event.scale(scale);

		for (int i = 0; i < text.length; i++) {
			event.rawText(text[i], centered ? -(textWidth[i] / 2F) : 0, i * lineSpacing, color.getAsInt(), shadow);
		}

		event.pop();
	}
}
