package dev.latvian.kubejs.util;

import dev.latvian.kubejs.util.JsonSerializable;
import org.jetbrains.annotations.Nullable;

public interface NamedObject extends JsonSerializable {
	String getName();
}
