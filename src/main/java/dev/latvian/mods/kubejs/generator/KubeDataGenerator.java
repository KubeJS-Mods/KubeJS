package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.datamaps.builtin.BiomeVillagerType;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.MonsterRoomMob;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.ParrotImitation;
import net.neoforged.neoforge.registries.datamaps.builtin.RaidHeroGift;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

public interface KubeDataGenerator extends KubeResourceGenerator {
	default void setCompostable(ItemPredicate items, float chance, boolean canVillagerCompost) {
		dataMap(NeoForgeDataMaps.COMPOSTABLES, callback -> {
			var data = new Compostable(chance, canVillagerCompost);

			for (var item : items.kjs$getItemTypes()) {
				callback.accept(item.kjs$getIdLocation(), data);
			}
		});
	}

	default void setFurnaceFuel(ItemPredicate items, TickDuration ticks) {
		dataMap(NeoForgeDataMaps.FURNACE_FUELS, callback -> {
			var data = new FurnaceFuel(ticks.intTicks());

			for (var item : items.kjs$getItemTypes()) {
				callback.accept(item.kjs$getIdLocation(), data);
			}
		});
	}

	default void setMonsterRoomMobs(EntityType<?> entityType, int weight) {
		dataMap(NeoForgeDataMaps.MONSTER_ROOM_MOBS, callback -> callback.accept(entityType.kjs$getIdLocation(), new MonsterRoomMob(Weight.of(weight))));
	}

	default void setOxidizable(Block from, Block to) {
		if (from != to) {
			dataMap(NeoForgeDataMaps.OXIDIZABLES, callback -> callback.accept(from.kjs$getIdLocation(), new Oxidizable(to)));
		}
	}

	default void setParrotImitation(EntityType<?> type, SoundEvent sound) {
		dataMap(NeoForgeDataMaps.PARROT_IMITATIONS, callback -> callback.accept(type.kjs$getIdLocation(), new ParrotImitation(sound)));
	}

	default void setRaidHeroGifts(VillagerProfession profession, ResourceKey<LootTable> lootTable) {
		dataMap(NeoForgeDataMaps.RAID_HERO_GIFTS, callback -> callback.accept(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession), new RaidHeroGift(lootTable)));
	}

	default void setVibrationFrequency(GameEvent gameEvent, int frequency) {
		dataMap(NeoForgeDataMaps.VIBRATION_FREQUENCIES, callback -> callback.accept(BuiltInRegistries.GAME_EVENT.getKey(gameEvent), new VibrationFrequency(frequency)));
	}

	default void setVibrationFrequency(Biome biome, VillagerType villagerType) {
		dataMap(NeoForgeDataMaps.VILLAGER_TYPES, callback -> callback.accept(getRegistries().access().registry(Registries.BIOME).orElseThrow().getKey(biome), new BiomeVillagerType(villagerType)));
	}

	default void setWaxable(Block from, Block to) {
		if (from != to) {
			dataMap(NeoForgeDataMaps.WAXABLES, callback -> callback.accept(from.kjs$getIdLocation(), new Waxable(to)));
		}
	}
}
