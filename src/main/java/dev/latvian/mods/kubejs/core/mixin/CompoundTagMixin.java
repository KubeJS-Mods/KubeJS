package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.util.NBTUtils;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(CompoundTag.class)
public abstract class CompoundTagMixin implements CustomJavaToJsWrapper {
	@Shadow
	@Final
	public Map<String, Tag> tags;

	@Override
	public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo target) {
		return new NativeJavaMap(cx, scope, this, tags, NBTUtils.STRING_TAG_MAP_TYPE);
	}
}
