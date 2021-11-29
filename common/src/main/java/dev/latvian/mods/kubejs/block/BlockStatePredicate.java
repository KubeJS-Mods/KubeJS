package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public abstract class BlockStatePredicate {
	public static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	public static class Empty extends BlockStatePredicate {
		public static final Empty INSTANCE = new Empty();

		@Override
		public boolean check(BlockState state) {
			return false;
		}

		@Override
		public Collection<Block> getBlocks() {
			return Collections.emptyList();
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			return Collections.emptySet();
		}
	}

	public static class FromID extends BlockStatePredicate {
		public final Block block;

		public FromID(Block b) {
			block = b;
		}

		@Override
		public boolean check(BlockState state) {
			return state.is(block);
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
			ResourceLocation blockId = KubeJSRegistries.blocks().getId(block);
			return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
		}
	}

	public static class FromState extends BlockStatePredicate {
		public final BlockState state;

		public FromState(BlockState s) {
			state = s;
		}

		@Override
		public boolean check(BlockState s) {
			return state == s;
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
			ResourceLocation blockId = KubeJSRegistries.blocks().getId(state.getBlock());
			return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
		}
	}

	public static class FromTag extends BlockStatePredicate {
		public final Tag<Block> tag;

		public FromTag(Tag<Block> t) {
			tag = t;
		}

		@Override
		public boolean check(BlockState state) {
			return tag.contains(state.getBlock());
		}

		@Override
		public Collection<Block> getBlocks() {
			return tag.getValues();
		}
	}

	public static class FromRegex extends BlockStatePredicate {
		public final Pattern pattern;
		private final LinkedHashSet<Block> matchedBlocks;

		public FromRegex(Pattern p) {
			pattern = p;
			matchedBlocks = new LinkedHashSet<>();

			for (Map.Entry<ResourceKey<Block>, Block> entry : KubeJSRegistries.blocks().entrySet()) {
				if (pattern.matcher(entry.getKey().location().toString()).find()) {
					matchedBlocks.add(entry.getValue());
				}
			}
		}

		@Override
		public boolean check(BlockState state) {
			return matchedBlocks.contains(state.getBlock());
		}

		@Override
		public Collection<Block> getBlocks() {
			return matchedBlocks;
		}
	}

	public static class FromList extends BlockStatePredicate {
		public final List<BlockStatePredicate> list = new ArrayList<>();

		@Override
		public boolean check(BlockState state) {
			for (var predicate : list) {
				if (predicate.check(state)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Collection<Block> getBlocks() {
			HashSet<Block> set = new HashSet<>();

			for (var predicate : list) {
				set.addAll(predicate.getBlocks());
			}

			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			HashSet<BlockState> set = new HashSet<>();

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
	}

	public static BlockStatePredicate parse(String s) {
		if (s.startsWith("#")) {
			Tag<Block> tag = Tags.blocks().getTag(new ResourceLocation(s.substring(1)));

			if (tag != null) {
				return new FromTag(tag);
			}
		} else if (s.indexOf('[') != -1) {
			BlockState state = UtilsJS.parseBlockState(s);

			if (state != Blocks.AIR.defaultBlockState()) {
				return new FromState(state);
			}
		} else {
			Block block = KubeJSRegistries.blocks().get(new ResourceLocation(s));

			if (block != Blocks.AIR) {
				return new FromID(block);
			}
		}

		return Empty.INSTANCE;
	}

	public static BlockStatePredicate of(Object blocks) {
		BlockStatePredicate.FromList predicate = new BlockStatePredicate.FromList();

		for (var o : ListJS.orSelf(blocks)) {
			BlockStatePredicate p = of0(o);

			if (p != BlockStatePredicate.Empty.INSTANCE) {
				predicate.list.add(p);
			}
		}

		return predicate.list.size() == 1 ? predicate.list.get(0) : predicate.list.isEmpty() ? BlockStatePredicate.Empty.INSTANCE : predicate;
	}

	private static BlockStatePredicate of0(Object o) {
		if (o instanceof Block) {
			return new FromID((Block) o);
		} else if (o instanceof BlockState) {
			return new FromState((BlockState) o);
		} else if (o instanceof Tag) {
			return new FromTag((Tag<Block>) o);
		}

		Pattern pattern = UtilsJS.parseRegex(o);
		return pattern == null ? BlockStatePredicate.parse(o.toString()) : new FromRegex(pattern);
	}

	public abstract boolean check(BlockState state);

	public abstract Collection<Block> getBlocks();

	public Collection<BlockState> getBlockStates() {
		List<BlockState> states = new ArrayList<>();

		for (var block : getBlocks()) {
			states.addAll(block.getStateDefinition().getPossibleStates());
		}

		return states;
	}

	public Set<ResourceLocation> getBlockIds() {
		Set<ResourceLocation> set = new LinkedHashSet<>();

		for (var block : getBlocks()) {
			ResourceLocation blockId = KubeJSRegistries.blocks().getId(block);

			if (blockId != null) {
				set.add(blockId);
			}
		}

		return set;
	}

	public boolean check(List<OreConfiguration.TargetBlockState> targetStates) {
		for (OreConfiguration.TargetBlockState state : targetStates) {
			if (check(state.state)) {
				return true;
			}
		}

		return false;
	}
}
