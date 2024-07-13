package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import dev.latvian.mods.rhino.Context;
import net.minecraft.nbt.Tag;

public interface WithCodec extends NBTSerializable, JsonSerializable {
	Codec<?> getCodec(Context cx);

	@Override
	default Tag toNBT(Context cx) {
		return getCodec(cx).encodeStart(RegistryAccessContainer.of(cx).nbt(), Cast.to(this)).getOrThrow();
	}

	@Override
	default JsonElement toJson(Context cx) {
		return getCodec(cx).encodeStart(RegistryAccessContainer.of(cx).json(), Cast.to(this)).getOrThrow();
	}
}