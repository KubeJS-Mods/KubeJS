package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.GameRulesKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(GameRules.class)
@RemapPrefixForJS("kjs$")
public abstract class GameRulesMixin implements GameRulesKJS {
	@Shadow
	public abstract <T> T get(GameRule<T> gameRule);

	@Shadow
	public abstract <T> void set(GameRule<T> gameRule, T value, @Nullable MinecraftServer server);

	@Shadow
	public abstract void visitGameRuleTypes(GameRuleTypeVisitor visitor);

	@Unique
	private Map<String, GameRule<?>> kjs$ruleCache;

	@Unique
	private void kjs$initCache() {
		if (kjs$ruleCache != null) {
			return;
		}

		kjs$ruleCache = new HashMap<>();

		visitGameRuleTypes(new GameRuleTypeVisitor() {
			@Override
			public <T> void visit(GameRule<T> gameRule) {
				kjs$ruleCache.put(gameRule.id(), gameRule);
				kjs$ruleCache.put(gameRule.getIdentifier().toString(), gameRule);
			}
		});
	}

	@Unique
	@Nullable
	private GameRule<?> kjs$getCachedRule(String rule) {
		kjs$initCache();
		return kjs$ruleCache.get(rule);
	}

	@Override
	@Nullable
	public MinecraftServer kjs$getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

	@Override
	@Nullable
	public GameRule<?> kjs$getRule(String rule) {
		GameRule<?> cached = kjs$getCachedRule(rule);
		if (cached != null) {
			return cached;
		}
		return GameRulesKJS.super.kjs$getRule(rule);
	}

	@Override
	public void kjs$set(String rule, String value) {
		GameRule<?> r = kjs$getRule(rule);
		if (r == null) {
			return;
		}

		GameRules self = (GameRules) (Object) this;
		MinecraftServer server = kjs$getServer();

		if (r.valueClass() == Boolean.class) {
			var parsed = ((GameRule<Boolean>) r).deserialize(value);
			Boolean v = parsed.result().orElse(null);
			if (v != null) {
				self.set((GameRule<Boolean>) r, v, server);
			}
		} else if (r.valueClass() == Integer.class) {
			var parsed = ((GameRule<Integer>) r).deserialize(value);
			Integer v = parsed.result().orElse(null);
			if (v != null) {
				self.set((GameRule<Integer>) r, v, server);
			}
		}
	}
}
