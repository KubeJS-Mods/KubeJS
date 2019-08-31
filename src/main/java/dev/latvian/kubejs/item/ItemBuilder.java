package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.ID;
import net.minecraft.item.EnumRarity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemBuilder
{
	public final ID id;
	private final Consumer<ItemBuilder> callback;
	public String translationKey;
	public int maxStackSize;
	public int maxDamage;
	public ID containerItem;
	public Map<String, Integer> tools;
	public String model;
	public EnumRarity rarity;
	public boolean glow;

	public ItemBuilder(String i, Consumer<ItemBuilder> c)
	{
		id = new ID(KubeJS.appendModId(i));
		callback = c;
		translationKey = id.namespace + "." + id.path;
		maxStackSize = 64;
		maxDamage = 0;
		containerItem = null;
		tools = new HashMap<>();
		model = id.namespace + ":" + id.path + "#inventory";
		rarity = EnumRarity.COMMON;
		glow = false;
	}

	public ItemBuilder translationKey(String v)
	{
		translationKey = v;
		return this;
	}

	public ItemBuilder maxStackSize(int v)
	{
		maxStackSize = v;
		return this;
	}

	public ItemBuilder unstackable()
	{
		return maxStackSize(1);
	}

	public ItemBuilder maxDamage(int v)
	{
		maxDamage = v;
		return this;
	}

	public ItemBuilder containerItem(@Nullable ID id)
	{
		containerItem = id;
		return this;
	}

	public ItemBuilder tool(String type, int level)
	{
		tools.put(type, level);
		return this;
	}

	public ItemBuilder model(String v)
	{
		model = v;
		return this;
	}

	public ItemBuilder rarity(EnumRarity v)
	{
		rarity = v;
		return this;
	}

	public ItemBuilder glow(boolean v)
	{
		glow = v;
		return this;
	}

	public void add()
	{
		callback.accept(this);
	}
}