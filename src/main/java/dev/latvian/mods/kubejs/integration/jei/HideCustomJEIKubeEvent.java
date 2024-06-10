package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.RegExpJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
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

public class HideCustomJEIKubeEvent implements KubeEvent {
	private final IJeiRuntime runtime;
	private final HashMap<IIngredientType<?>, HideJEIKubeEvent<?>> events;

	public HideCustomJEIKubeEvent(IJeiRuntime r) {
		runtime = r;
		events = new HashMap<>();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public HideJEIKubeEvent get(Context cx, IIngredientType s) {
		return events.computeIfAbsent(s, type -> new HideJEIKubeEvent(runtime, type, o -> {
			Function<Object, String> idFn = it -> runtime.getIngredientManager().getIngredientHelper(Cast.to(type)).getUniqueId(it, UidContext.Ingredient);
			List<Predicate> predicates = new ArrayList<>();

			for (Object o1 : ListJS.orSelf(o)) {
				var regex = RegExpJS.wrap(o1);
				if (regex != null) {
					predicates.add(it -> regex.asPredicate().test(idFn.apply(it)));
				} else if (o1 instanceof Predicate p) {
					predicates.add(p);
				} else if (o instanceof BaseFunction f) {
					predicates.add(UtilsJS.makeFunctionProxy(cx, TypeInfo.RAW_PREDICATE, f));
				} else if (o1 instanceof CharSequence || o1 instanceof ResourceLocation) {
					predicates.add(it -> Objects.equals(idFn.apply(it), o1.toString()));
				} else {
					predicates.add(Predicate.isEqual(o1));
				}
			}

			return (Predicate) (it) -> {
				for (Predicate p : predicates) {
					if (p.test(it)) {
						return true;
					}
				}
				return false;
			};
		}, o -> true));
	}

	@Override
	public void afterPosted(EventResult result) {
		for (var eventJS : events.values()) {
			eventJS.afterPosted(result);
		}
	}
}