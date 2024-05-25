package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.JsonUtils;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ResolvableProfile;
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
	UUID KJS_BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	UUID KJS_BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	UUID[] KJS_ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

	@Info("Returns an ItemStack of the input")
	static ItemStack of(ItemStack in) {
		return in;
	}

	@Info("Returns an ItemStack of the input, with the specified count")
	static ItemStack of(ItemStack in, int count) {
		return in.kjs$withCount(count);
	}

	@Info("Returns an ItemStack of the input, with the specified NBT data")
	static ItemStack of(ItemStack in, DataComponentMap components) {
		return in.kjs$withComponents(components);
	}

	@Info("Returns an ItemStack of the input, with the specified count and NBT data")
	static ItemStack of(ItemStack in, int count, DataComponentMap components) {
		var is = in.kjs$withComponents(components);
		is.setCount(count);
		return is;
	}

	@Info("Returns an ItemStack of the input, with the specified components")
	static ItemStack withComponents(ItemStack in, DataComponentMap components) {
		return in.kjs$withComponents(components);
	}

	@Info("Returns an ItemStack of the input, with the specified components")
	static ItemStack withComponentPatch(ItemStack in, DataComponentPatch components) {
		return in.kjs$withComponentPatch(components);
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
		return RegistryInfo.ITEM.getValue(id);
	}

	@Nullable
	@Info("Gets an items id from the Item")
	static ResourceLocation getId(Item item) {
		return RegistryInfo.ITEM.getId(item);
	}

	@Info("Checks if the provided item id exists in the registry")
	static boolean exists(ResourceLocation id) {
		return RegistryInfo.ITEM.hasValue(id);
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
		stack.set(DataComponents.PROFILE, new ResolvableProfile(new GameProfile(Util.NIL_UUID, name)));
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
}