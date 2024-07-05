package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.jetbrains.annotations.Nullable;

public class SeedItemBuilder extends BlockItemBuilder {
	public static class SeedItemJS extends BasicItemJS implements SpecialPlantable {
		public SeedItemJS(ItemBuilder p) {
			super(p);
		}

		@Override
		public boolean canPlacePlantAtPosition(ItemStack stack, LevelReader level, BlockPos pos, @Nullable Direction direction) {
			return false;
		}

		@Override
		public void spawnPlantAtPosition(ItemStack stack, LevelReader level, BlockPos pos, @Nullable Direction direction) {
		}

		@Override
		public boolean villagerCanPlantItem(Villager villager) {
			return false;
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
		// return new ItemNameBlockItem(blockBuilder.get(), createItemProperties());
		return new SeedItemJS(this);
	}

	@Override
	public void generateAssetJsons(KubeAssetGenerator generator) {
		if (modelJson != null) {
			generator.json(KubeAssetGenerator.asItemModelLocation(id), modelJson);
			return;
		}

		generator.itemModel(id, m -> {
			if (!parentModel.isEmpty()) {
				m.parent(parentModel);
			} else {
				m.parent("minecraft:item/generated");
			}

			if (textureJson.size() == 0) {
				texture(newID("item/", "").toString());
			}
			m.textures(textureJson);
		});
	}
}
