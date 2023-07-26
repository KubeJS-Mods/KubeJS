package dev.latvian.mods.kubejs.item.custom;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class ShearsItemBuilder extends ItemBuilder {
    public static final Set<ResourceLocation> SHEARS_ID_SET = new HashSet<>();
    public transient float speedBaseline;
    public ShearsItemBuilder(ResourceLocation i) {
        super(i);
        speedBaseline(5f);
        parentModel("minecraft:item/handheld");
        unstackable();
        if (Platform.isForge())
            tag(new ResourceLocation("forge", "shears"));

        SHEARS_ID_SET.add(i);
    }
    public ShearsItemBuilder speedBaseline(float f) {
        speedBaseline = f;
        return this;
    }
    @Override
    public Item createObject() {
        Item item = new ShearsItem(createItemProperties()) {
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
        DispenserBlock.registerBehavior(item, new ShearsDispenseItemBehavior());
        return item;
    }
}
