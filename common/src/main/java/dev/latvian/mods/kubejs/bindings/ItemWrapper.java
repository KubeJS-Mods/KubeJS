package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.FireworksJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemWrapper {
	public static ItemStack of(ItemStack in) {
		return in;
	}

	public static ItemStack of(ItemStack in, int count) {
		in.setCount(count);
		return in;
	}

	public static ItemStack of(ItemStack in, CompoundTag tag) {
		return in.kjs$withNBT(tag);
	}

	public static ItemStack of(ItemStack in, int count, CompoundTag nbt) {
		var is = in.kjs$withNBT(nbt);
		is.setCount(count);
		return is;
	}

	public static ItemStack withNBT(ItemStack in, CompoundTag nbt) {
		return in.kjs$withNBT(nbt);
	}

	public static ItemStackJS withChance(ItemStackJS in, double c) {
		return in.withChance(c);
	}

	public static List<ItemStack> getList() {
		return ItemStackJS.getList();
	}

	public static List<String> getTypeList() {
		return ItemStackJS.getTypeList();
	}

	public static ItemStackJS getEmpty() {
		return ItemStackJS.EMPTY;
	}

	public static void clearListCache() {
		ItemStackJS.clearListCache();
	}

	public static FireworksJS fireworks(Map<String, Object> properties) {
		return FireworksJS.of(properties);
	}

	public static Item getItem(ResourceLocation id) {
		return KubeJSRegistries.items().get(id);
	}

	@Nullable
	public static ResourceLocation getId(Item item) {
		return KubeJSRegistries.items().getId(item);
	}

	@Nullable
	public static CreativeModeTab findGroup(String id) {
		return ItemStackJS.findGroup(id);
	}

	public static boolean exists(ResourceLocation id) {
		return KubeJSRegistries.items().contains(id);
	}

	public static boolean isItem(@Nullable Object o) {
		return o instanceof ItemStackJS;
	}
}