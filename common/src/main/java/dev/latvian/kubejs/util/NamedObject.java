package dev.latvian.kubejs.util;

import dev.latvian.mods.rhino.mod.util.JsonSerializable;

public interface NamedObject extends JsonSerializable {
	String getName();
}
