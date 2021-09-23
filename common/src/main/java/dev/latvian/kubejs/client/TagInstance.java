package dev.latvian.kubejs.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class TagInstance {
	public final ResourceLocation tag;
	public boolean item = false;
	public boolean block = false;
	public boolean fluid = false;
	public boolean entity = false;

	public TagInstance(ResourceLocation i) {
		tag = i;
	}

	public Component toText() {
		StringBuilder builder = new StringBuilder(" #");
		builder.append(tag);
		builder.append(" [");
		boolean first = true;

		if (item) {
			first = false;
			builder.append("item");
		}

		if (block) {
			if (first) {
				first = false;
			} else {
				builder.append(" + ");
			}

			builder.append("block");
		}

		if (fluid) {
			if (first) {
				first = false;
			} else {
				builder.append(" + ");
			}

			builder.append("fluid");
		}

		if (entity) {
			if (first) {
				first = false;
			} else {
				builder.append(" + ");
			}

			builder.append("entity");
		}

		builder.append(']');

		return new TextComponent(builder.toString()).withStyle(ChatFormatting.DARK_GRAY);
	}
}
