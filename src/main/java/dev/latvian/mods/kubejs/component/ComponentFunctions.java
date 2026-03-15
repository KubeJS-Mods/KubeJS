package dev.latvian.mods.kubejs.component;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.latvian.mods.kubejs.component.DataComponentWrapper.tryWrapComponent;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface ComponentFunctions {
	default DataComponentMap kjs$getComponentMap() {
		return DataComponentMap.EMPTY;
	}

	@Nullable
	default <T> T kjs$get(DataComponentType<T> type) {
		return kjs$getComponentMap().get(type);
	}

	@HideFromJS
	<T> ComponentFunctions kjs$override(DataComponentType<T> type, @Nullable T value);

	default ComponentFunctions kjs$set(Context cx, DataComponentType<?> component, Object value) {
		var wrapped = tryWrapComponent(cx, component, value)
			.getOrThrow(msg ->
				new KubeRuntimeException("Failed to wrap data component %s from '%s': %s".formatted(component, value, msg))
					.source(SourceLine.of(cx))
			);

		if (wrapped.isPresent()) {
			return kjs$override((DataComponentType) component, wrapped.get());
		} else {
			return kjs$remove(component);
		}
	}

	default ComponentFunctions kjs$remove(DataComponentType<?> type) {
		return kjs$override(type, null);
	}

	default ComponentFunctions kjs$setUnit(DataComponentType<Unit> component) {
		return kjs$override(component, Unit.INSTANCE);
	}

	default ComponentFunctions kjs$set(Context cx, DataComponentMap components) {
		components.forEach(c -> kjs$override(c.type(), Cast.to(c.value())));
		return this;
	}

	default ComponentFunctions kjs$patch(Context cx, DataComponentPatch components) {
		for (var entry : components.entrySet()) {
			kjs$override(entry.getKey(), Cast.to(entry.getValue().orElse(null)));
		}

		return this;
	}

	default ComponentFunctions kjs$resetComponents(Context cx) {
		if (kjs$getComponentMap() instanceof PatchedDataComponentMap map) {
			map.restorePatch(DataComponentPatch.EMPTY);
		}

		return this;
	}

	default String kjs$getComponentString(Context cx) {
		if (kjs$getComponentMap() instanceof PatchedDataComponentMap map) {
			return DataComponentWrapper.patchToString(new StringBuilder(), RegistryAccessContainer.of(cx).nbt(), map.asPatch()).toString();
		}

		return "[]";
	}

	// Helper methods //

	default void kjs$setCustomData(CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			kjs$remove(DataComponents.CUSTOM_DATA);
		} else {
			kjs$override(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}
	}

	default CompoundTag kjs$getCustomData() {
		var d = kjs$get(DataComponents.CUSTOM_DATA);
		return d == null ? new CompoundTag() : d.copyTag();
	}

	default void kjs$setRarity(Rarity rarity) {
		kjs$override(DataComponents.RARITY, rarity);
	}

	default void kjs$setCustomName(@Nullable Component name) {
		if (name != null) {
			kjs$override(DataComponents.CUSTOM_NAME, name);
		} else {
			kjs$remove(DataComponents.CUSTOM_NAME);
		}
	}

	@Nullable
	default Component kjs$getCustomName() {
		return kjs$get(DataComponents.CUSTOM_NAME);
	}

	default void kjs$setLore(List<Component> lines) {
		kjs$override(DataComponents.LORE, new ItemLore(List.copyOf(lines)));
	}

	default void kjs$setLore(List<Component> lines, List<Component> styledLines) {
		kjs$override(DataComponents.LORE, new ItemLore(List.copyOf(lines), List.copyOf(styledLines)));
	}

	default void kjs$setCustomModelData(int data) {
		kjs$override(
			DataComponents.CUSTOM_MODEL_DATA,
			new CustomModelData(
				List.of(),
				List.of(),
				List.of(),
				List.of(data)
			)
		);
	}

	default void kjs$setTooltipHidden() {
		var display = kjs$get(DataComponents.TOOLTIP_DISPLAY);
		if (display == null) {
			display = TooltipDisplay.DEFAULT;
		}
		kjs$override(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, display.hiddenComponents()));
	}

	default void kjs$setAdditionalTooltipHidden() {
		var display = kjs$get(DataComponents.TOOLTIP_DISPLAY);
		if (display == null) {
			display = TooltipDisplay.DEFAULT;
		}

		display = display.withHidden(DataComponents.BANNER_PATTERNS, true);
		display = display.withHidden(DataComponents.BEES, true);
		display = display.withHidden(DataComponents.BLOCK_ENTITY_DATA, true);
		display = display.withHidden(DataComponents.BLOCK_STATE, true);
		display = display.withHidden(DataComponents.WRITTEN_BOOK_CONTENT, true);

		kjs$override(DataComponents.TOOLTIP_DISPLAY, display);
	}


	default void kjs$setGlintOverride(boolean override) {
		kjs$override(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, override);
	}

	default void kjs$setDyedColor(KubeColor color) {
		kjs$override(DataComponents.DYED_COLOR, new DyedItemColor(color.kjs$getRGB()));
	}

	default void kjs$setDyedColorWithTooltip(KubeColor color) {
		kjs$override(DataComponents.DYED_COLOR, new DyedItemColor(color.kjs$getRGB()));
	}

	default void kjs$setPotionContents(PotionContents contents) {
		kjs$override(DataComponents.POTION_CONTENTS, contents);
	}

	default void kjs$setPotionId(Holder<Potion> potion) {
		kjs$override(DataComponents.POTION_CONTENTS, new PotionContents(potion));
	}

	default void kjs$setEntityData(CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			kjs$remove(DataComponents.ENTITY_DATA);
			return;
		}

		var id = tag.getString("id");
		if (id.isEmpty()) {
			throw new IllegalArgumentException("ENTITY_DATA tag must contain non-empty \"id\"");
		}

		var type = EntityType.byString(id.get()).orElseThrow(() -> new IllegalArgumentException("Unknown entity id: " + id));
		kjs$override(DataComponents.ENTITY_DATA, TypedEntityData.of(type, tag));
	}


	default void kjs$setProfile(GameProfile profile) {
		kjs$override(DataComponents.PROFILE, ResolvableProfile.createResolved(profile));
	}

	default void kjs$setProfile(@Nullable String name, @Nullable UUID uuid) {
		var cleanName = name != null && name.isBlank() ? null : name;
		var cleanUuid = uuid != null && uuid.getLeastSignificantBits() == 0L && uuid.getMostSignificantBits() == 0L ? null : uuid;

		if (cleanUuid != null) {
			kjs$override(DataComponents.PROFILE, ResolvableProfile.createUnresolved(cleanUuid));
		} else if (cleanName != null) {
			kjs$override(DataComponents.PROFILE, ResolvableProfile.createUnresolved(cleanName));
		} else {
			kjs$remove(DataComponents.PROFILE);
		}
	}


	default void kjs$setBaseColor(DyeColor color) {
		kjs$override(DataComponents.BASE_COLOR, color);
	}

	default void kjs$setBlockStateProperties(Map<String, String> properties) {
		kjs$override(DataComponents.BLOCK_STATE, new BlockItemStateProperties(properties));
	}

	default void kjs$setLockCode(ItemPredicate lock) {
		kjs$override(DataComponents.LOCK, new LockCode(lock));
	}

	default void kjs$setContainerLootTable(ResourceKey<LootTable> lootTable) {
		kjs$override(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(lootTable, 0L));
	}

	default void kjs$setContainerLootTable(ResourceKey<LootTable> lootTable, long seed) {
		kjs$override(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(lootTable, seed));
	}
}
