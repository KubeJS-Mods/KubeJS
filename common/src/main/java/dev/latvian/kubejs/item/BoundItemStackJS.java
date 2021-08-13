package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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
	public MapJS getNbt() {
		MapJS nbt = MapJS.of(stack.getTag());

		if (nbt == null) {
			nbt = new MapJS();
		}

		nbt.changeListener = this;
		return nbt;
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
	public ItemStackJS withNBT(Object o) {
		ItemStack is = stack.copy();

		if (is.getTag() == null) {
			is.setTag(MapJS.nbt(o));
		} else {
			CompoundTag c = MapJS.nbt(o);

			if (c != null && !c.isEmpty()) {
				for (String key : c.getAllKeys()) {
					is.getTag().put(key, c.get(key));
				}
			}
		}

		return new BoundItemStackJS(is).withChance(getChance());
	}

	@Override
	public void setName(@Nullable Component displayName) {
		stack.setHoverName(displayName);
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
			CompoundTag nbt2 = MapJS.nbt(stack2.getNbt());
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
	public void onChanged(@Nullable MapJS o) {
		stack.setTag(MapJS.nbt(o));
	}

	@Override
	public ItemStackJS enchant(MapJS enchantments) {
		for (Map.Entry<String, Object> entry : enchantments.entrySet()) {
			Enchantment enchantment = KubeJSRegistries.enchantments().get(UtilsJS.getMCID(entry.getKey()));

			if (enchantment != null && entry.getValue() instanceof Number) {
				stack.enchant(enchantment, ((Number) entry.getValue()).intValue());
			}
		}

		return this;
	}

	@Override
	public ItemStackJS enchant(Enchantment enchantment, int level) {
		stack.enchant(enchantment, level);
		return this;
	}
}