package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Info("Various item related helper methods")
public interface ItemWrapper {
	ItemStack[] EMPTY_ARRAY = new ItemStack[0];
	TypeInfo ITEM_TYPE_INFO = TypeInfo.of(Item.class);
	TypeInfo TYPE_INFO = TypeInfo.of(ItemStack.class);

	@HideFromJS
	Lazy<List<String>> CACHED_ITEM_TYPE_LIST = Lazy.of(() -> {
		var cachedItemTypeList = new ArrayList<String>();

		for (var item : BuiltInRegistries.ITEM) {
			cachedItemTypeList.add(item.kjs$getId());
		}

		return cachedItemTypeList;
	});

	@HideFromJS
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
			var itemRl = ResourceLocation.parse(itemId);
			map.computeIfAbsent(itemRl, id -> Set.of(BuiltInRegistries.ITEM.get(id).getDefaultInstance()));
		}

		return map;
	});

	@HideFromJS
	Lazy<List<ItemStack>> CACHED_ITEM_LIST = Lazy.of(() -> CACHED_ITEM_MAP.get().values().stream().flatMap(Collection::stream).toList());

	@Info("Returns an ItemStack of the input")
	static ItemStack of(ItemStack in) {
		return in;
	}

	@Info("Returns an ItemStack of the input, with the specified count")
	static ItemStack of(ItemStack in, int count) {
		return in.kjs$withCount(count);
	}

	@HideFromJS
	static ItemStack wrap(Context cx, @Nullable Object o) {
		if (o instanceof Wrapper w) {
			o = w.unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR) {
			return ItemStack.EMPTY;
		} else if (o instanceof ItemStack stack) {
			return stack.isEmpty() ? ItemStack.EMPTY : stack;
		} else if (o == Ingredient.EMPTY) {
			throw new KubeRuntimeException("Tried to convert empy ingredient to ItemStack!").source(SourceLine.of(cx));
		} else if (o instanceof Ingredient) {
			throw new KubeRuntimeException("Use .first of an ingredient to get its ItemStack!").source(SourceLine.of(cx));
		} else if (o instanceof ResourceLocation id) {
			var item = BuiltInRegistries.ITEM.get(id);

			if (item == null || item == Items.AIR) {
				return ItemStack.EMPTY;
			}

			return item.getDefaultInstance();
		} else if (o instanceof ItemLike itemLike) {
			return itemLike.asItem().getDefaultInstance();
		} else if (o instanceof JsonElement json) {
			var registries = RegistryAccessContainer.of(cx);
			return parseJson(registries.nbt(), json);
		} else if (o instanceof StringTag tag) {
			return wrap(cx, tag.getAsString());
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			var reg = RegExpKJS.wrap(o);

			if (reg != null) {
				return new RegExIngredient(reg).toVanilla().kjs$getFirst();
			}

			return ItemStack.EMPTY;
		} else if (o instanceof CharSequence) {
			var os = o.toString().trim();
			var s = os;
			var registries = RegistryAccessContainer.of(cx);
			var cached = registries.itemStackParseCache().get(os);

			if (cached != null) {
				return cached.copy();
			}

			var count = 1;
			var spaceIndex = s.indexOf(' ');

			if (spaceIndex >= 2 && s.indexOf('x') == spaceIndex - 1) {
				count = Integer.parseInt(s.substring(0, spaceIndex - 1));
				s = s.substring(spaceIndex + 1);
			}

			cached = parseString(registries.nbt(), s);
			cached.setCount(count);
			registries.itemStackParseCache().put(os, cached);
			return cached.copy();
		}

		var map = cx.optionalMapOf(o);

		if (map != null) {
			if (map.containsKey("item")) {
				var id = ID.mc(map.get("item").toString());
				var item = BuiltInRegistries.ITEM.get(id);

				if (item == null || item == Items.AIR) {
					return ItemStack.EMPTY;
				}

				var stack = new ItemStack(item);

				if (map.get("count") instanceof Number number) {
					stack.setCount(number.intValue());
				}

				return stack;
			} else if (map.containsKey("tag")) {
				// var stack = new TagIngredient(registries.cachedItemTags, ItemTags.create(ID.mc(map.get("tag")))).toVanilla().kjs$getFirst();
				var stack = Ingredient.of(ItemTags.create(ID.mc(map.get("tag")))).kjs$getFirst();

				if (map.containsKey("count")) {
					stack.setCount(StringUtilsWrapper.parseInt(map.get("count"), 1));
				}

				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@HideFromJS
	static Item wrapItem(Context cx, @Nullable Object o) {
		if (o == null) {
			return Items.AIR;
		} else if (o instanceof ItemLike item) {
			return item.asItem();
		} else if (o instanceof CharSequence) {
			var s = o.toString();
			if (s.isEmpty()) {
				return Items.AIR;
			} else if (s.charAt(0) != '#') {
				return BuiltInRegistries.ITEM.get(ID.mc(s));
			}
		}

		return wrap(cx, o).getItem();
	}

	@Info("Get a list of most items in the game. Items not in a creative tab are ignored")
	static List<ItemStack> getList() {
		return CACHED_ITEM_LIST.get();
	}

	@Info("Get a list of all the item ids in the game")
	static List<String> getTypeList() {
		return CACHED_ITEM_TYPE_LIST.get();
	}

	static Map<ResourceLocation, Collection<ItemStack>> getTypeToStackMap() {
		return CACHED_ITEM_MAP.get();
	}

	static Collection<ItemStack> getVariants(ItemStack item) {
		return getTypeToStackMap().get(item.kjs$getIdLocation());
	}

	@Info("Get the item that represents air/an empty slot")
	static ItemStack getEmpty() {
		return ItemStack.EMPTY;
	}

	@Info("Returns a Firework with the input properties")
	static Fireworks fireworks(Fireworks fireworks) {
		return fireworks;
	}

	@Info("Gets an Item from an item id")
	static Item getItem(ResourceLocation id) {
		return BuiltInRegistries.ITEM.get(id);
	}

	@Info("Gets an items id from the Item")
	static ResourceLocation getId(Item item) {
		return BuiltInRegistries.ITEM.getKey(item);
	}

	@Info("Checks if the provided item id exists in the registry")
	static boolean exists(ResourceLocation id) {
		return BuiltInRegistries.ITEM.containsKey(id);
	}

	@Info("""
		Checks if the passed in object is an ItemStack.
		Note that this does not mean it will not function as an ItemStack if passed to something that requests one.
		""")
	static boolean isItem(@Nullable Object o) {
		return o instanceof ItemStack;
	}

	static ItemStack playerHead(String name) {
		var stack = new ItemStack(Items.PLAYER_HEAD);
		stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap()));
		return stack;
	}

	static ItemStack playerHeadFromBase64(UUID uuid, String textureBase64) {
		if (uuid == null || uuid.equals(Util.NIL_UUID)) {
			throw new IllegalArgumentException("UUID can't be null!");
		}

		if (textureBase64 == null || textureBase64.isBlank()) {
			throw new IllegalArgumentException("Texture Base 64 can't be empty!");
		}

		var stack = new ItemStack(Items.PLAYER_HEAD);
		var properties = new PropertyMap();
		properties.put("textures", new Property("textures", textureBase64));
		stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.of(uuid), properties));
		return stack;
	}

	static ItemStack playerHeadFromUrl(String url) {
		var root = new JsonObject();
		var textures = new JsonObject();
		var skin = new JsonObject();
		skin.addProperty("url", url);
		textures.add("SKIN", skin);
		root.add("textures", textures);
		var bytes = JsonUtils.toString(root).getBytes(StandardCharsets.UTF_8);
		return playerHeadFromBase64(UUID.nameUUIDFromBytes(bytes), Base64.getEncoder().encodeToString(bytes));
	}

	static ItemStack playerHeadFromSkinHash(String hash) {
		return playerHeadFromUrl("https://textures.minecraft.net/texture/" + hash);
	}

	static ItemAbility wrapItemAbility(Object object) {
		if (object instanceof ItemAbility ta) {
			return ta;
		} else if (object != null) {
			return ItemAbility.get(object.toString());
		} else {
			return null;
		}
	}

	static boolean isItemStackLike(Object from) {
		return from instanceof ItemStack;
	}

	static ItemStack parseJson(DynamicOps<Tag> registryOps, @Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return ItemStack.EMPTY;
		} else if (json.isJsonPrimitive()) {
			return parseString(registryOps, json.getAsString());
		} else if (json instanceof JsonObject) {
			return ItemStack.OPTIONAL_CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
		}

		return ItemStack.EMPTY;
	}

	static ItemStack parseString(DynamicOps<Tag> registryOps, String s) {
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
			reader.skipWhitespace();
			reader.expect('x');
			reader.skipWhitespace();

			if (count < 1) {
				throw new IllegalArgumentException("Item count smaller than 1 is not allowed!");
			}
		}

		var itemId = ResourceLocation.read(reader);
		var itemStack = new ItemStack(BuiltInRegistries.ITEM.get(itemId), count);

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			itemStack.applyComponents(DataComponentWrapper.readPatch(registryOps, reader));
		}

		return itemStack;
	}
}