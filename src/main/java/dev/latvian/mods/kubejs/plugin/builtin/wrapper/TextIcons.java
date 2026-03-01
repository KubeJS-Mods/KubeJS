package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

//import dev.latvian.mods.betteradvancedtooltips.BATIcons;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public interface TextIcons {
	Identifier FONT = KubeJS.id("icons");
	Style STYLE = Style.EMPTY.withFont(new FontDescription.Resource(FONT)).applyFormat(ChatFormatting.WHITE);

	Component CRAFTING = Component.literal("A").setStyle(STYLE);
	Component LOGO = Component.literal("K").setStyle(STYLE);
	Component VSCODE = Component.literal("V").setStyle(STYLE);

	Component NAME = Component.empty().append(LOGO).append(Component.literal(KubeJS.MOD_NAME));

	String ALL_ICONS = "AKV";

	static Component smallSpace() {
		return Component.empty();
	}

	static Component error() {
		return Component.empty();
	}

	static Component plus() {
		return Component.empty();
	}

	static Component minus() {
		return Component.empty();
	}

	static Component tilde() {
		return Component.empty();
	}

	static Component crafting() {
		return CRAFTING;
	}

	static Component copy() {
		return Component.empty();
	}

	static Component id() {
		return Component.empty();
	}

	static Component info() {
		return Component.empty();
	}

	static Component logo() {
		return LOGO;
	}

	static Component camera() {
		return Component.empty();
	}

	static Component no() {
		return Component.empty();
	}

	static Component prototypeComponent() {
		return Component.empty();
	}

	static Component patchedComponent() {
		return Component.empty();
	}

	static Component fire() {
		return Component.empty();
	}

	static Component tag() {
		return Component.empty();
	}

	static Component vscode() {
		return VSCODE;
	}

	static Component warn() {
		return Component.empty();
	}

	static Component yes() {
		return Component.empty();
	}

	static Component yes(boolean yes) {
		return Component.empty();
	}
}