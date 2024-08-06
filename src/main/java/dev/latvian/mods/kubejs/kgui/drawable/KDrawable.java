package dev.latvian.mods.kubejs.kgui.drawable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface KDrawable {
	KDrawable EMPTY = (ctx, x, y, w, h) -> {
	};

	static KDrawable read(StringReader reader) throws CommandSyntaxException {
		return EMPTY;
	}

	void draw(KDrawContext ctx, int x, int y, int w, int h);
}
