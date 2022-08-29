package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class GameRulesJS {
	private final GameRules rules;
	private Map<String, GameRules.Key<?>> cache;

	public GameRulesJS(GameRules r) {
		rules = r;
	}

	@Nullable
	public GameRules.Key<?> getKey(String rule) {
		if (cache == null) {
			cache = new HashMap<>();

			GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
				@Override
				public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
					cache.put(key.toString(), key);
				}
			});
		}

		return cache.get(rule);
	}

	@Nullable
	public GameRules.Value<?> get(String rule) {
		var key = getKey(rule);
		return key == null ? null : rules.getRule(key);
	}

	public String getString(String rule) {
		var o = get(rule);
		return o == null ? "" : o.serialize();
	}

	public boolean getBoolean(String rule) {
		return Boolean.parseBoolean(getString(rule));
	}

	public int getInt(String rule) {
		return UtilsJS.parseInt(getString(rule), 0);
	}

	public void set(String rule, Object value) {
		var gameRule = get(rule);
		if (gameRule != null) {
			gameRule.deserialize(String.valueOf(value));
		}
	}
}