package dev.latvian.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.item.ingredient.IgnoreNBTIngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.util.nbt.NBTListJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author LatvianModder
 */
public abstract class ItemStackJS implements IngredientJS
{
	private static List<ItemStackJS> cachedItemList;

	public static ItemStackJS of(@Nullable Object o)
	{
		if (o == null)
		{
			return EmptyItemStackJS.INSTANCE;
		}
		else if (o instanceof ItemStackJS)
		{
			return (ItemStackJS) o;
		}
		else if (o instanceof IngredientJS)
		{
			return ((IngredientJS) o).getFirst();
		}
		else if (o instanceof ItemStack)
		{
			ItemStack stack = (ItemStack) o;
			return stack.isEmpty() ? EmptyItemStackJS.INSTANCE : new BoundItemStackJS(stack);
		}

		Map<String, Object> map = UtilsJS.getNormalizedMap(o);

		if (map != null)
		{
			if (map.containsKey("item"))
			{
				ItemStackJS stack = new UnboundItemStackJS(new ResourceLocation(KubeJS.appendModId(map.get("item").toString())));

				if (map.get("count") instanceof Number)
				{
					stack.setCount(((Number) map.get("count")).intValue());
				}

				if (map.containsKey("nbt"))
				{
					stack.setNbt(map.get("nbt"));
				}

				return stack;
			}
			else if (map.get("tag") instanceof CharSequence)
			{
				ItemStackJS stack = new TagIngredientJS(new ResourceLocation(map.get("tag").toString())).getFirst();

				if (map.containsKey("count"))
				{
					stack.setCount(UtilsJS.parseInt(map.get("count"), 1));
				}

				return stack;
			}
		}

		String s0 = String.valueOf(o).trim();

		if (s0.isEmpty() || s0.equals("air"))
		{
			return EmptyItemStackJS.INSTANCE;
		}

		String[] s = s0.split("\\s", 3);

		if (s[0].startsWith("#"))
		{
			return new TagIngredientJS(new ResourceLocation(s[0].substring(1))).getFirst().count(s.length >= 2 ? UtilsJS.parseInt(s[1], 1) : 1);
		}

		String ids = KubeJS.appendModId(s[0]);
		ItemStackJS stack = new UnboundItemStackJS(new ResourceLocation(ids));

		if (s.length >= 2)
		{
			stack.setCount(Integer.parseInt(s[1]));
		}

		if (s.length >= 3)
		{
			stack.setNbt(s[2]);
		}

		return stack;
	}

	public static ItemStackJS fromRecipeJson(JsonElement json)
	{
		if (json.isJsonPrimitive())
		{
			return of(json.getAsString());
		}
		else if (json.isJsonObject())
		{
			JsonObject o = json.getAsJsonObject();

			if (o.has("item"))
			{
				ItemStackJS stack = ItemStackJS.of(o.get("item").getAsString());

				if (o.has("count"))
				{
					stack.setCount(o.get("count").getAsInt());
				}

				if (o.has("nbt"))
				{
					//stack.setNbt(JsonToNBT.getTagFromJson(o.get("nbt")));
				}

				return stack;
			}
		}

		return EmptyItemStackJS.INSTANCE;
	}

	public static List<ItemStackJS> getList()
	{
		if (cachedItemList != null)
		{
			return cachedItemList;
		}

		LinkedHashSet<ItemStackJS> set = new LinkedHashSet<>();
		NonNullList<ItemStack> stackList = NonNullList.create();

		for (Item item : ForgeRegistries.ITEMS)
		{
			item.fillItemGroup(ItemGroup.SEARCH, stackList);
		}

		for (ItemStack stack : stackList)
		{
			if (!stack.isEmpty())
			{
				set.add(new BoundItemStackJS(stack).getCopy().count(1));
			}
		}

		cachedItemList = Collections.unmodifiableList(Arrays.asList(set.toArray(new ItemStackJS[0])));
		return cachedItemList;
	}

