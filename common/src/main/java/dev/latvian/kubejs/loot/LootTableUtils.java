package dev.latvian.kubejs.loot;

import dev.latvian.kubejs.util.MapJS;
import org.jetbrains.annotations.Nullable;

public class LootTableUtils {
	public static MapJS createUniformNumberProvider(@Nullable Float min, @Nullable Float max, boolean includeType) {
		MapJS values = new MapJS();
		if (max != null) {
			values.put("max", max);
		}
		if (min != null) {
			values.put("min", min);
		}
		if (includeType) {
			values.put("type", "minecraft:uniform");
		}
		return values;
	}

	public static MapJS createBinomialNumberProvider(int n, float probability) {
		MapJS values = new MapJS();
		values.put("n", n);
		values.put("p", probability);
		values.put("type", "minecraft:binomial");
		return values;
	}
}
