package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.error.RecipeComponentTooLargeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.IntBounds;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record ListRecipeComponent<T>(
	RecipeComponent<T> component,
	boolean canWriteSelf,
	TypeInfo listTypeInfo,
	Codec<List<T>> listCodec,
	boolean conditional,
	IntBounds bounds,
	Optional<RecipeComponent<?>> spread
) implements RecipeComponent<List<T>> {
	static <L> ListRecipeComponent<L> create(RecipeComponent<L> component, boolean canWriteSelf, boolean conditional) {
		return create(component, canWriteSelf, conditional, IntBounds.DEFAULT, Optional.empty());
	}

	static <L> ListRecipeComponent<L> create(RecipeComponent<L> component, boolean canWriteSelf, boolean conditional, IntBounds bounds, Optional<RecipeComponent<?>> spread) {
		var typeInfo = component.typeInfo();
		var codec = component.codec();
		var listCodec = conditional ? NeoForgeExtraCodecs.listWithOptionalElements(ConditionalOps.createConditionalCodec(codec)) : codec.listOf();
		var listTypeInfo = TypeInfo.RAW_LIST.withParams(typeInfo);

		if (canWriteSelf) {
			listCodec = KubeJSCodecs.listOfOrSelf(listCodec, codec);
			listTypeInfo = listTypeInfo.or(typeInfo);
		}

		return new ListRecipeComponent<>(component, canWriteSelf, listTypeInfo, listCodec, conditional, bounds, spread);
	}

	public static final RecipeComponentType<?> TYPE = RecipeComponentType.<ListRecipeComponent<?>>dynamic(KubeJS.id("list"), (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ctx.recipeComponentCodec().fieldOf("component").forGetter(ListRecipeComponent::component),
		Codec.BOOL.optionalFieldOf("can_write_self", false).forGetter(ListRecipeComponent::canWriteSelf),
		Codec.BOOL.optionalFieldOf("conditional", false).forGetter(ListRecipeComponent::conditional),
		IntBounds.MAP_CODEC.forGetter(ListRecipeComponent::bounds),
		ctx.recipeComponentCodec().optionalFieldOf("spread").forGetter(ListRecipeComponent::spread)
	).apply(instance, ListRecipeComponent::create)));

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	@Override
	public Codec<List<T>> codec() {
		return listCodec;
	}

	@Override
	public TypeInfo typeInfo() {
		return listTypeInfo;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Iterable<?> || from != null && from.getClass().isArray();
	}

	public static <T> List<T> wrap0(Context cx, KubeRecipe recipe, RecipeComponent<T> component, Object from) {
		if (from instanceof Iterable<?> iterable) {
			int size;

			if (iterable instanceof Collection<?> c) {
				size = c.size();
			} else if (iterable instanceof JsonArray a) {
				size = a.size();
			} else {
				size = -1;
			}

			if (size == 0) {
				return List.of();
			} else if (size == 1) {
				return List.of(component.wrap(cx, recipe, iterable.iterator().next()));
			} else if (size == 2) {
				var itr = iterable.iterator();
				return List.of(component.wrap(cx, recipe, itr.next()), component.wrap(cx, recipe, itr.next()));
			} else if (size > 0) {
				var arr = new ArrayList<T>(size);

				for (var e : iterable) {
					arr.add(component.wrap(cx, recipe, e));
				}

				return arr;
			} else {
				var list = new ArrayList<T>();

				for (var e : iterable) {
					list.add(component.wrap(cx, recipe, e));
				}

				return list;
			}
		} else if (from.getClass().isArray()) {
			int length = Array.getLength(from);

			if (length == 0) {
				return List.of();
			}

			var arr = new ArrayList<T>(length);

			for (int i = 0; i < length; i++) {
				arr.add(component.wrap(cx, recipe, Array.get(from, i)));
			}

			return arr;
		}

		return List.of(component.wrap(cx, recipe, from));
	}

	@Override
	public List<T> wrap(Context cx, KubeRecipe recipe, Object from) {
		if (spread.isPresent()) {
			var spreadComponent = spread.get();
			var original = wrap0(cx, recipe, spreadComponent, from);
			var result = new ArrayList<T>();

			for (var o : original) {
				for (var s : spreadComponent.spread(Cast.to(o))) {
					result.add(component.wrap(cx, recipe, s));
				}
			}

			return result;
		} else {
			return wrap0(cx, recipe, component, from);
		}
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, List<T> value, ReplacementMatchInfo match) {
		for (var v : value) {
			if (component.matches(cx, recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<T> replace(Context cx, KubeRecipe recipe, List<T> original, ReplacementMatchInfo match, Object with) {
		var arr = original;

		for (int i = 0; i < original.size(); i++) {
			var r = component.replace(cx, recipe, original.get(i), match, with);

			if (arr.get(i) != r) {
				if (arr == original) {
					arr = new ArrayList<>(original);
				}

				if (arr != original) {
					arr.set(i, r);
				}
			}
		}

		return arr;
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, List<T> value) {
		for (int i = 0; i < value.size(); i++) {
			if (i > 0) {
				builder.appendSeparator();
			}

			component.buildUniqueId(builder, value.get(i));
		}
	}

	@Override
	public String toString() {
		return component + (canWriteSelf ? "[?]" : "[]") + (conditional ? "?" : "");
	}

	@Override
	public void validate(ValidationContext ctx, List<T> value) {
		RecipeComponent.super.validate(ctx, value);

		if (value.size() > bounds.max()) {
			throw new RecipeComponentTooLargeException(this, value, value.size(), bounds.max());
		}

		ctx.stack().push(this);

		for (int i = 0; i < value.size(); i++) {
			ctx.stack().setKey(i);
			component.validate(ctx, value.get(i));
		}

		ctx.stack().pop();
	}

	@Override
	public boolean allowEmpty() {
		return bounds.min() <= 0;
	}

	@Override
	public boolean isEmpty(List<T> value) {
		return value.isEmpty();
	}

	@Override
	public List<?> spread(List<T> value) {
		return value;
	}
}
