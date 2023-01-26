package dev.latvian.mods.kubejs.bindings;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class DamageSourceWrapper {
	private static Map<String, DamageSource> damageSourceMap;

	public static DamageSource of(Object name) {
		if (name instanceof DamageSource damageSource) {
			return damageSource;
		}

		if (name instanceof Player player) {
			return DamageSource.playerAttack(player);
		}

		if (damageSourceMap == null) {
			damageSourceMap = new HashMap<>();

			try {
				for (var field : DamageSource.class.getDeclaredFields()) {
					field.setAccessible(true);

					if (Modifier.isStatic(field.getModifiers()) && field.getType() == DamageSource.class) {
						var s = (DamageSource) field.get(null);
						damageSourceMap.put(s.getMsgId(), s);
					}
				}
			} catch (Exception ignored) {
			}
		}

		return damageSourceMap.getOrDefault(String.valueOf(name), DamageSource.GENERIC);
	}
}
