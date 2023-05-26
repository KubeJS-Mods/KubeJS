package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface DataSenderKJS {
	default void kjs$sendData(String channel, @Nullable CompoundTag data) {
		throw new NoMixinException();
	}
}