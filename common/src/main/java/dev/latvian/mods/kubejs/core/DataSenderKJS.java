package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@RemapPrefixForJS("kjs$")
public interface DataSenderKJS {
	void kjs$sendData(String channel, @Nullable CompoundTag data);
}