package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

public class ShearsItemBuilder extends ItemBuilder {

    public static final HashSet<ResourceLocation> SHEARS_ID_LIST = new HashSet<>();
    public transient float speedBaseline;
    public ShearsItemBuilder(ResourceLocation i) {
        super(i);
        speedBaseline = 5.0F;
        parentModel("minecraft:item/handheld");
        unstackable();

        SHEARS_ID_LIST.add(i);
    }
    public ShearsItemBuilder speedBaseline(float f) {
        speedBaseline = f;
        return this;
    }
    @Override
    public Item createObject() {
        return new ShearsItem(createItemProperties()) {
            @Override
            public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
                if (!blockState.is(BlockTags.LEAVES)) {
                    if (blockState.is(BlockTags.WOOL)) {
                        return speedBaseline;
                    } else {
                        return !blockState.is(Blocks.VINE) && !blockState.is(Blocks.GLOW_LICHEN) ?
                            super.getDestroySpeed(itemStack, blockState) :
                            speedBaseline / 2.5F;
                    }
                } else if (!blockState.is(Blocks.COBWEB)) {
                    return speedBaseline * 3;
                } else {
                    return 15.0F;
                }
            }
        };
    }
}
