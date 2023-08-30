package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.math.RoundingMode.CEILING;

public class MultitoolItemJS extends DiggerItem {
    public static class Builder extends HandheldItemBuilder {
        protected final List<Float> speedValues = new ArrayList<>(5);
        protected final List<Float> attackValues = new ArrayList<>(5);
        protected final EnumSet<ToolTypes> toolTypes = EnumSet.noneOf(ToolTypes.class);

        public Builder(ResourceLocation i) {
            super(i, 0, 0);
        }

        @Info(value = """
            Adds a tool to the multi-tool.
            
            Valid tool types include: 'axe', 'hoe', 'pickaxe', and 'shovel'.
            """, params =
        @Param(name = "tool", value = "The name of the tool to add to the multi-tool. Will error if it is an unknown tool type."))
        public Builder tool(ToolTypes tool) {
            toolTypes.add(tool);
            attackValues.add(tool.attack);
            speedValues.add(tool.speed);
            return this;
        }

        @Override
        public Builder attackDamageBaseline(float f) {
            attackValues.clear();
            attackValues.add(f);
            return this;
        }

        @Override
        public Builder speedBaseline(float f) {
            speedValues.clear();
            speedValues.add(f);
            return this;
        }

        protected void setValues() {
            // TOD0: find a way to make this better / more efficient
            // i hate floating-point jank

            // this finds the average of all the floats in attack/speedValues,
            // then does average += average * 0.8 or 0.5, and rounds it up to the
            // tenths place. the resulting value is the attackDamage/speedBaseline.
            var attack = new BigDecimal("0");
            for (final var f : attackValues) {
                attack = attack.add(new BigDecimal(Float.toString(f)));
            }
            attack = attack.divide(new BigDecimal(Integer.toString(attackValues.size())), 3, CEILING);
            attackDamageBaseline = attack.add(attack.multiply(BigDecimal.valueOf(.08)))
                .setScale(1, CEILING).floatValue();

            var speed = new BigDecimal("0");
            for (final var f : speedValues) {
                speed = speed.add(new BigDecimal(Float.toString(f)));
            }
            speed = speed.divide(new BigDecimal(Integer.toString(speedValues.size())), 3, CEILING);
            speedBaseline = speed.add(speed.multiply(BigDecimal.valueOf(.05)))
                .setScale(1, CEILING).floatValue();
        }

        @Override
        public Item createObject() {
            setValues();
            return new MultitoolItemJS(attackDamageBaseline, speedBaseline, toolTier, createItemProperties(), this);
        }
    }

    public enum ToolTypes {
        // maybe swords and shears? (i would need some help with that, as neither extends diggeritem)
        AXE(6, -3.1F, BlockTags.MINEABLE_WITH_AXE),
        HOE(-2, -1, BlockTags.MINEABLE_WITH_HOE),
        PICKAXE(1, -2.8F, BlockTags.MINEABLE_WITH_PICKAXE),
        SHOVEL(1.5F, -3, BlockTags.MINEABLE_WITH_SHOVEL);

        public final float attack, speed;
        @Nullable
        public final TagKey<Block> tag;

        ToolTypes(float attack, float speed, @Nullable TagKey<Block> tag) {
            this.attack = attack;
            this.speed = speed;
            this.tag = tag;
        }
    }

    {
        defaultModifiers = ArrayListMultimap.create(defaultModifiers);
    }

    public final Builder builder;
    public final Set<TagKey<Block>> mineableTags;
    private boolean modified = false;

    public MultitoolItemJS(float attack, float speed, MutableToolTier tier, Properties properties, Builder builder) {
        super(attack, speed, tier, null, properties);
        this.builder = builder;

        var mineables = new ImmutableSet.Builder<TagKey<Block>>();
        for (var toolType : builder.toolTypes)
            if (toolType.tag != null)
                mineables.add(toolType.tag);
        mineableTags = mineables.build();
    }

    public boolean isAxe() {
        return builder.toolTypes.contains(ToolTypes.AXE);
    }

    public boolean isHoe() {
        return builder.toolTypes.contains(ToolTypes.HOE);
    }

    public boolean isPickaxe() {
        return builder.toolTypes.contains(ToolTypes.PICKAXE);
    }

    public boolean isShovel() {
        return builder.toolTypes.contains(ToolTypes.SHOVEL);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return isInMineables(blockState) ? speed : 1f;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (!modified) {
            modified = true;
            builder.attributes.forEach((r, m) -> defaultModifiers.put(KubeJSRegistries.attributes().get(r), m));
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    // right-clicking on dirt will till, unless sneaking, then it will turn it into a path
    public InteractionResult useOn(UseOnContext ctx) {
        return (isAxe() && Items.IRON_AXE.useOn(ctx) != InteractionResult.PASS) ||
            (isShovel() && (!isHoe() || Optional.ofNullable(ctx.getPlayer())
                    .map(Entity::isCrouching).orElse(false)) && // player sneaking check
                Items.IRON_SHOVEL.useOn(ctx) != InteractionResult.PASS) ||
            (isHoe() && Items.IRON_HOE.useOn(ctx) != InteractionResult.PASS) ||
            (isPickaxe() && Items.IRON_HOE.useOn(ctx) != InteractionResult.PASS) ?
            InteractionResult.sidedSuccess(ctx.getLevel().isClientSide) : InteractionResult.PASS;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        final int i = tier.getLevel();

        return (i >= 3 || !state.is(BlockTags.NEEDS_DIAMOND_TOOL)) &&
               (i >= 2 || !state.is(BlockTags.NEEDS_IRON_TOOL)) &&
               (i >= 1 || !state.is(BlockTags.NEEDS_STONE_TOOL)) &&
               isInMineables(state);
    }

    protected boolean isInMineables(BlockState state) {
        for (final var tag : mineableTags)
            if (state.is(tag)) return true;
        return false;
    }
}