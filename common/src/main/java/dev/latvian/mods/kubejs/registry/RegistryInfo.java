package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.damagesource.DamageType;
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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
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
import java.util.Set;

public final class RegistryInfo<T> implements Iterable<BuilderBase<? extends T>>, TypeWrapperFactory<T> {
	public static final Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
	public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList<>();

	@Info("Platform-agnostic wrapper of minecraft registries, can be used to register content or get objects from the registry")
	private static final RegistrarManager REGISTRIES = RegistrarManager.get(KubeJS.MOD_ID);

	public static <T> RegistryInfo<T> of(ResourceKey<? extends Registry<?>> key, Class<T> type) {
		var r = MAP.get(key);

		if (r == null) {
			var reg = new RegistryInfo<>(UtilsJS.cast(key), type);
			MAP.put(key, reg);
			return reg;
		}

		return (RegistryInfo<T>) r;
	}

	public static RegistryInfo<?> of(ResourceKey<? extends Registry<?>> key) {
		return of(UtilsJS.cast(key), Object.class);
	}

	public static final RegistryInfo<SoundEvent> SOUND_EVENT = of(Registries.SOUND_EVENT, SoundEvent.class);
	public static final RegistryInfo<Fluid> FLUID = of(Registries.FLUID, Fluid.class);
	public static final RegistryInfo<MobEffect> MOB_EFFECT = of(Registries.MOB_EFFECT, MobEffect.class).languageKeyPrefix("effect");
	public static final RegistryInfo<Block> BLOCK = of(Registries.BLOCK, Block.class);
	public static final RegistryInfo<Enchantment> ENCHANTMENT = of(Registries.ENCHANTMENT, Enchantment.class);
	public static final RegistryInfo<EntityType> ENTITY_TYPE = of(Registries.ENTITY_TYPE, EntityType.class);
	public static final RegistryInfo<Item> ITEM = of(Registries.ITEM, Item.class).noAutoWrap();
	public static final RegistryInfo<Potion> POTION = of(Registries.POTION, Potion.class);
	public static final RegistryInfo<ParticleType> PARTICLE_TYPE = of(Registries.PARTICLE_TYPE, ParticleType.class);
	public static final RegistryInfo<BlockEntityType> BLOCK_ENTITY_TYPE = of(Registries.BLOCK_ENTITY_TYPE, BlockEntityType.class);
	public static final RegistryInfo<PaintingVariant> PAINTING_VARIANT = of(Registries.PAINTING_VARIANT, PaintingVariant.class);
	public static final RegistryInfo<ResourceLocation> CUSTOM_STAT = of(Registries.CUSTOM_STAT, ResourceLocation.class);
	public static final RegistryInfo<ChunkStatus> CHUNK_STATUS = of(Registries.CHUNK_STATUS, ChunkStatus.class);
	public static final RegistryInfo<RuleTestType> RULE_TEST = of(Registries.RULE_TEST, RuleTestType.class);
	public static final RegistryInfo<PosRuleTestType> POS_RULE_TEST = of(Registries.POS_RULE_TEST, PosRuleTestType.class);
	public static final RegistryInfo<MenuType> MENU = of(Registries.MENU, MenuType.class);
	public static final RegistryInfo<RecipeType> RECIPE_TYPE = of(Registries.RECIPE_TYPE, RecipeType.class);
	public static final RegistryInfo<RecipeSerializer> RECIPE_SERIALIZER = of(Registries.RECIPE_SERIALIZER, RecipeSerializer.class);
	public static final RegistryInfo<Attribute> ATTRIBUTE = of(Registries.ATTRIBUTE, Attribute.class);
	public static final RegistryInfo<GameEvent> GAME_EVENT = of(Registries.GAME_EVENT, GameEvent.class);
	public static final RegistryInfo<PositionSourceType> POSITION_SOURCE_TYPE = of(Registries.POSITION_SOURCE_TYPE, PositionSourceType.class);
	public static final RegistryInfo<StatType> STAT_TYPE = of(Registries.STAT_TYPE, StatType.class);
	public static final RegistryInfo<VillagerType> VILLAGER_TYPE = of(Registries.VILLAGER_TYPE, VillagerType.class);
	public static final RegistryInfo<VillagerProfession> VILLAGER_PROFESSION = of(Registries.VILLAGER_PROFESSION, VillagerProfession.class);
	public static final RegistryInfo<PoiType> POINT_OF_INTEREST_TYPE = of(Registries.POINT_OF_INTEREST_TYPE, PoiType.class);
	public static final RegistryInfo<MemoryModuleType> MEMORY_MODULE_TYPE = of(Registries.MEMORY_MODULE_TYPE, MemoryModuleType.class);
	public static final RegistryInfo<SensorType> SENSOR_TYPE = of(Registries.SENSOR_TYPE, SensorType.class);
	public static final RegistryInfo<Schedule> SCHEDULE = of(Registries.SCHEDULE, Schedule.class);
	public static final RegistryInfo<Activity> ACTIVITY = of(Registries.ACTIVITY, Activity.class);
	public static final RegistryInfo<LootPoolEntryType> LOOT_ENTRY = of(Registries.LOOT_POOL_ENTRY_TYPE, LootPoolEntryType.class);
	public static final RegistryInfo<LootItemFunctionType> LOOT_FUNCTION = of(Registries.LOOT_FUNCTION_TYPE, LootItemFunctionType.class);
	public static final RegistryInfo<LootItemConditionType> LOOT_ITEM = of(Registries.LOOT_CONDITION_TYPE, LootItemConditionType.class);
	public static final RegistryInfo<LootNumberProviderType> LOOT_NUMBER_PROVIDER = of(Registries.LOOT_NUMBER_PROVIDER_TYPE, LootNumberProviderType.class);
	public static final RegistryInfo<LootNbtProviderType> LOOT_NBT_PROVIDER = of(Registries.LOOT_NBT_PROVIDER_TYPE, LootNbtProviderType.class);
	public static final RegistryInfo<LootScoreProviderType> LOOT_SCORE_PROVIDER = of(Registries.LOOT_SCORE_PROVIDER_TYPE, LootScoreProviderType.class);
	public static final RegistryInfo<ArgumentTypeInfo> COMMAND_ARGUMENT_TYPE = of(Registries.COMMAND_ARGUMENT_TYPE, ArgumentTypeInfo.class);
	public static final RegistryInfo<DimensionType> DIMENSION_TYPE = of(Registries.DIMENSION_TYPE, DimensionType.class);
	public static final RegistryInfo<Level> DIMENSION = of(Registries.DIMENSION, Level.class);
	public static final RegistryInfo<LevelStem> LEVEL_STEM = of(Registries.LEVEL_STEM, LevelStem.class);
	public static final RegistryInfo<FloatProviderType> FLOAT_PROVIDER_TYPE = of(Registries.FLOAT_PROVIDER_TYPE, FloatProviderType.class);
	public static final RegistryInfo<IntProviderType> INT_PROVIDER_TYPE = of(Registries.INT_PROVIDER_TYPE, IntProviderType.class);
	public static final RegistryInfo<HeightProviderType> HEIGHT_PROVIDER_TYPE = of(Registries.HEIGHT_PROVIDER_TYPE, HeightProviderType.class);
	public static final RegistryInfo<BlockPredicateType> BLOCK_PREDICATE_TYPE = of(Registries.BLOCK_PREDICATE_TYPE, BlockPredicateType.class);
	public static final RegistryInfo<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS = of(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.class);
	public static final RegistryInfo<ConfiguredWorldCarver> CONFIGURED_CARVER = of(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.class);
	public static final RegistryInfo<ConfiguredFeature> CONFIGURED_FEATURE = of(Registries.CONFIGURED_FEATURE, ConfiguredFeature.class);
	public static final RegistryInfo<PlacedFeature> PLACED_FEATURE = of(Registries.PLACED_FEATURE, PlacedFeature.class);
	public static final RegistryInfo<Structure> STRUCTURE = of(Registries.STRUCTURE, Structure.class);
	public static final RegistryInfo<StructureSet> STRUCTURE_SET = of(Registries.STRUCTURE_SET, StructureSet.class);
	public static final RegistryInfo<StructureProcessorList> PROCESSOR_LIST = of(Registries.PROCESSOR_LIST, StructureProcessorList.class);
	public static final RegistryInfo<StructureTemplatePool> TEMPLATE_POOL = of(Registries.TEMPLATE_POOL, StructureTemplatePool.class);
	public static final RegistryInfo<Biome> BIOME = of(Registries.BIOME, Biome.class);
	public static final RegistryInfo<NormalNoise.NoiseParameters> NOISE = of(Registries.NOISE, NormalNoise.NoiseParameters.class);
	public static final RegistryInfo<DensityFunction> DENSITY_FUNCTION = of(Registries.DENSITY_FUNCTION, DensityFunction.class);
	public static final RegistryInfo<WorldPreset> WORLD_PRESET = of(Registries.WORLD_PRESET, WorldPreset.class);
	public static final RegistryInfo<FlatLevelGeneratorPreset> FLAT_LEVEL_GENERATOR_PRESET = of(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.class);
	public static final RegistryInfo<WorldCarver> CARVER = of(Registries.CARVER, WorldCarver.class);
	public static final RegistryInfo<Feature> FEATURE = of(Registries.FEATURE, Feature.class);
	public static final RegistryInfo<StructurePlacementType> STRUCTURE_PLACEMENT_TYPE = of(Registries.STRUCTURE_PLACEMENT, StructurePlacementType.class);
	public static final RegistryInfo<StructurePieceType> STRUCTURE_PIECE = of(Registries.STRUCTURE_PIECE, StructurePieceType.class);
	public static final RegistryInfo<StructureType> STRUCTURE_TYPE = of(Registries.STRUCTURE_TYPE, StructureType.class);
	public static final RegistryInfo<PlacementModifierType> PLACEMENT_MODIFIER = of(Registries.PLACEMENT_MODIFIER_TYPE, PlacementModifierType.class);
	public static final RegistryInfo<BlockStateProviderType> BLOCK_STATE_PROVIDER_TYPE = of(Registries.BLOCK_STATE_PROVIDER_TYPE, BlockStateProviderType.class);
	public static final RegistryInfo<FoliagePlacerType> FOLIAGE_PLACER_TYPE = of(Registries.FOLIAGE_PLACER_TYPE, FoliagePlacerType.class);
	public static final RegistryInfo<TrunkPlacerType> TRUNK_PLACER_TYPE = of(Registries.TRUNK_PLACER_TYPE, TrunkPlacerType.class);
	public static final RegistryInfo<TreeDecoratorType> TREE_DECORATOR_TYPE = of(Registries.TREE_DECORATOR_TYPE, TreeDecoratorType.class);
	public static final RegistryInfo<RootPlacerType> ROOT_PLACER_TYPE = of(Registries.ROOT_PLACER_TYPE, RootPlacerType.class);
	public static final RegistryInfo<FeatureSizeType> FEATURE_SIZE_TYPE = of(Registries.FEATURE_SIZE_TYPE, FeatureSizeType.class);
	public static final RegistryInfo<Codec> BIOME_SOURCE = of(Registries.BIOME_SOURCE, Codec.class);
	public static final RegistryInfo<Codec> CHUNK_GENERATOR = of(Registries.CHUNK_GENERATOR, Codec.class);
	public static final RegistryInfo<Codec> CONDITION = of(Registries.MATERIAL_CONDITION, Codec.class);
	public static final RegistryInfo<Codec> RULE = of(Registries.MATERIAL_RULE, Codec.class);
	public static final RegistryInfo<Codec> DENSITY_FUNCTION_TYPE = of(Registries.DENSITY_FUNCTION_TYPE, Codec.class);
	public static final RegistryInfo<StructureProcessorType> STRUCTURE_PROCESSOR = of(Registries.STRUCTURE_PROCESSOR, StructureProcessorType.class);
	public static final RegistryInfo<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = of(Registries.STRUCTURE_POOL_ELEMENT, StructurePoolElementType.class);
	public static final RegistryInfo<ChatType> CHAT_TYPE = of(Registries.CHAT_TYPE, ChatType.class);
	public static final RegistryInfo<CatVariant> CAT_VARIANT = of(Registries.CAT_VARIANT, CatVariant.class);
	public static final RegistryInfo<FrogVariant> FROG_VARIANT = of(Registries.FROG_VARIANT, FrogVariant.class);
	public static final RegistryInfo<BannerPattern> BANNER_PATTERN = of(Registries.BANNER_PATTERN, BannerPattern.class);
	public static final RegistryInfo<Instrument> INSTRUMENT = of(Registries.INSTRUMENT, Instrument.class);
	public static final RegistryInfo<TrimMaterial> TRIM_MATERIAL = of(Registries.TRIM_MATERIAL, TrimMaterial.class);
	public static final RegistryInfo<TrimPattern> TRIM_PATTERN = of(Registries.TRIM_PATTERN, TrimPattern.class);
	public static final RegistryInfo<CreativeModeTab> CREATIVE_MODE_TAB = of(Registries.CREATIVE_MODE_TAB, CreativeModeTab.class);
	public static final RegistryInfo<DamageType> DAMAGE_TYPE = of(Registries.DAMAGE_TYPE, DamageType.class);
	public static final RegistryInfo<LootItemConditionType> LOOT_CONDITION_TYPE = of(Registries.LOOT_CONDITION_TYPE, LootItemConditionType.class);
	public static final RegistryInfo<LootItemFunctionType> LOOT_FUNCTION_TYPE = of(Registries.LOOT_FUNCTION_TYPE, LootItemFunctionType.class);
	public static final RegistryInfo<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = of(Registries.LOOT_NBT_PROVIDER_TYPE, LootNbtProviderType.class);
	public static final RegistryInfo<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = of(Registries.LOOT_NUMBER_PROVIDER_TYPE, LootNumberProviderType.class);
	public static final RegistryInfo<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = of(Registries.LOOT_POOL_ENTRY_TYPE, LootPoolEntryType.class);
	public static final RegistryInfo<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = of(Registries.LOOT_SCORE_PROVIDER_TYPE, LootScoreProviderType.class);
	public static final RegistryInfo<Codec> MATERIAL_CONDITION = of(Registries.MATERIAL_CONDITION, Codec.class);
	public static final RegistryInfo<Codec> MATERIAL_RULE = of(Registries.MATERIAL_RULE, Codec.class);
	public static final RegistryInfo<PlacementModifierType> PLACEMENT_MODIFIER_TYPE = of(Registries.PLACEMENT_MODIFIER_TYPE, PlacementModifierType.class);
	public static final RegistryInfo<RuleBlockEntityModifierType> RULE_BLOCK_ENTITY_MODIFIER = of(Registries.RULE_BLOCK_ENTITY_MODIFIER, RuleBlockEntityModifierType.class);
	public static final RegistryInfo<StructurePlacementType> STRUCTURE_PLACEMENT = of(Registries.STRUCTURE_PLACEMENT, StructurePlacementType.class);
	public static final RegistryInfo<String> DECORATED_POT_PATTERNS = of(Registries.DECORATED_POT_PATTERNS, String.class);
	public static final RegistryInfo<NoiseGeneratorSettings> NOISE_SETTINGS = of(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.class);
	public static final RegistryInfo<MultiNoiseBiomeSourceParameterList> MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = of(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.class);

