package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.rhino.util.RemapForJS;

public interface WithAttachedData<T> {
	@RemapForJS("getData")
	AttachedData<T> kjs$getData();
}