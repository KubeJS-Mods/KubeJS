package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.GameRulesKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
	private @Nullable Map<String, GameRule<?>> kjs$ruleCache;

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
		return Objects.requireNonNull(kjs$ruleCache).get(rule);
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
}
