package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.errorprone.annotations.DoNotCall;
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
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.holder.NamespaceHolderSet;
import dev.latvian.mods.kubejs.holder.RegExHolderSet;
import dev.latvian.mods.kubejs.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.script.SourceLine;
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
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Info("Various Ingredient related helper methods")
public interface IngredientWrapper {
	@HideFromJS
	DataResult<Ingredient> EMPTY_INGREDIENT = DataResult.error(() -> "Empty ingredients aren't supported!");

	private static DataResult<Ingredient> unknownTag(Identifier tag) {
		return DataResult.error(() -> "Item tag " + tag + " does not exist!");
	}

	TypeInfo TYPE_INFO = TypeInfo.of(Ingredient.class);

	@Info("Returns an ingredient of the input")
	static Ingredient of(Ingredient ingredient) {
		return ingredient;
	}

	@Info("Returns an ingredient of the input, with the specified count")
	static SizedIngredient of(Ingredient ingredient, int count) {
		return ingredient.kjs$withCount(count);
	}

	@Info("An ingredient that matches everything")
	static Ingredient getAll(Context cx) {
		return Ingredient.of(new AnyHolderSet<>(RegistryAccessContainer.of(cx).item()));
	}

	@DoNotCall
	@Deprecated(forRemoval = true)
	@Info("Empty ingredients are no longer supported. Do not call!")
	static Ingredient getNone(Context cx) {
		return EMPTY_INGREDIENT.getOrThrow(str -> new KubeRuntimeException(str).source(SourceLine.of(cx)));
	}

	@DoNotCall
	@Deprecated(forRemoval = true)
	@Info("Empty ingredients are no longer supported. Do not call!")
	static Ingredient of(Context cx) {
		return getNone(cx);
	}

	@Info("Returns an ingredient that accepts the given set of items under the given component filter.")
	static Ingredient withData(HolderSet<Item> base, DataComponentMap data) {
		return withData(base, data, false);
	}

	@Info("Returns an ingredient that accepts the given set of items under the given (optionally strict) component filter.")
	static Ingredient withData(HolderSet<Item> base, DataComponentMap data, boolean strict) {
		return DataComponentIngredient.of(strict, data, base);
	}

	@HideFromJS
	static DataResult<Ingredient> wrapResult(Context cx, @Nullable Object from) {
		while (from instanceof Wrapper w) {
			from = w.unwrap();
		}

		var registries = RegistryAccessContainer.of(cx);

		var trivial = switch (from) {
			case null -> EMPTY_INGREDIENT;
			case Ingredient id -> DataResult.success(id);
			case ItemStack s when s.isEmpty() -> EMPTY_INGREDIENT;
			case ItemLike i when i.asItem() == Items.AIR -> EMPTY_INGREDIENT;
			case IngredientSupplierKJS ingr -> DataResult.success(ingr.kjs$asIngredient());
			case ItemLike i -> DataResult.success(Ingredient.of(i));
			case TagKey<?>(ResourceKey<?> reg, var id) -> {
				var tagKey = ItemTags.create(id);
				yield registries.get(tagKey)
					.map(Ingredient::of)
					.map(DataResult::success)
					.orElseGet(() -> unknownTag(id))
					;
			}

			default -> null;
		};

		if (trivial != null) {
			return trivial;
		}

		if (from instanceof Pattern || from instanceof NativeRegExp) {
			var str = String.valueOf(from);
			//noinspection DataFlowIssue (safe, no idea what idea is smoking)
			return Optional.ofNullable(RegExpKJS.wrap(from))
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "Invalid regex " + str))
				.map(regex -> RegExHolderSet.of(registries.item(), regex))
				.map(Ingredient::of)
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

				ingredient.resultOrPartial().ifPresent(results::add);

				if (ingredient.isError()) {
					failed = true;
					errors.add(o1 + ": " + ingredient.error().orElseThrow().message());
				}
			}

			if (failed) {
				var msg = errors.build().collect(Collectors.joining("; "));
				return DataResult.error(() -> "Failed to parse ingredient list: " + msg);
			} else {
				return switch (results.size()) {
					case 0 -> EMPTY_INGREDIENT;
					case 1 -> DataResult.success(results.getFirst());
					default -> DataResult.success(new CompoundIngredient(results).toVanilla());
				};
			}
		}

		var map = cx.optionalMapOf(from);

		if (map != null) {
			return Ingredient.CODEC.parse(JavaOps.INSTANCE, map);
		}

		return ItemWrapper.wrapResult(cx, from).flatMap(IngredientWrapper::tryFromStack);
	}

	private static DataResult<Ingredient> tryFromStack(ItemStack stack) {
		if (stack.isEmpty()) {
			return DataResult.error(() -> "Ingredient cannot be made from empty stack!");
		} else {
			return DataResult.success(Ingredient.of(stack.getItem()));
		}
	}

	@HideFromJS
	static Ingredient wrap(Context cx, @Nullable Object from) {
		return wrapResult(cx, from)
			.getOrThrow(error -> new KubeRuntimeException("Failed to read ingredient from %s: %s".formatted(from, error))
				.source(SourceLine.of(cx)));
	}

	static boolean isIngredientLike(@Nullable Object from) {
		return from instanceof Ingredient || from instanceof SizedIngredient || from instanceof ItemStack;
	}

	static DataResult<Ingredient> parseJson(Context cx, @Nullable JsonElement json) {
		return switch (json) {
			case null -> EMPTY_INGREDIENT;
			case JsonNull jsonNull -> EMPTY_INGREDIENT;
			case JsonArray arr when arr.isEmpty() -> EMPTY_INGREDIENT;
			case JsonPrimitive primitive -> wrapResult(cx, json.getAsString());
			default -> Ingredient.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);
		};
	}

	static DataResult<Ingredient> parseString(Context cx, String s) {
		return switch (s) {
			case "", "-", "air", "minecraft:air" -> EMPTY_INGREDIENT;
			case "*" -> DataResult.success(IngredientWrapper.getAll(cx));
			default -> read(cx, new StringReader(s));
		};
	}

	static DataResult<Ingredient> read(Context cx, StringReader reader) {
		var registries = RegistryAccessContainer.of(cx);

		reader.skipWhitespace();

		if (!reader.canRead()) {
			return EMPTY_INGREDIENT;
		}

		return switch (reader.peek()) {
			case '-' -> {
				reader.skip();
				yield EMPTY_INGREDIENT;
			}
			case '*' -> {
				reader.skip();
				yield DataResult.success(IngredientWrapper.getAll(cx));
			}
			case '#' -> {
				reader.skip();
				yield ID.read(reader).map(ItemTags::create).flatMap(tagKey ->
					registries.get(tagKey)
						.map(set -> DataResult.success(Ingredient.of(set)))
						.orElseGet(() -> unknownTag(tagKey.location()))
				);
			}
			case '@' -> {
				reader.skip();
				yield DataResult.success(new Ingredient(NamespaceHolderSet.of(registries.item(), reader.readUnquotedString())));
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
			case '/' -> RegExpKJS.tryRead(reader).map(regex -> RegExHolderSet.of(registries.item(), regex)).map(Ingredient::of);
			case '[' -> {
				reader.skip();
				reader.skipWhitespace();

				if (!reader.canRead() || reader.peek() == ']') {
					yield EMPTY_INGREDIENT;
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
						var components = DataComponentWrapper.readMap(registries.nbt(), reader);

						if (!components.isEmpty()) {
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
		if (in.isCustom()) {
			return null;
		}

		var values = in.getValues();
		return values.unwrapKey().orElse(null);
	}
}