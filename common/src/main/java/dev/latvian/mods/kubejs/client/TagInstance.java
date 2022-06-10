package dev.latvian.mods.kubejs.client;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class TagInstance {
	public final ResourceLocation tag;
	public final LinkedHashSet<ResourceKey<? extends Registry<?>>> registries = new LinkedHashSet<>();

	public TagInstance(ResourceLocation tag) {
		this.tag = tag;
	}

	public Component toText() {
		var string = " #" + tag + registries.stream()
				.map(ResourceKey::location)
				.map(id -> {
					if (id.getNamespace().equals("minecraft")) {
						return id.getPath();
					} else {
						return id.toString();
					}
				})
				.collect(Collectors.joining(" + ", " [", "]"));

		return Component.literal(string).withStyle(ChatFormatting.DARK_GRAY);
	}
}
