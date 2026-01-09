package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.betteradvancedtooltips.BATIcons;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public interface TextIcons {
	ResourceLocation FONT = KubeJS.id("icons");
	Style STYLE = Style.EMPTY.withFont(FONT).applyFormat(ChatFormatting.WHITE);

	Component CRAFTING = Component.literal("A").setStyle(STYLE);
	Component LOGO = Component.literal("K").setStyle(STYLE);
	Component VSCODE = Component.literal("V").setStyle(STYLE);

	Component NAME = Component.empty().append(LOGO).append(BATIcons.SMALL_SPACE).append(Component.literal(KubeJS.MOD_NAME));

	String ALL_ICONS = "AKV";

	static Component smallSpace() {
		return BATIcons.SMALL_SPACE;
	}

	static Component error() {
		return BATIcons.ERROR;
	}

	static Component plus() {
		return BATIcons.PLUS;
	}

	static Component minus() {
		return BATIcons.MINUS;
	}

	static Component tilde() {
		return BATIcons.TILDE;
	}

	static Component crafting() {
		return CRAFTING;
	}

	static Component copy() {
		return BATIcons.COPY;
	}

	static Component id() {
		return BATIcons.ID;
	}

	static Component info() {
		return BATIcons.INFO;
	}

	static Component logo() {
		return LOGO;
	}

	static Component camera() {
		return BATIcons.CAMERA;
	}

	static Component no() {
		return BATIcons.NO;
	}

	static Component prototypeComponent() {
		return BATIcons.PROTOTYPE_COMPONENT;
	}

	static Component patchedComponent() {
		return BATIcons.PATCHED_COMPONENT;
	}

	static Component fire() {
		return BATIcons.FIRE;
	}

	static Component tag() {
		return BATIcons.TAG;
	}

	static Component vscode() {
		return VSCODE;
	}

	static Component warn() {
		return BATIcons.WARN;
	}

	static Component yes() {
		return BATIcons.YES;
	}

	static Component yes(boolean yes) {
		return yes ? BATIcons.YES : BATIcons.NO;
	}
}
