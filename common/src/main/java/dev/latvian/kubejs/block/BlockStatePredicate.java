package dev.latvian.kubejs.block;

import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public abstract class BlockStatePredicate
{
	public static class Empty extends BlockStatePredicate
	{
		public static final Empty INSTANCE = new Empty();

		@Override
		public boolean check(BlockState state)
		{
			return false;
		}

		@Override
		public Collection<Block> getBlocks()
		{
			return Collections.emptyList();
		}
	}

	public static class FromID extends BlockStatePredicate
	{
		public final Block block;

		public FromID(Block b)
		{
			block = b;
		}

		@Override
		public boolean check(BlockState state)
		{
			return state.is(block);
		}

		@Override
		public Collection<Block> getBlocks()
		{
			return Collections.singleton(block);
		}

		@Override
		public Collection<BlockState> getBlockStates()
		{
			return block.getStateDefinition().getPossibleStates();
		}
	}

	public static class FromState extends BlockStatePredicate
	{
		public final BlockState state;

		public FromState(BlockState s)
		{
			state = s;
		}

		@Override
		public boolean check(BlockState s)
		{
			return state == s;
		}

		@Override
		public Collection<Block> getBlocks()
		{
			return Collections.singleton(state.getBlock());
		}

		@Override
		public Collection<BlockState> getBlockStates()
		{
			return Collections.singleton(state);
		}
	}

	public static class FromTag extends BlockStatePredicate
	{
		public final Tag<Block> tag;

		public FromTag(Tag<Block> t)
		{
			tag = t;
		}

		@Override
		public boolean check(BlockState state)
		{
			return tag.contains(state.getBlock());
		}

		@Override
		public Collection<Block> getBlocks()
		{
			return tag.getValues();
		}
	}

	public static class FromRegex extends BlockStatePredicate
	{
		public final Pattern pattern;
		private final LinkedHashSet<Block> matchedBlocks;

		public FromRegex(Pattern p)
		{
			pattern = p;
			matchedBlocks = new LinkedHashSet<>();

			for (Map.Entry<ResourceKey<Block>, Block> entry : Registry.BLOCK.entrySet())
			{
				if (pattern.matcher(entry.getKey().location().toString()).find())
				{
					matchedBlocks.add(entry.getValue());
				}
			}
		}

		@Override
		public boolean check(BlockState state)
		{
			return matchedBlocks.contains(state.getBlock());
		}

		@Override
		public Collection<Block> getBlocks()
		{
			return matchedBlocks;
		}
	}

	public static class FromList extends BlockStatePredicate
	{
		public final List<BlockStatePredicate> list = new ArrayList<>();

		@Override
		public boolean check(BlockState state)
		{
			for (BlockStatePredicate predicate : list)
			{
				if (predicate.check(state))
				{
					return true;
				}
			}

			return false;
		}

		@Override
		public Collection<Block> getBlocks()
		{
			HashSet<Block> set = new HashSet<>();

			for (BlockStatePredicate predicate : list)
			{
				set.addAll(predicate.getBlocks());
			}

			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates()
		{
			HashSet<BlockState> set = new HashSet<>();

			for (BlockStatePredicate predicate : list)
			{
				set.addAll(predicate.getBlockStates());
			}

			return set;
		}
	}

	public static BlockStatePredicate parse(String s)
	{
		if (s.startsWith("#"))
		{
			Tag<Block> tag = SerializationTags.getInstance().getBlocks().getTag(new ResourceLocation(s.substring(1)));

			if (tag != null)
			{
				return new FromTag(tag);
			}
		}
		else if (s.indexOf('[') != -1)
		{
			BlockState state = UtilsJS.parseBlockState(s);

			if (state != Blocks.AIR.defaultBlockState())
			{
				return new FromState(state);
			}
		}
		else
		{
			Block block = Registry.BLOCK.get(new ResourceLocation(s));

			if (block != Blocks.AIR)
			{
				return new FromID(block);
			}
		}

		return Empty.INSTANCE;
	}

	public static BlockStatePredicate of(Object blocks)
	{
		BlockStatePredicate.FromList predicate = new BlockStatePredicate.FromList();

		for (Object o : ListJS.orSelf(blocks))
		{
			Pattern pattern = UtilsJS.parseRegex(o);
			BlockStatePredicate p = pattern == null ? BlockStatePredicate.parse(o.toString()) : new FromRegex(pattern);

			if (p != BlockStatePredicate.Empty.INSTANCE)
			{
				predicate.list.add(p);
			}
		}

		return predicate.list.size() == 1 ? predicate.list.get(0) : predicate.list.isEmpty() ? BlockStatePredicate.Empty.INSTANCE : predicate;
	}

	public abstract boolean check(BlockState state);

	public abstract Collection<Block> getBlocks();

	public Collection<BlockState> getBlockStates()
	{
		List<BlockState> states = new ArrayList<>();

		for (Block block : getBlocks())
		{
			states.addAll(block.getStateDefinition().getPossibleStates());
		}

		return states;
	}
}
