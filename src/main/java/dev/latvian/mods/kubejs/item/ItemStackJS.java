package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.bindings.DataComponentWrapper;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.RegExpJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public interface ItemStackJS {
	ItemStack[] EMPTY_ARRAY = new ItemStack[0];
	TypeInfo TYPE_INFO = TypeInfo.of(ItemStack.class);

	Lazy<List<String>> CACHED_ITEM_TYPE_LIST = Lazy.of(() -> {
		var cachedItemTypeList = new ArrayList<String>();

		for (var entry : RegistryInfo.ITEM.entrySet()) {
			cachedItemTypeList.add(entry.getKey().location().toString());
		}

		return cachedItemTypeList;
	});

	Lazy<Map<ResourceLocation, Collection<ItemStack>>> CACHED_ITEM_MAP = Lazy.of(() -> {
		var map = new HashMap<ResourceLocation, Collection<ItemStack>>();
		var stackList = ItemStackLinkedSet.createTypeAndComponentsSet();

		stackList.addAll(CreativeModeTabs.searchTab().getDisplayItems());

		for (var stack : stackList) {
			if (!stack.isEmpty()) {
				map.computeIfAbsent(
					stack.getItem().kjs$getIdLocation(),
					_rl -> ItemStackLinkedSet.createTypeAndComponentsSet()
				).add(stack.kjs$withCount(1));
			}
		}

		for (var itemId : CACHED_ITEM_TYPE_LIST.get()) {
			var itemRl = new ResourceLocation(itemId);
			map.computeIfAbsent(itemRl, id -> Set.of(RegistryInfo.ITEM.getValue(id).getDefaultInstance()));
		}

		return map;
	});

	Lazy<List<ItemStack>> CACHED_ITEM_LIST = Lazy.of(() -> CACHED_ITEM_MAP.get().values().stream().flatMap(Collection::stream).toList());

	static ItemStack wrap(Context cx, @Nullable Object o) {
		if (o instanceof Wrapper w) {
			o = w.unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR) {
			return ItemStack.EMPTY;
		} else if (o instanceof ItemStack stack) {
			return stack.isEmpty() ? ItemStack.EMPTY : stack;
		} else if (o instanceof Ingredient ingr) {
			return ingr.kjs$getFirst();
		} else if (o instanceof ResourceLocation id) {
			var item = RegistryInfo.ITEM.getValue(id);

			if (item == null || item == Items.AIR) {
				if (KubeRecipe.itemErrors) {
					throw new RecipeExceptionJS("Item '" + id + "' not found!").error();
				}

				return ItemStack.EMPTY;
			}

			return item.getDefaultInstance();
		} else if (o instanceof ItemLike itemLike) {
			return itemLike.asItem().getDefaultInstance();
		} else if (o instanceof JsonElement json) {
			return resultFromRecipeJson(((KubeJSContext) cx).getNbtRegistryOps(), json);
		} else if (o instanceof StringTag tag) {
			return wrap(cx, tag.getAsString());
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			var reg = RegExpJS.wrap(o);

			if (reg != null) {
				return IngredientHelper.get().regex(reg).kjs$getFirst();
			}

			return ItemStack.EMPTY;
		} else if (o instanceof CharSequence) {
			var os = o.toString().trim();
			var s = os;

			var cached = ((KubeJSContext) cx).itemStackParseCache.get(os);

			if (cached != null) {
				return cached.copy();
			}

			var count = 1;
			var spaceIndex = s.indexOf(' ');

			if (spaceIndex >= 2 && s.indexOf('x') == spaceIndex - 1) {
				count = Integer.parseInt(s.substring(0, spaceIndex - 1));
				s = s.substring(spaceIndex + 1);
			}

			cached = ofString(((KubeJSContext) cx).getNbtRegistryOps(), s);
			cached.setCount(count);
			((KubeJSContext) cx).itemStackParseCache.put(os, cached);
			return cached.copy();
		}

		var map = MapJS.of(o);

		if (map != null) {
			if (map.containsKey("item")) {
				var id = ID.mc(map.get("item").toString());
				var item = RegistryInfo.ITEM.getValue(id);

				if (item == Items.AIR) {
					if (KubeRecipe.itemErrors) {
						throw new RecipeExceptionJS("Item '" + id + "' not found!").error();
					}

					return ItemStack.EMPTY;
				}

				var stack = new ItemStack(item);

				if (map.get("count") instanceof Number number) {
					stack.setCount(number.intValue());
				}

				return stack;
			} else if (map.containsKey("tag")) {
				var stack = IngredientHelper.get().tag(ID.mc(map.get("tag"))).kjs$getFirst();

				if (map.containsKey("count")) {
					stack.setCount(UtilsJS.parseInt(map.get("count"), 1));
				}

				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	static ItemStack ofString(DynamicOps<Tag> registryOps, String s) {
		if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
			return ItemStack.EMPTY;
		} else {
			try {
				var reader = new StringReader(s);
				reader.skipWhitespace();

				if (!reader.canRead()) {
					return ItemStack.EMPTY;
				}

				return read(registryOps, new StringReader(s));
			} catch (CommandSyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	static Item getRawItem(Context cx, @Nullable Object o) {
		if (o == null) {
			return Items.AIR;
		} else if (o instanceof ItemLike item) {
			return item.asItem();
		} else if (o instanceof CharSequence) {
			var s = o.toString();
			if (s.isEmpty()) {
				return Items.AIR;
			} else if (s.charAt(0) != '#') {
				return RegistryInfo.ITEM.getValue(ID.mc(s));
			}
		}

		return wrap(cx, o).getItem();
	}

	// Use ItemStackJS.of(object)
	static ItemStack resultFromRecipeJson(DynamicOps<Tag> registryOps, @Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return ItemStack.EMPTY;
		} else if (json.isJsonPrimitive()) {
			return ofString(registryOps, json.getAsString());
		} else if (json instanceof JsonObject) {
			return ItemStack.OPTIONAL_CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
		}

		return ItemStack.EMPTY;
	}

	static List<ItemStack> getList() {
		return CACHED_ITEM_LIST.get();
	}

	static List<String> getTypeList() {
		return CACHED_ITEM_TYPE_LIST.get();
	}

	static Map<ResourceLocation, Collection<ItemStack>> getTypeToStacks() {
		return CACHED_ITEM_MAP.get();
	}

	static boolean isItemStackLike(Object from) {
		return from instanceof ItemStack;
	}

	static ItemStack read(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return ItemStack.EMPTY;
		}

		if (reader.peek() == '-') {
			return ItemStack.EMPTY;
		}

		int count = 1;

		if (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
			count = Mth.ceil(reader.readDouble());
			reader.expect('x');
			reader.skipWhitespace();

			if (count < 1) {
				throw new IllegalArgumentException("Item count smaller than 1 is not allowed!");
			}
		}

		var itemId = ResourceLocation.read(reader);
		var itemStack = new ItemStack(RegistryInfo.ITEM.getValue(itemId), count);

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			itemStack.applyComponents(DataComponentWrapper.readPatch(registryOps, reader));
		}

		return itemStack;
	}
}