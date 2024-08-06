package dev.latvian.mods.kubejs.kgui;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum KGUIType implements StringRepresentable {
	GUI("gui", true, false, false),
	HUD("hud", false, true, false),
	INVENTORY("inventory", false, false, true),
	OVERLAY("overlay", false, true, true);

	public static final KGUIType[] VALUES = values();
	public static final StreamCodec<ByteBuf, KGUIType> STREAM_CODEC = ByteBufCodecs.idMapper(i -> VALUES[i], Enum::ordinal);

	public final String name;
	public final boolean gui;
	public final boolean hud;
	public final boolean inventory;

	KGUIType(String name, boolean gui, boolean hud, boolean inventory) {
		this.name = name;
		this.gui = gui;
		this.hud = hud;
		this.inventory = inventory;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
