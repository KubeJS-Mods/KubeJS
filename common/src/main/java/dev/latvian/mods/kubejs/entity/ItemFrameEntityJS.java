package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class ItemFrameEntityJS extends EntityJS {
	private final ItemFrame itemFrameEntity;

	public ItemFrameEntityJS(LevelJS l, ItemFrame e) {
		super(l, e);
		itemFrameEntity = e;
	}

	@Override
	public boolean isFrame() {
		return true;
	}

	@Override
	@Nullable
	public ItemStackJS getItem() {
		var stack = itemFrameEntity.getItem();
		return stack.isEmpty() ? null : ItemStackJS.of(stack);
	}

	public void setItem(Object item) {
		itemFrameEntity.setItem(ItemStackJS.of(item).getItemStack());
	}

	public int getFrameRotation() {
		return itemFrameEntity.getRotation();
	}

	public void setFrameRotation(int rotation) {
		itemFrameEntity.setRotation(rotation);
	}
}
