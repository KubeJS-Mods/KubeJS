package dev.latvian.kubejs.player;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS {
	private final PlayerJS<?> player;
	private final StatsCounter statFile;

	public PlayerStatsJS(PlayerJS<?> p, StatsCounter s) {
		player = p;
		statFile = s;
	}

	public PlayerJS<?> getPlayer() {
		return player;
	}

	public int get(ResourceLocation id) {
		return statFile.getValue(UtilsJS.getStat(id));
	}

	public void set(ResourceLocation id, int value) {
		statFile.setValue(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}

	public void add(ResourceLocation id, int value) {
		statFile.increment(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}

	public int getBlocksMined(Block block) {
		return statFile.getValue(Stats.BLOCK_MINED.get(block));
	}

	public int getItemsCrafted(Item item) {
		return statFile.getValue(Stats.ITEM_CRAFTED.get(item));
	}

	public int getItemsUsed(Item item) {
		return statFile.getValue(Stats.ITEM_USED.get(item));
	}

	public int getItemsBroken(Item item) {
		return statFile.getValue(Stats.ITEM_BROKEN.get(item));
	}

	public int getItemsPickedUp(Item item) {
		return statFile.getValue(Stats.ITEM_PICKED_UP.get(item));
	}

	public int getItemsDropped(Item item) {
		return statFile.getValue(Stats.ITEM_DROPPED.get(item));
	}

	public int getKilled(EntityType<?> entity) {
		return statFile.getValue(Stats.ENTITY_KILLED.get(entity));
	}

	public int getKilledBy(EntityType<?> entity) {
		return statFile.getValue(Stats.ENTITY_KILLED_BY.get(entity));
	}
}