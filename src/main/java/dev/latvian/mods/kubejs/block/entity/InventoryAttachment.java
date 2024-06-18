package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InventoryAttachment extends SimpleContainer implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(
		"inventory",
		JSObjectTypeInfo.of(
			new JSOptionalParam("xsize", TypeInfo.INT),
			new JSOptionalParam("ysize", TypeInfo.INT),
			new JSOptionalParam("inputFilter", IngredientJS.TYPE_INFO)
		),
		(cx, map) -> {
			var width = ScriptRuntime.toInt32(cx, map.get("width"));
			var height = ScriptRuntime.toInt32(cx, map.get("height"));
			var inputFilter = map.containsKey("inputFilter") ? ItemPredicate.wrap(cx, map.get("inputFilter")) : null;
			return new InventoryAttachmentFactory(width, height, inputFilter);
		}
	);

	private record InventoryAttachmentFactory(int width, int height, @Nullable ItemPredicate inputFilter) implements Factory {
		@Override
		public BlockEntityAttachment create(BlockEntityJS entity) {
			return new InventoryAttachment(entity, width, height, inputFilter);
		}
	}

	public final int width, height;
	public final BlockEntityJS blockEntity;
	public final ItemPredicate inputFilter;

	public InventoryAttachment(BlockEntityJS blockEntity, int width, int height, @Nullable ItemPredicate inputFilter) {
		super(width * height);
		this.width = width;
		this.height = height;
		this.blockEntity = blockEntity;
		this.inputFilter = inputFilter;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		blockEntity.save();
	}

	@Override
	public CompoundTag writeAttachment(HolderLookup.Provider registries) {
		var tag = new CompoundTag();
		var list = new ListTag();

		for (int i = 0; i < getContainerSize(); i++) {
			var stack = getItem(i);

			if (!stack.isEmpty()) {
				var itemTag = (CompoundTag) stack.save(registries, new CompoundTag());
				itemTag.putByte("slot", (byte) i);
				list.add(itemTag);
			}
		}

		tag.put("items", list);
		return tag;
	}

	@Override
	public void readAttachment(HolderLookup.Provider registries, CompoundTag tag) {
		for (int i = 0; i < getContainerSize(); ++i) {
			removeItemNoUpdate(i);
		}

		var list = tag.getList("items", Tag.TAG_COMPOUND);

		for (int i = 0; i < list.size(); ++i) {
			var itemTag = list.getCompound(i);
			var slot = itemTag.getByte("slot");

			if (slot >= 0 && slot < getContainerSize()) {
				setItem(slot, ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY));
			}
		}
	}

	@Override
	public void onRemove(BlockState newState) {
		Containers.dropContents(blockEntity.getLevel(), blockEntity.getBlockPos(), this);
	}

	@Override
	public boolean canAddItem(ItemStack itemStack) {
		return (inputFilter == null || inputFilter.test(itemStack)) && super.canAddItem(itemStack);
	}

	@Override
	public boolean canPlaceItem(int i, ItemStack itemStack) {
		return (inputFilter == null || inputFilter.test(itemStack)) && super.canPlaceItem(i, itemStack);
	}

	@Override
	public boolean stillValid(Player player) {
		return !blockEntity.isRemoved();
	}

	@Override
	public int kjs$getWidth() {
		return width;
	}

	@Override
	public int kjs$getHeight() {
		return height;
	}
}
