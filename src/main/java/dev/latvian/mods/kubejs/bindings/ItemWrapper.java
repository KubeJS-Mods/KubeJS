package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonObject;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.JsonUtils;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Info("Various item related helper methods")
public interface ItemWrapper {
	MapCodec<EntityType<?>> ENTITY_TYPE_FIELD_CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id");

	@Info("Returns an ItemStack of the input")
	static ItemStack of(ItemStack in) {
		return in;
	}

	@Info("Returns an ItemStack of the input, with the specified count")
	static ItemStack of(ItemStack in, int count) {
		return in.kjs$withCount(count);
	}

	@Info("Get a list of most items in the game. Items not in a creative tab are ignored")
	static List<ItemStack> getList() {
		return ItemStackJS.getList();
	}

	@Info("Get a list of all the item ids in the game")
	static List<String> getTypeList() {
		return ItemStackJS.getTypeList();
	}

	static Map<ResourceLocation, Collection<ItemStack>> getTypeToStackMap() {
		return ItemStackJS.getTypeToStacks();
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
		return o instanceof ItemStackJS;
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

	static ItemAbility itemAbilityOf(Object object) {
		if (object instanceof ItemAbility ta) {
			return ta;
		} else if (object != null) {
			return ItemAbility.get(object.toString());
		} else {
			return null;
		}
	}
}