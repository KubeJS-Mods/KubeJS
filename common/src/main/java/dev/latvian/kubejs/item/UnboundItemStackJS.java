package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.CompoundTagWrapper;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class UnboundItemStackJS extends ItemStackJS {
	private final String item;
	private final ResourceLocation itemRL;
	private int count;
	private CompoundTagWrapper nbt;
	private ItemStack cached;

	public UnboundItemStackJS(ResourceLocation i) {
		item = i.toString();
		itemRL = i;
		count = 1;
		nbt = null;
		cached = null;

		if (RecipeJS.itemErrors && !KubeJSRegistries.items().contains(i)) {
			throw new RecipeExceptionJS("Item '" + item + "' not found!").error();
		}
	}

	@Override
	public Item getItem() {
		return KubeJSRegistries.items().get(new ResourceLocation(item));
	}

	@Override
	public ItemStack getItemStack() {
		if (cached == null) {
			Item i = getItem();

			if (i == Items.AIR) {
				return ItemStack.EMPTY;
			}

			cached = new ItemStack(i, count);

			if (nbt != null) {
				cached.setTag(nbt.minecraftTag);
			}
		}

		return cached;
	}

	@Override
	public String getId() {
		return item;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() || getItem() == Items.AIR;
	}

	@Override
	public UnboundItemStackJS copy() {
		UnboundItemStackJS stack = new UnboundItemStackJS(itemRL);
		stack.count = count;
		stack.nbt = nbt == null ? null : new CompoundTagWrapper(nbt.minecraftTag.copy());
		stack.setChance(getChance());
		return stack;
	}

	@Override
	public void setCount(int c) {
		count = Math.max(c, 0);
		cached = null;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	@Nullable
	public CompoundTagWrapper getNbt() {
		return nbt;
	}

	@Override
	public ItemStackJS removeNBT() {
		UnboundItemStackJS stack = new UnboundItemStackJS(itemRL);
		stack.count = count;
		stack.nbt = null;
		stack.setChance(getChance());
		return stack;
	}

	@Override
	public ItemStackJS withNBT(CompoundTag tag) {
		UnboundItemStackJS is = copy();

		if (tag == null) {
			is.nbt = null;
		} else {
			CompoundTagWrapper nbt0 = is.getNbt();

			if (nbt0 == null) {
				is.nbt = new CompoundTagWrapper(tag);
				is.nbt.listener = this;
			} else {
				for (String key : tag.getAllKeys()) {
					nbt0.minecraftTag.put(key, tag.get(key));
				}
			}
		}

		return is;
	}

	@Override
	public boolean hasNBT() {
		return nbt != null;
	}

	@Override
	public boolean areItemsEqual(ItemStackJS stack) {
		if (stack instanceof UnboundItemStackJS) {
			return this == stack || itemRL.equals(((UnboundItemStackJS) stack).itemRL);
		}

		return getItem() == stack.getItem();
	}

	@Override
	public boolean areItemsEqual(ItemStack stack) {
		return itemRL.equals(Registries.getId(stack.getItem(), Registry.ITEM_REGISTRY));
	}

	@Override
	public void onChanged(@Nullable Tag o) {
		cached = null;
	}

	@Override
	public String getMod() {
		return itemRL.getNamespace();
	}

	@Override
	public ItemStackJS enchant(Enchantment enchantment, int level) {
		return super.enchant(enchantment, level);
	}
}
