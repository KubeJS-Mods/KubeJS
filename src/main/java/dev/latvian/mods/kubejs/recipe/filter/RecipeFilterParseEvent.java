package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.rhino.Context;
import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.Map;

public class RecipeFilterParseEvent extends Event {
	public final Context cx;
	public final List<RecipeFilter> filters;
	public final Map<?, ?> map;

	public RecipeFilterParseEvent(Context cx, List<RecipeFilter> filters, Map<?, ?> map) {
		this.cx = cx;
		this.filters = filters;
		this.map = map;
	}
}
