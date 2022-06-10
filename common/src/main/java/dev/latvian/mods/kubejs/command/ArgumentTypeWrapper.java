package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.util.ClassWrapper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

// FIXME: implement! (thanks forge)
public enum ArgumentTypeWrapper {
	// builtin types, other argument types can still be accessed through byName(),
	// however those will be using class wrappers

	// numeric types
	/*BOOLEAN(BoolArgumentType::bool, BoolArgumentType::getBool),
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
	UUID(UuidArgument::uuid, UuidArgument::getUuid),*/
	;

	private final Function<CommandBuildContext, ? extends ArgumentType<?>> factory;
	private final ArgumentFunction<?> getter;

	private static Map<ResourceLocation, ClassWrapper<?>> byNameCache;

	/*public static ClassWrapper<?> byName(ResourceLocation name) {
		var wrapper = getOrCacheByName().get(name);
		if (wrapper == null) {
			throw new IllegalStateException("No argument type found for " + name);
		}
		return wrapper;
	}*/

	public static void printAll() {
		/*for (var argType : getOrCacheByName().entrySet()) {
			ConsoleJS.SERVER.info("Argument type: " + argType.getKey() + " -> " + argType.getValue());
		}*/
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	/*private static Map<ResourceLocation, ClassWrapper<?>> getOrCacheByName() {
		if (byNameCache == null) {
			byNameCache = new HashMap<>();

			for (var argType : ArgumentTypeInfos.BY_CLASS.entrySet()) {
				byNameCache.putIfAbsent(argType.getValue().name, new ClassWrapper(argType.getKey()));
			}
		}
		return byNameCache;
	}*/

	ArgumentTypeWrapper(Supplier<? extends ArgumentType<?>> factory, ArgumentFunction<?> getter) {
		this.factory = (ctx) -> factory.get();
		this.getter = getter;
	}

	ArgumentTypeWrapper(Function<CommandBuildContext, ? extends ArgumentType<?>> argType, ArgumentFunction<?> getter) {
		this.factory = argType;
		this.getter = getter;
	}

	public ArgumentType<?> create() {
		CommandBuildContext ctx = null;
		throw new UnsupportedOperationException("Not implemented");
		// return factory.apply(ctx);
	}

	public Object getResult(CommandContext<CommandSourceStack> context, String input) throws CommandSyntaxException {
		return getter.getResult(context, input);
	}
}
