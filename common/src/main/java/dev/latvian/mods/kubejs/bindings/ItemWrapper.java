package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.FireworksJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Info("Various item related helper methods")
public interface ItemWrapper {
	UUID KJS_BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	UUID KJS_BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	UUID[] KJS_ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

	@Info("Returns an ItemStack of the input")
	static ItemStack of(ItemStack in) {
		return in;
	}

	@Info("Returns an ItemStack of the input, with the specified count")
	static ItemStack of(ItemStack in, int count) {
		return in.kjs$withCount(count);
	}

	@Info("Returns an ItemStack of the input, with the specified NBT data")
	static ItemStack of(ItemStack in, CompoundTag tag) {
		return in.kjs$withNBT(tag);
	}

	@Info("Returns an ItemStack of the input, with the specified count and NBT data")
	static ItemStack of(ItemStack in, int count, CompoundTag nbt) {
		var is = in.kjs$withNBT(nbt);
		is.setCount(count);
		return is;
	}

	@Info("Returns an ItemStack of the input, with the specified NBT data")
	static ItemStack withNBT(ItemStack in, CompoundTag nbt) {
		return in.kjs$withNBT(nbt);
	}

	@Info("Get a list of most items in the game. Items not in a creative tab are ignored")
	static List<ItemStack> getList() {
		return ItemStackJS.getList();
	}

	@Info("Get a list of all the item ids in the game")
	static List<String> getTypeList() {
		return ItemStackJS.getTypeList();
	}

	static Map<ResourceLocation, Collection<ItemStack>> getTypeToStackMap() {
		return ItemStackJS.getTypeToStacks();
	}

	static Collection<ItemStack> getVariants(ItemStack item) {
		return getTypeToStackMap().get(item.kjs$getIdLocation());
	}

	@Info("Get the item that represents air/an empty slot")
	static ItemStack getEmpty() {
		return ItemStack.EMPTY;
	}

	@Info("Returns a Firework with the input properties")
	static FireworksJS fireworks(Map<String, Object> properties) {
		return FireworksJS.of(properties);
	}

	@Info("Gets an Item from an item id")
	static Item getItem(ResourceLocation id) {
		return RegistryInfo.ITEM.getValue(id);
	}

	@Nullable
	@Info("Gets an items id from the Item")
	static ResourceLocation getId(Item item) {
		return RegistryInfo.ITEM.getId(item);
	}

	@Info("Checks if the provided item id exists in the registry")
	static boolean exists(ResourceLocation id) {
		return RegistryInfo.ITEM.hasValue(id);
	}

	@Info("""
		Checks if the passed in object is an ItemStack.
		Note that this does not mean it will not function as an ItemStack if passed to something that requests one.
		""")
	static boolean isItem(@Nullable Object o) {
		return o instanceof ItemStackJS;
	}
}