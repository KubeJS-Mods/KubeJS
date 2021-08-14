package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.kubejs.text.Text;
import dev.latvian.mods.rhino.util.unit.Unit;
import me.shedaniel.architectury.utils.NbtType;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class OverlayObject extends ScreenPainterObject {
	private final List<Component> text;
	private int color;

	public OverlayObject() {
		text = new ArrayList<>(1);
		color = 0x101010;
		w = Unit.fixed(Integer.MAX_VALUE);
		h = Unit.fixed(Integer.MAX_VALUE);
	}

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		if (properties.hasString("text")) {
			text.clear();
			text.add(Text.componentOf(properties.tag.get("text")));
		} else if (properties.has("text", NbtType.LIST)) {
			text.clear();

			for (Tag tag : (ListTag) properties.tag.get("text")) {
				text.add(Text.componentOf(tag));
			}
		}

		color = properties.getRGB("color", color);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		int maxWidth = (int) Math.min(w.get(), event.width / 4F);

		List<FormattedCharSequence> list = new ArrayList<>(text.size());
		int l = 10;

		for (Component t : text) {
			list.addAll(event.font.split(t, maxWidth));
		}

		int mw = 0;

		for (FormattedCharSequence s : list) {
			mw = Math.max(mw, event.font.width(s));
		}

		if (mw == 0) {
			return;
		}

		int p = 3;
		int aw = mw + p * 2;
		int ah = list.size() * l + p * 2 - 2;
		float ax = event.alignX(x.get(), aw, alignX);
		float ay = event.alignY(y.get(), ah, alignY);
		float az = z.get();

		int colFull = color | 0xFF000000;

		event.setTextureEnabled(false);
		event.beginQuads(DefaultVertexFormat.POSITION_COLOR);

		if (event.inventory) {
			event.rectangle(ax, ay, az, aw, ah, colFull);
			event.rectangle(ax, ay + 1, az, 1, ah - 2, 0x50000000);
			event.rectangle(ax + aw - 1, ay + 1, az, 1, ah - 2, 0x50000000);
			event.rectangle(ax, ay, az, aw, 1, 0x50000000);
			event.rectangle(ax, ay + ah - 1, az, aw, 1, 0x50000000);
		} else {
			event.rectangle(ax, ay, az, aw, ah, color | 0xC8000000);
			event.rectangle(ax, ay + 1, az, 1, ah - 2, colFull);
			event.rectangle(ax + aw - 1, ay + 1, az, 1, ah - 2, colFull);
			event.rectangle(ax, ay, az, aw, 1, colFull);
			event.rectangle(ax, ay + ah - 1, az, aw, 1, colFull);
		}

		event.end();
		event.setTextureEnabled(true);

		for (int i = 0; i < list.size(); i++) {
			event.rawText(list.get(i), ax + p, ay + i * l + p, 0xFFFFFFFF, true);
		}
	}
}