package dev.latvian.mods.kubejs.item.custom.fabric;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class FabricBasicItemJS extends BasicItemJS implements DynamicAttributeTool {
	private final Map<ResourceLocation, Integer> toolsMap;
	private final float miningSpeed;

	public FabricBasicItemJS(ItemBuilder p) {
		super(p);
		toolsMap = new HashMap<>();

		p.getToolsMap().forEach((type, level) -> {
			var tag = type.fabricTag.get();

			if (tag instanceof Tag.Named) {
				toolsMap.put(((Tag.Named<Item>) tag).getName(), level);
			}
		});

		miningSpeed = p.getMiningSpeed();
	}

	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag instanceof Tag.Named<Item> identified) {
			var level = toolsMap.get(identified.getName());

			if (level != null) {
				return level;
			}
		}
		return 0;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (toolsMap.isEmpty()) {
			return miningSpeed;
		}

		return super.getDestroySpeed(stack, state);
	}

	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag instanceof Tag.Named<Item> identified) {
			var level = toolsMap.get(identified.getName());

			if (level != null) {
				return miningSpeed;
			}
		}

		return 1.0F;
	}
}