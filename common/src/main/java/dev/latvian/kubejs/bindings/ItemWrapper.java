package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.FireworksJS;
import dev.latvian.mods.rhino.util.wrap.Wrap;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemWrapper
{
	public ItemStackJS of(Object object)
	{
		return ItemStackJS.of(object);
	}

	public ItemStackJS of(Object object, Object countOrNBT)
	{
		return ItemStackJS.of(object, countOrNBT);
	}

	public ItemStackJS of(Object object, int count, Object nbt)
	{
		return ItemStackJS.of(object, count, nbt);
	}

	public ListJS getList()
	{
		return ListJS.of(ItemStackJS.getList());
	}

	public ListJS getTypeList()
	{
		return ItemStackJS.getTypeList();
	}

	public ItemStackJS getEmpty()
	{
		return EmptyItemStackJS.INSTANCE;
	}

	public void clearListCache()
	{
		ItemStackJS.clearListCache();
	}

	public FireworksJS fireworks(Map<String, Object> properties)
	{
		return FireworksJS.of(properties);
	}

	@MinecraftClass
	public Item getItem(@Wrap("id") String id)
	{
		Item item = Registries.get(KubeJS.MOD_ID).get(Registry.ITEM_REGISTRY).get(UtilsJS.getMCID(id));
		return item == null ? Items.AIR : item;
	}

	@Nullable
	@MinecraftClass
	public CreativeModeTab findGroup(String id)
	{
		return ItemStackJS.findGroup(id);
	}
}