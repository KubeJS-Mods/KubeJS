package dev.latvian.mods.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.bindings.DamageSourceWrapper;
import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.bindings.JavaWrapper;
import dev.latvian.mods.kubejs.bindings.KMath;
import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.bindings.UtilsWrapper;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.NetworkEvents;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.block.BlockTintFunction;
import dev.latvian.mods.kubejs.block.DetectorBlock;
import dev.latvian.mods.kubejs.block.MapColorHelper;
import dev.latvian.mods.kubejs.block.MaterialJS;
import dev.latvian.mods.kubejs.block.MaterialListJS;
import dev.latvian.mods.kubejs.block.SoundTypeWrapper;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import dev.latvian.mods.kubejs.block.custom.CropBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FallingBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceGateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.HorizontalDirectionalBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.SlabBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StairBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StoneButtonBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StonePressurePlateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WallBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WoodenButtonBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WoodenPressurePlateBlockBuilder;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentType;
import dev.latvian.mods.kubejs.block.entity.InventoryAttachment;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupWrapper;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.integration.rei.REIEvents;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemTintFunction;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.item.custom.ArmorItemBuilder;
import dev.latvian.mods.kubejs.item.custom.AxeItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import dev.latvian.mods.kubejs.item.custom.HoeItemBuilder;
import dev.latvian.mods.kubejs.item.custom.ItemArmorTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.PickaxeItemBuilder;
import dev.latvian.mods.kubejs.item.custom.RecordItemJS;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import dev.latvian.mods.kubejs.item.custom.ShovelItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SwordItemBuilder;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.MobFilter;
import dev.latvian.mods.kubejs.level.gen.ruletest.KubeJSRuleTests;
import dev.latvian.mods.kubejs.misc.BasicMobEffect;
import dev.latvian.mods.kubejs.misc.CustomStatBuilder;
import dev.latvian.mods.kubejs.misc.EnchantmentBuilder;
import dev.latvian.mods.kubejs.misc.PaintingVariantBuilder;
import dev.latvian.mods.kubejs.misc.ParticleTypeBuilder;
import dev.latvian.mods.kubejs.misc.PoiTypeBuilder;
import dev.latvian.mods.kubejs.misc.PotionBuilder;
import dev.latvian.mods.kubejs.misc.SoundEventBuilder;
import dev.latvian.mods.kubejs.misc.VillagerProfessionBuilder;
import dev.latvian.mods.kubejs.misc.VillagerTypeBuilder;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionFilter;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.CookingRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.SmithingRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.StonecuttingRecipeSchema;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.CustomJavaToJsWrappersEvent;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.FluidAmounts;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.LegacyCodeHandler;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.NBTIOWrapper;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import dev.latvian.mods.kubejs.util.RotationAxis;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.CollectionTagWrapper;
import dev.latvian.mods.rhino.mod.util.CompoundTagWrapper;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.AABBWrapper;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.rhino.mod.wrapper.DirectionWrapper;
import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import dev.latvian.mods.unit.Unit;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinKubeJSPlugin extends KubeJSPlugin {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	@Override
	public void init() {
		RegistryInfo.SOUND_EVENT.addType("basic", SoundEventBuilder.class, SoundEventBuilder::new);

		RegistryInfo.BLOCK.addType("basic", BasicBlockJS.Builder.class, BasicBlockJS.Builder::new);
		RegistryInfo.BLOCK.addType("detector", DetectorBlock.Builder.class, DetectorBlock.Builder::new);
		RegistryInfo.BLOCK.addType("slab", SlabBlockBuilder.class, SlabBlockBuilder::new);
		RegistryInfo.BLOCK.addType("stairs", StairBlockBuilder.class, StairBlockBuilder::new);
		RegistryInfo.BLOCK.addType("fence", FenceBlockBuilder.class, FenceBlockBuilder::new);
		RegistryInfo.BLOCK.addType("fence_gate", FenceGateBlockBuilder.class, FenceGateBlockBuilder::new);
		RegistryInfo.BLOCK.addType("wall", WallBlockBuilder.class, WallBlockBuilder::new);
		RegistryInfo.BLOCK.addType("wooden_pressure_plate", WoodenPressurePlateBlockBuilder.class, WoodenPressurePlateBlockBuilder::new);
		RegistryInfo.BLOCK.addType("stone_pressure_plate", StonePressurePlateBlockBuilder.class, StonePressurePlateBlockBuilder::new);
		RegistryInfo.BLOCK.addType("wooden_button", WoodenButtonBlockBuilder.class, WoodenButtonBlockBuilder::new);
		RegistryInfo.BLOCK.addType("stone_button", StoneButtonBlockBuilder.class, StoneButtonBlockBuilder::new);
		RegistryInfo.BLOCK.addType("falling", FallingBlockBuilder.class, FallingBlockBuilder::new);
		RegistryInfo.BLOCK.addType("crop", CropBlockBuilder.class, CropBlockBuilder::new);
		RegistryInfo.BLOCK.addType("cardinal", HorizontalDirectionalBlockBuilder.class, HorizontalDirectionalBlockBuilder::new);

		RegistryInfo.ITEM.addType("basic", BasicItemJS.Builder.class, BasicItemJS.Builder::new);
		RegistryInfo.ITEM.addType("sword", SwordItemBuilder.class, SwordItemBuilder::new);
		RegistryInfo.ITEM.addType("pickaxe", PickaxeItemBuilder.class, PickaxeItemBuilder::new);
		RegistryInfo.ITEM.addType("axe", AxeItemBuilder.class, AxeItemBuilder::new);
		RegistryInfo.ITEM.addType("shovel", ShovelItemBuilder.class, ShovelItemBuilder::new);
		RegistryInfo.ITEM.addType("shears", ShearsItemBuilder.class, ShearsItemBuilder::new);
		RegistryInfo.ITEM.addType("hoe", HoeItemBuilder.class, HoeItemBuilder::new);
		RegistryInfo.ITEM.addType("helmet", ArmorItemBuilder.Helmet.class, ArmorItemBuilder.Helmet::new);
		RegistryInfo.ITEM.addType("chestplate", ArmorItemBuilder.Chestplate.class, ArmorItemBuilder.Chestplate::new);
		RegistryInfo.ITEM.addType("leggings", ArmorItemBuilder.Leggings.class, ArmorItemBuilder.Leggings::new);
		RegistryInfo.ITEM.addType("boots", ArmorItemBuilder.Boots.class, ArmorItemBuilder.Boots::new);
		RegistryInfo.ITEM.addType("music_disc", RecordItemJS.Builder.class, RecordItemJS.Builder::new);

		RegistryInfo.FLUID.addType("basic", FluidBuilder.class, FluidBuilder::new);
		RegistryInfo.ENCHANTMENT.addType("basic", EnchantmentBuilder.class, EnchantmentBuilder::new);
		RegistryInfo.MOB_EFFECT.addType("basic", BasicMobEffect.Builder.class, BasicMobEffect.Builder::new);
		// ENTITY_TYPE
		// BLOCK_ENTITY_TYPE
		RegistryInfo.POTION.addType("basic", PotionBuilder.class, PotionBuilder::new);
		RegistryInfo.PARTICLE_TYPE.addType("basic", ParticleTypeBuilder.class, ParticleTypeBuilder::new);
		RegistryInfo.PAINTING_VARIANT.addType("basic", PaintingVariantBuilder.class, PaintingVariantBuilder::new);
		RegistryInfo.CUSTOM_STAT.addType("basic", CustomStatBuilder.class, CustomStatBuilder::new);
		RegistryInfo.POINT_OF_INTEREST_TYPE.addType("basic", PoiTypeBuilder.class, PoiTypeBuilder::new);
		RegistryInfo.VILLAGER_TYPE.addType("basic", VillagerTypeBuilder.class, VillagerTypeBuilder::new);
		RegistryInfo.VILLAGER_PROFESSION.addType("basic", VillagerProfessionBuilder.class, VillagerProfessionBuilder::new);
	}

	@Override
	public void initStartup() {
		ItemEvents.TOOL_TIER_REGISTRY.post(ScriptType.STARTUP, new ItemToolTierRegistryEventJS());
		ItemEvents.ARMOR_TIER_REGISTRY.post(ScriptType.STARTUP, new ItemArmorTierRegistryEventJS());
		KubeJSRuleTests.init();

		/*
		for (var types : RegistryObjectBuilderTypes.MAP.values()) {
			// types.postEvent();
		}
		 */
	}

	@Override
	public void registerEvents() {
		StartupEvents.GROUP.register();
		ServerEvents.GROUP.register();
		LevelEvents.GROUP.register();
		WorldgenEvents.GROUP.register();
		NetworkEvents.GROUP.register();
		ItemEvents.GROUP.register();
		BlockEvents.GROUP.register();
		EntityEvents.GROUP.register();
		PlayerEvents.GROUP.register();

		if (Platform.isModLoaded("roughlyenoughitems")) {
			REIEvents.register();
		}
	}

	@Override
	public void registerClasses(ScriptType type, ClassFilter filter) {
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
		filter.allow("java.lang.Class");
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

		// Mods
		filter.allow("mezz.jei"); // JEI
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("global", GLOBAL);
		event.add("Platform", PlatformWrapper.class);
		event.add("console", event.getType().console);

		for (var group : EventGroup.getGroups().values()) {
			event.add(group.name, new EventGroupWrapper(event.getType(), group));
		}

		event.add("JavaMath", Math.class);
		event.add("ResourceLocation", ResourceLocation.class);
		event.add("Duration", Duration.class);

		if (event.getType().isServer()) {
			event.add("settings", new LegacyCodeHandler("settings"));
		}

		event.add("onEvent", new LegacyCodeHandler("onEvent()"));
		event.add("java", new LegacyCodeHandler("java()"));

		event.add("KMath", KMath.class);
		event.add("Utils", UtilsWrapper.class);
		event.add("Java", new JavaWrapper(event.manager));
		event.add("Text", TextWrapper.class);
		event.add("Component", TextWrapper.class);
		event.add("UUID", UUIDWrapper.class);
		event.add("JsonIO", JsonIO.class);
		event.add("Block", BlockWrapper.class);
		event.add("Blocks", Blocks.class);
		event.add("Item", ItemWrapper.class);
		event.add("Items", Items.class);
		event.add("Ingredient", IngredientWrapper.class);
		event.add("IngredientHelper", IngredientPlatformHelper.get());
		event.add("NBT", NBTUtils.class);
		event.add("NBTIO", NBTIOWrapper.class);
		event.add("Direction", DirectionWrapper.class);
		event.add("Facing", DirectionWrapper.class);
		event.add("AABB", AABBWrapper.class);
		event.add("Stats", Stats.class);
		event.add("FluidAmounts", FluidAmounts.class);
		event.add("Notification", NotificationBuilder.class);
		event.add("InputItem", InputItem.class);
		event.add("OutputItem", OutputItem.class);

		event.add("Fluid", FluidWrapper.class);

		event.add("SECOND", 1000L);
		event.add("MINUTE", 60000L);
		event.add("HOUR", 3600000L);

		event.add("Color", ColorWrapper.class);
		event.add("BlockStatePredicate", BlockStatePredicate.class);

		event.add("Vec3", Vec3.class);
		event.add("Vec3d", Vec3.class);
		event.add("Vec3i", Vec3i.class);
		event.add("Vec3f", Vector3f.class);
		event.add("Vec4f", Vector4f.class);
		event.add("Matrix3f", Matrix3f.class);
		event.add("Matrix4f", Matrix4f.class);
		event.add("Quaternionf", Quaternion.class);
		event.add("RotationAxis", RotationAxis.class);
		event.add("BlockPos", BlockPos.class);
		event.add("DamageSource", DamageSource.class);
		event.add("SoundType", SoundType.class);

		event.add("BlockProperties", BlockStateProperties.class);
	}

	@Override
	public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		// Java / Minecraft //
		typeWrappers.registerSimple(String.class, String::valueOf);
		typeWrappers.registerSimple(CharSequence.class, String::valueOf);
		typeWrappers.registerSimple(UUID.class, UUIDWrapper::fromString);
		typeWrappers.registerSimple(Pattern.class, UtilsJS::parseRegex);
		typeWrappers.registerSimple(JsonObject.class, MapJS::json);
		typeWrappers.registerSimple(JsonArray.class, ListJS::json);
		typeWrappers.registerSimple(JsonElement.class, JsonIO::of);
		typeWrappers.registerSimple(JsonPrimitive.class, JsonIO::primitiveOf);
		typeWrappers.registerSimple(Path.class, UtilsJS::getPath);
		typeWrappers.registerSimple(File.class, UtilsJS::getFileFromPath);
		typeWrappers.register(Unit.class, Painter.INSTANCE::unitOf);
		typeWrappers.registerSimple(TemporalAmount.class, UtilsJS::getTemporalAmount);
		typeWrappers.registerSimple(Duration.class, UtilsJS::getDuration);

		typeWrappers.register(ResourceLocation.class, UtilsJS::getMCID);
		typeWrappers.registerSimple(CompoundTag.class, NBTUtils::isTagCompound, NBTUtils::toTagCompound);
		typeWrappers.registerSimple(CollectionTag.class, NBTUtils::isTagCollection, NBTUtils::toTagCollection);
		typeWrappers.registerSimple(ListTag.class, NBTUtils::isTagCollection, NBTUtils::toTagList);
		typeWrappers.registerSimple(Tag.class, NBTUtils::toTag);

		typeWrappers.registerSimple(BlockPos.class, UtilsJS::blockPosOf);
		typeWrappers.registerSimple(Vec3.class, UtilsJS::vec3Of);

		typeWrappers.register(Item.class, ItemStackJS::getRawItem);
		typeWrappers.register(ItemLike.class, ItemStackJS::getRawItem);
		typeWrappers.registerSimple(MobCategory.class, o -> o == null ? null : UtilsJS.mobCategoryByName(o.toString()));

		typeWrappers.registerSimple(AABB.class, AABBWrapper::wrap);
		typeWrappers.registerSimple(IntProvider.class, UtilsJS::intProviderOf);
		typeWrappers.registerSimple(NumberProvider.class, UtilsJS::numberProviderOf);
		typeWrappers.registerSimple(LootContext.EntityTarget.class, o -> o == null ? null : LootContext.EntityTarget.getByName(o.toString().toLowerCase()));
		typeWrappers.registerSimple(CopyNameFunction.NameSource.class, o -> o == null ? null : CopyNameFunction.NameSource.getByName(o.toString().toLowerCase()));

		// KubeJS //
		typeWrappers.registerSimple(Map.class, MapJS::of);
		typeWrappers.registerSimple(List.class, ListJS::of);
		typeWrappers.registerSimple(Iterable.class, ListJS::of);
		typeWrappers.registerSimple(Collection.class, ListJS::of);
		typeWrappers.registerSimple(Set.class, ListJS::ofSet);
		typeWrappers.registerSimple(ItemStack.class, ItemStackJS::of);
		typeWrappers.registerSimple(Ingredient.class, IngredientJS::of);
		typeWrappers.registerSimple(InputReplacement.class, InputReplacement::of);
		typeWrappers.registerSimple(OutputReplacement.class, OutputReplacement::of);
		typeWrappers.registerSimple(InputItem.class, InputItem::of);
		typeWrappers.registerSimple(OutputItem.class, OutputItem::of);
		typeWrappers.registerSimple(BlockStatePredicate.class, BlockStatePredicate::of);
		typeWrappers.registerSimple(RuleTest.class, BlockStatePredicate::ruleTestOf);
		typeWrappers.register(BiomeFilter.class, BiomeFilter::of);
		typeWrappers.register(MobFilter.class, MobFilter::of);
		typeWrappers.registerSimple(FluidStackJS.class, FluidStackJS::of);
		typeWrappers.register(RecipeFilter.class, RecipeFilter::of);
		typeWrappers.registerSimple(MaterialJS.class, MaterialListJS.INSTANCE::of);
		typeWrappers.registerSimple(IngredientActionFilter.class, IngredientActionFilter::filterOf);
		typeWrappers.registerSimple(Tier.class, ItemBuilder::toToolTier);
		typeWrappers.registerSimple(ArmorMaterial.class, ItemBuilder::toArmorMaterial);
		typeWrappers.registerSimple(PlayerSelector.class, PlayerSelector::of);
		typeWrappers.registerSimple(DamageSource.class, DamageSourceWrapper::of);
		typeWrappers.registerSimple(EntitySelector.class, UtilsJS::entitySelector);
		typeWrappers.registerSimple(ReplacementMatch.class, ReplacementMatch::of);
		typeWrappers.registerSimple(Stat.class, PlayerStatsJS::statOf);
		typeWrappers.register(NotificationBuilder.class, NotificationBuilder::of);
		typeWrappers.registerSimple(MaterialColor.class, MapColorHelper::of);
		typeWrappers.register(SoundType.class, SoundTypeWrapper.INSTANCE);
		typeWrappers.registerSimple(ParticleOptions.class, UtilsWrapper::particleOptions);
		typeWrappers.register(ItemTintFunction.class, ItemTintFunction::of);
		typeWrappers.register(BlockTintFunction.class, BlockTintFunction::of);

		// components //
		typeWrappers.registerSimple(Component.class, TextWrapper::of);
		typeWrappers.registerSimple(MutableComponent.class, TextWrapper::of);
		typeWrappers.registerSimple(Color.class, ColorWrapper::of);
		typeWrappers.registerSimple(TextColor.class, o -> ColorWrapper.of(o).createTextColorJS());
		typeWrappers.registerSimple(ClickEvent.class, TextWrapper::clickEventOf);
	}

	@Override
	public void registerCustomJavaToJsWrappers(CustomJavaToJsWrappersEvent event) {
		event.add(CompoundTag.class, CompoundTagWrapper::new);
		event.add(CollectionTag.class, CollectionTagWrapper::new);
	}

	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace("kubejs")
			.shaped("shaped")
			.shapeless("shapeless")
		;

		event.namespace("minecraft")
			.shaped("crafting_shaped")
			.shapeless("crafting_shapeless")
			.register("stonecutting", StonecuttingRecipeSchema.SCHEMA)
			.register("smelting", CookingRecipeSchema.SCHEMA)
			.register("blasting", CookingRecipeSchema.SCHEMA)
			.register("smoking", CookingRecipeSchema.SCHEMA)
			.register("campfire_cooking", CookingRecipeSchema.SCHEMA)
			.register("smithing", SmithingRecipeSchema.SCHEMA)
			.special("crafting_special_armordye")
			.special("crafting_special_shulkerboxcoloring")
			.special("crafting_special_bannerduplicate")
			.special("crafting_special_suspiciousstew")
			.special("crafting_special_bookcloning")
			.special("crafting_special_mapextending")
			.special("crafting_special_tippedarrow")
			.special("crafting_special_firework_star")
			.special("crafting_special_shielddecoration")
			.special("crafting_special_firework_star_fade")
			.special("crafting_special_firework_rocket")
			.special("crafting_special_mapcloning")
			.special("crafting_special_repairitem")
		;

		event.namespace("cucumber")
			.shaped("shaped_no_mirror")
		;

		event.namespace("extendedcrafting")
			.shaped("shaped_table")
			.shapeless("shapeless_table")
		;

		event.mapRecipe("extendedCraftingShaped", "extendedcrafting:shaped_table");
		event.mapRecipe("extendedCraftingShapeless", "extendedcrafting:shapeless_table");

		event.namespace("dankstorage")
			.shaped("upgrade")
		;

		event.mapRecipe("dankStorageUpgrade", "dankstorage:upgrade");
	}

	@Override
	public void registerBlockEntityAttachments(List<BlockEntityAttachmentType> types) {
		types.add(InventoryAttachment.TYPE);
	}

	@Override
	public void clearCaches() {
		ItemStackJS.clearAllCaches();
	}
}
