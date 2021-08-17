package dev.latvian.kubejs.loot.handler;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockLootTableHandler extends LootTableHandler{
	public BlockLootTableHandler(Map<ResourceLocation, JsonElement> tables) {
		super(tables);
	}

	@Override
	public String getType() {
		return "minecraft:block";
	}

	public void simple(Object blocks, IngredientJS ingredient) {
		build(blocks, lootTableBuilder -> {
			lootTableBuilder.pool(pool -> {
				pool.survivesExplosion();
				pool.addEntry(ingredient);
			});
		});
	}

	public void simple(Object blocks) {
		getBlocksIdStream(blocks).forEach(resourceLocation -> {
			Item item = KubeJSRegistries.items().get(resourceLocation);
			if (item != null) {
				simple(resourceLocation, IngredientJS.of(item));
			}
		});
	}

	protected Stream<ResourceLocation> getBlocksIdStream(Object objects) {
		return BlockStatePredicate.of(objects).getBlocks()
				.stream()
				.map(block -> KubeJSRegistries.blocks().getId(block))
				.filter(rl -> rl != null && !rl.equals(AIR_ID));
	}

	@Override
	public Set<ResourceLocation> getLocations(Object objects) {
		return getBlocksIdStream(objects)
				.map(id -> new ResourceLocation(id.getNamespace(), "blocks/" + id.getPath()))
				.collect(Collectors.toSet());
	}
}
