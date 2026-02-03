package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

public interface GameRulesKJS {
	@Nullable
	MinecraftServer kjs$getServer();

	@Nullable
	default GameRule<?> kjs$getRule(String rule) {
		Identifier id = Identifier.tryParse(rule);
		if (id == null) {
			id = Identifier.fromNamespaceAndPath("minecraft", rule);
		}
		return BuiltInRegistries.GAME_RULE.getOptional(id).orElse(null);
	}

	default String kjs$getString(String rule) {
		GameRule<?> r = kjs$getRule(rule);
		if (r == null) {
			return "";
		}
		return kjs$serialize((GameRules) (Object) this, r);
	}

	default boolean kjs$getBoolean(String rule) {
		GameRule<?> r = kjs$getRule(rule);
		if (r == null) {
			return false;
		}
		Object v = ((GameRules) (Object) this).get((GameRule<Object>) r);
		return v instanceof Boolean b && b;
	}

	default int kjs$getInt(String rule) {
		GameRule<?> r = kjs$getRule(rule);
		if (r == null) {
			return 0;
		}
		Object v = ((GameRules) (Object) this).get((GameRule<Object>) r);
		return v instanceof Integer i ? i : 0;
	}

	default void kjs$set(String rule, String value) {
		GameRule<?> r = kjs$getRule(rule);
		if (r == null) {
			return;
		}

		GameRules self = (GameRules) (Object) this;

		if (r.valueClass() == Boolean.class) {
			DataResult<Boolean> parsed = ((GameRule<Boolean>) r).deserialize(value);
			Boolean v = parsed.result().orElse(null);
			if (v != null) {
				self.set((GameRule<Boolean>) r, v, kjs$getServer());
			}
		} else if (r.valueClass() == Integer.class) {
			DataResult<Integer> parsed = ((GameRule<Integer>) r).deserialize(value);
			Integer v = parsed.result().orElse(null);
			if (v != null) {
				self.set((GameRule<Integer>) r, v, kjs$getServer());
			}
		}
	}

	static <T> String kjs$serialize(GameRules rules, GameRule<T> rule) {
		return rule.serialize(rules.get(rule));
	}
}
