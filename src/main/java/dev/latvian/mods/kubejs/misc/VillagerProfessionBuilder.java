package dev.latvian.mods.kubejs.misc;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@ReturnsSelf
public class VillagerProfessionBuilder extends BuilderBase<VillagerProfession> {
	public transient Either<ResourceKey<PoiType>, TagKey<PoiType>> poiType;

	public transient ImmutableSet<Item> requestedItems;
	public transient ImmutableSet<Block> secondaryPoi;
	public transient @Nullable SoundEvent workSound;

	public VillagerProfessionBuilder(ResourceLocation i) {
		super(i);
		poiType = Either.right(PoiTypeTags.ACQUIRABLE_JOB_SITE);
		requestedItems = ImmutableSet.of();
		secondaryPoi = ImmutableSet.of();
		workSound = null;
	}

	@Override
	public VillagerProfession createObject() {
		Predicate<Holder<PoiType>> validPois = holder -> poiType.map(holder::is, holder::is);
		return new VillagerProfession(id.getPath(), validPois, validPois, requestedItems, secondaryPoi, workSound);
	}

	public VillagerProfessionBuilder poiType(ResourceLocation t) {
		poiType = Either.left(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, t));
		return this;
	}

	public VillagerProfessionBuilder poiTypeTag(ResourceLocation t) {
		poiType = Either.right(TagKey.create(Registries.POINT_OF_INTEREST_TYPE, t));
		return this;
	}

	public VillagerProfessionBuilder requestedItems(Item[] t) {
		requestedItems = ImmutableSet.copyOf(t);
		return this;
	}

	public VillagerProfessionBuilder secondaryPoi(Block[] t) {
		secondaryPoi = ImmutableSet.copyOf(t);
		return this;
	}

	public VillagerProfessionBuilder workSound(@Nullable SoundEvent t) {
		workSound = t;
		return this;
	}
}
