package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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
	Lazy<Map<ResourceLocation, Collection<ItemStack>>> CACHED_ITEM_MAP = Lazy.map(map -> {
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
	private static ItemStack wrapTrivial(Context cx, @Nullable Object from) {
		while (from instanceof Wrapper w) {
			from = w.unwrap();
		}

		return switch (from) {
			case null -> ItemStack.EMPTY;
			case ItemStack s -> s.isEmpty() ? ItemStack.EMPTY : s;
			case ItemLike i when i.asItem() == Items.AIR -> ItemStack.EMPTY;
			case Ingredient i -> throw new KubeRuntimeException("Use .first of an ingredient to get its ItemStack!").source(SourceLine.of(cx));
			case ItemLike i -> i.asItem().getDefaultInstance();
			default -> null;
		};
	}

	@HideFromJS
	static DataResult<ItemStack> wrapResult(Context cx, @Nullable Object from) {
		if (from instanceof Wrapper w) {
			from = w.unwrap();
		}

		var trivial = wrapTrivial(cx, from);
		if (trivial != null) {
			return DataResult.success(trivial);
		}

		var registries = RegistryAccessContainer.of(cx);

		if (from instanceof ResourceLocation id) {
			return findItem(id).map(Holder::value).map(Item::getDefaultInstance);
		} else if (from instanceof JsonElement json) {
			return parseJson(cx, registries.nbt(), json);
		} else if (from instanceof StringTag tag) {
			return wrapResult(cx, tag.getAsString());
		} else if (from instanceof Pattern || from instanceof NativeRegExp) {
			return IngredientWrapper.wrapResult(cx, from).map(IngredientKJS::kjs$getFirst);
		} else if (from instanceof CharSequence) {
			var os = from.toString().trim();
			var s = os;
			var cached = registries.itemStackParseCache().get(os);

			if (cached != null) {
				return DataResult.success(cached.copy());
			}

			int count;
			var spaceIndex = s.indexOf(' ');

			if (spaceIndex >= 2 && s.indexOf('x') == spaceIndex - 1) {
				count = Integer.parseInt(s.substring(0, spaceIndex - 1));
				s = s.substring(spaceIndex + 1);
			} else {
				count = 1;
			}

			return parseString(cx, registries.nbt(), s)
				.map(stack -> stack.kjs$withCount(count))
				.ifSuccess(stack -> registries.itemStackParseCache().put(os, stack.copy()))
				;
		}

		var map = cx.optionalMapOf(from);

		if (map != null) {
			// todo: if someone does something weird here, improve upon this parser
			return ItemStack.CODEC.parse(registries.java(), map);
		}

		var invalid = from;
		return DataResult.error(() -> "Could not parse input %s for item stack".formatted(invalid));
	}

	@HideFromJS
	static ItemStack wrap(Context cx, @Nullable Object from) {
		var trivial = wrapTrivial(cx, from);
		if (trivial != null) {
			return trivial;
		}

		return wrapResult(cx, from)
			.getOrThrow(error -> new KubeRuntimeException("Failed to read sized item stack from %s: %s".formatted(from, error))
				.source(SourceLine.of(cx)));
	}

	@HideFromJS
	static Item wrapItem(Context cx, @Nullable Object o) {
		return switch (o) {
			case null -> Items.AIR;
			case ItemLike item -> item.asItem();
			case CharSequence cs -> findItem(cs.toString())
				.getOrThrow(error -> new KubeRuntimeException("Failed to read item from %s: %s".formatted(cs, error))
					.source(SourceLine.of(cx)));
			default -> wrap(cx, o).getItem();
		};
	}

	static DataResult<Item> findItem(String s) {
		s = s.trim();
		return switch (s) {
			case "", "-", "air", "minecraft:air" -> DataResult.success(Items.AIR);
			default -> ResourceLocation.read(s).flatMap(ItemWrapper::findItem).map(Holder::value);
		};
	}

	@HideFromJS
	static DataResult<Holder<Item>> findItem(ResourceLocation id) {
		return BuiltInRegistries.ITEM
			.getHolder(id)
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Item with ID " + id + " does not exist!"))
			.map(Function.identity());
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
		return from instanceof ItemStack || from instanceof ItemLike;
	}

	static DataResult<ItemStack> parseJson(Context cx, DynamicOps<Tag> registryOps, @Nullable JsonElement json) {
		return switch (json) {
			case null -> DataResult.success(ItemStack.EMPTY);
			case JsonNull jsonNull -> DataResult.success(ItemStack.EMPTY);
			case JsonPrimitive primitive -> parseString(cx, registryOps, primitive.getAsString());
			case JsonObject obj -> ItemStack.OPTIONAL_CODEC.decode(JsonOps.INSTANCE, obj).map(Pair::getFirst);
			default -> DataResult.error(() -> "Could not parse item stack from JSON " + json);
		};
	}

	static DataResult<ItemStack> parseString(Context cx, DynamicOps<Tag> registryOps, String s) {
		return switch (s) {
			case "", "-", "air", "minecraft:air" -> DataResult.success(ItemStack.EMPTY);
			default -> {
				try {
					yield read(cx, registryOps, new StringReader(s));
				} catch (CommandSyntaxException ex) {
					yield DataResult.error(() -> "Error parsing item from string: " + ex);
				}
			}
		};
	}

	static DataResult<ItemStack> read(Context cx, DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		reader.skipWhitespace();

		if (!reader.canRead() || reader.peek() == '-') {
			return DataResult.success(ItemStack.EMPTY);
		}

		int count;

		if (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
			count = Mth.ceil(reader.readDouble());
			reader.skipWhitespace();
			reader.expect('x');
			reader.skipWhitespace();

			if (count < 1) {
				return DataResult.error(() -> "Item count smaller than 1 is not allowed!");
			}
		} else {
			count = 1;
		}

		var itemStack = ID.read(reader)
			.flatMap(ItemWrapper::findItem)
			.map(item -> new ItemStack(item, count));

		var next = reader.canRead() ? reader.peek() : 0;

		if (next == '[' || next == '{') {
			return itemStack.flatMap(stack -> {
				try {
					var components = DataComponentWrapper.readPatch(registryOps, reader);
					stack.applyComponents(components);
					return DataResult.success(stack);
				} catch (CommandSyntaxException e) {
					return DataResult.error(e::getMessage);
				}
			});
		}

		return itemStack;
	}
}