package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.custom.BasicCropBlockJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeedItemBuilder extends BlockItemBuilder {
	public static class SeedKubeItem extends ItemNameBlockItem implements SpecialPlantable {
		public SeedKubeItem(SeedItemBuilder b) {
			super(b.blockBuilder.get(), b.createItemProperties());
		}

		@Override
		public boolean canPlacePlantAtPosition(@NotNull ItemStack stack, @NotNull LevelReader level, @NotNull BlockPos pos, @Nullable Direction direction) {
			BasicCropBlockJS cropBlock = (BasicCropBlockJS) getBlock();
			return cropBlock.canSurvive(cropBlock.defaultBlockState(), level, pos);
		}

		@Override
		public void spawnPlantAtPosition(@NotNull ItemStack stack, LevelAccessor level, @NotNull BlockPos pos, @Nullable Direction direction) {
			level.setBlock(pos, getBlock().defaultBlockState(), 2);
		}

		@Override
		public boolean villagerCanPlantItem(@NotNull Villager villager) {
			return true;
		}
	}

	public SeedItemBuilder(ResourceLocation i) {
		super(i);
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
