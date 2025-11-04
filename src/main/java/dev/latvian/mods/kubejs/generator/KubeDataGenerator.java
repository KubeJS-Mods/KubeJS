package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.script.data.VirtualDataMapFile;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface KubeDataGenerator extends KubeResourceGenerator {
	<R, T> void dataMap(DataMapType<R, T> type, Consumer<VirtualDataMapFile<R, T>> consumer);

	default void setCompostable(ItemPredicate items, float chance, boolean canVillagerCompost) {
		dataMap(NeoForgeDataMaps.COMPOSTABLES, callback -> add(callback, items, new Compostable(chance, canVillagerCompost)));
	}

	default void removeCompostable(ItemPredicate items) {
		dataMap(NeoForgeDataMaps.COMPOSTABLES, callback -> remove(callback, items));
	}

	default void setFurnaceFuel(ItemPredicate items, TickDuration ticks) {
		dataMap(NeoForgeDataMaps.FURNACE_FUELS, callback -> add(callback, items, new FurnaceFuel(ticks.intTicks())));
	}

	default void removeFurnaceFuel(ItemPredicate items) {
		dataMap(NeoForgeDataMaps.FURNACE_FUELS, callback -> remove(callback, items));
	}

	private static <T> void add(VirtualDataMapFile<Item, T> dataMap, ItemPredicate filter, T data) {
		var tag = filter instanceof Ingredient ingredient ? ingredient.kjs$getTagKey() : null;

		if (tag != null) {
			dataMap.addTag(tag, data);
		} else {
			for (var item : filter.kjs$getItemTypes()) {
				dataMap.add(item, data);
			}
		}
	}

	private static void remove(VirtualDataMapFile<Item, ?> dataMap, ItemPredicate filter) {
		var tag = filter instanceof Ingredient ingredient ? ingredient.kjs$getTagKey() : null;

		if (tag != null) {
			dataMap.removeTag(tag);
		} else {
			for (var item : filter.kjs$getItemTypes()) {
				dataMap.remove(item);
			}
		}
	}

	private static <T> void use(ItemPredicate filter, BiConsumer<TagKey<Item>, T> ifTag, BiConsumer<Item, T> ifItem, T data) {
		var tag = filter instanceof Ingredient ingredient ? ingredient.kjs$getTagKey() : null;

		if (tag != null) {
			ifTag.accept(tag, data);
		} else {
			for (var item : filter.kjs$getItemTypes()) {
				ifItem.accept(item, data);
			}
		}
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

	default void setVillagerType(ResourceKey<Biome> biome, VillagerType villagerType) {
		dataMap(NeoForgeDataMaps.VILLAGER_TYPES, callback -> callback.accept(biome.location(), new BiomeVillagerType(villagerType)));
	}

	default void setWaxable(Block from, Block to) {
		if (from != to) {
			dataMap(NeoForgeDataMaps.WAXABLES, callback -> callback.accept(from.kjs$getIdLocation(), new Waxable(to)));
		}
	}
}
