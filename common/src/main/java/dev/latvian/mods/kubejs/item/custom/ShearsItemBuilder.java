package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

public class ShearsItemBuilder extends ItemBuilder {

    public static final HashSet<Item> SHEARS_LIST = new HashSet<>();
    public transient float speedBaseline;
    public ShearsItemBuilder(ResourceLocation i) {
        super(i);
        speedBaseline(5f);
        parentModel("minecraft:item/handheld");
        unstackable();

        SHEARS_LIST.add(Registry.ITEM.get(i));
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
                if (blockState.is(BlockTags.LEAVES)) {
                    return 15f;
                } else if (blockState.is(Blocks.COBWEB)) {
                    return speedBaseline * 3;
                } else if (blockState.is(Blocks.VINE) || blockState.is(Blocks.GLOW_LICHEN)) {
                    return speedBaseline / 2.5f;
                } else if (blockState.is(BlockTags.WOOL)) {
                    return speedBaseline;
                } else return super.getDestroySpeed(itemStack, blockState);
            }
        };
    }
}
