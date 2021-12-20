package dev.latvian.mods.kubejs.server;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class GameRulesJS {
	private GameRules rules;
	private Map<String, GameRules.Key> cache;

	public GameRulesJS(GameRules r) {
		rules = r;
	}

	@Nullable
	private GameRules.Key getKey(String rule) {
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
	private Object get(String rule) {
		var key = getKey(rule);
		return key == null ? null : rules.getRule(key);
	}

	public String getString(String rule) {
		var o = get(rule);
		return o == null ? "" : String.valueOf(o);
	}

	public boolean getBoolean(String rule) {
		var o = get(rule);
		return o instanceof Boolean && (Boolean) o;
	}

	public int getInt(String rule) {
		var o = get(rule);
		return o instanceof Number ? ((Number) o).intValue() : 0;
	}

	public void set(String rule, Object value) {
		var nbt = rules.createTag();
		nbt.putString(rule, String.valueOf(value));
		rules = new GameRules(new Dynamic<>(NbtOps.INSTANCE, nbt)); //TODO: Check if works
	}
}