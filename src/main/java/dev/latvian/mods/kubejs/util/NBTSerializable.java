package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.Context;
import net.minecraft.nbt.Tag;

public interface NBTSerializable {
	Tag toNBT(Context cx);
}