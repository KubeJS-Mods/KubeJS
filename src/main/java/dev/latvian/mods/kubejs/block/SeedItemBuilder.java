package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.custom.BasicCropBlockJS;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
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
	public static class SeedItemJS extends ItemNameBlockItem implements SpecialPlantable {
		public SeedItemJS(SeedItemBuilder b) {
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
		return new SeedItemJS(this);
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		if (modelJson != null) {
			generator.json(id.withPath(ID.ITEM_MODEL), modelJson);
			return;
		}

		generator.itemModel(id, m -> {
			m.parent(parentModel != null ? parentModel : KubeAssetGenerator.GENERATED_ITEM_MODEL);

			if (textures.isEmpty()) {
				texture(id.withPath(ID.ITEM).toString());
			}

			m.textures(textures);
		});
	}
}
