package dev.latvian.mods.kubejs.entity;

import dev.architectury.hooks.level.entity.ItemEntityHooks;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ItemEntityJS extends EntityJS {
	private final ItemEntity itemEntity;

	public ItemEntityJS(ItemEntity e) {
		super(e);
		itemEntity = e;
	}

	@Override
	@Nullable
	public ItemStackJS getItem() {
		var stack = itemEntity.getItem();
		return stack.isEmpty() ? null : ItemStackJS.of(stack);
	}

	public void setItem(Object item) {
		itemEntity.setItem(ItemStackJS.of(item).getItemStack());
	}

	public int getLifespan() {
		return ItemEntityHooks.lifespan(itemEntity).getAsInt();
	}

	public void setLifespan(int lifespan) {
		ItemEntityHooks.lifespan(itemEntity).accept(lifespan);
	}

	@Nullable
	public UUID getOwner() {
		return itemEntity.getOwner();
	}

	public void setOwner(UUID owner) {
		itemEntity.setOwner(owner);
	}

	@Nullable
	public UUID getThrower() {
		return itemEntity.getThrower();
	}

	public void setThrower(UUID thrower) {
		itemEntity.setThrower(thrower);
	}

	public void setDefaultPickupDelay() {
		setPickupDelay(10);
	}

	public void setNoPickupDelay() {
		setPickupDelay(0);
	}

	public void setInfinitePickupDelay() {
		setPickupDelay(Short.MAX_VALUE);
	}

	public void setPickupDelay(int ticks) {
		itemEntity.setPickUpDelay(ticks);
	}

	public void setNoDespawn() {
		itemEntity.setUnlimitedLifetime();
	}

	public int getAge() {
		return itemEntity.age;
	}

	public void setAge(int age) {
		itemEntity.age = age;
	}

	public int getTicksUntilDespawn() {
		return ItemEntity.LIFETIME - itemEntity.age;
	}

	public void setTicksUntilDespawn(int ticks) {
		setAge(ItemEntity.LIFETIME - ticks);
	}
}