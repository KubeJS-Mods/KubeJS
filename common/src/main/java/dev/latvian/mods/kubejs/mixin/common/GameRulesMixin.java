package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.GameRulesKJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(GameRules.class)
@RemapPrefixForJS("kjs$")
public abstract class GameRulesMixin implements GameRulesKJS {
	@Shadow
	public abstract <T extends GameRules.Value<T>> T getRule(GameRules.Key<T> key);

	private Map<String, GameRules.Key<?>> kjs$keyCache;

	@Nullable
	private GameRules.Key<?> getKey(String rule) {
		if (kjs$keyCache == null) {
			kjs$keyCache = new HashMap<>();

			GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
				@Override
				public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
					kjs$keyCache.put(key.toString(), key);
				}
			});
		}

		return kjs$keyCache.get(rule);
	}

	@Override
	@Nullable
	public GameRules.Value<?> kjs$get(String rule) {
		var key = getKey(rule);
		return key == null ? null : getRule(key);
	}

	@Override
	public void kjs$set(String rule, String value) {
		var key = getKey(rule);
		var r = key == null ? null : getRule(key);

		if (r != null) {
			r.deserialize(value);

			if (UtilsJS.staticServer != null) {
				r.onChanged(UtilsJS.staticServer);
			}
		}
	}
}
