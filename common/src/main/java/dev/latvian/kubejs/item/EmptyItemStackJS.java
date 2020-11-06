package dev.latvian.kubejs.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ToolType;

import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class EmptyItemStackJS extends ItemStackJS
{
	public static final EmptyItemStackJS INSTANCE = new EmptyItemStackJS();

	private EmptyItemStackJS()
	{
	}

	@Override
	public String getId()
	{
		return "minecraft:air";
	}

	@Override
	public Item getItem()
	{
		return Items.AIR;
	}

	@Override
	public ItemStack getItemStack()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStackJS getCopy()
	{
		return this;
	}

	@Override
	public void setCount(int c)
	{
	}

	@Override
	public int getCount()
	{
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public MapJS getNbt()
	{
		return new MapJS()
		{
			@Override
			protected boolean setChangeListener(@Nullable Object v)
			{
				return false;
			}
		};
	}

	@Override
	public void setChance(double c)
	{
	}

	@Override
	public double getChance()
	{
		return 1D;
	}

	public String toString()
	{
		return "air";
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return false;
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return false;
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return Collections.emptySet();
	}

	@Override
	public ItemStackJS getFirst()
	{
		return this;
	}

	@Override
	public IngredientJS not()
	{
		return MatchAllIngredientJS.INSTANCE;
	}

	@Override
	public void setName(@Nullable Object displayName)
	{
	}

	@Override
	public MapJS getEnchantments()
	{
		return new MapJS()
		{
			@Override
			protected boolean setChangeListener(@Nullable Object v)
			{
				return false;
			}
		};
	}

	@Override
	public ItemStackJS enchant(Object map)
	{
		return this;
	}

	@Override
	public String getMod()
	{
		return "minecraft";
	}

	@Override
	public ListJS getLore()
	{
		return new ListJS()
		{
			@Override
			protected boolean setChangeListener(@Nullable Object v)
			{
				return false;
			}
		};
	}

	@Override
	public boolean areItemsEqual(ItemStackJS stack)
	{
		return stack.isEmpty();
	}

	@Override
	public boolean areItemsEqual(ItemStack stack)
	{
		return stack.isEmpty();
	}

	@Override
	public boolean isNBTEqual(ItemStackJS stack)
	{
		return stack.getNbt().isEmpty();
	}

	@Override
	public boolean isNBTEqual(ItemStack stack)
	{
		return !stack.hasTag();
	}

	@Override
	public boolean equals(Object o)
	{
		return ItemStackJS.of(o).isEmpty();
	}

	@Override
	public boolean strongEquals(Object o)
	{
		return ItemStackJS.of(o).isEmpty();
	}

	@Override
	public int getHarvestLevel(ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block)
	{
		return -1;
	}

	@Override
	public JsonElement toJson()
	{
		return new JsonArray();
	}

	@Override
	public JsonElement toResultJson()
	{
		JsonObject json = new JsonObject();
		json.addProperty("item", "minecraft:air");
		return json;
	}
}