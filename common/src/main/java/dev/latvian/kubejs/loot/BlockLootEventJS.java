package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockLootEventJS extends LootEventJS {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	public BlockLootEventJS(Map<ResourceLocation, JsonElement> c) {
		super(c);
	}

	public void build(BlockStatePredicate blocks, Consumer<BlockLootBuilder> b) {
		addBlock(blocks, b);
		ConsoleJS.SERVER.setLineNumber(true);
		ConsoleJS.SERVER.warn("This method is no longer supported! Use event.addBlock(blockPredicate, loot => {...})");
		ConsoleJS.SERVER.setLineNumber(false);
	}

	@Override
	public String getDirectory() {
		return "blocks";
	}

	public void addBlock(BlockStatePredicate blocks, Consumer<BlockLootBuilder> b) {
		BlockLootBuilder builder = new BlockLootBuilder();
		b.accept(builder);
		JsonObject json = builder.toJson(this);

		for (Block block : blocks.getBlocks()) {
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

			if (!item1.isEmpty()) {
				addBlock(new BlockStatePredicate.FromID(block), loot -> {
					loot.pool(pool -> {
						pool.addItem(item1);
						pool.survivesExplosion();
					});
				});
			}
		}
	}
}
