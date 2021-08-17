package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.CompoundTagWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public class BoundItemStackJS extends ItemStackJS {
	private final ItemStack stack;

	public BoundItemStackJS(ItemStack is) {
		stack = is;
	}

	@Override
	public Item getItem() {
		return stack.getItem();
	}

	@Override
	public ItemStack getItemStack() {
		return stack;
	}

	@Override
	public ItemStackJS copy() {
		return new BoundItemStackJS(stack.copy()).withChance(getChance());
	}

	@Override
	public void setCount(int c) {
		stack.setCount(c);
	}

	@Override
	public int getCount() {
		return stack.getCount();
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public ItemStackJS withCount(int c) {
		if (c <= 0) {
			return EmptyItemStackJS.INSTANCE;
		}

		ItemStack is = stack.copy();
		is.setCount(c);
		return new BoundItemStackJS(is).withChance(getChance());
	}

	@Override
	@Nullable
	public CompoundTagWrapper getNbt() {
		if (stack.getTag() != null) {
			CompoundTagWrapper wrapper = new CompoundTagWrapper(stack.getTag());
			wrapper.listener = this;
			return wrapper;
		}

		return null;
	}

	@Override
	public boolean hasNBT() {
		return stack.hasTag();
	}

	@Override
	@Nullable
	public CompoundTag getMinecraftNbt() {
		return stack.getTag();
	}

	@Override
	public ItemStackJS removeNBT() {
		ItemStack is = stack.copy();
		is.setTag(null);
		return new BoundItemStackJS(is).withChance(getChance());
	}

	@Override
	public ItemStackJS withNBT(CompoundTag o) {
		ItemStack is = stack.copy();

		if (is.getTag() == null) {
			is.setTag(o);
		} else {
			if (o != null && !o.isEmpty()) {
				for (String key : o.getAllKeys()) {
					is.getTag().put(key, o.get(key));
				}
			}
		}

		return new BoundItemStackJS(is).withChance(getChance());
	}

	@Override
	public ItemStackJS withName(@Nullable Component displayName) {
		ItemStack is = stack.copy();

		if (displayName != null) {
			is.setHoverName(displayName);
		} else {
			is.resetHoverName();
		}

		return new BoundItemStackJS(is);
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return stack instanceof BoundItemStackJS ? testVanilla(((BoundItemStackJS) stack).stack) : super.test(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack2) {
		if (stack.getItem() == stack2.getItem()) {
			CompoundTag nbt = stack.getTag();
			CompoundTag nbt2 = stack2.getTag();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	@Override
	public boolean isNBTEqual(ItemStackJS stack2) {
		if (hasNBT() == stack2.hasNBT()) {
			CompoundTag nbt = stack.getTag();
			CompoundTag nbt2 = stack2.getMinecraftNbt();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	@Override
	public boolean isNBTEqual(ItemStack stack2) {
		if (hasNBT() == stack2.hasTag()) {
			CompoundTag nbt = stack.getTag();
			CompoundTag nbt2 = stack2.getTag();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	@Override
	public void onChanged(@Nullable Tag o) {
		if (o == null || o instanceof CompoundTag) {
			stack.setTag((CompoundTag) o);
		}
	}

	@Override
	public boolean hasEnchantment(Enchantment enchantment, int level) {
		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) >= level;
	}

	@Override
	public ItemStackJS enchant(Enchantment enchantment, int level) {
		ItemStack is = stack.copy();

		if (is.getItem() == Items.ENCHANTED_BOOK) {
			EnchantedBookItem.addEnchantment(is, new EnchantmentInstance(enchantment, level));
		} else {
			is.enchant(enchantment, level);
		}
		return new BoundItemStackJS(is).withChance(getChance());
	}
}