package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.BaseFunction;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class HideCustomJEIEventJS extends EventJS {
	private final IJeiRuntime runtime;
	private final HashMap<IIngredientType<?>, HideJEIEventJS<?>> events;

	public HideCustomJEIEventJS(IJeiRuntime r) {
		runtime = r;
		events = new HashMap<>();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public HideJEIEventJS get(IIngredientType s) {
		return events.computeIfAbsent(s, type -> {
			return new HideJEIEventJS(runtime, type, o -> {
				Function<Object, String> idFn = it -> runtime.getIngredientManager().getIngredientHelper(UtilsJS.cast(type)).getUniqueId(it, UidContext.Ingredient);
				List<Predicate> predicates = new ArrayList<>();

				for (Object o1 : ListJS.orSelf(o)) {
					var regex = UtilsJS.parseRegex(o1);
					if (regex != null) {
						predicates.add(it -> regex.asPredicate().test(idFn.apply(it)));
					} else if (o1 instanceof Predicate p) {
						predicates.add(p);
					} else if (o instanceof BaseFunction f) {
						predicates.add(UtilsJS.makeFunctionProxy(ScriptType.CLIENT, Predicate.class, f));
					} else if (o1 instanceof CharSequence || o1 instanceof ResourceLocation) {
						predicates.add(it -> Objects.equals(idFn.apply(it), o1.toString()));
					} else {
						predicates.add(Predicate.isEqual(o1));
					}
				}

				return (Predicate) (it) -> {
					for (Predicate p : predicates) {
						if (p.test(it)) return true;
					}
					return false;
				};
			}, o -> true);
		});
	}

	@Override
	protected void afterPosted(EventResult result) {
		for (var eventJS : events.values()) {
			eventJS.afterPosted(result);
		}
	}
}