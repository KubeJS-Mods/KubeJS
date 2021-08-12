package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.latvian.kubejs.client.painter.RenderObjectProperties;
import dev.latvian.kubejs.text.Text;
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
		w = Integer.MAX_VALUE;
		h = Integer.MAX_VALUE;
	}

	@Override
	protected void load(RenderObjectProperties properties) {
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
		int maxWidth = (int) Math.min(w, event.width / 4F);

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
		float ax = event.alignX(x, aw, alignX);
		float ay = event.alignY(y, ah, alignY);

		int colFull = color | 0xFF000000;

		event.setTextureEnabled(false);
		event.beginQuads(DefaultVertexFormat.POSITION_COLOR);

		if (event.inventory) {
			event.rectangle(ax, ay, z, aw, ah, colFull);
			event.rectangle(ax, ay + 1, z, 1, ah - 2, 0x50000000);
			event.rectangle(ax + aw - 1, ay + 1, z, 1, ah - 2, 0x50000000);
			event.rectangle(ax, ay, z, aw, 1, 0x50000000);
			event.rectangle(ax, ay + ah - 1, z, aw, 1, 0x50000000);
		} else {
			event.rectangle(ax, ay, z, aw, ah, color | 0xC8000000);
			event.rectangle(ax, ay + 1, z, 1, ah - 2, colFull);
			event.rectangle(ax + aw - 1, ay + 1, z, 1, ah - 2, colFull);
			event.rectangle(ax, ay, z, aw, 1, colFull);
			event.rectangle(ax, ay + ah - 1, z, aw, 1, colFull);
		}

		event.end();
		event.setTextureEnabled(true);

		for (int i = 0; i < list.size(); i++) {
			event.rawText(list.get(i), x + p, y + i * l + p, 0xFFFFFFFF, true);
		}
	}
}