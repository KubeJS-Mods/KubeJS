package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.world.FireworksJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemWrapper {
	public ItemStackJS of(ItemStackJS in) {
		return in;
	}

	public ItemStackJS of(ItemStackJS in, Object countOrNBT) {
		return ItemStackJS.of(in, countOrNBT);
	}

	public ItemStackJS of(ItemStackJS in, int count, MapJS nbt) {
		return ItemStackJS.of(in, count, nbt);
	}

	public ItemStackJS withNBT(ItemStackJS in, MapJS nbt) {
		return in.withNBT(nbt);
	}

	public ItemStackJS withChance(ItemStackJS in, double c) {
		return in.withChance(c);
	}

	public ListJS getList() {
		return ListJS.of(ItemStackJS.getList());
	}

	public ListJS getTypeList() {
		return ItemStackJS.getTypeList();
	}

	public ItemStackJS getEmpty() {
		return EmptyItemStackJS.INSTANCE;
	}

	public void clearListCache() {
		ItemStackJS.clearListCache();
	}

	public FireworksJS fireworks(Map<String, Object> properties) {
		return FireworksJS.of(properties);
	}

	@MinecraftClass
	public Item getItem(ResourceLocation id) {
		return KubeJSRegistries.items().get(id);
	}

	@Nullable
	@MinecraftClass
	public CreativeModeTab findGroup(String id) {
		return ItemStackJS.findGroup(id);
	}

	public boolean exists(ResourceLocation id) {
		return KubeJSRegistries.items().contains(id);
	}
}