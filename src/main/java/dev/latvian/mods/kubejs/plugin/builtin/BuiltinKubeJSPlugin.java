package dev.latvian.mods.kubejs.plugin.builtin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockTintFunction;
import dev.latvian.mods.kubejs.block.DetectorBlock;
import dev.latvian.mods.kubejs.block.MapColorHelper;
import dev.latvian.mods.kubejs.block.SoundTypeWrapper;
import dev.latvian.mods.kubejs.block.custom.BasicKubeBlock;
import dev.latvian.mods.kubejs.block.custom.ButtonBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.CardinalBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.CarpetBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.CropBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.DoorBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FallingBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceGateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.PressurePlateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.SlabBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StairBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.TrapdoorBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WallBlockBuilder;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentRegistry;
import dev.latvian.mods.kubejs.block.entity.CustomCapabilityAttachment;
import dev.latvian.mods.kubejs.block.entity.EnergyStorageAttachment;
import dev.latvian.mods.kubejs.block.entity.FluidTankAttachment;
import dev.latvian.mods.kubejs.block.entity.InventoryAttachment;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.client.icon.AtlasSpriteKubeIcon;
import dev.latvian.mods.kubejs.client.icon.ItemKubeIcon;
import dev.latvian.mods.kubejs.client.icon.KubeIcon;
import dev.latvian.mods.kubejs.client.icon.KubeIconTypeRegistry;
import dev.latvian.mods.kubejs.client.icon.TextureKubeIcon;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.entity.AttributeBuilder;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventGroupWrapper;
import dev.latvian.mods.kubejs.event.EventGroups;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.fluid.FluidTypeBuilder;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.fluid.ThickFluidBuilder;
import dev.latvian.mods.kubejs.fluid.ThinFluidBuilder;
import dev.latvian.mods.kubejs.item.ArmorMaterialBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemEnchantmentsWrapper;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ItemTintFunction;
import dev.latvian.mods.kubejs.item.ItemToolTiers;
import dev.latvian.mods.kubejs.item.JukeboxSongBuilder;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabBuilder;
import dev.latvian.mods.kubejs.item.custom.ArmorItemBuilder;
import dev.latvian.mods.kubejs.item.custom.DiggerItemBuilder;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SmithingTemplateItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SwordItemBuilder;
import dev.latvian.mods.kubejs.misc.CustomStatBuilder;
import dev.latvian.mods.kubejs.misc.MobEffectBuilder;
import dev.latvian.mods.kubejs.misc.PaintingVariantBuilder;
import dev.latvian.mods.kubejs.misc.ParticleTypeBuilder;
import dev.latvian.mods.kubejs.misc.PoiTypeBuilder;
import dev.latvian.mods.kubejs.misc.PotionBuilder;
import dev.latvian.mods.kubejs.misc.SoundEventBuilder;
import dev.latvian.mods.kubejs.misc.VillagerProfessionBuilder;
import dev.latvian.mods.kubejs.misc.VillagerTypeBuilder;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.EntityEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.LevelEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.NetworkEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.PlayerEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.StartupEvents;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.AABBWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ColorWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DamageSourceWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.EntitySelectorWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.JavaWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.KMath;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.MiscWrappers;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NBTWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NativeEventWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ParticleOptionsWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.RegistryWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextIcons;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UUIDWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UtilsWrapper;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.component.BlockStateComponent;
import dev.latvian.mods.kubejs.recipe.component.BookCategoryComponent;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.CharacterComponent;
import dev.latvian.mods.kubejs.recipe.component.CustomObjectRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.EitherRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NestedRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.PairRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.component.RegistryComponent;
import dev.latvian.mods.kubejs.recipe.component.ResourceKeyComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedFluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.component.StringGridComponent;
import dev.latvian.mods.kubejs.recipe.component.TagKeyComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.component.validator.AlwaysValidValidator;
import dev.latvian.mods.kubejs.recipe.component.validator.AndValidator;
import dev.latvian.mods.kubejs.recipe.component.validator.NonEmptyValidator;
import dev.latvian.mods.kubejs.recipe.component.validator.OrValidator;
import dev.latvian.mods.kubejs.recipe.component.validator.RecipeComponentValidatorTypeRegistry;
import dev.latvian.mods.kubejs.recipe.component.validator.ValidatedRecipeComponent;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ConsumeAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionTypeRegistry;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.UnknownKubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapedKubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapelessKubeRecipe;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.DataComponentTypeInfoRegistry;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.RecordDefaultsRegistry;
import dev.latvian.mods.kubejs.script.TypeDescriptionRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.server.ScheduledServerEvent;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.FluidAmounts;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.kubejs.util.NBTIOWrapper;
import dev.latvian.mods.kubejs.util.NameProvider;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RotationAxis;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.kubejs.util.SlotFilter;
import dev.latvian.mods.kubejs.util.MobEffectUtil;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.kubejs.util.Tristate;
import dev.latvian.mods.kubejs.util.registrypredicate.RegistryPredicate;
import dev.latvian.mods.kubejs.web.LocalWebServerRegistry;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.Util;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.LockCode;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinKubeJSPlugin implements KubeJSPlugin {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	@Override
	public void registerBuilderTypes(BuilderTypeRegistry registry) {
		registry.addDefault(Registries.SOUND_EVENT, SoundEventBuilder.class, SoundEventBuilder::new);

		registry.addDefault(Registries.BLOCK, BasicKubeBlock.Builder.class, BasicKubeBlock.Builder::new);

		registry.of(Registries.BLOCK, reg -> {
			reg.add(KubeJS.id("detector"), DetectorBlock.Builder.class, DetectorBlock.Builder::new);
			reg.add(KubeJS.id("slab"), SlabBlockBuilder.class, SlabBlockBuilder::new);
			reg.add(KubeJS.id("stairs"), StairBlockBuilder.class, StairBlockBuilder::new);
			reg.add(KubeJS.id("fence"), FenceBlockBuilder.class, FenceBlockBuilder::new);
			reg.add(KubeJS.id("wall"), WallBlockBuilder.class, WallBlockBuilder::new);
			reg.add(KubeJS.id("fence_gate"), FenceGateBlockBuilder.class, FenceGateBlockBuilder::new);
			reg.add(KubeJS.id("pressure_plate"), PressurePlateBlockBuilder.class, PressurePlateBlockBuilder::new);
			reg.add(KubeJS.id("button"), ButtonBlockBuilder.class, ButtonBlockBuilder::new);
			reg.add(KubeJS.id("falling"), FallingBlockBuilder.class, FallingBlockBuilder::new);
			reg.add(KubeJS.id("crop"), CropBlockBuilder.class, CropBlockBuilder::new);
			reg.add(KubeJS.id("cardinal"), CardinalBlockBuilder.class, CardinalBlockBuilder::new);
			reg.add(KubeJS.id("carpet"), CarpetBlockBuilder.class, CarpetBlockBuilder::new);
			reg.add(KubeJS.id("door"), DoorBlockBuilder.class, DoorBlockBuilder::new);
			reg.add(KubeJS.id("trapdoor"), TrapdoorBlockBuilder.class, TrapdoorBlockBuilder::new);
		});

		registry.addDefault(Registries.ITEM, ItemBuilder.class, ItemBuilder::new);

		registry.of(Registries.ITEM, reg -> {
			reg.add(KubeJS.id("sword"), SwordItemBuilder.class, SwordItemBuilder::new);
			reg.add(KubeJS.id("pickaxe"), DiggerItemBuilder.Pickaxe.class, DiggerItemBuilder.Pickaxe::new);
			reg.add(KubeJS.id("axe"), DiggerItemBuilder.Axe.class, DiggerItemBuilder.Axe::new);
			reg.add(KubeJS.id("shovel"), DiggerItemBuilder.Shovel.class, DiggerItemBuilder.Shovel::new);
			reg.add(KubeJS.id("hoe"), DiggerItemBuilder.Hoe.class, DiggerItemBuilder.Hoe::new);
			reg.add(KubeJS.id("shears"), ShearsItemBuilder.class, ShearsItemBuilder::new);
			reg.add(KubeJS.id("helmet"), ArmorItemBuilder.Helmet.class, ArmorItemBuilder.Helmet::new);
			reg.add(KubeJS.id("chestplate"), ArmorItemBuilder.Chestplate.class, ArmorItemBuilder.Chestplate::new);
			reg.add(KubeJS.id("leggings"), ArmorItemBuilder.Leggings.class, ArmorItemBuilder.Leggings::new);
			reg.add(KubeJS.id("boots"), ArmorItemBuilder.Boots.class, ArmorItemBuilder.Boots::new);
			reg.add(KubeJS.id("animal_armor"), ArmorItemBuilder.AnimalArmor.class, ArmorItemBuilder.AnimalArmor::new);
			reg.add(KubeJS.id("smithing_template"), SmithingTemplateItemBuilder.class, SmithingTemplateItemBuilder::new);
		});

		registry.addDefault(Registries.FLUID, FluidBuilder.class, FluidBuilder::new);

		registry.of(Registries.FLUID, reg -> {
			reg.add(KubeJS.id("thin"), ThinFluidBuilder.class, ThinFluidBuilder::new);
			reg.add(KubeJS.id("thick"), ThickFluidBuilder.class, ThickFluidBuilder::new);
		});

		registry.addDefault(NeoForgeRegistries.Keys.FLUID_TYPES, FluidTypeBuilder.class, FluidTypeBuilder::new);
		registry.addDefault(Registries.MOB_EFFECT, MobEffectBuilder.class, MobEffectBuilder::new);
		registry.addDefault(Registries.POTION, PotionBuilder.class, PotionBuilder::new);
		registry.addDefault(Registries.PARTICLE_TYPE, ParticleTypeBuilder.class, ParticleTypeBuilder::new);
		registry.addDefault(Registries.CUSTOM_STAT, CustomStatBuilder.class, CustomStatBuilder::new);
		registry.addDefault(Registries.POINT_OF_INTEREST_TYPE, PoiTypeBuilder.class, PoiTypeBuilder::new);
		registry.addDefault(Registries.VILLAGER_TYPE, VillagerTypeBuilder.class, VillagerTypeBuilder::new);
		registry.addDefault(Registries.VILLAGER_PROFESSION, VillagerProfessionBuilder.class, VillagerProfessionBuilder::new);
		registry.addDefault(Registries.CREATIVE_MODE_TAB, CreativeTabBuilder.class, CreativeTabBuilder::new);
		registry.addDefault(Registries.ARMOR_MATERIAL, ArmorMaterialBuilder.class, ArmorMaterialBuilder::new);

		// FIXME registry.addDefault(Registries.ENCHANTMENT, EnchantmentBuilder.class, EnchantmentBuilder::new);
		registry.addDefault(Registries.PAINTING_VARIANT, PaintingVariantBuilder.class, PaintingVariantBuilder::new);
		registry.addDefault(Registries.JUKEBOX_SONG, JukeboxSongBuilder.class, JukeboxSongBuilder::new);
		registry.addDefault(Registries.ATTRIBUTE, AttributeBuilder.class, AttributeBuilder::new);
	}

	@Override
	public void registerServerRegistries(ServerRegistryRegistry registry) {
		// VanillaRegistries
		registry.register(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC, DimensionType.class);
		registry.register(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC, TypeInfo.of(ConfiguredWorldCarver.class));
		registry.register(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC, TypeInfo.of(ConfiguredFeature.class));
		registry.register(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC, PlacedFeature.class);
		registry.register(Registries.STRUCTURE, Structure.DIRECT_CODEC, Structure.class);
		registry.register(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC, StructureSet.class);
		registry.register(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC, StructureProcessorList.class);
		registry.register(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC, StructureTemplatePool.class);
		registry.register(Registries.BIOME, Biome.DIRECT_CODEC, Biome.class);
		registry.register(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC, MultiNoiseBiomeSourceParameterList.class);
		registry.register(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC, NormalNoise.NoiseParameters.class);
		registry.register(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC, DensityFunction.class);
		registry.register(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC, NoiseGeneratorSettings.class);
		registry.register(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC, WorldPreset.class);
		registry.register(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC, FlatLevelGeneratorPreset.class);
		registry.register(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC, ChatType.class);
		registry.register(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC, TrimPattern.class);
		registry.register(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC, TrimMaterial.class);
		registry.register(Registries.WOLF_VARIANT, WolfVariant.DIRECT_CODEC, WolfVariant.class);
		registry.register(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, PaintingVariant.class);
		registry.register(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC, DamageType.class);
		registry.register(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC, BannerPattern.class);
		registry.register(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC, Enchantment.class);
		registry.register(Registries.ENCHANTMENT_PROVIDER, EnchantmentProvider.DIRECT_CODEC, EnchantmentProvider.class);
		registry.register(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC, JukeboxSong.class);
	}

	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(StartupEvents.GROUP);
		registry.register(ServerEvents.GROUP);
		registry.register(LevelEvents.GROUP);
		registry.register(NetworkEvents.GROUP);
		registry.register(ItemEvents.GROUP);
		registry.register(BlockEvents.GROUP);
		registry.register(EntityEvents.GROUP);
		registry.register(PlayerEvents.GROUP);
		registry.register(RecipeViewerEvents.GROUP);
	}

	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("global", bindings.type().isStartup() ? GLOBAL : Collections.unmodifiableMap(GLOBAL));
		bindings.add("Platform", PlatformWrapper.class);
		bindings.add("console", bindings.type().console);

		for (var group : EventGroups.ALL.get().map().values()) {
			bindings.add(group.name, new EventGroupWrapper(bindings.type(), group));
		}

		bindings.add("JavaMath", Math.class);
		bindings.add("ID", ID.class);

		bindings.add("Duration", Duration.class);

		// event.add("onEvent", new LegacyCodeHandler("onEvent()"));

		if (bindings.type().isServer() && bindings.context().kjsFactory.manager instanceof ServerScriptManager) {
			var se = ScheduledServerEvent.EVENTS;

			bindings.add("setTimeout", new ScheduledEvents.TimeoutJSFunction(se, false, false));
			bindings.add("clearTimeout", new ScheduledEvents.TimeoutJSFunction(se, true, false));
			bindings.add("setInterval", new ScheduledEvents.TimeoutJSFunction(se, false, true));
			bindings.add("clearInterval", new ScheduledEvents.TimeoutJSFunction(se, true, true));
		}

		bindings.add("KMath", KMath.class);
		bindings.add("Utils", UtilsWrapper.class);
		bindings.add("StringUtils", StringUtilsWrapper.class);
		bindings.add("Java", JavaWrapper.class);
		bindings.add("Text", TextWrapper.class);
		bindings.add("Component", TextWrapper.class);
		bindings.add("TextIcons", TextIcons.class);
		bindings.add("UUID", UUIDWrapper.class);
		bindings.add("JsonUtils", JsonUtils.class);
		bindings.add("JsonIO", JsonIO.class);
		bindings.add("Block", BlockWrapper.class);
		bindings.add("Blocks", Blocks.class);
		bindings.add("Item", ItemWrapper.class);
		bindings.add("Items", Items.class);
		bindings.add("Ingredient", IngredientWrapper.class);
		bindings.add("NBT", NBTWrapper.class);
		bindings.add("NBTIO", NBTIOWrapper.class);
		bindings.add("Direction", DirectionWrapper.class);
		bindings.add("Facing", DirectionWrapper.class);
		bindings.add("AABB", AABBWrapper.class);
		bindings.add("Stats", Stats.class);
		bindings.add("FluidAmounts", FluidAmounts.class);
		bindings.add("Notification", NotificationToastData.class);
		bindings.add("SizedIngredient", SizedIngredientWrapper.class);
		bindings.add("ParticleOptions", ParticleOptionsWrapper.class);
		bindings.add("Registry", RegistryWrapper.class);
		bindings.add("EntitySelector", EntitySelectorWrapper.class);

		bindings.add("Fluid", FluidWrapper.class);

		bindings.add("SECOND", 1000L);
		bindings.add("MINUTE", 60000L);
		bindings.add("HOUR", 3600000L);

		bindings.add("Color", ColorWrapper.class);
		bindings.add("BlockStatePredicate", BlockStatePredicate.class);

		bindings.add("Vec3d", Vec3.class);
		bindings.add("Vec3i", Vec3i.class);
		bindings.add("Vec3f", Vector3f.class);
		bindings.add("Vec4f", Vector4f.class);
		bindings.add("Matrix3f", Matrix3f.class);
		bindings.add("Matrix4f", Matrix4f.class);
		bindings.add("Matrix4f", Matrix4f.class);
		bindings.add("Quaternionf", Quaternionf.class);
		bindings.add("RotationAxis", RotationAxis.class);
		bindings.add("BlockPos", BlockPos.class);
		bindings.add("DamageSource", DamageSource.class);
		bindings.add("SoundType", SoundType.class);

		bindings.add("BlockProperties", BlockStateProperties.class);

		bindings.add("NativeEvents", NativeEventWrapper.class);

		bindings.add("MobEffectInstance", MobEffectUtil.class)
	}

	@Override
	public void registerTypeWrappers(TypeWrapperRegistry registry) {
		registry.register(RegistryPredicate.class, RegistryPredicate::of);

		// Java / Minecraft //
		// registry.register(String.class, String::valueOf);
		// registry.register(CharSequence.class, String::valueOf);
		registry.register(UUID.class, UUIDWrapper::fromString);
		registry.register(Pattern.class, RegExpKJS::wrap);
		registry.register(JsonObject.class, JsonUtils::objectOf);
		registry.register(JsonArray.class, JsonUtils::arrayOf);
		registry.register(JsonElement.class, JsonUtils::of);
		registry.register(JsonPrimitive.class, JsonUtils::primitiveOf);
		registry.register(Path.class, MiscWrappers::wrapPath);
		registry.register(File.class, MiscWrappers::wrapFile);
		registry.register(TemporalAmount.class, TimeJS::wrapTemporalAmount);
		registry.register(Duration.class, TimeJS::wrapDuration);
		registry.register(TickDuration.class, TickDuration::wrap);

		registry.register(ResourceLocation.class, ID::mc);
		registry.register(KubeResourceLocation.class, KubeResourceLocation::wrap);
		registry.register(CompoundTag.class, (from, target) -> NBTWrapper.isTagCompound(from), NBTWrapper::wrapCompound);
		registry.register(CollectionTag.class, (from, target) -> NBTWrapper.isTagCollection(from), NBTWrapper::wrapCollection);
		registry.register(ListTag.class, (from, target) -> NBTWrapper.isTagCollection(from), NBTWrapper::wrapListTag);
		registry.register(Tag.class, NBTWrapper::wrap);
		registry.register(DataComponentType.class, DataComponentWrapper::wrapType);
		registry.register(DataComponentMap.class, DataComponentWrapper::filter, (cx, from, target) -> DataComponentWrapper.mapOf(RegistryAccessContainer.of(cx).nbt(), from));
		registry.register(DataComponentPatch.class, DataComponentWrapper::filter, (cx, from, target) -> DataComponentWrapper.patchOf(RegistryAccessContainer.of(cx).nbt(), from));

		registry.register(BlockPos.class, MiscWrappers::wrapBlockPos);
		registry.register(Vec3.class, MiscWrappers::wrapVec3);
		registry.register(Vec3i.class, MiscWrappers::wrapBlockPos);

		registry.register(Item.class, ItemWrapper::wrapItem);
		registry.register(ItemLike.class, ItemWrapper::wrapItem);
		registry.registerEnumFromStringCodec(MobCategory.class, MobCategory.CODEC);
		registry.register(ItemEnchantments.class, ItemEnchantmentsWrapper::wrap);

		registry.register(AABB.class, AABBWrapper::wrap);
		registry.register(IntProvider.class, MiscWrappers::wrapIntProvider);
		registry.register(FloatProvider.class, MiscWrappers::wrapFloatProvider);
		registry.register(NumberProvider.class, MiscWrappers::wrapNumberProvider);
		registry.registerEnumFromStringCodec(LootContext.EntityTarget.class, LootContext.EntityTarget.CODEC);
		registry.registerEnumFromStringCodec(CopyNameFunction.NameSource.class, CopyNameFunction.NameSource.CODEC);
		// FIXME registry.register(Enchantment.Cost.class, EnchantmentBuilder::wrapCost);
		registry.registerEnumFromStringCodec(ArmorItem.Type.class, ArmorItem.Type.CODEC);
		registry.register(BlockSetType.class, BlockWrapper::wrapSetType);
		registry.register(BlockState.class, BlockWrapper::wrapBlockState);
		registry.register(ItemAbility.class, ItemWrapper::wrapItemAbility);
		registry.register(ColorRGBA.class, ColorWrapper::wrapColorRGBA);

		// KubeJS //
		registry.register(ItemStack.class, ItemWrapper::wrap);
		registry.register(Ingredient.class, IngredientWrapper::wrap);
		registry.register(ItemPredicate.class, ItemPredicate::wrap);
		registry.register(SizedIngredient.class, SizedIngredientWrapper::wrap);
		registry.register(BlockStatePredicate.class, BlockStatePredicate::wrap);
		registry.register(RuleTest.class, BlockStatePredicate::wrapRuleTest);
		registry.register(FluidStack.class, FluidWrapper::wrap);
		registry.register(FluidIngredient.class, FluidWrapper::wrapIngredient);
		registry.register(SizedFluidIngredient.class, FluidWrapper::wrapSizedIngredient);
		registry.register(RecipeFilter.class, RecipeFilter::wrap);
		registry.register(SlotFilter.class, SlotFilter::wrap);
		registry.register(Tier.class, ItemToolTiers::wrap);
		registry.register(PlayerSelector.class, PlayerSelector::wrap);
		registry.register(DamageSource.class, DamageSourceWrapper::wrap);
		registry.register(EntitySelector.class, EntitySelectorWrapper::wrap);
		registry.register(ReplacementMatch.class, ReplacementMatch::wrap);
		registry.register(ReplacementMatchInfo.class, ReplacementMatchInfo::wrap);
		registry.register(Stat.class, PlayerStatsJS::wrapStat);
		registry.register(MapColor.class, MapColorHelper::wrap);
		registry.register(SoundType.class, SoundTypeWrapper.INSTANCE);
		registry.register(ParticleOptions.class, ParticleOptionsWrapper::wrap);
		registry.register(ItemTintFunction.class, ItemTintFunction::wrap);
		registry.register(BlockTintFunction.class, BlockTintFunction::wrap);
		registry.register(Tristate.class, Tristate::wrap);

		// components //
		registry.register(Component.class, TextWrapper::wrap);
		registry.register(MutableComponent.class, TextWrapper::wrap);
		registry.register(KubeColor.class, ColorWrapper::wrap);
		registry.register(TextColor.class, ColorWrapper::wrapTextColor);
		registry.register(ClickEvent.class, TextWrapper::wrapClickEvent);

		// codecs
		registry.registerCodec(Fireworks.class, Fireworks.CODEC);
		registry.registerCodec(KubeIcon.class, KubeIcon.CODEC);

		// alias
		registry.registerAlias(Unit.class, TypeInfo.NONE, o -> Unit.INSTANCE);
		registry.registerAlias(CustomData.class, CompoundTag.class, CustomData::of);
		registry.registerAlias(CustomModelData.class, TypeInfo.PRIMITIVE_INT, CustomModelData::new);
		registry.registerAlias(LockCode.class, TypeInfo.STRING, LockCode::new);
		registry.registerAlias(BlockItemStateProperties.class, TypeInfo.RAW_MAP.withParams(TypeInfo.STRING, TypeInfo.STRING), BlockItemStateProperties::new);
	}

	@Override
	public void registerRecordDefaults(RecordDefaultsRegistry registry) {
		registry.register(BlockSetType.OAK);
		registry.register(new NotificationToastData(NotificationToastData.DEFAULT_DURATION, Component.empty(), Optional.empty(), 16, Optional.empty(), Optional.empty(), Optional.empty(), false));
	}

	@Override
	public void registerTypeDescriptions(TypeDescriptionRegistry registry) {
		registry.register(SlotFilter.class, ((RecordTypeInfo) TypeInfo.of(SlotFilter.class)).createCombinedType(TypeInfo.INT, IngredientWrapper.TYPE_INFO));
	}

	@Override
	public void registerRecipeFactories(RecipeFactoryRegistry registry) {
		registry.register(UnknownKubeRecipe.RECIPE_FACTORY);
		registry.register(ShapedKubeRecipe.RECIPE_FACTORY);
		registry.register(ShapelessKubeRecipe.RECIPE_FACTORY);
	}

	@Override
	public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
		registry.register(BooleanComponent.BOOLEAN);
		registry.register(StringComponent.ANY);
		registry.register(StringComponent.NON_EMPTY);
		registry.register(StringComponent.NON_BLANK);
		registry.register(StringComponent.ID);
		registry.register(CharacterComponent.CHARACTER);
		registry.register(StringGridComponent.STRING_GRID);

		registry.register(NumberComponent.INT_TYPE);
		registry.register(NumberComponent.LONG_TYPE);
		registry.register(NumberComponent.FLOAT_TYPE);
		registry.register(NumberComponent.DOUBLE_TYPE);

		registry.register(IngredientComponent.INGREDIENT);
		registry.register(IngredientComponent.NON_EMPTY_INGREDIENT);
		registry.register(IngredientComponent.UNWRAPPED_INGREDIENT_LIST);

		registry.register(SizedIngredientComponent.FLAT);
		registry.register(SizedIngredientComponent.NESTED);

		registry.register(ItemStackComponent.ITEM_STACK);
		registry.register(ItemStackComponent.STRICT_ITEM_STACK);

		registry.register(FluidStackComponent.FLUID_STACK);
		registry.register(FluidStackComponent.STRICT_FLUID_STACK);
		registry.register(FluidIngredientComponent.FLUID_INGREDIENT);

		registry.register(SizedFluidIngredientComponent.FLAT);
		registry.register(SizedFluidIngredientComponent.NESTED);

		registry.register(BlockComponent.BLOCK);

		registry.register(BlockStateComponent.BLOCK);
		registry.register(BlockStateComponent.BLOCK_STRING);

		registry.register(TimeComponent.TICKS);
		registry.register(TimeComponent.SECONDS);
		registry.register(TimeComponent.MINUTES);
		registry.register(TimeComponent.HOURS);

		registry.register(TagKeyComponent.BLOCK);
		registry.register(TagKeyComponent.ITEM);
		registry.register(TagKeyComponent.FLUID);
		registry.register(TagKeyComponent.ENTITY_TYPE);
		registry.register(TagKeyComponent.BIOME);
		registry.register(NestedRecipeComponent.RECIPE);

		registry.register(BookCategoryComponent.CRAFTING_BOOK_CATEGORY);
		registry.register(BookCategoryComponent.COOKING_BOOK_CATEGORY);

		registry.register(ResourceKeyComponent.DIMENSION);
		registry.register(ResourceKeyComponent.LOOT_TABLE);

		registry.register(ValidatedRecipeComponent.TYPE);
		registry.register(TagKeyComponent.TYPE);
		registry.register(RegistryComponent.TYPE);
		registry.register(EnumComponent.TYPE);
		registry.register(MapRecipeComponent.TYPE);
		registry.register(MapRecipeComponent.PATTERN_TYPE);
		registry.register(EitherRecipeComponent.TYPE);
		registry.register(ResourceKeyComponent.TYPE);
		registry.register(PairRecipeComponent.TYPE);
		registry.register(CustomObjectRecipeComponent.TYPE);
	}

	@Override
	public void registerBlockEntityAttachments(BlockEntityAttachmentRegistry registry) {
		registry.register(CustomCapabilityAttachment.TYPE);
		registry.register(InventoryAttachment.TYPE);
		registry.register(FluidTankAttachment.TYPE);
		registry.register(EnergyStorageAttachment.TYPE);
	}

	@Override
	public void registerIngredientActionTypes(IngredientActionTypeRegistry registry) {
		registry.register(ConsumeAction.TYPE);
		registry.register(CustomIngredientAction.TYPE);
		registry.register(DamageAction.TYPE);
		registry.register(KeepAction.TYPE);
		registry.register(ReplaceAction.TYPE);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void clearCaches() {
		ItemWrapper.CACHED_ITEM_MAP.forget();
		ItemWrapper.CACHED_ITEM_LIST.forget();
		ItemWrapper.CACHED_ITEM_TYPE_LIST.forget();
	}

	@Override
	public void registerDataComponentTypeDescriptions(DataComponentTypeInfoRegistry registry) {
		// DataComponents.ATTRIBUTE_MODIFIERS
	}

	@Override
	public void registerLocalWebServer(LocalWebServerRegistry registry) {
		KubeJSWeb.register(registry);
	}

	@Override
	public void registerLocalWebServerWithAuth(LocalWebServerRegistry registry) {
		KubeJSWeb.registerWithAuth(registry);
	}

	@Override
	public void registerItemNameProviders(NameProvider.Registry<Item, ItemStack> registry) {
		registry.register(Items.ENCHANTED_BOOK, (registries, stack) -> {
			var enchants = EnchantmentHelper.getEnchantmentsForCrafting(stack);

			if (enchants.isEmpty()) {
				return null;
			}

			var c = Component.empty();
			c.append(stack.getHoverName());
			boolean first = true;

			for (var e : enchants.entrySet()) {
				if (first) {
					first = false;
					c.append(": ");
				} else {
					c.append(", ");
				}

				c.append(Enchantment.getFullname(e.getKey(), e.getIntValue()));
			}

			return c;
		});

		/*
		registry.register(List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION), (registries, stack) -> {
			var potions = stack.get(DataComponents.POTION_CONTENTS);

			if (potions == null || potions.potion().isEmpty()) {
				return null;
			}

			var c = Component.empty();
			c.append(stack.getHoverName());
			// c.append(Potion.getName(potions.potion().get(), potions.));
			return c;
		});
		 */

		for (var item : BuiltInRegistries.ITEM) {
			var song = item.components().get(DataComponents.JUKEBOX_PLAYABLE);

			if (song != null) {
				registry.register(item, (registries, stack) -> {
					var key = Util.makeDescriptionId("jukebox_song", song.song().key().location());
					return Component.empty().append(stack.getHoverName()).append(": ").append(Component.translatable(key));
				});
			}
		}

		registry.register(Items.PAINTING, (registries, stack) -> {
			var customData = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);

			if (!customData.isEmpty()) {
				var key = customData.read(registries.createSerializationContext(NbtOps.INSTANCE), Painting.VARIANT_MAP_CODEC)
					.result()
					.flatMap(Holder::unwrapKey)
					.map(ResourceKey::location)
					.orElse(null);

				if (key != null) {
					return Component.empty().append(stack.getHoverName()).append(": ").append(Component.translatable(key.toLanguageKey("painting", "author"))).append(" - ").append(Component.translatable(key.toLanguageKey("painting", "title")));
				}
			}

			return null;
		});
	}

	@Override
	public void registerRecipeComponentValidatorTypes(RecipeComponentValidatorTypeRegistry registry) {
		registry.register(NonEmptyValidator.TYPE);
		registry.register(AlwaysValidValidator.TYPE);
		registry.register(AndValidator.TYPE);
		registry.register(OrValidator.TYPE);
	}

	@Override
	public void registerIconTypes(KubeIconTypeRegistry registry) {
		registry.register(TextureKubeIcon.TYPE);
		registry.register(AtlasSpriteKubeIcon.TYPE);
		registry.register(ItemKubeIcon.TYPE);
	}
}
