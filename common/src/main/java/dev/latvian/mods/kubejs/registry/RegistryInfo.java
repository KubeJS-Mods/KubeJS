package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class RegistryInfo implements Iterable<BuilderBase<?>> {
	public static final Map<ResourceKey<? extends Registry<?>>, RegistryInfo> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
	public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList<>();

	public static RegistryInfo of(ResourceKey<? extends Registry<?>> key) {
		return MAP.computeIfAbsent(key, RegistryInfo::new);
	}

	public static final RegistryInfo SOUND_EVENT = of(Registries.SOUND_EVENT).type(SoundEvent.class);
	public static final RegistryInfo FLUID = of(Registries.FLUID).type(Fluid.class);
	public static final RegistryInfo MOB_EFFECT = of(Registries.MOB_EFFECT).type(MobEffect.class);
	public static final RegistryInfo BLOCK = of(Registries.BLOCK).type(Block.class);
	public static final RegistryInfo ENCHANTMENT = of(Registries.ENCHANTMENT).type(Enchantment.class);
	public static final RegistryInfo ENTITY_TYPE = of(Registries.ENTITY_TYPE).type(EntityType.class);
	public static final RegistryInfo ITEM = of(Registries.ITEM).type(Item.class).noAutoWrap();
	public static final RegistryInfo POTION = of(Registries.POTION).type(Potion.class);
	public static final RegistryInfo PARTICLE_TYPE = of(Registries.PARTICLE_TYPE).type(ParticleType.class);
	public static final RegistryInfo BLOCK_ENTITY_TYPE = of(Registries.BLOCK_ENTITY_TYPE).type(BlockEntityType.class);
	public static final RegistryInfo PAINTING_VARIANT = of(Registries.PAINTING_VARIANT).type(PaintingVariant.class);
	public static final RegistryInfo CUSTOM_STAT = of(Registries.CUSTOM_STAT).type(ResourceLocation.class);
	public static final RegistryInfo CHUNK_STATUS = of(Registries.CHUNK_STATUS).type(ChunkStatus.class);
	public static final RegistryInfo RULE_TEST = of(Registries.RULE_TEST).type(RuleTestType.class);
	public static final RegistryInfo POS_RULE_TEST = of(Registries.POS_RULE_TEST).type(PosRuleTestType.class);
	public static final RegistryInfo MENU = of(Registries.MENU).type(MenuType.class);
	public static final RegistryInfo RECIPE_TYPE = of(Registries.RECIPE_TYPE).type(RecipeType.class);
	public static final RegistryInfo RECIPE_SERIALIZER = of(Registries.RECIPE_SERIALIZER).type(RecipeSerializer.class);
	public static final RegistryInfo ATTRIBUTE = of(Registries.ATTRIBUTE).type(Attribute.class);
	public static final RegistryInfo GAME_EVENT = of(Registries.GAME_EVENT).type(GameEvent.class);
	public static final RegistryInfo POSITION_SOURCE_TYPE = of(Registries.POSITION_SOURCE_TYPE).type(PositionSourceType.class);
	public static final RegistryInfo STAT_TYPE = of(Registries.STAT_TYPE).type(StatType.class);
	public static final RegistryInfo VILLAGER_TYPE = of(Registries.VILLAGER_TYPE).type(VillagerType.class);
	public static final RegistryInfo VILLAGER_PROFESSION = of(Registries.VILLAGER_PROFESSION).type(VillagerProfession.class);
	public static final RegistryInfo POINT_OF_INTEREST_TYPE = of(Registries.POINT_OF_INTEREST_TYPE).type(PoiType.class);
	public static final RegistryInfo MEMORY_MODULE_TYPE = of(Registries.MEMORY_MODULE_TYPE).type(MemoryModuleType.class);
	public static final RegistryInfo SENSOR_TYPE = of(Registries.SENSOR_TYPE).type(SensorType.class);
	public static final RegistryInfo SCHEDULE = of(Registries.SCHEDULE).type(Schedule.class);
	public static final RegistryInfo ACTIVITY = of(Registries.ACTIVITY).type(Activity.class);
	public static final RegistryInfo LOOT_ENTRY = of(Registries.LOOT_POOL_ENTRY_TYPE).type(LootPoolEntryType.class);
	public static final RegistryInfo LOOT_FUNCTION = of(Registries.LOOT_FUNCTION_TYPE).type(LootItemFunctionType.class);
	public static final RegistryInfo LOOT_ITEM = of(Registries.LOOT_CONDITION_TYPE).type(LootItemConditionType.class);
	public static final RegistryInfo LOOT_NUMBER_PROVIDER = of(Registries.LOOT_NUMBER_PROVIDER_TYPE).type(LootNumberProviderType.class);
	public static final RegistryInfo LOOT_NBT_PROVIDER = of(Registries.LOOT_NBT_PROVIDER_TYPE).type(LootNbtProviderType.class);
	public static final RegistryInfo LOOT_SCORE_PROVIDER = of(Registries.LOOT_SCORE_PROVIDER_TYPE).type(LootScoreProviderType.class);
	public static final RegistryInfo COMMAND_ARGUMENT_TYPE = of(Registries.COMMAND_ARGUMENT_TYPE).type(ArgumentTypeInfo.class);
	public static final RegistryInfo DIMENSION_TYPE = of(Registries.DIMENSION_TYPE).type(DimensionType.class);
	public static final RegistryInfo DIMENSION = of(Registries.DIMENSION).type(Level.class);
	public static final RegistryInfo LEVEL_STEM = of(Registries.LEVEL_STEM).type(LevelStem.class);
	public static final RegistryInfo FLOAT_PROVIDER_TYPE = of(Registries.FLOAT_PROVIDER_TYPE).type(FloatProviderType.class);
	public static final RegistryInfo INT_PROVIDER_TYPE = of(Registries.INT_PROVIDER_TYPE).type(IntProviderType.class);
	public static final RegistryInfo HEIGHT_PROVIDER_TYPE = of(Registries.HEIGHT_PROVIDER_TYPE).type(HeightProviderType.class);
	public static final RegistryInfo BLOCK_PREDICATE_TYPE = of(Registries.BLOCK_PREDICATE_TYPE).type(BlockPredicateType.class);
	public static final RegistryInfo NOISE_GENERATOR_SETTINGS = of(Registries.NOISE_SETTINGS).type(NoiseGeneratorSettings.class);
	public static final RegistryInfo CONFIGURED_CARVER = of(Registries.CONFIGURED_CARVER).type(ConfiguredWorldCarver.class);
	public static final RegistryInfo CONFIGURED_FEATURE = of(Registries.CONFIGURED_FEATURE).type(ConfiguredFeature.class);
	public static final RegistryInfo PLACED_FEATURE = of(Registries.PLACED_FEATURE).type(PlacedFeature.class);
	public static final RegistryInfo STRUCTURE = of(Registries.STRUCTURE).type(Structure.class);
	public static final RegistryInfo STRUCTURE_SET = of(Registries.STRUCTURE_SET).type(StructureSet.class);
	public static final RegistryInfo PROCESSOR_LIST = of(Registries.PROCESSOR_LIST).type(StructureProcessorList.class);
	public static final RegistryInfo TEMPLATE_POOL = of(Registries.TEMPLATE_POOL).type(StructureTemplatePool.class);
	public static final RegistryInfo BIOME = of(Registries.BIOME).type(Biome.class);
	public static final RegistryInfo NOISE = of(Registries.NOISE).type(NormalNoise.NoiseParameters.class);
	public static final RegistryInfo DENSITY_FUNCTION = of(Registries.DENSITY_FUNCTION).type(DensityFunction.class);
	public static final RegistryInfo WORLD_PRESET = of(Registries.WORLD_PRESET).type(WorldPreset.class);
	public static final RegistryInfo FLAT_LEVEL_GENERATOR_PRESET = of(Registries.FLAT_LEVEL_GENERATOR_PRESET).type(FlatLevelGeneratorPreset.class);
	public static final RegistryInfo CARVER = of(Registries.CARVER).type(WorldCarver.class);
	public static final RegistryInfo FEATURE = of(Registries.FEATURE).type(Feature.class);
	public static final RegistryInfo STRUCTURE_PLACEMENT_TYPE = of(Registries.STRUCTURE_PLACEMENT).type(StructurePlacementType.class);
	public static final RegistryInfo STRUCTURE_PIECE = of(Registries.STRUCTURE_PIECE).type(StructurePieceType.class);
	public static final RegistryInfo STRUCTURE_TYPE = of(Registries.STRUCTURE_TYPE).type(StructureType.class);
	public static final RegistryInfo PLACEMENT_MODIFIER = of(Registries.PLACEMENT_MODIFIER_TYPE).type(PlacementModifierType.class);
	public static final RegistryInfo BLOCK_STATE_PROVIDER_TYPE = of(Registries.BLOCK_STATE_PROVIDER_TYPE).type(BlockStateProviderType.class);
	public static final RegistryInfo FOLIAGE_PLACER_TYPE = of(Registries.FOLIAGE_PLACER_TYPE).type(FoliagePlacerType.class);
	public static final RegistryInfo TRUNK_PLACER_TYPE = of(Registries.TRUNK_PLACER_TYPE).type(TrunkPlacerType.class);
	public static final RegistryInfo TREE_DECORATOR_TYPE = of(Registries.TREE_DECORATOR_TYPE).type(TreeDecoratorType.class);
	public static final RegistryInfo ROOT_PLACER_TYPE = of(Registries.ROOT_PLACER_TYPE).type(RootPlacerType.class);
	public static final RegistryInfo FEATURE_SIZE_TYPE = of(Registries.FEATURE_SIZE_TYPE).type(FeatureSizeType.class);
	public static final RegistryInfo BIOME_SOURCE = of(Registries.BIOME_SOURCE).type(Codec.class).noAutoWrap();
	public static final RegistryInfo CHUNK_GENERATOR = of(Registries.CHUNK_GENERATOR).type(Codec.class).noAutoWrap();
	public static final RegistryInfo CONDITION = of(Registries.MATERIAL_CONDITION).type(Codec.class).noAutoWrap();
	public static final RegistryInfo RULE = of(Registries.MATERIAL_RULE).type(Codec.class).noAutoWrap();
	public static final RegistryInfo DENSITY_FUNCTION_TYPE = of(Registries.DENSITY_FUNCTION_TYPE).type(Codec.class).noAutoWrap();
	public static final RegistryInfo STRUCTURE_PROCESSOR = of(Registries.STRUCTURE_PROCESSOR).type(StructureProcessorType.class);
	public static final RegistryInfo STRUCTURE_POOL_ELEMENT = of(Registries.STRUCTURE_POOL_ELEMENT).type(StructurePoolElementType.class);
	public static final RegistryInfo CHAT_TYPE = of(Registries.CHAT_TYPE).type(ChatType.class);
	public static final RegistryInfo CAT_VARIANT = of(Registries.CAT_VARIANT).type(CatVariant.class);
	public static final RegistryInfo FROG_VARIANT = of(Registries.FROG_VARIANT).type(FrogVariant.class);
	public static final RegistryInfo BANNER_PATTERN = of(Registries.BANNER_PATTERN).type(BannerPattern.class);
	public static final RegistryInfo INSTRUMENT = of(Registries.INSTRUMENT).type(Instrument.class);

	/**
	 * Add your registry to these to make sure it comes after vanilla registries, if it depends on them.
	 * Only works on Fabric, since Forge already has ordered registries.
	 */
	public static final LinkedList<RegistryInfo> AFTER_VANILLA = new LinkedList<>();

	public final ResourceKey<? extends Registry<?>> key;
	public Class<?> objectBaseClass;
	public final Map<String, BuilderType> types;
	public final Map<ResourceLocation, BuilderBase<?>> objects;
	public boolean hasDefaultTags = false;
	private BuilderType defaultType;
	public boolean bypassServerOnly;
	public boolean autoWrap;

	private RegistryInfo(ResourceKey<? extends Registry<?>> key) {
		this.key = key;
		this.objectBaseClass = Object.class;
		this.types = new LinkedHashMap<>();
		this.objects = new LinkedHashMap<>();
		this.bypassServerOnly = false;
		this.autoWrap = true;
	}

	public RegistryInfo type(Class<?> baseClass) {
		this.objectBaseClass = baseClass;
		return this;
	}

	public RegistryInfo bypassServerOnly() {
		this.bypassServerOnly = true;
		return this;
	}

	public RegistryInfo noAutoWrap() {
		this.autoWrap = false;
		return this;
	}

	public void addType(String type, Class<? extends BuilderBase> builderType, BuilderFactory factory, boolean isDefault) {
		var b = new BuilderType(type, builderType, factory);
		types.put(type, b);

		if (isDefault) {
			if (defaultType != null) {
				ConsoleJS.STARTUP.warn("Previous default type '" + defaultType.type() + "' for registry '" + key.location() + "' replaced with '" + type + "'!");
			}

			defaultType = b;
		}
	}

	public void addType(String type, Class<? extends BuilderBase<?>> builderType, BuilderFactory factory) {
		addType(type, builderType, factory, type.equals("basic"));
	}

	public void addBuilder(BuilderBase<?> builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Can't add null builder in registry '" + key.location() + "'!");
		}

		if (DevProperties.get().debugInfo) {
			ConsoleJS.STARTUP.info("~ " + key.location() + " | " + builder.id);
		}

		if (objects.containsKey(builder.id)) {
			throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + key.location() + "'!");
		}

		objects.put(builder.id, builder);
		ALL_BUILDERS.add(builder);
	}

	@Nullable
	public BuilderType getDefaultType() {
		if (types.isEmpty()) {
			return null;
		} else if (defaultType == null) {
			defaultType = types.values().iterator().next();
		}

		return defaultType;
	}

	@Override
	public String toString() {
		return key.location().toString();
	}

	public int registerObjects(BiConsumer<ResourceLocation, Supplier<Object>> function) {
		if (DevProperties.get().debugInfo) {
			if (objects.isEmpty()) {
				KubeJS.LOGGER.info("Skipping " + this + " registry");
			} else {
				KubeJS.LOGGER.info("Building " + objects.size() + " objects of " + this + " registry");
			}
		}

		if (objects.isEmpty()) {
			return 0;
		}

		int added = 0;

		for (var builder : this) {
			if (!builder.dummyBuilder && (builder.getRegistryType().bypassServerOnly || !CommonProperties.get().serverOnly)) {
				function.accept(builder.id, builder::createTransformedObject);

				if (DevProperties.get().debugInfo) {
					ConsoleJS.STARTUP.info("+ " + this + " | " + builder.id);
				}

				added++;
			}
		}

		if (!objects.isEmpty() && DevProperties.get().debugInfo) {
			KubeJS.LOGGER.info("Registered " + added + "/" + objects.size() + " objects of " + this);
		}

		return added;
	}

	@NotNull
	@Override
	public Iterator<BuilderBase<?>> iterator() {
		return objects.values().iterator();
	}
}
