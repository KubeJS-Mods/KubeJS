package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.nbt.Tag;

/**
 * @author LatvianModder
 */
public interface NBTSerializable {
	@RemapForJS("toNBT")
	Tag toNBTJS(Context cx);
}