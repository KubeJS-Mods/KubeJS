package dev.latvian.mods.kubejs.block.state;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.level.ruletest.AllMatchRuleTest;
import dev.latvian.mods.kubejs.level.ruletest.AlwaysFalseRuleTest;
import dev.latvian.mods.kubejs.level.ruletest.AnyMatchRuleTest;
import dev.latvian.mods.kubejs.level.ruletest.InvertRuleTest;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.NBTUtils;
import dev.latvian.mods.kubejs.util.RegExpJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.rhino.Context;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public sealed interface BlockStatePredicate extends Predicate<BlockState>, ReplacementMatch {
	ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	@Override
	boolean test(BlockState state);

	default boolean testBlock(Block block) {
		return test(block.defaultBlockState());
	}

	@Nullable
	default RuleTest asRuleTest() {
		return null;
	}

	static BlockStatePredicate fromString(String s) {
		if (s.equals("*")) {
			return Simple.ALL;
		} else if (s.equals("-")) {
			return Simple.NONE;
		} else if (s.startsWith("#")) {
			return new TagMatch(Tags.block(new ResourceLocation(s.substring(1))));
		} else if (s.indexOf('[') != -1) {
			var state = BlockWrapper.parseBlockState(s);

			if (state != Blocks.AIR.defaultBlockState()) {
				return new StateMatch(state);
			}
		} else {
			var block = RegistryInfo.BLOCK.getValue(new ResourceLocation(s));

			if (block != Blocks.AIR) {
				return new BlockMatch(block);
			}
		}

		return Simple.NONE;
	}

	static BlockStatePredicate of(Object o) {
		if (o == null || o == Simple.ALL) {
			return Simple.ALL;
		} else if (o == Simple.NONE) {
			return Simple.NONE;
		}

		var list = ListJS.orSelf(o);
		if (list.isEmpty()) {
			return Simple.NONE;
		} else if (list.size() > 1) {
			var predicates = new ArrayList<BlockStatePredicate>();

			for (var o1 : list) {
				var p = of(o1);
				if (p == Simple.ALL) {
					return Simple.ALL;
				} else if (p != Simple.NONE) {
					predicates.add(p);
				}
			}

			return predicates.isEmpty() ? Simple.NONE : predicates.size() == 1 ? predicates.get(0) : new OrMatch(predicates);
		}

		var map = MapJS.of(o);
		if (map != null) {
			if (map.isEmpty()) {
				return Simple.ALL;
			}

			var predicates = new ArrayList<BlockStatePredicate>();

			if (map.get("or") != null) {
				predicates.add(of(map.get("or")));
			}

			if (map.get("not") != null) {
				predicates.add(new NotMatch(of(map.get("not"))));
			}

			return new AndMatch(predicates);
		}

		return ofSingle(o);
	}

	static RuleTest ruleTestOf(Context cx, Object o) {
		if (o instanceof RuleTest rule) {
			return rule;
		} else if (o instanceof BlockStatePredicate bsp && bsp.asRuleTest() != null) {
			return bsp.asRuleTest();
		}

		return Optional.ofNullable(NBTUtils.toTagCompound(cx, o))
			.map(tag -> RuleTest.CODEC.parse(NbtOps.INSTANCE, tag))
			.flatMap(DataResult::result)
			.or(() -> Optional.ofNullable(of(o).asRuleTest()))
			.orElseThrow(() -> new IllegalArgumentException("Could not parse valid rule test from " + o + "!"));
	}

	@SuppressWarnings("unchecked")
	private static BlockStatePredicate ofSingle(Object o) {
		if (o instanceof BlockStatePredicate bsp) {
			return bsp;
		} else if (o instanceof Block block) {
			return new BlockMatch(block);
		} else if (o instanceof BlockState state) {
			return new StateMatch(state);
		} else if (o instanceof TagKey tag) {
			return new TagMatch((TagKey<Block>) tag);
		}

		var pattern = RegExpJS.of(o);
		return pattern == null ? BlockStatePredicate.fromString(o.toString()) : new RegexMatch(pattern);
	}

	default Collection<BlockState> getBlockStates() {
		var states = new HashSet<BlockState>();
		for (var state : BlockWrapper.getAllBlockStates()) {
			if (test(state)) {
				states.add(state);
			}
		}
		return states;
	}

	default Collection<Block> getBlocks() {
		var blocks = new HashSet<Block>();
		for (var state : getBlockStates()) {
			blocks.add(state.getBlock());
		}
		return blocks;
	}

	default Set<ResourceLocation> getBlockIds() {
		Set<ResourceLocation> set = new LinkedHashSet<>();

		for (var block : getBlocks()) {
			var blockId = RegistryInfo.BLOCK.getId(block);

			if (blockId != null) {
				set.add(blockId);
			}
		}

		return set;
	}

	default boolean check(List<OreConfiguration.TargetBlockState> targetStates) {
		for (var state : targetStates) {
			if (test(state.state)) {
				return true;
			}
		}

		return false;
	}

	enum Simple implements BlockStatePredicate {
		ALL(true),
		NONE(false);

		private final boolean match;

		Simple(boolean match) {
			this.match = match;
		}

		@Override
		public boolean test(BlockState state) {
			return match;
		}

		@Override
		public boolean testBlock(Block block) {
			return match;
		}

		@Override
		public RuleTest asRuleTest() {
			return match ? AlwaysTrueTest.INSTANCE : AlwaysFalseRuleTest.INSTANCE;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return match ? BlockWrapper.getAllBlockStates() : List.of();
		}
	}

	record BlockMatch(Block block) implements BlockStatePredicate {
		@Override
		public boolean test(BlockState state) {
			return state.is(block);
		}

		@Override
		public boolean testBlock(Block block) {
			return this.block == block;
		}

		@Override
		public Collection<Block> getBlocks() {
			return Collections.singleton(block);
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return block.getStateDefinition().getPossibleStates();
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			var blockId = RegistryInfo.BLOCK.getId(block);
			return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
		}

		@Override
		public RuleTest asRuleTest() {
			return new BlockMatchTest(block);
		}
	}

	record StateMatch(BlockState state) implements BlockStatePredicate {
		@Override
		public boolean test(BlockState s) {
			return state == s;
		}

		@Override
		public boolean testBlock(Block block) {
			return state.getBlock() == block;
		}

		@Override
		public Collection<Block> getBlocks() {
			return Collections.singleton(state.getBlock());
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return Collections.singleton(state);
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			var blockId = RegistryInfo.BLOCK.getId(state.getBlock());
			return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
		}

		@Override
		public RuleTest asRuleTest() {
			return new BlockStateMatchTest(state);
		}
	}

	record TagMatch(TagKey<Block> tag) implements BlockStatePredicate {
		@Override
		public boolean test(BlockState state) {
			return state.is(tag);
		}

		@Override
		public boolean testBlock(Block block) {
			return block.builtInRegistryHolder().is(tag);
		}

		@Override
		public Collection<Block> getBlocks() {
			return Util.make(new LinkedHashSet<>(), set -> {
				for (var holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
					set.add(holder.value());
				}
			});
		}

		@Override
		public RuleTest asRuleTest() {
			return new TagMatchTest(tag);
		}
	}

	final class RegexMatch implements BlockStatePredicate {
		public final Pattern pattern;
		private final LinkedHashSet<Block> matchedBlocks;

		public RegexMatch(Pattern p) {
			pattern = p;
			matchedBlocks = new LinkedHashSet<>();
			for (var state : BlockWrapper.getAllBlockStates()) {
				var block = state.getBlock();
				if (!matchedBlocks.contains(block) && pattern.matcher(block.kjs$getId()).find()) {
					matchedBlocks.add(state.getBlock());
				}
			}
		}

		@Override
		public boolean test(BlockState state) {
			return matchedBlocks.contains(state.getBlock());
		}

		@Override
		public boolean testBlock(Block block) {
			return matchedBlocks.contains(block);
		}

		@Override
		public Collection<Block> getBlocks() {
			return matchedBlocks;
		}

		@Override
		public RuleTest asRuleTest() {
			var test = new AnyMatchRuleTest();
			for (var block : matchedBlocks) {
				test.rules.add(new BlockMatchTest(block));
			}
			return test;
		}
	}

	record OrMatch(List<BlockStatePredicate> list) implements BlockStatePredicate {
		@Override
		public boolean test(BlockState state) {
			for (var predicate : list) {
				if (predicate.test(state)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean testBlock(Block block) {
			for (var predicate : list) {
				if (predicate.testBlock(block)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Collection<Block> getBlocks() {
			var set = new HashSet<Block>();

			for (var predicate : list) {
				set.addAll(predicate.getBlocks());
			}

			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			var set = new HashSet<BlockState>();

			for (var predicate : list) {
				set.addAll(predicate.getBlockStates());
			}

			return set;
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			Set<ResourceLocation> set = new LinkedHashSet<>();

			for (var predicate : list) {
				set.addAll(predicate.getBlockIds());
			}

			return set;
		}

		@Override
		public RuleTest asRuleTest() {
			var test = new AnyMatchRuleTest();
			for (var predicate : list) {
				test.rules.add(predicate.asRuleTest());
			}
			return test;
		}
	}

	final class NotMatch implements BlockStatePredicate {
		private final BlockStatePredicate predicate;
		private final Collection<BlockState> cachedStates;

		public NotMatch(BlockStatePredicate predicate) {
			this.predicate = predicate;

			cachedStates = new LinkedHashSet<>();

			for (var entry : RegistryInfo.BLOCK.entrySet()) {
				for (var state : entry.getValue().getStateDefinition().getPossibleStates()) {
					if (!predicate.test(state)) {
						cachedStates.add(state);
					}
				}
			}
		}

		@Override
		public boolean test(BlockState state) {
			return !predicate.test(state);
		}

		@Override
		public boolean testBlock(Block block) {
			return !predicate.testBlock(block);
		}

		@Override
		public Collection<Block> getBlocks() {
			Set<Block> set = new HashSet<>();
			for (var blockState : getBlockStates()) {
				set.add(blockState.getBlock());
			}
			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return cachedStates;
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			var set = new HashSet<ResourceLocation>();

			for (var block : getBlocks()) {
				set.add(RegistryInfo.BLOCK.getId(block));
			}

			return set;
		}

		@Override
		public RuleTest asRuleTest() {
			return new InvertRuleTest(predicate.asRuleTest());
		}
	}

	final class AndMatch implements BlockStatePredicate {
		private final List<BlockStatePredicate> list;
		private final Collection<BlockState> cachedStates;

		public AndMatch(List<BlockStatePredicate> list) {
			this.list = list;
			cachedStates = new LinkedHashSet<>();

			for (var entry : RegistryInfo.BLOCK.entrySet()) {
				for (var state : entry.getValue().getStateDefinition().getPossibleStates()) {
					var match = true;
					for (var predicate : list) {
						if (!predicate.test(state)) {
							match = false;
							break;
						}
					}
					if (match) {
						cachedStates.add(state);
					}
				}
			}
		}

		@Override
		public boolean test(BlockState state) {
			for (var predicate : list) {
				if (!predicate.test(state)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean testBlock(Block block) {
			for (var predicate : list) {
				if (!predicate.testBlock(block)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public Collection<Block> getBlocks() {
			Set<Block> set = new HashSet<>();
			for (var blockState : getBlockStates()) {
				set.add(blockState.getBlock());
			}
			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return cachedStates;
		}

		@Override
		public RuleTest asRuleTest() {
			var test = new AllMatchRuleTest();
			for (var predicate : list) {
				test.rules.add(predicate.asRuleTest());
			}
			return test;
		}
	}
}
