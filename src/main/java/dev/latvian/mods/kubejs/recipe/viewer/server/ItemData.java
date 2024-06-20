package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.MutableBoolean;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public record ItemData(
	List<ItemStack> addedEntries,
	boolean removeAll,
	List<Ingredient> removedEntries,
	List<Ingredient> directlyRemovedEntries,
	List<Group> groupedEntries,
	List<Info> info
) {
	public record Group(ResourceLocation groupId, Component description, Ingredient filter) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Group> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, Group::groupId,
			ComponentSerialization.STREAM_CODEC, Group::description,
			Ingredient.CONTENTS_STREAM_CODEC, Group::filter,
			Group::new
		);
	}

	public record Info(Ingredient filter, List<Component> info) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Info> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, Info::filter,
			ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), Info::info,
			Info::new
		);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemData> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::addedEntries,
		ByteBufCodecs.BOOL, ItemData::removeAll,
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::removedEntries,
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::directlyRemovedEntries,
		Group.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::groupedEntries,
		Info.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::info,
		ItemData::new
	);

	public static ItemData collect() {
		var addedEntries = new ArrayList<ItemStack>();
		var removeAll = new MutableBoolean(false);
		var removedEntries = new ArrayList<Ingredient>();
		var directlyRemovedEntries = new ArrayList<Ingredient>();
		var groupedEntries = new ArrayList<Group>();
		var info = new ArrayList<Info>();

		if (RecipeViewerEvents.ADD_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerAddItemEntriesKubeEvent(addedEntries));
		}

		if (RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerRemoveItemEntriesKubeEvent(removedEntries, directlyRemovedEntries, removeAll));
		}

		if (RecipeViewerEvents.GROUP_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.GROUP_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerGroupItemEntriesKubeEvent(groupedEntries));
		}

		if (RecipeViewerEvents.ADD_INFORMATION.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerAddItemInformationKubeEvent(info));
		}

		return new ItemData(
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
