package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.custom.BasicCropBlockJS;
import dev.latvian.mods.kubejs.block.custom.CropBlockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.jspecify.annotations.Nullable;

public class SeedItemBuilder extends BlockItemBuilder {
	public static class SeedKubeItem extends BlockItem implements SpecialPlantable {
		private final boolean villagerCanPlant;

		public SeedKubeItem(SeedItemBuilder b) {
			super(b.blockBuilder.get(), b.createItemProperties());
			this.villagerCanPlant = b.villagerCanPlant;
		}

		@Override
		public boolean canPlacePlantAtPosition(ItemStack stack, LevelReader level, BlockPos pos, @Nullable Direction direction) {
			BasicCropBlockJS cropBlock = (BasicCropBlockJS) getBlock();
			return cropBlock.canSurvive(cropBlock.defaultBlockState(), level, pos);
		}

		@Override
		public void spawnPlantAtPosition(ItemStack stack, LevelAccessor level, BlockPos pos, @Nullable Direction direction) {
			level.setBlock(pos, getBlock().defaultBlockState(), 2);
		}

		@Override
		public boolean villagerCanPlantItem(Villager villager) {
			return villagerCanPlant;
		}
	}

	public transient boolean villagerCanPlant;

	public SeedItemBuilder(CropBlockBuilder b, Identifier i) {
		super(b, i);
		this.villagerCanPlant = true;
	}

	public SeedItemBuilder villagerCanPlant(boolean canPlant) {
		this.villagerCanPlant = canPlant;
		return this;
	}

	@Override
	public String getTranslationKeyGroup() {
		return "item";
	}

	@Override
	public Item createObject() {
		return new SeedKubeItem(this);
	}
}
