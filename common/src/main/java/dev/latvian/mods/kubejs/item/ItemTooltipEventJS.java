package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.bindings.ComponentWrapper;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.mods.kubejs.util.ListJS;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemTooltipEventJS extends EventJS {
	@FunctionalInterface
	public interface StaticTooltipHandler {
		void tooltip(ItemStack stack, boolean advanced, List<Component> components);
	}

	@FunctionalInterface
	public interface StaticTooltipHandlerFromJS {
		void accept(ItemStackJS stack, boolean advanced, List<Object> text);
	}

	public static class StaticTooltipHandlerFromLines implements StaticTooltipHandler {
		public final List<Component> lines;
		private final IngredientJS ingredient;

		public StaticTooltipHandlerFromLines(List<Component> l, IngredientJS comparison) {
			lines = l;
			ingredient = comparison == null ? MatchAllIngredientJS.INSTANCE : comparison;
		}

		public StaticTooltipHandlerFromLines(Object o, IngredientJS comparison) {
			lines = new ArrayList<>();

			for (var o1 : ListJS.orSelf(o)) {
				lines.add(ComponentWrapper.of(o1));
			}

			ingredient = comparison == null ? MatchAllIngredientJS.INSTANCE : comparison;
		}

		@Override
		public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
			if (!ingredient.test(ItemStackJS.of(stack))) {
				return;
			}
			components.addAll(lines);
		}
	}

	public static class StaticTooltipHandlerFromJSWrapper implements StaticTooltipHandler {
		private final StaticTooltipHandlerFromJS handler;
		private final IngredientJS ingredient;

		public StaticTooltipHandlerFromJSWrapper(StaticTooltipHandlerFromJS h, IngredientJS comparison) {
			handler = h;
			ingredient = (comparison == null) ? MatchAllIngredientJS.INSTANCE : comparison;
		}

		@Override
		public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
			List<Object> text = new ArrayList<>(components);
			final ItemStackJS itemStack = ItemStackJS.of(stack);
			if (ingredient != null && !ingredient.test(itemStack)) {
				return;
			}
			handler.accept(itemStack, advanced, text);

			components.clear();

			for (var o : text) {
				components.add(ComponentWrapper.of(o));
			}
		}
	}

	private final Map<Item, List<StaticTooltipHandler>> map;

	public ItemTooltipEventJS(Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> m) {
		map = m;
	}

	public void add(Object item, Object text) {
		if ("*".equals(item)) {
			addToAll(text);
			return;
		}

		final var ingredient = IngredientJS.of(item);

		var l = new StaticTooltipHandlerFromLines(text, ingredient);

		if (!l.lines.isEmpty()) {
			for (var i : ingredient.getVanillaItems()) {
				if (i != Items.AIR) {
					map.computeIfAbsent(i, k -> new ArrayList<>()).add(l);
				}
			}
		}
	}

	public void addToAll(Object text) {
		var l = new StaticTooltipHandlerFromLines(text, null);

		if (!l.lines.isEmpty()) {
			map.computeIfAbsent(Items.AIR, k -> new ArrayList<>()).add(l);
		}
	}

	public void addAdvanced(Object item, StaticTooltipHandlerFromJS handler) {
		if ("*".equals(item)) {
			addAdvancedToAll(handler);
			return;
		}

		final var ingredient = IngredientJS.of(item);

		var l = new StaticTooltipHandlerFromJSWrapper(handler, ingredient);

		for (var i : ingredient.getVanillaItems()) {
			if (i != Items.AIR) {
				map.computeIfAbsent(i, k -> new ArrayList<>()).add(l);
			}
		}
	}

	public void addAdvancedToAll(StaticTooltipHandlerFromJS handler) {
		map.computeIfAbsent(Items.AIR, k -> new ArrayList<>()).add(new StaticTooltipHandlerFromJSWrapper(handler, null));
	}

	public boolean isShift() {
		return Screen.hasShiftDown();
	}

	public boolean isCtrl() {
		return Screen.hasControlDown();
	}

	public boolean isAlt() {
		return Screen.hasAltDown();
	}

}