	/**
	 * Add your registry to these to make sure it comes after vanilla registries, if it depends on them.
	 * Only works on Fabric, since Forge already has ordered registries.
	 */
	public static final LinkedList<RegistryInfo<?>> AFTER_VANILLA = new LinkedList<>();

	public final ResourceKey<? extends Registry<T>> key;
	public final Class<?> objectBaseClass;
	public final Map<String, BuilderType<T>> types;
	public final Map<ResourceLocation, BuilderBase<? extends T>> objects;
	public boolean hasDefaultTags = false;
	private BuilderType<T> defaultType;
	public boolean bypassServerOnly;
	public boolean autoWrap;
	private Registrar<T> architecturyRegistrar;
	public String languageKeyPrefix;

	private RegistryInfo(ResourceKey<? extends Registry<T>> key, Class<T> objectBaseClass) {
		this.key = key;
		this.objectBaseClass = objectBaseClass;
		this.types = new LinkedHashMap<>();
		this.objects = new LinkedHashMap<>();
		this.bypassServerOnly = false;
		this.autoWrap = objectBaseClass != Codec.class && objectBaseClass != ResourceLocation.class && objectBaseClass != String.class;
		this.languageKeyPrefix = key.location().getPath().replace('/', '.');
	}

	public RegistryInfo<T> bypassServerOnly() {
		this.bypassServerOnly = true;
		return this;
	}

