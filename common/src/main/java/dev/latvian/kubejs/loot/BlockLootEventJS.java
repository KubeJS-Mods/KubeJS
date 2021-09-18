package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockStatePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockLootEventJS extends LootEventJS<BlockLootBuilder> {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	public BlockLootEventJS(Map<ResourceLocation, JsonElement> c) {
		super(c);
	}

	@Override
	public BlockLootBuilder newLootBuilder() {
		return new BlockLootBuilder();
	}

	@Override
	public void addJson(ResourceLocation id, JsonObject json) {
		super.addJson(new ResourceLocation(id.getNamespace(), "blocks/" + id.getPath()), json);
	}

	public void addBlock(BlockStatePredicate blocks, Consumer<BlockLootBuilder> b) {
		BlockLootBuilder builder = new BlockLootBuilder();
		b.accept(builder);
		JsonObject json = builder.toJson(this);

		for (Block block : BlockStatePredicate.of(blocks).getBlocks()) {
			ResourceLocation blockId = KubeJSRegistries.blocks().getId(block);

			if (blockId != null && !blockId.equals(AIR_ID)) {
				addJson(blockId, json);
			}
		}
	}

	public void addSimpleBlock(BlockStatePredicate blocks) {
		addSimpleBlock(blocks, ItemStack.EMPTY);
	}

	public void addSimpleBlock(BlockStatePredicate blocks, ItemStack item) {
		for (Block block : blocks.getBlocks()) {
			ItemStack item1 = item.isEmpty() ? new ItemStack(block.asItem()) : item;

			if (item1.isEmpty()) {
				continue;
			}

			ResourceLocation blockId = KubeJSRegistries.blocks().getId(block);

			if (blockId != null && !blockId.equals(AIR_ID)) {
				build(blockId, loot -> {
					loot.pool(pool -> {
						pool.addItem(item1);
						pool.survivesExplosion();
					});
				});
			}
		}
	}
}
