package dev.latvian.mods.kubejs.item.fabric.custom;

import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.item.custom.MultitoolItemJS;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MultitoolItemJSFabric extends MultitoolItemJS {
    public static class Builder extends MultitoolItemJS.Builder {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public Item createObject() {
            setValues();
            return new MultitoolItemJSFabric(attackDamageBaseline, speedBaseline, toolTier, createItemProperties(), this);
        }
    }
    public MultitoolItemJSFabric(float attack, float speed, MutableToolTier tier, Properties properties, Builder builder) {
        super(attack, speed, tier, properties, builder);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return tier.getLevel() >= MiningLevelManager.getRequiredMiningLevel(state) && isInMineables(state);
    }
}
