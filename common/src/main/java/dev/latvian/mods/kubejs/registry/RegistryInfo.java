package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
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

	public static final RegistryInfo SOUND_EVENT = of(Registry.SOUND_EVENT_REGISTRY).type(SoundEvent.class);
	public static final RegistryInfo FLUID = of(Registry.FLUID_REGISTRY).type(Fluid.class);
	public static final RegistryInfo MOB_EFFECT = of(Registry.MOB_EFFECT_REGISTRY).type(MobEffect.class).languageKeyPrefix("effect");
	public static final RegistryInfo BLOCK = of(Registry.BLOCK_REGISTRY).type(Block.class);
	public static final RegistryInfo ENCHANTMENT = of(Registry.ENCHANTMENT_REGISTRY).type(Enchantment.class);
	public static final RegistryInfo ENTITY_TYPE = of(Registry.ENTITY_TYPE_REGISTRY).type(EntityType.class);
	public static final RegistryInfo ITEM = of(Registry.ITEM_REGISTRY).type(Item.class).noAutoWrap();
	public static final RegistryInfo POTION = of(Registry.POTION_REGISTRY).type(Potion.class);
	public static final RegistryInfo PARTICLE_TYPE = of(Registry.PARTICLE_TYPE_REGISTRY).type(ParticleType.class);
	public static final RegistryInfo BLOCK_ENTITY_TYPE = of(Registry.BLOCK_ENTITY_TYPE_REGISTRY).type(BlockEntityType.class);
	public static final RegistryInfo PAINTING_VARIANT = of(Registry.PAINTING_VARIANT_REGISTRY).type(PaintingVariant.class);
	public static final RegistryInfo CUSTOM_STAT = of(Registry.CUSTOM_STAT_REGISTRY).type(ResourceLocation.class);
	public static final RegistryInfo CHUNK_STATUS = of(Registry.CHUNK_STATUS_REGISTRY).type(ChunkStatus.class);
	public static final RegistryInfo RULE_TEST = of(Registry.RULE_TEST_REGISTRY).type(RuleTestType.class);
	public static final RegistryInfo POS_RULE_TEST = of(Registry.POS_RULE_TEST_REGISTRY).type(PosRuleTestType.class);
	public static final RegistryInfo MENU = of(Registry.MENU_REGISTRY).type(MenuType.class);
	public static final RegistryInfo RECIPE_TYPE = of(Registry.RECIPE_TYPE_REGISTRY).type(RecipeType.class);
	public static final RegistryInfo RECIPE_SERIALIZER = of(Registry.RECIPE_SERIALIZER_REGISTRY).type(RecipeSerializer.class);
	public static final RegistryInfo ATTRIBUTE = of(Registry.ATTRIBUTE_REGISTRY).type(Attribute.class);
	public static final RegistryInfo GAME_EVENT = of(Registry.GAME_EVENT_REGISTRY).type(GameEvent.class);
	public static final RegistryInfo POSITION_SOURCE_TYPE = of(Registry.POSITION_SOURCE_TYPE_REGISTRY).type(PositionSourceType.class);
	public static final RegistryInfo STAT_TYPE = of(Registry.STAT_TYPE_REGISTRY).type(StatType.class);
	public static final RegistryInfo VILLAGER_TYPE = of(Registry.VILLAGER_TYPE_REGISTRY).type(VillagerType.class);
	public static final RegistryInfo VILLAGER_PROFESSION = of(Registry.VILLAGER_PROFESSION_REGISTRY).type(VillagerProfession.class);
	public static final RegistryInfo POINT_OF_INTEREST_TYPE = of(Registry.POINT_OF_INTEREST_TYPE_REGISTRY).type(PoiType.class);
	public static final RegistryInfo MEMORY_MODULE_TYPE = of(Registry.MEMORY_MODULE_TYPE_REGISTRY).type(MemoryModuleType.class);
	public static final RegistryInfo SENSOR_TYPE = of(Registry.SENSOR_TYPE_REGISTRY).type(SensorType.class);
	public static final RegistryInfo SCHEDULE = of(Registry.SCHEDULE_REGISTRY).type(Schedule.class);
	public static final RegistryInfo ACTIVITY = of(Registry.ACTIVITY_REGISTRY).type(Activity.class);
	public static final RegistryInfo LOOT_ENTRY = of(Registry.LOOT_ENTRY_REGISTRY).type(LootPoolEntryType.class);
	public static final RegistryInfo LOOT_FUNCTION = of(Registry.LOOT_FUNCTION_REGISTRY).type(LootItemFunctionType.class);
	public static final RegistryInfo LOOT_ITEM = of(Registry.LOOT_ITEM_REGISTRY).type(LootItemConditionType.class);
	public static final RegistryInfo LOOT_NUMBER_PROVIDER = of(Registry.LOOT_NUMBER_PROVIDER_REGISTRY).type(LootNumberProviderType.class);
	public static final RegistryInfo LOOT_NBT_PROVIDER = of(Registry.LOOT_NBT_PROVIDER_REGISTRY).type(LootNbtProviderType.class);
	public static final RegistryInfo LOOT_SCORE_PROVIDER = of(Registry.LOOT_SCORE_PROVIDER_REGISTRY).type(LootScoreProviderType.class);
	public static final RegistryInfo COMMAND_ARGUMENT_TYPE = of(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY).type(ArgumentTypeInfo.class);
	public static final RegistryInfo DIMENSION_TYPE = of(Registry.DIMENSION_TYPE_REGISTRY).type(DimensionType.class);
	public static final RegistryInfo DIMENSION = of(Registry.DIMENSION_REGISTRY).type(Level.class);
	public static final RegistryInfo LEVEL_STEM = of(Registry.LEVEL_STEM_REGISTRY).type(LevelStem.class);
	public static final RegistryInfo FLOAT_PROVIDER_TYPE = of(Registry.FLOAT_PROVIDER_TYPE_REGISTRY).type(FloatProviderType.class);
	public static final RegistryInfo INT_PROVIDER_TYPE = of(Registry.INT_PROVIDER_TYPE_REGISTRY).type(IntProviderType.class);
	public static final RegistryInfo HEIGHT_PROVIDER_TYPE = of(Registry.HEIGHT_PROVIDER_TYPE_REGISTRY).type(HeightProviderType.class);
	public static final RegistryInfo BLOCK_PREDICATE_TYPE = of(Registry.BLOCK_PREDICATE_TYPE_REGISTRY).type(BlockPredicateType.class);
	public static final RegistryInfo NOISE_GENERATOR_SETTINGS = of(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).type(NoiseGeneratorSettings.class);
	public static final RegistryInfo CONFIGURED_CARVER = of(Registry.CONFIGURED_CARVER_REGISTRY).type(ConfiguredWorldCarver.class);
	public static final RegistryInfo CONFIGURED_FEATURE = of(Registry.CONFIGURED_FEATURE_REGISTRY).type(ConfiguredFeature.class);
	public static final RegistryInfo PLACED_FEATURE = of(Registry.PLACED_FEATURE_REGISTRY).type(PlacedFeature.class);
	public static final RegistryInfo STRUCTURE = of(Registry.STRUCTURE_REGISTRY).type(Structure.class);
	public static final RegistryInfo STRUCTURE_SET = of(Registry.STRUCTURE_SET_REGISTRY).type(StructureSet.class);
	public static final RegistryInfo PROCESSOR_LIST = of(Registry.PROCESSOR_LIST_REGISTRY).type(StructureProcessorList.class);
	public static final RegistryInfo TEMPLATE_POOL = of(Registry.TEMPLATE_POOL_REGISTRY).type(StructureTemplatePool.class);
	public static final RegistryInfo BIOME = of(Registry.BIOME_REGISTRY).type(Biome.class);
	public static final RegistryInfo NOISE = of(Registry.NOISE_REGISTRY).type(NormalNoise.NoiseParameters.class);
	public static final RegistryInfo DENSITY_FUNCTION = of(Registry.DENSITY_FUNCTION_REGISTRY).type(DensityFunction.class);
	public static final RegistryInfo WORLD_PRESET = of(Registry.WORLD_PRESET_REGISTRY).type(WorldPreset.class);
	public static final RegistryInfo FLAT_LEVEL_GENERATOR_PRESET = of(Registry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY).type(FlatLevelGeneratorPreset.class);
	public static final RegistryInfo CARVER = of(Registry.CARVER_REGISTRY).type(WorldCarver.class);
	public static final RegistryInfo FEATURE = of(Registry.FEATURE_REGISTRY).type(Feature.class);
	public static final RegistryInfo STRUCTURE_PLACEMENT_TYPE = of(Registry.STRUCTURE_PLACEMENT_TYPE_REGISTRY).type(StructurePlacementType.class);
	public static final RegistryInfo STRUCTURE_PIECE = of(Registry.STRUCTURE_PIECE_REGISTRY).type(StructurePieceType.class);
	public static final RegistryInfo STRUCTURE_TYPE = of(Registry.STRUCTURE_TYPE_REGISTRY).type(StructureType.class);
	public static final RegistryInfo PLACEMENT_MODIFIER = of(Registry.PLACEMENT_MODIFIER_REGISTRY).type(PlacementModifierType.class);
	public static final RegistryInfo BLOCK_STATE_PROVIDER_TYPE = of(Registry.BLOCK_STATE_PROVIDER_TYPE_REGISTRY).type(BlockStateProviderType.class);
	public static final RegistryInfo FOLIAGE_PLACER_TYPE = of(Registry.FOLIAGE_PLACER_TYPE_REGISTRY).type(FoliagePlacerType.class);
	public static final RegistryInfo TRUNK_PLACER_TYPE = of(Registry.TRUNK_PLACER_TYPE_REGISTRY).type(TrunkPlacerType.class);
	public static final RegistryInfo TREE_DECORATOR_TYPE = of(Registry.TREE_DECORATOR_TYPE_REGISTRY).type(TreeDecoratorType.class);
	public static final RegistryInfo ROOT_PLACER_TYPE = of(Registry.ROOT_PLACER_TYPE_REGISTRY).type(RootPlacerType.class);
	public static final RegistryInfo FEATURE_SIZE_TYPE = of(Registry.FEATURE_SIZE_TYPE_REGISTRY).type(FeatureSizeType.class);
	public static final RegistryInfo BIOME_SOURCE = of(Registry.BIOME_SOURCE_REGISTRY).type(Codec.class).noAutoWrap();
	public static final RegistryInfo CHUNK_GENERATOR = of(Registry.CHUNK_GENERATOR_REGISTRY).type(Codec.class).noAutoWrap();
	public static final RegistryInfo CONDITION = of(Registry.CONDITION_REGISTRY).type(Codec.class).noAutoWrap();
	public static final RegistryInfo RULE = of(Registry.RULE_REGISTRY).type(Codec.class).noAutoWrap();
	public static final RegistryInfo DENSITY_FUNCTION_TYPE = of(Registry.DENSITY_FUNCTION_TYPE_REGISTRY).type(Codec.class).noAutoWrap();
	public static final RegistryInfo STRUCTURE_PROCESSOR = of(Registry.STRUCTURE_PROCESSOR_REGISTRY).type(StructureProcessorType.class);
	public static final RegistryInfo STRUCTURE_POOL_ELEMENT = of(Registry.STRUCTURE_POOL_ELEMENT_REGISTRY).type(StructurePoolElementType.class);
	public static final RegistryInfo CHAT_TYPE = of(Registry.CHAT_TYPE_REGISTRY).type(ChatType.class);
	public static final RegistryInfo CAT_VARIANT = of(Registry.CAT_VARIANT_REGISTRY).type(CatVariant.class);
	public static final RegistryInfo FROG_VARIANT = of(Registry.FROG_VARIANT_REGISTRY).type(FrogVariant.class);
	public static final RegistryInfo BANNER_PATTERN = of(Registry.BANNER_PATTERN_REGISTRY).type(BannerPattern.class);
	public static final RegistryInfo INSTRUMENT = of(Registry.INSTRUMENT_REGISTRY).type(Instrument.class);

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
	public String languageKeyPrefix;

	private RegistryInfo(ResourceKey<? extends Registry<?>> key) {
		this.key = key;
		this.objectBaseClass = Object.class;
		this.types = new LinkedHashMap<>();
		this.objects = new LinkedHashMap<>();
		this.bypassServerOnly = false;
		this.autoWrap = true;
		this.languageKeyPrefix = key.location().getPath().replace('/', '.');
	}

	public RegistryInfo type(Class<?> baseClass) {
		this.objectBaseClass = baseClass;
		return this;
	}

	public RegistryInfo bypassServerOnly() {
		this.bypassServerOnly = true;
		return this;
	}

	public RegistryInfo languageKeyPrefix(String prefix) {
		this.languageKeyPrefix = prefix;
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
