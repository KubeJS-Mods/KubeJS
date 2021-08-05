package dev.latvian.kubejs.loot;

import dev.latvian.kubejs.util.JsonSerializable;
import org.jetbrains.annotations.Nullable;

public interface LootElement extends JsonSerializable {
	@Nullable
	String getName();
}
