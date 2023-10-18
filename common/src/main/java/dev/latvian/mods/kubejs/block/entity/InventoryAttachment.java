package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.typings.desc.PrimitiveDescJS;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.rhino.mod.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InventoryAttachment extends SimpleContainer implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(
		"inventory",
		TypeDescJS.object()
			.add("xsize", TypeDescJS.NUMBER, false)
			.add("ysize", TypeDescJS.NUMBER, false)
			.add("inputFilter", new PrimitiveDescJS("Ingredient"), true),
		map -> {
			var width = ((Number) map.get("width")).intValue();
			var height = ((Number) map.get("height")).intValue();
			var inputFilter = map.containsKey("inputFilter") ? IngredientJS.of(map.get("inputFilter")) : null;
			return entity -> new InventoryAttachment(entity, width, height, inputFilter);
		}
	);

	public final int width, height;
	public final BlockEntityJS blockEntity;
	public final Ingredient inputFilter;

	public InventoryAttachment(BlockEntityJS blockEntity, int width, int height, @Nullable Ingredient inputFilter) {
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
	public CompoundTag writeAttachment() {
		var tag = new CompoundTag();
		var list = new ListTag();

		for (int i = 0; i < getContainerSize(); i++) {
			var stack = getItem(i);

			if (!stack.isEmpty()) {
				var itemTag = new CompoundTag();
				itemTag.putByte("Slot", (byte) i);
				stack.save(itemTag);
				list.add(itemTag);
			}
		}

		tag.put("items", list);
		return tag;
	}

	@Override
	public void readAttachment(CompoundTag tag) {
		for (int i = 0; i < getContainerSize(); ++i) {
			removeItemNoUpdate(i);
		}

		var list = tag.getList("items", NbtType.COMPOUND);

		for (int i = 0; i < list.size(); ++i) {
			var itemTag = list.getCompound(i);
			var slot = itemTag.getByte("Slot");

			if (slot >= 0 && slot < getContainerSize()) {
				setItem(slot, ItemStack.of(itemTag));
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
