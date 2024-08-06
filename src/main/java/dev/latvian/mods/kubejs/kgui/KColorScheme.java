package dev.latvian.mods.kubejs.kgui;

import dev.latvian.mods.kubejs.kgui.drawable.KBox;
import dev.latvian.mods.kubejs.kgui.drawable.KColor;

public enum KColorScheme {
	DARK_GRAY(
		KColor.of(0xFF333334),
		KColor.of(0xFF48494A),
		KColor.of(0xFF5A5B5C),
		KColor.WHITE
	),

	LIGHT_GRAY(
		KColor.of(0xFF656465),
		KColor.of(0xFFC6C6C6),
		KColor.of(0xFFF7F7F7),
		KColor.DARK_GRAY
	),

	PURPLE(
		KColor.of(0xFF442CAA),
		KColor.of(0xFF6C51E3),
		KColor.of(0xFF986CF0),
		KColor.WHITE
	),

	;

	public static final KColorScheme[] VALUES = values();

	public final KColor fill;
	public final KColor background;
	public final KColor border;
	public final KColor textColor;

	public final KBox largeBox;
	public final KBox smallBox;
	public final KBox smallBoxHover;

	KColorScheme(
		KColor background,
		KColor fill,
		KColor border,
		KColor textColor
	) {
		this.background = background;
		this.fill = fill;
		this.border = border;
		this.textColor = textColor;

		largeBox = new KBox(3, this, KColor.BLACK);
		smallBox = new KBox(2, this, KColor.BLACK);
		smallBoxHover = new KBox(2, this, KColor.WHITE);
	}
}
