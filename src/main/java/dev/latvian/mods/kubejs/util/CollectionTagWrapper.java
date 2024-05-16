package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaList;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;

public record CollectionTagWrapper(CollectionTag<?> tag) implements CustomJavaToJsWrapper {
	@Override
	public Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
		return new NativeJavaList(cx, scope, tag, tag, Tag.class, NBTUtils.VALUE_UNWRAPPER);
	}
}
