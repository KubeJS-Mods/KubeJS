package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemBuilder
{
	public final ResourceLocation id;
	private final Consumer<ItemBuilder> callback;
	@Ignore
	public int maxStackSize;
	@Ignore
	public int maxDamage;
	@Ignore
	public ResourceLocation containerItem;
	@Ignore
	public Map<ToolType, Integer> tools;
	@Ignore
	public Rarity rarity;
	@Ignore
	public boolean glow;
	@Ignore
	public final List<Text> tooltip;
	@Ignore
	public ItemGroup group;

	public ItemBuilder(String i, Consumer<ItemBuilder> c)
	{
		id = UtilsJS.getID(KubeJS.appendModId(i));
		callback = c;
		maxStackSize = 64;
		maxDamage = 0;
		containerItem = UtilsJS.NULL_ID;
		tools = new HashMap<>();
		rarity = Rarity.COMMON;
		glow = false;
		tooltip = new ArrayList<>();
		group = ItemGroup.MISC;
	}

	public ItemBuilder maxStackSize(@P("size") int v)
	{
		maxStackSize = v;
		return this;
	}

	public ItemBuilder unstackable()
	{
		return maxStackSize(1);
	}

	public ItemBuilder maxDamage(@P("damage") int v)
	{
		maxDamage = v;
		return this;
	}

	public ItemBuilder containerItem(@P("id") @T(ResourceLocation.class) Object id)
	{
		containerItem = UtilsJS.getID(id);
		return this;
	}

	public ItemBuilder tool(@P("type") ToolType type, @P("level") int level)
	{
		tools.put(type, level);
		return this;
	}

	public ItemBuilder rarity(@P("rarity") Rarity v)
	{
		rarity = v;
		return this;
	}

	public ItemBuilder glow(@P("glow") boolean v)
	{
		glow = v;
		return this;
	}

	public ItemBuilder tooltip(@P("text") @T(Text.class) Object text)
	{
		tooltip.add(Text.of(text));
		return this;
	}

	public ItemBuilder group(String g)
	{
		for (ItemGroup ig : ItemGroup.GROUPS)
		{
			if (ig.getPath().equals(g))
			{
				group = ig;
				return this;
			}
		}

		return this;
	}

	public void add()
	{
		callback.accept(this);
	}

	public Item.Properties createItemProperties()
	{
		Item.Properties properties = new Item.Properties();

		properties.group(group);
		properties.maxDamage(maxDamage);
		properties.maxStackSize(maxStackSize);
		properties.rarity(rarity);

		for (Map.Entry<ToolType, Integer> entry : tools.entrySet())
		{
			properties.addToolType(entry.getKey(), entry.getValue());
		}

		Item item = containerItem == UtilsJS.NULL_ID ? null : ForgeRegistries.ITEMS.getValue(containerItem);

		if (item != null && item != Items.AIR)
		{
			properties.containerItem(item);
		}

		return properties;
	}
}