	public RegistryInfo<T> noAutoWrap() {
		this.autoWrap = false;
		return this;
	}

	public RegistryInfo<T> languageKeyPrefix(String prefix) {
		this.languageKeyPrefix = prefix;
		return this;
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory, boolean isDefault) {
		var b = new BuilderType<>(type, builderType, factory);
		types.put(type, b);

		if (isDefault) {
			if (defaultType != null) {
				ConsoleJS.STARTUP.warn("Previous default type '" + defaultType.type() + "' for registry '" + key.location() + "' replaced with '" + type + "'!");
			}

			defaultType = b;
		}
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
		addType(type, builderType, factory, type.equals("basic"));
	}

	public void addBuilder(BuilderBase<? extends T> builder) {
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
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof RegistryInfo ri && key.equals(ri.key);
	}

	@Override
	public String toString() {
		return key.location().toString();
	}

	public int registerObjects(RegistryCallback<T> function) {
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
	public Iterator<BuilderBase<? extends T>> iterator() {
		return objects.values().iterator();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public Registrar<T> getArchitecturyRegistrar() {
		if (architecturyRegistrar == null) {
			architecturyRegistrar = REGISTRIES.get((ResourceKey) key);
		}

		return architecturyRegistrar;
	}

	public Registry<T> getVanillaRegistry() {
		return BuiltInRegistries.REGISTRY.get((ResourceKey) key);
	}

	public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
		return getArchitecturyRegistrar().entrySet();
	}

	public ResourceLocation getId(T value) {
		return getArchitecturyRegistrar().getId(value);
	}

	public T getValue(ResourceLocation id) {
		return getArchitecturyRegistrar().get(id);
	}

	public boolean hasValue(ResourceLocation id) {
		return getArchitecturyRegistrar().contains(id);
	}

	@Override
	public T wrap(Context cx, Object o) {
		if (o == null) {
			return null;
		} else if (objectBaseClass.isInstance(o)) {
			return (T) o;
		}

		var id = UtilsJS.getMCID(cx, o);
		var value = getValue(id);

		if (value == null) {
			var npe = new NullPointerException("No such element with id %s in registry %s!".formatted(id, this));
			ConsoleJS.getCurrent(cx).error("Error while wrapping registry element type!", npe);
			throw npe;
		}

		return value;
	}

	public void fireRegistryEvent() {
		var event = new RegistryEventJS<>(this);
		StartupEvents.REGISTRY.post(event, key);
		event.created.forEach(BuilderBase::createAdditionalObjects);
	}
}
