package dev.latvian.kubejs.item.fabric;

import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemJS;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class FabricItemJS extends ItemJS implements DynamicAttributeTool
{
	private final Map<ResourceLocation, Integer> toolsMap;
	private final float miningSpeed;

	public FabricItemJS(ItemBuilder p)
	{
		super(p);
		toolsMap = new HashMap<>();
		p.getToolsMap().forEach((type, level) -> {
			Tag<Item> tag = type.fabricTag.get();
			if (tag instanceof Tag.Named)
			{
				toolsMap.put(((Tag.Named<Item>) tag).getName(), level);
			}
		});
		miningSpeed = p.getMiningSpeed();
	}

	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user)
	{
		if (tag instanceof Tag.Named)
		{
			Tag.Named<Item> identified = (Tag.Named<Item>) tag;
			Integer level = toolsMap.get(identified.getName());
			if (level != null)
			{
				return level;
			}
		}
		return 0;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if (toolsMap.isEmpty())
		{
			return miningSpeed;
		}
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user)
	{
		if (tag instanceof Tag.Named)
		{
			Tag.Named<Item> identified = (Tag.Named<Item>) tag;
			Integer level = toolsMap.get(identified.getName());
			if (level != null)
			{
				return miningSpeed;
			}
		}
		return 1.0F;
	}
}