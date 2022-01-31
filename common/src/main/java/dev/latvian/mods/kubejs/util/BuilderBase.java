package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public abstract class BuilderBase {
	public final ResourceLocation id;
	public String translationKey;
	public String displayName;

	public transient Set<ResourceLocation> defaultTags;

	public BuilderBase(String s) {
		id = UtilsJS.getMCID(KubeJS.appendModId(s));
		translationKey = getBuilderType() + "." + id.getNamespace() + "." + id.getPath();
		displayName = Arrays.stream(id.getPath().split("_")).map(UtilsJS::toTitleCase).collect(Collectors.joining(" "));
		defaultTags = new HashSet<>();
	}

	public abstract String getBuilderType();

	public BuilderBase translationKey(String key) {
		translationKey = key;
		return this;
	}

	public BuilderBase displayName(String name) {
		displayName = name;
		return this;
	}

	public BuilderBase tag(ResourceLocation tag) {
		defaultTags.add(tag);
		return this;
	}

	public ResourceLocation newID(String pre, String post) {
		if (pre.isEmpty() && post.isEmpty()) {
			return id;
		}

		return new ResourceLocation(id.getNamespace() + ':' + pre + id.getPath() + post);
	}
}
