package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedBooleanUnit;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.FormattedCharSequence;

public class TextObject extends ScreenPainterObject {
	private FormattedCharSequence[] text = new FormattedCharSequence[0];
	private Unit shadow = FixedBooleanUnit.FALSE;
	private Unit scale = FixedNumberUnit.ONE;
	private Unit color = FixedColorUnit.WHITE;
	private Unit centered = FixedBooleanUnit.FALSE;
	private Unit lineSpacing = FixedNumberUnit.TEN;

	private float[] textWidth = new float[0];

	public TextObject(Painter painter) {
	}

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		if (properties.tag.get("textLines") instanceof ListTag list) {
			text = new FormattedCharSequence[list.size()];

			for (int i = 0; i < list.size(); i++) {
				text[i] = TextWrapper.of(list.get(i)).getVisualOrderText();
			}
		} else {
			text = new FormattedCharSequence[]{
				TextWrapper.of(properties.tag.get("text")).getVisualOrderText()
			};
		}

		shadow = properties.getUnit("shadow", shadow);
		scale = properties.getUnit("scale", scale);
		color = properties.getColor("color", color);
		centered = properties.getUnit("centered", centered);
		lineSpacing = properties.getUnit("lineSpacing", lineSpacing);

		textWidth = new float[text.length];
	}

	@Override
	public void preDraw(PaintScreenEventJS event) {
		float maxTextWidth = 0F;

		for (int i = 0; i < text.length; i++) {
			textWidth[i] = event.font.getSplitter().stringWidth(text[i]);
			maxTextWidth = Math.max(maxTextWidth, textWidth[i]);
		}

		w = scale.mul(FixedNumberUnit.of(maxTextWidth));
		h = scale.mul(lineSpacing).mul(FixedNumberUnit.of(text.length));
	}

	@Override
	public void draw(PaintScreenEventJS event) {
		var ls = lineSpacing.getFloat(event);
		var ax = event.alignX(x.getFloat(event), w.getFloat(event), alignX);
		var ay = event.alignY(y.getFloat(event), h.getFloat(event), alignY);
		var az = z.getFloat(event);
		boolean c = centered.getBoolean(event);
		boolean s = shadow.getBoolean(event);

		event.push();
		event.translate(ax, ay, az);
		event.scale(scale.getFloat(event));

		for (int i = 0; i < text.length; i++) {
			event.rawText(text[i], c ? -(textWidth[i] / 2F) : 0, i * ls, color.getInt(event), s);
		}

		event.pop();
	}
}
