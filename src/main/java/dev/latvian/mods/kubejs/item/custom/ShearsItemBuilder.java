package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

@ReturnsSelf
public class ShearsItemBuilder extends ItemBuilder {
	public static final ResourceLocation[] SHEAR_TAGS = {
		Tags.Items.TOOLS_SHEAR.location(),
	};

	public static boolean isCustomShears(ItemStack stack) {
		return stack.getItem() instanceof ShearsItemKJS;
	}

	public transient float speedBaseline;

	public ShearsItemBuilder(ResourceLocation i) {
		super(i);
		speedBaseline(5f);
		parentModel(KubeAssetGenerator.HANDHELD_ITEM_MODEL);
		unstackable();
		tag(SHEAR_TAGS);
		tool = ShearsItem.createToolProperties();
	}

	public ShearsItemBuilder speedBaseline(float f) {
		speedBaseline = f;
		return this;
	}

	@Override
	public Item createObject() {
		var item = new ShearsItemKJS(this);
		DispenserBlock.registerBehavior(item, new ShearsDispenseItemBehavior());
		return item;
	}

	public static class ShearsItemKJS extends ShearsItem {
		public final ShearsItemBuilder builder;

		public ShearsItemKJS(ShearsItemBuilder builder) {
			super(builder.createItemProperties());
			this.builder = builder;
		}

		@Override
		public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
			if (blockState.is(BlockTags.LEAVES)) {
				return 15F;
			} else if (blockState.is(Blocks.COBWEB)) {
				return builder.speedBaseline * 3F;
			} else if (blockState.is(Blocks.VINE) || blockState.is(Blocks.GLOW_LICHEN)) {
				return builder.speedBaseline / 2.5F;
			} else if (blockState.is(BlockTags.WOOL)) {
				return builder.speedBaseline;
			} else {
				return super.getDestroySpeed(itemStack, blockState);
			}
		}
	}
}
