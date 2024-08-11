package dev.latvian.mods.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.bindings.AABBWrapper;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.bindings.ColorWrapper;
import dev.latvian.mods.kubejs.bindings.DamageSourceWrapper;
import dev.latvian.mods.kubejs.bindings.DirectionWrapper;
import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.bindings.JavaWrapper;
import dev.latvian.mods.kubejs.bindings.KMath;
import dev.latvian.mods.kubejs.bindings.ParticleOptionsWrapper;
import dev.latvian.mods.kubejs.bindings.RegistryWrapper;
import dev.latvian.mods.kubejs.bindings.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.bindings.TextIcons;
import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.bindings.UUIDWrapper;
import dev.latvian.mods.kubejs.bindings.UtilsWrapper;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.bindings.event.KGUIEvents;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.NetworkEvents;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.block.BlockTintFunction;
import dev.latvian.mods.kubejs.block.DetectorBlock;
import dev.latvian.mods.kubejs.block.MapColorHelper;
import dev.latvian.mods.kubejs.block.SoundTypeWrapper;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import dev.latvian.mods.kubejs.block.custom.ButtonBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.CarpetBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.CropBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FallingBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceGateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.HorizontalDirectionalBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.PressurePlateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.SlabBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StairBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WallBlockBuilder;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentType;
import dev.latvian.mods.kubejs.block.entity.InventoryAttachment;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.color.Color;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.PlayerSelector;
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
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemTintFunction;
import dev.latvian.mods.kubejs.item.ItemToolTiers;
import dev.latvian.mods.kubejs.item.JukeboxSongBuilder;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabBuilder;
import dev.latvian.mods.kubejs.item.custom.ArmorItemBuilder;
import dev.latvian.mods.kubejs.item.custom.DiggerItemBuilder;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SmithingTemplateItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SwordItemBuilder;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.misc.CustomStatBuilder;
import dev.latvian.mods.kubejs.misc.MobEffectBuilder;
import dev.latvian.mods.kubejs.misc.PaintingVariantBuilder;
import dev.latvian.mods.kubejs.misc.ParticleTypeBuilder;
import dev.latvian.mods.kubejs.misc.PoiTypeBuilder;
import dev.latvian.mods.kubejs.misc.PotionBuilder;
import dev.latvian.mods.kubejs.misc.SoundEventBuilder;
import dev.latvian.mods.kubejs.misc.VillagerProfessionBuilder;
import dev.latvian.mods.kubejs.misc.VillagerTypeBuilder;
import dev.latvian.mods.kubejs.neoforge.NativeEventWrapper;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.component.BlockStateComponent;
import dev.latvian.mods.kubejs.recipe.component.BookCategoryComponent;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.CharacterComponent;
import dev.latvian.mods.kubejs.recipe.component.EitherRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NestedRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RegistryComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedFluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.component.StringGridComponent;
import dev.latvian.mods.kubejs.recipe.component.TagKeyComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ConsumeAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionTypeRegistry;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.UnknownKubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapedKubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapelessKubeRecipe;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEvents;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.DataComponentTypeInfoRegistry;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.TypeDescriptionRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.server.ScheduledServerEvent;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.FluidAmounts;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.NBTIOWrapper;
import dev.latvian.mods.kubejs.util.NBTUtils;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RotationAxis;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.kubejs.util.SlotFilter;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.kubejs.util.Tristate;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.registrypredicate.RegistryPredicate;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinKubeJSPlugin implements KubeJSPlugin {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	@Override
	public void registerBuilderTypes(BuilderTypeRegistry registry) {
		registry.addDefault(Registries.SOUND_EVENT, SoundEventBuilder.class, SoundEventBuilder::new);

		registry.addDefault(Registries.BLOCK, BasicBlockJS.Builder.class, BasicBlockJS.Builder::new);

		registry.of(Registries.BLOCK, reg -> {
			reg.add("detector", DetectorBlock.Builder.class, DetectorBlock.Builder::new);
			reg.add("slab", SlabBlockBuilder.class, SlabBlockBuilder::new);
			reg.add("stairs", StairBlockBuilder.class, StairBlockBuilder::new);
			reg.add("fence", FenceBlockBuilder.class, FenceBlockBuilder::new);
			reg.add("wall", WallBlockBuilder.class, WallBlockBuilder::new);
			reg.add("fence_gate", FenceGateBlockBuilder.class, FenceGateBlockBuilder::new);
			reg.add("pressure_plate", PressurePlateBlockBuilder.class, PressurePlateBlockBuilder::new);
			reg.add("button", ButtonBlockBuilder.class, ButtonBlockBuilder::new);
			reg.add("falling", FallingBlockBuilder.class, FallingBlockBuilder::new);
			reg.add("crop", CropBlockBuilder.class, CropBlockBuilder::new);
			reg.add("cardinal", HorizontalDirectionalBlockBuilder.class, HorizontalDirectionalBlockBuilder::new);
			reg.add("carpet", CarpetBlockBuilder.class, CarpetBlockBuilder::new);
		});

		registry.addDefault(Registries.ITEM, ItemBuilder.class, ItemBuilder::new);

		registry.of(Registries.ITEM, reg -> {
			reg.add("sword", SwordItemBuilder.class, SwordItemBuilder::new);
			reg.add("pickaxe", DiggerItemBuilder.Pickaxe.class, DiggerItemBuilder.Pickaxe::new);
			reg.add("axe", DiggerItemBuilder.Axe.class, DiggerItemBuilder.Axe::new);
			reg.add("shovel", DiggerItemBuilder.Shovel.class, DiggerItemBuilder.Shovel::new);
			reg.add("hoe", DiggerItemBuilder.Hoe.class, DiggerItemBuilder.Hoe::new);
			reg.add("shears", ShearsItemBuilder.class, ShearsItemBuilder::new);
			reg.add("helmet", ArmorItemBuilder.Helmet.class, ArmorItemBuilder.Helmet::new);
			reg.add("chestplate", ArmorItemBuilder.Chestplate.class, ArmorItemBuilder.Chestplate::new);
			reg.add("leggings", ArmorItemBuilder.Leggings.class, ArmorItemBuilder.Leggings::new);
			reg.add("boots", ArmorItemBuilder.Boots.class, ArmorItemBuilder.Boots::new);
			reg.add("animal_armor", ArmorItemBuilder.AnimalArmor.class, ArmorItemBuilder.AnimalArmor::new);
			reg.add("smithing_template", SmithingTemplateItemBuilder.class, SmithingTemplateItemBuilder::new);
		});

		registry.addDefault(Registries.FLUID, FluidBuilder.class, FluidBuilder::new);

		registry.of(Registries.FLUID, reg -> {
			reg.add("thin", ThinFluidBuilder.class, ThinFluidBuilder::new);
			reg.add("thick", ThickFluidBuilder.class, ThickFluidBuilder::new);
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
		registry.register(KGUIEvents.GROUP);
	}

	@Override
	public void registerClasses(ClassFilter filter) {
		filter.deny("java.lang"); // java.lang
		filter.allow("java.lang.Number");
		filter.allow("java.lang.String");
		filter.allow("java.lang.Character");
		filter.allow("java.lang.Byte");
		filter.allow("java.lang.Short");
		filter.allow("java.lang.Integer");
		filter.allow("java.lang.Long");
		filter.allow("java.lang.Float");
		filter.allow("java.lang.Double");
		filter.allow("java.lang.Boolean");
		filter.allow("java.lang.Runnable");
		filter.allow("java.lang.Iterable");
		filter.allow("java.lang.Comparable");
		filter.allow("java.lang.CharSequence");
		filter.allow("java.lang.Void");
		filter.allow("java.lang.Package");
		filter.allow("java.lang.Appendable");
		filter.allow("java.lang.AutoCloseable");
		filter.allow("java.lang.Comparable");
		filter.allow("java.lang.Iterable");
		filter.allow("java.lang.Object");
		filter.allow("java.lang.Runnable");
		filter.allow("java.lang.StringBuilder");

		filter.allow("java.math.BigInteger"); // java.math
		filter.allow("java.math.BigDecimal");

		filter.deny("java.io"); // IO
		filter.allow("java.io.Closeable");
		filter.allow("java.io.Serializable");

		filter.deny("java.nio"); // NIO
		filter.allow("java.nio.ByteOrder");

		filter.allow("java.util"); // Utils
		filter.deny("java.util.jar");
		filter.deny("java.util.zip");

		filter.allow("it.unimi.dsi.fastutil"); // FastUtil

		filter.allow("dev.latvian.mods.kubejs"); // KubeJS
		filter.deny("dev.latvian.mods.kubejs.script");
		filter.deny("dev.latvian.mods.kubejs.plugin");
		filter.deny("dev.latvian.mods.kubejs.mixin");
		filter.deny(KubeJSPlugin.class);
		filter.deny(KubeJSPlugins.class);

		filter.allow("net.minecraft"); // Minecraft
		filter.allow("com.mojang.authlib.GameProfile");
		filter.allow("com.mojang.util.UUIDTypeAdapter");
		filter.allow("com.mojang.brigadier");
		filter.allow("com.mojang.blaze3d");

		filter.allow("dev.architectury"); // Architectury

		// Misc
		filter.deny("java.net"); // Networks
		filter.deny("sun"); // Sun
		filter.deny("com.sun"); // Sun
		filter.deny("io.netty"); // Netty
		filter.deny("org.objectweb.asm"); // ASM
		filter.deny("org.spongepowered.asm"); // Sponge ASM
		filter.deny("org.openjdk.nashorn"); // Nashorn
		filter.deny("jdk.nashorn"); // Nashorn
		filter.deny("org.lwjgl.system"); // LWJGL

		filter.allow("net.neoforged"); // Forge
		filter.deny("net.neoforged.fml");
		filter.deny("net.neoforged.accesstransformer");
		filter.deny("net.neoforged.coremod");

		filter.deny("cpw.mods.modlauncher"); // FML
		filter.deny("cpw.mods.gross");

		// Mods
		filter.allow("mezz.jei"); // JEI
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
		bindings.add("NBT", NBTUtils.class);
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
	}

	@Override
	public void registerTypeWrappers(TypeWrapperRegistry registry) {
		registry.register(RegistryPredicate.class, RegistryPredicate::of);

		// Java / Minecraft //
		registry.register(String.class, String::valueOf);
		registry.register(CharSequence.class, String::valueOf);
		registry.register(UUID.class, UUIDWrapper::fromString);
		registry.register(Pattern.class, RegExpKJS::wrap);
		registry.register(JsonObject.class, MapJS::json);
		registry.register(JsonArray.class, ListJS::json);
		registry.register(JsonElement.class, JsonUtils::of);
		registry.register(JsonPrimitive.class, JsonUtils::primitiveOf);
		registry.register(Path.class, KubeJSTypeWrappers::pathOf);
		registry.register(File.class, KubeJSTypeWrappers::fileOf);
		registry.register(TemporalAmount.class, TimeJS::temporalAmountOf);
		registry.register(Duration.class, TimeJS::durationOf);

		registry.register(ResourceLocation.class, ID::mc);
		registry.register(KubeResourceLocation.class, KubeResourceLocation::wrap);
		registry.register(CompoundTag.class, (from, target) -> NBTUtils.isTagCompound(from), NBTUtils::toTagCompound);
		registry.register(CollectionTag.class, (from, target) -> NBTUtils.isTagCollection(from), NBTUtils::toTagCollection);
		registry.register(ListTag.class, (from, target) -> NBTUtils.isTagCollection(from), NBTUtils::toTagList);
		registry.register(Tag.class, NBTUtils::toTag);
		registry.register(DataComponentType.class, DataComponentWrapper::wrapType);
		registry.register(DataComponentMap.class, DataComponentWrapper::filter, (cx, from, target) -> DataComponentWrapper.mapOf(RegistryAccessContainer.of(cx), from));
		registry.register(DataComponentPatch.class, DataComponentWrapper::filter, (cx, from, target) -> DataComponentWrapper.patchOf(RegistryAccessContainer.of(cx), from));

		registry.register(BlockPos.class, KubeJSTypeWrappers::blockPosOf);
		registry.register(Vec3.class, KubeJSTypeWrappers::vec3Of);

		registry.register(Item.class, ItemStackJS::getRawItem);
		registry.register(ItemLike.class, ItemStackJS::getRawItem);
		registry.registerEnumFromStringCodec(MobCategory.class, MobCategory.CODEC);
		registry.register(ItemEnchantments.class, ItemEnchantmentsWrapper::from);

		registry.register(AABB.class, AABBWrapper::wrap);
		registry.register(IntProvider.class, KubeJSTypeWrappers::intProviderOf);
		registry.register(FloatProvider.class, KubeJSTypeWrappers::floatProviderOf);
		registry.register(NumberProvider.class, KubeJSTypeWrappers::numberProviderOf);
		registry.registerEnumFromStringCodec(LootContext.EntityTarget.class, LootContext.EntityTarget.CODEC);
		registry.registerEnumFromStringCodec(CopyNameFunction.NameSource.class, CopyNameFunction.NameSource.CODEC);
		// FIXME registry.register(Enchantment.Cost.class, EnchantmentBuilder::costOf);
		registry.registerEnumFromStringCodec(ArmorItem.Type.class, ArmorItem.Type.CODEC);
		registry.register(BlockSetType.class, BlockWrapper::setTypeOf);
		registry.register(BlockState.class, BlockWrapper::wrapBlockState);
		registry.register(ItemAbility.class, ItemWrapper::itemAbilityOf);

		// KubeJS //
		registry.register(Map.class, MapJS::of);
		registry.register(List.class, ListJS::of);
		registry.register(Iterable.class, ListJS::of);
		registry.register(Collection.class, ListJS::of);
		registry.register(Set.class, ListJS::ofSet);
		registry.register(ItemStack.class, ItemStackJS::wrap);
		registry.register(Ingredient.class, IngredientJS::wrap);
		registry.register(ItemPredicate.class, ItemPredicate::wrap);
		registry.register(SizedIngredient.class, SizedIngredientWrapper::wrap);
		registry.register(BlockStatePredicate.class, BlockStatePredicate::of);
		registry.register(RuleTest.class, BlockStatePredicate::ruleTestOf);
		registry.register(FluidStack.class, FluidWrapper::wrap);
		registry.register(FluidIngredient.class, FluidWrapper::wrapIngredient);
		registry.register(SizedFluidIngredient.class, FluidWrapper::wrapSizedIngredient);
		registry.register(RecipeFilter.class, RecipeFilter::of);
		registry.register(SlotFilter.class, SlotFilter::wrap);
		registry.register(Tier.class, ItemToolTiers::wrap);
		registry.register(PlayerSelector.class, PlayerSelector::of);
		registry.register(DamageSource.class, DamageSourceWrapper::of);
		registry.register(EntitySelector.class, UtilsJS::entitySelector);
		registry.register(ReplacementMatch.class, ReplacementMatch::wrap);
		registry.register(ReplacementMatchInfo.class, ReplacementMatchInfo::wrap);
		registry.register(Stat.class, PlayerStatsJS::statOf);
		registry.register(MapColor.class, MapColorHelper::of);
		registry.register(SoundType.class, SoundTypeWrapper.INSTANCE);
		registry.register(ParticleOptions.class, ParticleOptionsWrapper::wrap);
		registry.register(ItemTintFunction.class, ItemTintFunction::of);
		registry.register(BlockTintFunction.class, BlockTintFunction::of);
		registry.register(Tristate.class, Tristate::wrap);

		// components //
		registry.register(Component.class, TextWrapper::of);
		registry.register(MutableComponent.class, TextWrapper::of);
		registry.register(Color.class, ColorWrapper::of);
		registry.register(TextColor.class, ColorWrapper::textColorOf);
		registry.register(ClickEvent.class, TextWrapper::clickEventOf);

		// codecs
		registry.registerCodec(Fireworks.class, Fireworks.CODEC);
	}

	@Override
	public void registerTypeDescriptions(TypeDescriptionRegistry registry) {
		registry.register(SlotFilter.class, ((RecordTypeInfo) TypeInfo.of(SlotFilter.class)).createCombinedType(TypeInfo.INT, IngredientJS.TYPE_INFO));
	}

	@Override
	public void registerRecipeFactories(RecipeFactoryRegistry registry) {
		registry.register(UnknownKubeRecipe.RECIPE_FACTORY);
		registry.register(ShapedKubeRecipe.RECIPE_FACTORY);
		registry.register(ShapelessKubeRecipe.RECIPE_FACTORY);
	}

	@Override
	public void registerRecipeComponents(RecipeComponentFactoryRegistry registry) {
		registry.register(BooleanComponent.BOOLEAN);
		registry.register(StringComponent.ANY);
		registry.register(StringComponent.NON_EMPTY);
		registry.register(StringComponent.NON_BLANK);
		registry.register(StringComponent.ID);
		registry.register(CharacterComponent.CHARACTER);
		registry.register(StringGridComponent.STRING_GRID);

		registry.register("int", NumberComponent.INT_FACTORY);
		registry.register("long", NumberComponent.LONG_FACTORY);
		registry.register("float", NumberComponent.FLOAT_FACTORY);
		registry.register("double", NumberComponent.DOUBLE_FACTORY);

		registry.register(IngredientComponent.INGREDIENT);
		registry.register(IngredientComponent.NON_EMPTY_INGREDIENT);
		registry.register(IngredientComponent.UNWRAPPED_INGREDIENT_LIST);

		registry.register(SizedIngredientComponent.FLAT);
		registry.register(SizedIngredientComponent.NESTED);

		registry.register(ItemStackComponent.ITEM_STACK);
		registry.register(ItemStackComponent.STRICT_ITEM_STACK);

		registry.register(FluidStackComponent.FLUID_STACK);
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

		registry.register("tag", TagKeyComponent.FACTORY);
		registry.register("registry_element", RegistryComponent.FACTORY);
		registry.register("enum", EnumComponent.FACTORY);
		registry.register("map", MapRecipeComponent.FACTORY);
		registry.register("pattern", MapRecipeComponent.PATTERN_FACTORY);
		registry.register("either", EitherRecipeComponent.FACTORY);
	}

	@Override
	public void registerBlockEntityAttachments(List<BlockEntityAttachmentType> types) {
		types.add(InventoryAttachment.TYPE);
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
		ItemStackJS.CACHED_ITEM_MAP.forget();
		ItemStackJS.CACHED_ITEM_LIST.forget();
		ItemStackJS.CACHED_ITEM_TYPE_LIST.forget();
	}

	@Override
	public void registerDataComponentTypeDescriptions(DataComponentTypeInfoRegistry registry) {
		// DataComponents.ATTRIBUTE_MODIFIERS
	}
}
