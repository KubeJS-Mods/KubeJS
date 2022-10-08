package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS {
	public final Player player;
	private final StatsCounter statFile;

	public PlayerStatsJS(Player p, StatsCounter s) {
		player = p;
		statFile = s;
	}

	public static Stat<?> statOf(Object o) {
		if (o instanceof Stat s) {
			return s;
		} else if (o instanceof ResourceLocation rl) {
			return Stats.CUSTOM.get(rl);
		} else if (o instanceof CharSequence cs) {
			return Stats.CUSTOM.get(new ResourceLocation(cs.toString()));
		}
		return null;
	}

	public int get(Stat<?> stat) {
		return statFile.getValue(stat);
	}

	@HideFromJS // To prevent ambiguity. The type wrapper should automatically wrap RL's to Stats using #statOf if you wish to grab a stat by RL from a script
	public int get(ResourceLocation rl) {
		return get(Stats.CUSTOM.get(rl));
	}

	public int getPlayTime() {
		return get(Stats.PLAY_TIME);
	}

	public int getTimeSinceDeath() {
		return get(Stats.TIME_SINCE_DEATH);
	}

	public int getTimeSinceRest() {
		return get(Stats.TIME_SINCE_REST);
	}

	public int getTimeCrouchTime() {
		return get(Stats.CROUCH_TIME);
	}

	public int getJumps() {
		return get(Stats.JUMP);
	}

	public int getWalkDistance() {
		return get(Stats.WALK_ONE_CM);
	}

	public int getSprintDistance() {
		return get(Stats.SPRINT_ONE_CM);
	}

	public int getSwimDistance() {
		return get(Stats.SWIM_ONE_CM);
	}

	public int getCrouchDistance() {
		return get(Stats.CROUCH_ONE_CM);
	}

	public int getDamageDealt() {
		return get(Stats.DAMAGE_DEALT);
	}

	public int getDamageDealt_absorbed() {
		return get(Stats.DAMAGE_DEALT_ABSORBED);
	}

	public int getDamageDealt_resisted() {
		return get(Stats.DAMAGE_DEALT_RESISTED);
	}

	public int getDamageTaken() {
		return get(Stats.DAMAGE_TAKEN);
	}

	public int getDamageBlocked_by_shield() {
		return get(Stats.DAMAGE_BLOCKED_BY_SHIELD);
	}

	public int getDamageAbsorbed() {
		return get(Stats.DAMAGE_ABSORBED);
	}

	public int getDamageResisted() {
		return get(Stats.DAMAGE_RESISTED);
	}

	public int getDeaths() {
		return get(Stats.DEATHS);
	}

	public int getMobKills() {
		return get(Stats.MOB_KILLS);
	}

	public int getAnimalsBred() {
		return get(Stats.ANIMALS_BRED);
	}

	public int getPlayerKills() {
		return get(Stats.PLAYER_KILLS);
	}

	public int getFishCaught() {
		return get(Stats.FISH_CAUGHT);
	}

	public void set(Stat<?> stat, int value) {
		statFile.setValue(player, stat, value);
	}

	public void add(Stat<?> stat, int value) {
		statFile.increment(player, stat, value);
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