package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.world.FireworksJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemWrapper {
	public static ItemStackJS of(ItemStackJS in) {
		return in;
	}

	public static ItemStackJS of(ItemStackJS in, int count) {
		in.setCount(count);
		return in;
	}

	public static ItemStackJS of(ItemStackJS in, CompoundTag tag) {
		return in.withNBT(tag);
	}

	public static ItemStackJS of(ItemStackJS in, int count, CompoundTag nbt) {
		ItemStackJS is = in.withNBT(nbt);
		is.setCount(count);
		return is;
	}

	public static ItemStackJS withNBT(ItemStackJS in, CompoundTag nbt) {
		return in.withNBT(nbt);
	}

	public static ItemStackJS withChance(ItemStackJS in, double c) {
		return in.withChance(c);
	}

	public static ListJS getList() {
		return ListJS.of(ItemStackJS.getList());
	}

	public static ListJS getTypeList() {
		return ItemStackJS.getTypeList();
	}

	public static ItemStackJS getEmpty() {
		return EmptyItemStackJS.INSTANCE;
	}

	public static void clearListCache() {
		ItemStackJS.clearListCache();
	}

	public static FireworksJS fireworks(Map<String, Object> properties) {
		return FireworksJS.of(properties);
	}

	@MinecraftClass
	public static Item getItem(ResourceLocation id) {
		return KubeJSRegistries.items().get(id);
	}

	@Nullable
	@MinecraftClass
	public static CreativeModeTab findGroup(String id) {
		return ItemStackJS.findGroup(id);
	}

	public static boolean exists(ResourceLocation id) {
		return KubeJSRegistries.items().contains(id);
	}
}