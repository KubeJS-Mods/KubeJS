package dev.latvian.mods.kubejs.error;

import net.minecraft.tags.TagKey;

public class EmptyTagException extends KubeRuntimeException {
	public final TagKey<?> tagKey;

	public EmptyTagException(TagKey<?> tagKey) {
		super("Empty tag: " + tagKey.location());
		this.tagKey = tagKey;
	}
}
