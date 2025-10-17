package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.kubejs.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.ingredient.NamespaceIngredient;
import dev.latvian.mods.kubejs.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Info("Various Ingredient related helper methods")
public interface IngredientWrapper {
	TypeInfo TYPE_INFO = TypeInfo.of(Ingredient.class);

	@Info("A completely empty ingredient that will only match air")
	Ingredient none = Ingredient.EMPTY;

	@Info("An ingredient that matches everything")
	Ingredient all = WildcardIngredient.INSTANCE.toVanilla();

	@Info("Returns an ingredient of the input")
	static Ingredient of(Ingredient ingredient) {
		return ingredient;
	}

	@Info("Returns an ingredient of the input, with the specified count")
	static SizedIngredient of(Ingredient ingredient, int count) {
		return ingredient.kjs$withCount(count);
	}

	@HideFromJS
	private static Ingredient wrapTrivial(@Nullable Object from) {
		while (from instanceof Wrapper w) {
			from = w.unwrap();
		}

		return switch (from) {
			case null -> Ingredient.EMPTY;
			case Ingredient id -> id;
			case ItemStack s when s.isEmpty() -> Ingredient.EMPTY;
			case ItemLike i when i.asItem() == Items.AIR -> Ingredient.EMPTY;
			case IngredientSupplierKJS ingr -> ingr.kjs$asIngredient();
			case ItemLike i -> Ingredient.of(i);
			case TagKey<?>(var reg, var location) -> Ingredient.of(ItemTags.create(location));
			default -> null;
		};
	}

