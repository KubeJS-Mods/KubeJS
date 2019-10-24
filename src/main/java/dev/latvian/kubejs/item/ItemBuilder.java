package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.ID;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.text.ITextComponent;

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
	public static ItemBuilder current;

	public final ID id;
	private final Consumer<ItemBuilder> callback;
	@Ignore
	public String translationKey;
	@Ignore
	public int maxStackSize;
	@Ignore
	public int maxDamage;
	@Ignore
	public ID containerItem;
	@Ignore
	public Map<String, Integer> tools;
	@Ignore
	public String model;
	@Ignore
	public EnumRarity rarity;
	@Ignore
	public boolean glow;
	@Ignore
	public final List<ITextComponent> tooltip;

	public ItemBuilder(String i, Consumer<ItemBuilder> c)
	{
		id = ID.of(KubeJS.appendModId(i));
		callback = c;
		translationKey = id.getNamespace() + "." + id.getPath();
		maxStackSize = 64;
		maxDamage = 0;
		containerItem = null;
		tools = new HashMap<>();
		model = id.getNamespace() + ":" + id.getPath() + "#inventory";
		rarity = EnumRarity.COMMON;
		glow = false;
		tooltip = new ArrayList<>();
	}

	public ItemBuilder translationKey(@P("translationKey") String v)
	{
		translationKey = v;
		return this;
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

	public ItemBuilder containerItem(@P("id") ID id)
	{
		containerItem = id;
		return this;
	}

	public ItemBuilder tool(@P("type") String type, @P("level") int level)
	{
		tools.put(type, level);
		return this;
	}

	public ItemBuilder model(@P("model") String v)
	{
		model = v;
		return this;
	}

	public ItemBuilder rarity(@P("rarity") EnumRarity v)
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
		tooltip.add(Text.of(text).component());
		return this;
	}

	public void add()
	{
		callback.accept(this);
	}
}