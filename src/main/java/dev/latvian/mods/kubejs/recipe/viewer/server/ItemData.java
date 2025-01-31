package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.component.DataComponentType;
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
	List<Ingredient> removedEntries,
	List<Ingredient> completelyRemovedEntries,
	List<Group> groupedEntries,
	List<Info> info,
	List<DataComponentSubtypes> dataComponentSubtypes
) {
	public record Group(Ingredient filter, ResourceLocation groupId, Component description) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Group> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, Group::filter,
			ResourceLocation.STREAM_CODEC, Group::groupId,
			ComponentSerialization.STREAM_CODEC, Group::description,
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

	public record DataComponentSubtypes(Ingredient filter, List<DataComponentType<?>> components) {
		public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentSubtypes> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, DataComponentSubtypes::filter,
			DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()), DataComponentSubtypes::components,
			DataComponentSubtypes::new
		);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemData> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::addedEntries,
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::removedEntries,
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::completelyRemovedEntries,
		Group.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::groupedEntries,
		Info.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::info,
		DataComponentSubtypes.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemData::dataComponentSubtypes,
		ItemData::new
	);

	public static ItemData collect() {
		var addedEntries = new ArrayList<ItemStack>();
		var removedEntries = new ArrayList<Ingredient>();
		var completelyRemovedEntries = new ArrayList<Ingredient>();
		var groupedEntries = new ArrayList<Group>();
		var info = new ArrayList<Info>();
		var dataComponentSubtypes = new ArrayList<DataComponentSubtypes>();

		if (RecipeViewerEvents.ADD_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerAddItemEntriesKubeEvent(addedEntries));
		}

		if (RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerRemoveItemEntriesKubeEvent(removedEntries));
		}

		if (RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerRemoveItemEntriesKubeEvent(completelyRemovedEntries));
		}

		if (RecipeViewerEvents.GROUP_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.GROUP_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerGroupItemEntriesKubeEvent(groupedEntries));
		}

		if (RecipeViewerEvents.ADD_INFORMATION.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerAddItemInformationKubeEvent(info));
		}

		if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerRegisterItemSubtypesKubeEvent(dataComponentSubtypes));
		}

		return new ItemData(
			List.copyOf(addedEntries),
			List.copyOf(removedEntries),
			List.copyOf(completelyRemovedEntries),
			List.copyOf(groupedEntries),
			List.copyOf(info),
			List.copyOf(dataComponentSubtypes)
		);
	}

	public boolean isEmpty() {
		return addedEntries.isEmpty() && removedEntries.isEmpty() && completelyRemovedEntries.isEmpty() && groupedEntries.isEmpty() && info.isEmpty() && dataComponentSubtypes.isEmpty();
	}
}