	public static void clearListCache()
	{
		cachedItemList = null;
	}

	public static List<ResourceLocation> getTypeList()
	{
		List<ResourceLocation> list = new ArrayList<>();

		for (Item item : ForgeRegistries.ITEMS)
		{
			list.add(item.getRegistryName());
		}

		return list;
	}

	public abstract Item getItem();

	@MinecraftClass
	public abstract ItemStack getItemStack();

	public ResourceLocation getId()
	{
		return getItem().getRegistryName();
	}

	public abstract ItemStackJS getCopy();

	public abstract void setCount(int count);

	@Override
	public abstract int getCount();

	@Override
	public final ItemStackJS count(int c)
	{
		setCount(c);
		return this;
	}

	@Override
	public boolean isEmpty()
	{
		return getCount() <= 0;
	}

	public boolean isBlock()
	{
		return getItem() instanceof BlockItem;
	}

	public abstract void setNbt(Object nbt);

	public abstract NBTCompoundJS getNbt();

	public final ItemStackJS nbt(@Nullable Object o)
	{
		setNbt(NBTBaseJS.of(o).asCompound());
		return this;
	}

	public NBTCompoundJS getNbtOrNew()
	{
		NBTCompoundJS nbt = getNbt();

		if (nbt.isNull())
		{
			nbt = new NBTCompoundJS();
			setNbt(nbt);
		}

		return nbt;
	}

	public Text getName()
	{
		return Text.of(getItemStack().getDisplayName());
	}

	public void setName(Object displayName)
	{
		Text t = Text.of(displayName);
		NBTCompoundJS nbt = getNbtOrNew();

		if (t instanceof TextTranslate)
		{
			nbt.compoundOrNew("display").set("LocName", ((TextTranslate) t).getKey());
		}
		else
		{
			String s = t.getFormattedString();

			if (s.endsWith("\u00a7r"))
			{
				s = s.substring(0, s.length() - 2);
			}

			nbt.compoundOrNew("display").set("Name", s);
		}

		setNbt(nbt);
	}

	public final ItemStackJS name(String displayName)
	{
		setName(displayName);
		return this;
	}

	public String toString()
	{
		boolean hasCount = getCount() > 1;
		boolean hasNBT = !getNbt().isNull();

		if (hasCount || hasNBT)
		{
			StringBuilder builder = new StringBuilder("{");

			if (hasCount)
			{
				builder.append(getCount());
				builder.append('x');
				builder.append(' ');
			}

			builder.append(getId());

			if (hasNBT)
			{
				builder.append(' ');
				builder.append(getNbt());
			}

			builder.append('}');
			return builder.toString();
		}

		return getId().toString();
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		if (stack.getCount() >= getCount() && areItemsEqual(stack))
		{
			return Objects.equals(getNbt(), stack.getNbt());
		}

		return false;
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		if (stack.getCount() >= getCount())
		{
			if (areItemsEqual(stack))
			{
				NBTCompoundJS nbt = getNbt();
				CompoundNBT nbt1 = stack.getTag();
				return nbt.isNull() == (nbt1 == null) && (nbt1 == null || Objects.equals(nbt1, nbt.createNBT()));
			}
		}

		return false;
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return Collections.singleton(this);
	}

