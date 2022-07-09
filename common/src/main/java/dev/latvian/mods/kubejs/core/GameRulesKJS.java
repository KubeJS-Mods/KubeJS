package dev.latvian.mods.kubejs.core;

import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

public interface GameRulesKJS {
	@Nullable
	GameRules.Value<?> kjs$get(String rule);

	void kjs$set(String rule, String value);

	default String kjs$getString(String rule) {
		var o = kjs$get(rule);
		return o == null ? "" : o.serialize();
	}

	default boolean kjs$getBoolean(String rule) {
		var o = kjs$get(rule);
		return o instanceof GameRules.BooleanValue v && v.get();
	}

	default int kjs$getInt(String rule) {
		var o = kjs$get(rule);
		return o instanceof GameRules.IntegerValue v ? v.get() : 0;
	}
}
