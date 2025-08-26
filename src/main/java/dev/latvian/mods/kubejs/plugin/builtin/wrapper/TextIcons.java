package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public interface TextIcons {
	ResourceLocation FONT = KubeJS.id("icons");
	Style STYLE = Style.EMPTY.withFont(FONT).applyFormat(ChatFormatting.WHITE);
	Component NAME = Component.empty().append(icons("K.")).append(Component.literal(KubeJS.MOD_NAME));
	String ALL_ICONS = "!+~ABCDEFIJKMNPQRTVWY";

	static MutableComponent icon(MutableComponent character) {
		return character.setStyle(STYLE);
	}

	static MutableComponent icons(String characters) {
		return Component.literal(characters).setStyle(STYLE);
	}

	static MutableComponent smallSpace() {
		return icons(".");
	}

	static MutableComponent error() {
		return icons("!");
	}

	static MutableComponent plus() {
		return icons("+");
	}

	static MutableComponent minus() {
		return icons("-");
	}

	static MutableComponent tilde() {
		return icons("~");
	}

	static MutableComponent crafting() {
		return icons("A");
	}

	static MutableComponent blockTagIcon() {
		return icons("B");
	}

	static MutableComponent copy() {
		return icons("C");
	}

	static MutableComponent id() {
		return icons("D");
	}

	static MutableComponent entityTypeTag() {
		return icons("E");
	}

	static MutableComponent fluidTag() {
		return icons("F");
	}

	static MutableComponent info() {
		return icons("I");
	}

	static MutableComponent itemTag() {
		return icons("J");
	}

	static MutableComponent logo() {
		return icons("K");
	}

	static MutableComponent camera() {
		return icons("M");
	}

	static MutableComponent no() {
		return icons("N");
	}

	static MutableComponent prototypeComponent() {
		return icons("P");
	}

	static MutableComponent patchedComponent() {
		return icons("Q");
	}

	static MutableComponent fire() {
		return icons("R");
	}

	static MutableComponent tag() {
		return icons("T");
	}

	static MutableComponent vscode() {
		return icons("V");
	}

	static MutableComponent warn() {
		return icons("W");
	}

	static MutableComponent yes() {
		return icons("Y");
	}

	static MutableComponent yes(boolean yes) {
		return icons(yes ? "Y" : "N");
	}
}
