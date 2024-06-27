package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.nbt.Tag;

public interface WithCodec extends NBTSerializable, JsonSerializable {
	Codec<?> getCodec(Context cx);

	@Override
	default Tag toNBT(Context cx) {
		return getCodec(cx).encodeStart(((KubeJSContext) cx).getRegistries().nbt(), Cast.to(this)).getOrThrow();
	}

	@Override
	default JsonElement toJson(Context cx) {
		return getCodec(cx).encodeStart(((KubeJSContext) cx).getRegistries().json(), Cast.to(this)).getOrThrow();
	}
}