package dev.latvian.mods.kubejs.item.forge.custom;

import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.item.custom.MultitoolItemJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class MultitoolItemJSForge extends MultitoolItemJS {
    public static class Builder extends MultitoolItemJS.Builder {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public Item createObject() {
            setValues();
            return new MultitoolItemJSForge(attackDamageBaseline, speedBaseline, toolTier, createItemProperties(), this);
        }
    }

    public MultitoolItemJSForge(float attack, float speed, MutableToolTier tier, Properties properties, Builder builder) {
        super(attack, speed, tier, properties, builder);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return super.canPerformAction(stack, toolAction) ||
            ((isAxe() && ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction)) ||
             (isHoe() && ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction)) ||
             (isPickaxe() && ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction)) ||
             (isShovel() && ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction)));
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return isInMineables(state) && TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return isCorrectToolForDrops(state);
    }
}
