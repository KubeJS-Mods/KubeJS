package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.mojang.brigadier.StringReader;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EntitySelectorWrapper {
	private static final Map<String, EntitySelector> ENTITY_SELECTOR_CACHE = new HashMap<>();
	private static final EntitySelector ALL_ENTITIES_SELECTOR = new EntitySelector(EntitySelector.INFINITE, true, false, List.of(), MinMaxBounds.Doubles.ANY, Function.identity(), null, EntitySelectorParser.ORDER_RANDOM, false, null, null, null, true);

	public static EntitySelector of(EntitySelector selector) {
		return selector;
	}

	@HideFromJS
	public static EntitySelector wrap(@Nullable Object o) {
		if (o == null) {
			return ALL_ENTITIES_SELECTOR;
		} else if (o instanceof EntitySelector s) {
			return s;
		}

		String s = o.toString();

		if (s.isBlank()) {
			return ALL_ENTITIES_SELECTOR;
		}

		var sel = ENTITY_SELECTOR_CACHE.get(s);

		if (sel == null) {
			sel = ALL_ENTITIES_SELECTOR;

			try {
				sel = new EntitySelectorParser(new StringReader(s), true).parse();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		ENTITY_SELECTOR_CACHE.put(s, sel);
		return sel;
	}
}
