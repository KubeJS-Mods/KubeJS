package dev.latvian.mods.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
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
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventGroupWrapper;
import dev.latvian.mods.kubejs.event.EventGroups;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import dev.latvian.mods.kubejs.integration.RecipeViewerEvents;
import dev.latvian.mods.kubejs.item.ArmorMaterialBuilder;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemTintFunction;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabBuilder;
import dev.latvian.mods.kubejs.item.custom.ArmorItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import dev.latvian.mods.kubejs.item.custom.DiggerItemBuilder;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.RecordItemJS;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SmithingTemplateItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SwordItemBuilder;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.level.ruletest.KubeJSRuleTests;
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
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.component.BlockStateComponent;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RegistryComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.component.TagKeyComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionFilter;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistryEvent;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.CookingRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.SmithingTransformRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.SmithingTrimRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.StonecuttingRecipeSchema;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.CustomJavaToJsWrappersEvent;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.WrapperRegistry;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.CollectionTagWrapper;
import dev.latvian.mods.kubejs.util.CompoundTagWrapper;
import dev.latvian.mods.kubejs.util.FluidAmounts;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.NBTIOWrapper;
import dev.latvian.mods.kubejs.util.NBTUtils;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.kubejs.util.RotationAxis;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.kubejs.util.StringWithContext;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.AABBWrapper;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.rhino.mod.wrapper.DirectionWrapper;
import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import dev.latvian.mods.unit.Unit;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
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
		RegistryInfo.BLOCK.addType("wall", WallBlockBuilder.class, WallBlockBuilder::new);
		RegistryInfo.BLOCK.addType("fence_gate", FenceGateBlockBuilder.class, FenceGateBlockBuilder::new);
		RegistryInfo.BLOCK.addType("pressure_plate", PressurePlateBlockBuilder.class, PressurePlateBlockBuilder::new);
		RegistryInfo.BLOCK.addType("button", ButtonBlockBuilder.class, ButtonBlockBuilder::new);
		RegistryInfo.BLOCK.addType("falling", FallingBlockBuilder.class, FallingBlockBuilder::new);
		RegistryInfo.BLOCK.addType("crop", CropBlockBuilder.class, CropBlockBuilder::new);
		RegistryInfo.BLOCK.addType("cardinal", HorizontalDirectionalBlockBuilder.class, HorizontalDirectionalBlockBuilder::new);
		RegistryInfo.BLOCK.addType("carpet", CarpetBlockBuilder.class, CarpetBlockBuilder::new);

		RegistryInfo.ITEM.addType("basic", BasicItemJS.Builder.class, BasicItemJS.Builder::new);
		RegistryInfo.ITEM.addType("sword", SwordItemBuilder.class, SwordItemBuilder::new);
		RegistryInfo.ITEM.addType("pickaxe", DiggerItemBuilder.Pickaxe.class, DiggerItemBuilder.Pickaxe::new);
		RegistryInfo.ITEM.addType("axe", DiggerItemBuilder.Axe.class, DiggerItemBuilder.Axe::new);
		RegistryInfo.ITEM.addType("shovel", DiggerItemBuilder.Shovel.class, DiggerItemBuilder.Shovel::new);
		RegistryInfo.ITEM.addType("hoe", DiggerItemBuilder.Hoe.class, DiggerItemBuilder.Hoe::new);
		RegistryInfo.ITEM.addType("shears", ShearsItemBuilder.class, ShearsItemBuilder::new);
		RegistryInfo.ITEM.addType("helmet", ArmorItemBuilder.Helmet.class, ArmorItemBuilder.Helmet::new);
		RegistryInfo.ITEM.addType("chestplate", ArmorItemBuilder.Chestplate.class, ArmorItemBuilder.Chestplate::new);
		RegistryInfo.ITEM.addType("leggings", ArmorItemBuilder.Leggings.class, ArmorItemBuilder.Leggings::new);
		RegistryInfo.ITEM.addType("boots", ArmorItemBuilder.Boots.class, ArmorItemBuilder.Boots::new);
		RegistryInfo.ITEM.addType("animal_armor", ArmorItemBuilder.AnimalArmor.class, ArmorItemBuilder.AnimalArmor::new);
		RegistryInfo.ITEM.addType("music_disc", RecordItemJS.Builder.class, RecordItemJS.Builder::new);
		RegistryInfo.ITEM.addType("smithing_template", SmithingTemplateItemBuilder.class, SmithingTemplateItemBuilder::new);

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
		RegistryInfo.CREATIVE_MODE_TAB.addType("basic", CreativeTabBuilder.class, CreativeTabBuilder::new);
		RegistryInfo.ARMOR_MATERIAL.addType("basic", ArmorMaterialBuilder.class, ArmorMaterialBuilder::new);
	}

	@Override
	public void initStartup() {
		ItemEvents.TOOL_TIER_REGISTRY.post(ScriptType.STARTUP, new ItemToolTierRegistryEventJS());
		KubeJSRuleTests.init();

		/*
		for (var types : RegistryObjectBuilderTypes.MAP.values()) {
			// types.postEvent();
		}
		 */
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
		event.add("console", event.type().console);

		for (var group : EventGroups.ALL.get().map().values()) {
			event.add(group.name, new EventGroupWrapper(event.type(), group));
		}

		event.add("JavaMath", Math.class);
		event.add("ResourceLocation", ResourceLocation.class);
		event.add("Duration", Duration.class);

		// event.add("onEvent", new LegacyCodeHandler("onEvent()"));

		if (event.type().isServer() && event.context().kjsFactory.manager instanceof ServerScriptManager sm && sm.server != null) {
			var se = sm.server.kjs$getScheduledEvents();

			event.add("setTimeout", new ScheduledEvents.TimeoutJSFunction(se, false, false));
			event.add("clearTimeout", new ScheduledEvents.TimeoutJSFunction(se, true, false));
			event.add("setInterval", new ScheduledEvents.TimeoutJSFunction(se, false, true));
			event.add("clearInterval", new ScheduledEvents.TimeoutJSFunction(se, true, true));
		}

		event.add("KMath", KMath.class);
		event.add("Utils", UtilsWrapper.class);
		event.add("Java", JavaWrapper.class);
		event.add("Text", TextWrapper.class);
		event.add("Component", TextWrapper.class);
		event.add("UUID", UUIDWrapper.class);
		event.add("JsonIO", JsonIO.class);
		event.add("Block", BlockWrapper.class);
		event.add("Blocks", Blocks.class);
		event.add("Item", ItemWrapper.class);
		event.add("Items", Items.class);
		event.add("Ingredient", IngredientWrapper.class);
		event.add("IngredientHelper", IngredientHelper.get());
		event.add("NBT", NBTUtils.class);
		event.add("NBTIO", NBTIOWrapper.class);
		event.add("Direction", DirectionWrapper.class);
		event.add("Facing", DirectionWrapper.class);
		event.add("AABB", AABBWrapper.class);
		event.add("Stats", Stats.class);
		event.add("FluidAmounts", FluidAmounts.class);
		event.add("Notification", NotificationToastData.class);
		event.add("InputItem", InputItem.class);
		event.add("OutputItem", OutputItem.class);

		event.add("Fluid", FluidWrapper.class);

		event.add("SECOND", 1000L);
		event.add("MINUTE", 60000L);
		event.add("HOUR", 3600000L);

		event.add("Color", ColorWrapper.class);
		event.add("BlockStatePredicate", BlockStatePredicate.class);

		event.add("Vec3d", Vec3.class);
		event.add("Vec3i", Vec3i.class);
		event.add("Vec3f", Vector3f.class);
		event.add("Vec4f", Vector4f.class);
		event.add("Matrix3f", Matrix3f.class);
		event.add("Matrix4f", Matrix4f.class);
		event.add("Matrix4f", Matrix4f.class);
		event.add("Quaternionf", Quaternionf.class);
		event.add("RotationAxis", RotationAxis.class);
		event.add("BlockPos", BlockPos.class);
		event.add("DamageSource", DamageSource.class);
		event.add("SoundType", SoundType.class);

		event.add("BlockProperties", BlockStateProperties.class);
	}

	@Override
	public void registerTypeWrappers(WrapperRegistry registry) {
		registry.register(StringWithContext.class, StringWithContext::of);

		// Java / Minecraft //
		registry.registerSimple(String.class, String::valueOf);
		registry.registerSimple(CharSequence.class, String::valueOf);
		registry.registerSimple(UUID.class, UUIDWrapper::fromString);
		registry.registerSimple(Pattern.class, UtilsJS::parseRegex);
		registry.registerSimple(JsonObject.class, MapJS::json);
		registry.registerSimple(JsonArray.class, ListJS::json);
		registry.registerSimple(JsonElement.class, JsonIO::of);
		registry.registerSimple(JsonPrimitive.class, JsonIO::primitiveOf);
		registry.registerSimple(Path.class, UtilsJS::getPath);
		registry.registerSimple(File.class, UtilsJS::getFileFromPath);
		registry.register(Unit.class, Painter.INSTANCE::unitOf);
		registry.registerSimple(TemporalAmount.class, UtilsJS::getTemporalAmount);
		registry.registerSimple(Duration.class, UtilsJS::getDuration);

		registry.register(ResourceLocation.class, UtilsJS::getMCID);
		registry.register(CompoundTag.class, NBTUtils::isTagCompound, NBTUtils::toTagCompound);
		registry.register(CollectionTag.class, NBTUtils::isTagCollection, NBTUtils::toTagCollection);
		registry.register(ListTag.class, NBTUtils::isTagCollection, NBTUtils::toTagList);
		registry.register(Tag.class, NBTUtils::toTag);
		registry.register(DataComponentMap.class, KubeJSComponents::mapOf);
		registry.register(DataComponentPatch.class, KubeJSComponents::patchOf);

		registry.registerSimple(BlockPos.class, UtilsJS::blockPosOf);
		registry.registerSimple(Vec3.class, UtilsJS::vec3Of);

		registry.register(Item.class, ItemStackJS::getRawItem);
		registry.register(ItemLike.class, ItemStackJS::getRawItem);
		registry.registerEnumFromStringCodec(MobCategory.class, MobCategory.CODEC);

		registry.registerSimple(AABB.class, AABBWrapper::wrap);
		registry.register(IntProvider.class, UtilsJS::intProviderOf);
		registry.registerSimple(NumberProvider.class, UtilsJS::numberProviderOf);
		registry.registerEnumFromStringCodec(LootContext.EntityTarget.class, LootContext.EntityTarget.CODEC);
		registry.registerEnumFromStringCodec(CopyNameFunction.NameSource.class, CopyNameFunction.NameSource.CODEC);
		registry.registerSimple(Enchantment.Cost.class, EnchantmentBuilder::costOf);
		registry.registerEnumFromStringCodec(ArmorItem.Type.class, ArmorItem.Type.CODEC);

		// KubeJS //
		registry.registerSimple(Map.class, MapJS::of);
		registry.registerSimple(List.class, ListJS::of);
		registry.registerSimple(Iterable.class, ListJS::of);
		registry.registerSimple(Collection.class, ListJS::of);
		registry.registerSimple(Set.class, ListJS::ofSet);
		registry.registerSimple(ItemStack.class, ItemStackJS::of);
		registry.registerSimple(Ingredient.class, IngredientJS::of);
		registry.registerSimple(InputReplacement.class, InputReplacement::of);
		registry.registerSimple(OutputReplacement.class, OutputReplacement::of);
		registry.registerSimple(InputItem.class, InputItem::of);
		registry.registerSimple(OutputItem.class, OutputItem::of);
		registry.register(BlockStatePredicate.class, BlockStatePredicate::of);
		registry.register(RuleTest.class, BlockStatePredicate::ruleTestOf);
		registry.registerSimple(FluidStack.class, FluidWrapper::wrap);
		registry.registerSimple(dev.architectury.fluid.FluidStack.class, FluidWrapper::wrapArch);
		registry.register(RecipeFilter.class, RecipeFilter::of);
		registry.registerSimple(IngredientActionFilter.class, IngredientActionFilter::filterOf);
		registry.registerSimple(Tier.class, ItemBuilder::toToolTier);
		registry.registerSimple(PlayerSelector.class, PlayerSelector::of);
		// FIXME (high): Damage sources are dynamic registries now!!
		//typeWrappers.registerSimple(DamageSource.class, DamageSourceWrapper::of);
		registry.registerSimple(EntitySelector.class, UtilsJS::entitySelector);
		registry.registerSimple(ReplacementMatch.class, ReplacementMatch::of);
		registry.registerSimple(Stat.class, PlayerStatsJS::statOf);
		registry.registerSimple(MapColor.class, MapColorHelper::of);
		registry.register(SoundType.class, SoundTypeWrapper.INSTANCE);
		registry.register(ParticleOptions.class, UtilsWrapper::particleOptions);
		registry.register(ItemTintFunction.class, ItemTintFunction::of);
		registry.register(BlockTintFunction.class, BlockTintFunction::of);

		// components //
		registry.registerSimple(Component.class, TextWrapper::of);
		registry.registerSimple(MutableComponent.class, TextWrapper::of);
		registry.registerSimple(Color.class, ColorWrapper::of);
		registry.registerSimple(TextColor.class, o -> ColorWrapper.of(o).createTextColorJS());
		registry.registerSimple(ClickEvent.class, TextWrapper::clickEventOf);

		// codecs
		registry.registerCodec(Fireworks.class, Fireworks.CODEC);
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
			.register("smithing_transform", SmithingTransformRecipeSchema.SCHEMA)
			.register("smithing_trim", SmithingTrimRecipeSchema.SCHEMA)
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
	public void registerRecipeComponents(RecipeComponentFactoryRegistryEvent event) {
		event.register("bool", BooleanComponent.BOOLEAN);

		event.register("intNumber", NumberComponent.INT);
		event.register("longNumber", NumberComponent.LONG);
		event.register("floatNumber", NumberComponent.FLOAT);
		event.register("doubleNumber", NumberComponent.DOUBLE);

		event.register("anyIntNumber", NumberComponent.ANY_INT);
		event.register("anyLongNumber", NumberComponent.ANY_LONG);
		event.register("anyFloatNumber", NumberComponent.ANY_FLOAT);
		event.register("anyDoubleNumber", NumberComponent.ANY_DOUBLE);

		event.registerDynamic("intNumberRange", NumberComponent.DYNAMIC_INT);
		event.registerDynamic("longNumberRange", NumberComponent.DYNAMIC_LONG);
		event.registerDynamic("floatNumberRange", NumberComponent.DYNAMIC_FLOAT);
		event.registerDynamic("doubleNumberRange", NumberComponent.DYNAMIC_DOUBLE);

		event.register("anyString", StringComponent.ANY);
		event.register("nonEmptyString", StringComponent.NON_EMPTY);
		event.register("nonBlankString", StringComponent.NON_BLANK);
		event.register("id", StringComponent.ID);
		event.register("character", StringComponent.CHARACTER);
		event.registerDynamic("filteredString", StringComponent.DYNAMIC);

		event.register("inputItem", ItemComponents.INPUT);
		event.register("inputItemArray", ItemComponents.INPUT_ARRAY);
		event.register("unwrappedInputItemArray", ItemComponents.UNWRAPPED_INPUT_ARRAY);
		event.register("outputItem", ItemComponents.OUTPUT);
		event.register("outputItemArray", ItemComponents.OUTPUT_ARRAY);
		event.register("outputItemIdWithCount", ItemComponents.OUTPUT_ID_WITH_COUNT);

		// event.register("inputFluid", FluidComponents.INPUT);
		// event.register("inputFluidArray", FluidComponents.INPUT_ARRAY);
		// event.register("inputFluidOrItem", FluidComponents.INPUT_OR_ITEM);
		// event.register("inputFluidOrItemArray", FluidComponents.INPUT_OR_ITEM_ARRAY);
		// event.register("outputFluid", FluidComponents.OUTPUT);
		// event.register("outputFluidArray", FluidComponents.OUTPUT_ARRAY);
		// event.register("outputFluidOrItem", FluidComponents.OUTPUT_OR_ITEM);
		// event.register("outputFluidOrItemArray", FluidComponents.OUTPUT_OR_ITEM_ARRAY);

		event.register("inputBlock", BlockComponent.INPUT);
		event.register("outputBlock", BlockComponent.OUTPUT);
		event.register("otherBlock", BlockComponent.BLOCK);

		event.register("inputBlockState", BlockStateComponent.INPUT);
		event.register("outputBlockState", BlockStateComponent.OUTPUT);
		event.register("otherBlockState", BlockStateComponent.BLOCK);
		event.register("inputBlockStateString", BlockStateComponent.INPUT_STRING);
		event.register("outputBlockStateString", BlockStateComponent.OUTPUT_STRING);
		event.register("otherBlockStateString", BlockStateComponent.BLOCK_STRING);

		event.register("ticks", TimeComponent.TICKS);
		event.register("seconds", TimeComponent.SECONDS);
		event.register("minutes", TimeComponent.MINUTES);
		event.registerDynamic("time", TimeComponent.DYNAMIC);

		event.register("blockTag", TagKeyComponent.BLOCK);
		event.register("itemTag", TagKeyComponent.ITEM);
		event.register("fluidTag", TagKeyComponent.FLUID);
		event.register("entityTypeTag", TagKeyComponent.ENTITY_TYPE);
		event.register("biomeTag", TagKeyComponent.BIOME);
		event.registerDynamic("tag", TagKeyComponent.DYNAMIC);

		event.registerDynamic("registryObject", RegistryComponent.DYNAMIC);
		event.registerDynamic("enum", EnumComponent.DYNAMIC);
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