	@HideFromJS
	static DataResult<Ingredient> wrapResult(Context cx, @Nullable Object from) {
		while (from instanceof Wrapper w) {
			from = w.unwrap();
		}

		var trivial = wrapTrivial(from);
		if (trivial != null) {
			return DataResult.success(trivial);
		}

		if (from instanceof Pattern || from instanceof NativeRegExp) {
			var str = String.valueOf(from);
			return Optional.ofNullable(RegExpKJS.wrap(from))
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "Invalid regex " + str))
				.map(RegExIngredient::new)
				.map(ICustomIngredient::toVanilla)
				;
		} else if (from instanceof JsonElement json) {
			return parseJson(cx, json);
		} else if (from instanceof CharSequence) {
			return parseString(cx, from.toString());
		}

		List<?> list = ListJS.of(from);

		if (list != null) {
			List<Ingredient> results = new ArrayList<>(list.size());
			var failed = false;
			Stream.Builder<String> errors = Stream.builder();

			for (var o1 : list) {
				var ingredient = wrapResult(cx, o1);

				ingredient.resultOrPartial()
					.filter(ingr -> ingr != Ingredient.EMPTY)
					.ifPresent(results::add);

				if (ingredient.isError()) {
					failed = true;
					errors.add(o1 + ": " + ingredient.error().orElseThrow().message());
				}
			}

			if (failed) {
				var msg = errors.build().collect(Collectors.joining("; "));
				return DataResult.error(() -> "Failed to parse ingredient list: " + msg);
			} else {
				return DataResult.success(switch (results.size()) {
					case 0 -> Ingredient.EMPTY;
					case 1 -> results.getFirst();
					default -> new CompoundIngredient(results).toVanilla();
				});
			}
		}

		var map = cx.optionalMapOf(from);

		if (map != null) {
			return Ingredient.CODEC.parse(JavaOps.INSTANCE, map);
		}

		return ItemWrapper.wrapResult(cx, from).map(ItemStackKJS::kjs$asIngredient);
	}

	@HideFromJS
	static Ingredient wrap(Context cx, @Nullable Object from) {
		var trivial = wrapTrivial(from);
		if (trivial != null) {
			return trivial;
		}

		return wrapResult(cx, from)
			.resultOrPartial(error -> ConsoleJS.getCurrent(cx).error("Failed to read ingredient from %s: %s".formatted(from, error)))
			.orElse(Ingredient.EMPTY);
	}

	static boolean isIngredientLike(Object from) {
		return from instanceof Ingredient || from instanceof SizedIngredient || from instanceof ItemStack;
	}

	static DataResult<Ingredient> parseJson(Context cx, JsonElement json) {
		return switch (json) {
			case null -> DataResult.success(Ingredient.EMPTY);
			case JsonNull jsonNull -> DataResult.success(Ingredient.EMPTY);
			case JsonArray arr when arr.isEmpty() -> DataResult.success(Ingredient.EMPTY);
			case JsonPrimitive primitive -> wrapResult(cx, json.getAsString());
			default -> Ingredient.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);
		};
	}

	static DataResult<Ingredient> parseString(Context cx, String s) {
		return switch (s) {
			case "", "-", "air", "minecraft:air" -> DataResult.success(Ingredient.EMPTY);
			case "*" -> DataResult.success(IngredientWrapper.all);
			default -> read(cx, new StringReader(s));
		};
	}

	static DataResult<Ingredient> read(Context cx, StringReader reader) {
		var registries = RegistryAccessContainer.of(cx);

		reader.skipWhitespace();

		if (!reader.canRead()) {
			return DataResult.success(Ingredient.EMPTY);
		}

		return switch (reader.peek()) {
			case '-' -> {
				reader.skip();
				yield DataResult.success(Ingredient.EMPTY);
			}
			case '*' -> {
				reader.skip();
				yield DataResult.success(IngredientWrapper.all);
			}
			case '#' -> {
				reader.skip();
				// yield new TagIngredient(registries.cachedItemTags, ItemTags.create(ID.read(reader))).toVanilla();
				yield ID.read(reader).map(ItemTags::create).map(Ingredient::of);
			}
			case '@' -> {
				reader.skip();
				yield DataResult.success(new NamespaceIngredient(reader.readUnquotedString()).toVanilla());
			}
			case '%' -> {
				reader.skip();
				yield ID.read(reader)
					.flatMap(input -> {
						var tab = UtilsJS.findCreativeTab(input);
						return tab != null ? DataResult.success(tab) : DataResult.error(() -> "Creative tab " + input + " does not exist!");
					})
					.map(group -> new CreativeTabIngredient(group).toVanilla());
			}
			case '/' -> {
				try {
					var regex = RegExpKJS.read(reader);
					yield DataResult.success(new RegExIngredient(regex).toVanilla());
				} catch (IllegalArgumentException e) {
					yield DataResult.error(() -> "Could not parse regex ingredient: " + e);
				}
			}
			case '[' -> {
				reader.skip();
				reader.skipWhitespace();

				if (!reader.canRead() || reader.peek() == ']') {
					yield DataResult.success(Ingredient.EMPTY);
				}

				var ingredients = new ArrayList<Ingredient>(2);

				while (true) {
					var ingredient = read(cx, reader);

					if (ingredient.isSuccess()) {
						ingredients.add(ingredient.getOrThrow());
					} else {
						yield DataResult.error(() -> "Invalid ingredient in list: " + ingredient.error().orElseThrow().message());
					}

					reader.skipWhitespace();

					if (reader.canRead() && reader.peek() == ',') {
						reader.skip();
						reader.skipWhitespace();
					} else if (!reader.canRead() || reader.peek() == ']') {
						break;
					}
				}

				if (!reader.canRead() || reader.peek() != ']') {
					yield DataResult.error(() -> "Unterminated compound ingredient");
				}

				reader.skip();
				reader.skipWhitespace();

				yield DataResult.success(new CompoundIngredient(ingredients).toVanilla());
			}
			default -> {
				var item = ID.read(reader).flatMap(ItemWrapper::findItem);

				var next = reader.canRead() ? reader.peek() : 0;

				if (next == '[' || next == '{') {
					try {
						var components = DataComponentWrapper.readPredicate(registries.nbt(), reader);

						if (components != DataComponentPredicate.EMPTY) {
							yield item.map(holder -> DataComponentIngredient.of(false, components, holder));
						}
					} catch (CommandSyntaxException e) {
						yield DataResult.error(e::getMessage);
					}
				}

				yield item.map(Holder::value).map(Ingredient::of);
			}
		};
	}

	@Info("""
		Checks if the passed in object is an Ingredient.
		Note that this does not mean it will not function as an Ingredient if passed to something that requests one.
		""")
	static boolean isIngredient(@Nullable Object o) {
		return o instanceof Ingredient;
	}

	static ItemStack first(Ingredient ingredient) {
		return ingredient.kjs$getFirst();
	}

	@Nullable
	static TagKey<Item> tagKeyOf(Ingredient in) {
		if (!in.isCustom() && in.getValues().length == 1 && in.getValues()[0] instanceof Ingredient.TagValue(TagKey<Item> tag)) {
			return tag;
		} else {
			return null;
		}
	}

	static boolean containsAnyTag(Ingredient in) {
		if (in.isCustom()) {
			return false;
		}

		for (var value : in.getValues()) {
			if (value instanceof Ingredient.TagValue) {
				return true;
			}
		}

		return false;
	}
}