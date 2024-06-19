package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public interface TextIcons {
	ResourceLocation FONT = KubeJS.id("icons");
	Component NAME = Component.empty().append(Component.literal("K.").kjs$font(FONT).kjs$white()).append(Component.literal(KubeJS.MOD_NAME));

	static MutableComponent icon(MutableComponent character) {
		return character.kjs$font(FONT);
	}

	static MutableComponent smallSpace() {
		return icon(Component.literal("."));
	}

	static MutableComponent blockTagIcon() {
		return icon(Component.literal("B"));
	}

	static MutableComponent copy() {
		return icon(Component.literal("C"));
	}

	static MutableComponent id() {
		return icon(Component.literal("D"));
	}

	static MutableComponent entityTypeTag() {
		return icon(Component.literal("E"));
	}

	static MutableComponent fluidTag() {
		return icon(Component.literal("F"));
	}

	static MutableComponent info() {
		return icon(Component.literal("I"));
	}

	static MutableComponent itemTag() {
		return icon(Component.literal("J"));
	}

	static MutableComponent logo() {
		return icon(Component.literal("K"));
	}

	static MutableComponent no() {
		return icon(Component.literal("N"));
	}

	static MutableComponent prototypeComponent() {
		return icon(Component.literal("P"));
	}

	static MutableComponent patchedComponent() {
		return icon(Component.literal("Q"));
	}

	static MutableComponent tag() {
		return icon(Component.literal("T"));
	}

	static MutableComponent warn() {
		return icon(Component.literal("W"));
	}

	static MutableComponent yes() {
		return icon(Component.literal("Y"));
	}

	static MutableComponent yes(boolean yes) {
		return icon(yes ? Component.literal("Y") : Component.literal("N"));
	}
}
