package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.FireworksJS;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@DisplayName("ItemUtilities")
public class ItemWrapper
{
	public ItemStackJS of(Object object)
	{
		return ItemStackJS.of(object);
	}

	public List<ItemStackJS> getList()
	{
		return ItemStackJS.getList();
	}

	public List<ResourceLocation> getTypeList()
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

	public FireworksJS fireworks(@P("properties") Map<String, Object> properties)
	{
		return FireworksJS.of(properties);
	}

	@MinecraftClass
	public Item getItem(@P("id") @T(ResourceLocation.class) Object id)
	{
		Item i = ForgeRegistries.ITEMS.getValue(UtilsJS.getID(id));
		return i == null ? Items.AIR : i;
	}
}