package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.util.ClassWrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public enum ArgumentTypeWrapper {
	// builtin types, other argument types can still be accessed through byName(),
	// however those will be using class wrappers

	// numeric types
	BOOLEAN(BoolArgumentType::bool, BoolArgumentType::getBool),
	FLOAT(FloatArgumentType::floatArg, FloatArgumentType::getFloat),
	DOUBLE(DoubleArgumentType::doubleArg, DoubleArgumentType::getDouble),
	INTEGER(IntegerArgumentType::integer, IntegerArgumentType::getInteger),
	LONG(LongArgumentType::longArg, LongArgumentType::getLong),
	// string types
	STRING(StringArgumentType::string, StringArgumentType::getString),
	GREEDY_STRING(StringArgumentType::greedyString, StringArgumentType::getString),
	WORD(StringArgumentType::word, StringArgumentType::getString),
	// entity / player types
	ENTITY(EntityArgument::entity, EntityArgument::getEntity),
	ENTITIES(EntityArgument::entities, EntityArgument::getEntities),
	PLAYER(EntityArgument::player, EntityArgument::getPlayer),
	PLAYERS(EntityArgument::players, EntityArgument::getPlayers),
	GAME_PROFILE(GameProfileArgument::gameProfile, GameProfileArgument::getGameProfiles),
	// position types
	BLOCK_POS(BlockPosArgument::blockPos, BlockPosArgument::getSpawnablePos),
	BLOCK_POS_LOADED(BlockPosArgument::blockPos, BlockPosArgument::getLoadedBlockPos),
	COLUMN_POS(ColumnPosArgument::columnPos, ColumnPosArgument::getColumnPos),
	// by default, vector arguments are automatically placed at the **center** of the block
	// if no explicit offset is given, since devs may not necessarily want that, we provide both options
	VEC3(() -> Vec3Argument.vec3(false), Vec3Argument::getVec3),
	VEC2(() -> Vec2Argument.vec2(false), Vec2Argument::getVec2),
	VEC3_CENTERED(Vec3Argument::vec3, Vec3Argument::getVec3),
	VEC2_CENTERED(Vec2Argument::vec2, Vec2Argument::getVec2),
	// block-based types
	BLOCK_STATE(BlockStateArgument::block, BlockStateArgument::getBlock),
	BLOCK_PREDICATE(BlockPredicateArgument::blockPredicate, BlockPredicateArgument::getBlockPredicate),
	// item-based types
	ITEM_STACK(ItemArgument::item, ItemArgument::getItem),
	ITEM_PREDICATE(ItemPredicateArgument::itemPredicate, ItemPredicateArgument::getItemPredicate),
	// message / chat types
	COLOR(ColorArgument::color, ColorArgument::getColor),
	COMPONENT(ComponentArgument::textComponent, ComponentArgument::getComponent),
	MESSAGE(MessageArgument::message, MessageArgument::getMessage),
	// nbt
	NBT_COMPOUND(CompoundTagArgument::compoundTag, CompoundTagArgument::getCompoundTag),
	NBT_TAG(NbtTagArgument::nbtTag, NbtTagArgument::getNbtTag),
	NBT_PATH(NbtPathArgument::nbtPath, NbtPathArgument::getPath),
	// random / misc
	PARTICLE(ParticleArgument::particle, ParticleArgument::getParticle),
	ANGLE(AngleArgument::angle, AngleArgument::getAngle),
	ROTATION(RotationArgument::rotation, RotationArgument::getRotation),
	SWIZZLE(SwizzleArgument::swizzle, SwizzleArgument::getSwizzle), // i have no idea wtf this is
	ITEM_SLOT(SlotArgument::slot, SlotArgument::getSlot),
	RESOURCE_LOCATION(ResourceLocationArgument::id, ResourceLocationArgument::getId),
	MOB_EFFECT(MobEffectArgument::effect, MobEffectArgument::getEffect),
	ENTITY_ANCHOR(EntityAnchorArgument::anchor, EntityAnchorArgument::getAnchor),
	INT_RANGE(RangeArgument::intRange, RangeArgument.Ints::getRange),
	FLOAT_RANGE(RangeArgument::floatRange, RangeArgument.Floats::getRange),
	ITEM_ENCHANTMENT(ItemEnchantmentArgument::enchantment, ItemEnchantmentArgument::getEnchantment),
	ENTITY_SUMMON(EntitySummonArgument::id, EntitySummonArgument::getSummonableEntity),
	DIMENSION(DimensionArgument::dimension, DimensionArgument::getDimension),
	TIME(TimeArgument::time, IntegerArgumentType::getInteger),
	UUID(UuidArgument::uuid, UuidArgument::getUuid),
	;

	private final Supplier<? extends ArgumentType<?>> factory;
	private final ArgumentFunction<?> getter;

	private static final Map<ResourceLocation, ClassWrapper<?>> byName = new HashMap<>();

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static ClassWrapper byName(ResourceLocation name) {
		if (byName.containsKey(name)) {
			return byName.get(name);
		}

		for (var argType : ArgumentTypes.BY_CLASS.entrySet()) {
			byName.putIfAbsent(argType.getValue().name, new ClassWrapper(argType.getKey()));
		}

		return Objects.requireNonNull(byName.get(name), "No argument type found for " + name);
	}

	ArgumentTypeWrapper(Supplier<? extends ArgumentType<?>> factory, ArgumentFunction<?> getter) {
		this.factory = factory;
		this.getter = getter;
	}

	public ArgumentType<?> create() {
		return factory.get();
	}

	public Object getResult(CommandContext<CommandSourceStack> context, String input) throws CommandSyntaxException {
		return getter.getResult(context, input);
	}

	interface ArgumentFunction<U> {
		U getResult(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException;
	}
}
