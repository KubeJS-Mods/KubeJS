package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.ID;
import net.minecraft.item.EnumRarity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemProperties
{
	public final ID id;
	public final transient Consumer<ItemProperties> callback;
	public String translationKey;
	public int maxStackSize;
	public int maxDamage;
	public ID containerItem;
	public Map<String, Integer> tools;
	public String model;
	public EnumRarity rarity;
	public boolean glow;

	public ItemProperties(String i, Consumer<ItemProperties> c)
	{
		id = new ID(KubeJS.appendModId(i));
		callback = c;
		translationKey = id.namespace + "." + id.path;
		maxStackSize = 64;
		maxDamage = 0;
		containerItem = ItemStackJS.EMPTY.id();
		tools = new HashMap<>();
		model = id.namespace + ":" + id.path + "#inventory";
		rarity = EnumRarity.COMMON;
		glow = false;
	}

	public ItemProperties translationKey(String v)
	{
		translationKey = v;
		return this;
	}

	public ItemProperties maxStackSize(int v)
	{
		maxStackSize = v;
		return this;
	}

	public ItemProperties unstackable()
	{
		return maxStackSize(1);
	}

	public ItemProperties maxDamage(int v)
	{
		maxDamage = v;
		return this;
	}

	public ItemProperties containerItem(ID id)
	{
		containerItem = id;
		return this;
	}

	public ItemProperties tool(String type, int level)
	{
		tools.put(type, level);
		return this;
	}

	public ItemProperties model(String v)
	{
		model = v;
		return this;
	}

	public ItemProperties rarity(EnumRarity v)
	{
		rarity = v;
		return this;
	}

	public ItemProperties glow(boolean v)
	{
		glow = v;
		return this;
	}

	public void register()
	{
		callback.accept(this);
	}
}