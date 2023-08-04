package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.FireworksJS;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ItemWrapper {
	UUID KJS_BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	UUID KJS_BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	UUID[] KJS_ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

	static ItemStack of(ItemStack in) {
		return in;
	}

	static ItemStack of(ItemStack in, int count) {
		return in.kjs$withCount(count);
	}

	static ItemStack of(ItemStack in, CompoundTag tag) {
		return in.kjs$withNBT(tag);
	}

	static ItemStack of(ItemStack in, int count, CompoundTag nbt) {
		var is = in.kjs$withNBT(nbt);
		is.setCount(count);
		return is;
	}

	static ItemStack withNBT(ItemStack in, CompoundTag nbt) {
		return in.kjs$withNBT(nbt);
	}

	static List<ItemStack> getList() {
		return ItemStackJS.getList();
	}

	static List<String> getTypeList() {
		return ItemStackJS.getTypeList();
	}

	static Map<ResourceLocation, NonNullList<ItemStack>> getTypeToStackMap() {
		return ItemStackJS.getTypeToStacks();
	}

	static List<ItemStack> getVariants(ItemStack item) {
		return getTypeToStackMap().get(item.kjs$getIdLocation());
	}

	static ItemStack getEmpty() {
		return ItemStack.EMPTY;
	}

	static FireworksJS fireworks(Map<String, Object> properties) {
		return FireworksJS.of(properties);
	}

	static Item getItem(ResourceLocation id) {
		return KubeJSRegistries.items().get(id);
	}

	@Nullable
	static ResourceLocation getId(Item item) {
		return KubeJSRegistries.items().getId(item);
	}

	static boolean exists(ResourceLocation id) {
		return KubeJSRegistries.items().contains(id);
	}

	static boolean isItem(@Nullable Object o) {
		return o instanceof ItemStackJS;
	}
}