package dev.latvian.mods.kubejs.recipe;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class CachedItemTagLookup extends CachedTagLookup<Item> {
	public CachedItemTagLookup(Registry<Item> registry, Map<ResourceLocation, List<TagLoader.EntryWithSource>> originalMap) {
		super(registry, originalMap);
	}

	@Override
	public boolean isEmpty(TagKey<Item> key) {
		var set = values(key);
		return set.size() - ((set.contains(Items.AIR) ? 1 : 0) + (set.contains(Items.BARRIER) ? 1 : 0)) <= 0;
	}
}
