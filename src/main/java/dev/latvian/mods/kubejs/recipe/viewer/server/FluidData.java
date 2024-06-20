package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.util.MutableBoolean;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.List;

public record FluidData(
	List<FluidStack> addedEntries,
	boolean removeAll,
	List<FluidIngredient> removedEntries,
	List<FluidIngredient> directlyRemovedEntries,
	List<Group> groupedEntries,
	List<Info> info
) {
	public record Group(ResourceLocation groupId, Component description, FluidIngredient filter) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Group> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, Group::groupId,
			ComponentSerialization.STREAM_CODEC, Group::description,
			FluidIngredient.STREAM_CODEC, Group::filter,
			Group::new
		);
	}

	public record Info(FluidIngredient ingredient, List<Component> info) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Info> STREAM_CODEC = StreamCodec.composite(
			FluidIngredient.STREAM_CODEC, Info::ingredient,
			ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), Info::info,
			Info::new
		);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidData> STREAM_CODEC = StreamCodec.composite(
		FluidStack.STREAM_CODEC.apply(ByteBufCodecs.list()), FluidData::addedEntries,
		ByteBufCodecs.BOOL, FluidData::removeAll,
		FluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), FluidData::removedEntries,
		FluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), FluidData::directlyRemovedEntries,
		Group.STREAM_CODEC.apply(ByteBufCodecs.list()), FluidData::groupedEntries,
		Info.STREAM_CODEC.apply(ByteBufCodecs.list()), FluidData::info,
		FluidData::new
	);

	public static FluidData collect() {
		var addedEntries = new ArrayList<FluidStack>();
		var removeAll = new MutableBoolean(false);
		var removedEntries = new ArrayList<FluidIngredient>();
		var directlyRemovedEntries = new ArrayList<FluidIngredient>();
		var groupedEntries = new ArrayList<Group>();
		var info = new ArrayList<Info>();

		return new FluidData(
			List.copyOf(addedEntries),
			removeAll.value,
			List.copyOf(removedEntries),
			List.copyOf(directlyRemovedEntries),
			List.copyOf(groupedEntries),
			List.copyOf(info)
		);
	}

	public boolean isEmpty() {
		return addedEntries.isEmpty() && !removeAll && removedEntries.isEmpty() && directlyRemovedEntries.isEmpty() && groupedEntries.isEmpty() && info.isEmpty();
	}
}