	@Override
	public ItemStackJS getFirst()
	{
		return getCopy();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getItem(), getNbt());
	}

	@Override
	public boolean equals(Object o)
	{
		ItemStackJS s = of(o);

		if (s.isEmpty())
		{
			return false;
		}

		return areItemsEqual(s) && Objects.equals(getNbt(), s.getNbt());
	}

	public boolean strongEquals(Object o)
	{
		ItemStackJS s = of(o);

		if (s.isEmpty())
		{
			return false;
		}

		return getCount() == s.getCount() && areItemsEqual(s) && Objects.equals(getNbt(), s.getNbt());
	}

	public Map<ResourceLocation, Integer> getEnchantments()
	{
		Map<ResourceLocation, Integer> map = new LinkedHashMap<>();

		for (NBTBaseJS base : getNbt().get("ench", Constants.NBT.TAG_COMPOUND).asList())
		{
			NBTCompoundJS ench = base.asCompound();
			Enchantment enchantment = Enchantment.getEnchantmentByID(ench.get("id").asShort());

			if (enchantment != null)
			{
				int level = ench.get("lvl").asInt();

				if (level > 0)
				{
					map.put(enchantment.getRegistryName(), level);
				}
			}
		}

		return map;
	}

	public void setEnchantments(Map<ResourceLocation, Integer> map)
	{
		NBTCompoundJS nbt = getNbt();
		nbt.remove("ench");

		if (!map.isEmpty())
		{
			if (nbt.isNull())
			{
				nbt = new NBTCompoundJS();
			}

			NBTListJS list = nbt.listOrNew("ench");

			for (Map.Entry<ResourceLocation, Integer> entry : map.entrySet())
			{
				NBTCompoundJS ench = new NBTCompoundJS();
				ench.set("id", entry.getKey().toString());
				ench.set("lvl", entry.getValue());
				list.add(ench);
			}
		}

		setNbt(nbt);
	}

	public int getEnchantment(Object id)
	{
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(UtilsJS.getID(id));

		if (enchantment != null)
		{
			String enchantmentID = enchantment.getName();

			for (NBTBaseJS base : getNbt().get("ench", Constants.NBT.TAG_COMPOUND).asList())
			{
				NBTCompoundJS ench = base.asCompound();

				if (enchantmentID.equals(ench.get("id").asString()))
				{
					return ench.get("lvl").asShort();
				}
			}
		}

		return 0;
	}

	public ItemStackJS enchant(Map<Object, Integer> enchantments)
	{
		Map<ResourceLocation, Integer> map = getEnchantments();

		for (Map.Entry<Object, Integer> entry : enchantments.entrySet())
		{
			map.put(UtilsJS.getID(entry.getKey()), entry.getValue());
		}

		setEnchantments(map);
		return this;
	}

	public String getMod()
	{
		return getItem().getRegistryName().getNamespace();
	}

	public void addLore(Object text)
	{
		NBTCompoundJS nbt = getNbtOrNew();
		nbt.compoundOrNew("display").listOrNew("Lore").add(Text.of(text).getFormattedString());
		setNbt(nbt);
	}

	public IgnoreNBTIngredientJS ignoreNBT()
	{
		return new IgnoreNBTIngredientJS(this);
	}

	public boolean areItemsEqual(ItemStackJS stack)
	{
		return getItem() == stack.getItem();
	}

	public boolean areItemsEqual(ItemStack stack)
	{
		return getItem() == stack.getItem();
	}

	public int getHarvestLevel(ToolType tool, @Nullable PlayerJS player, @Nullable BlockContainerJS block)
	{
		ItemStack stack = getItemStack();
		return stack.getItem().getHarvestLevel(stack, tool, player == null ? null : player.minecraftPlayer, block == null ? null : block.getBlockState());
	}

	public int getHarvestLevel(ToolType tool)
	{
		return getHarvestLevel(tool, null, null);
	}

	@Override
	public JsonElement getJson()
	{
		JsonObject json = new JsonObject();
		json.addProperty("item", getId().toString());
		return json;
	}

	public JsonElement getResultJson()
	{
		JsonObject json = new JsonObject();
		json.addProperty("item", getId().toString());

		int c = getCount();

		if (c > 1)
		{
			json.addProperty("count", c);
		}

		CompoundNBT nbt = getNbt().createNBT();

		if (nbt != null && !nbt.isEmpty())
		{
			json.addProperty("nbt", nbt.toString());
		}

		return json;
	}
}