package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.FireworksJS;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

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
	public Item getItem(@ID String id)
	{
		Item i = ForgeRegistries.ITEMS.getValue(UtilsJS.getMCID(id));
		return i == null ? Items.AIR : i;
	}
}