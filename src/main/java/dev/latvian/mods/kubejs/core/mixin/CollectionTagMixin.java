package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaList;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(CollectionTag.class)
public interface CollectionTagMixin extends CustomJavaToJsWrapper {
	@Override
	default Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo target) {
		return new NativeJavaList(cx, scope, this, (List) this, TypeInfo.RAW_LIST.withParams(TypeInfo.of(Tag.class)));
	}
}
