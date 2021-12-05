package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.block.ToolType;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.GroupIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IgnoreNBTIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.RegexIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.WeakNBTIngredientJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.text.Text;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.world.BlockContainerJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.ChangeListener;
import dev.latvian.mods.rhino.mod.util.CompoundTagWrapper;
import dev.latvian.mods.rhino.mod.util.NBTSerializable;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.mod.util.NbtType;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ItemStackJS implements IngredientJS, NBTSerializable, ChangeListener<Tag>, SpecialEquality {
	public static final ItemStackJS EMPTY = new ItemStackJS(ItemStack.EMPTY) {
		@Override
		public String getId() {
			return "minecraft:air";
		}

		@Override
		public Collection<ResourceLocation> getTags() {
			return Collections.emptySet();
		}

		@Override
		public boolean hasTag(ResourceLocation tag) {
			return false;
		}

		@Override
		public Item getItem() {
			return Items.AIR;
		}

		@Override
		public ItemStackJS copy() {
			return this;
		}

		@Override
		public void setCount(int c) {
		}

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public ItemStackJS withCount(int c) {
			return this;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		@Nullable
		public CompoundTag getNbt() {
			return null;
		}

		@Override
		public void setNbt(@Nullable CompoundTag tag) {
		}

		@Override
		public boolean hasNBT() {
			return false;
		}

		@Override
		public String getNbtString() {
			return "null";
		}

		@Override
		public ItemStackJS removeNBT() {
			return this;
		}

		@Override
		public ItemStackJS withNBT(CompoundTag nbt) {
			return this;
		}

		@Override
		public void setChance(double c) {
		}

		@Override
		public double getChance() {
			return Double.NaN;
		}

		@Override
		public boolean hasChance() {
			return false;
		}

		public String toString() {
			return "Item.empty";
		}

		@Override
		public boolean test(ItemStackJS other) {
			return false;
		}

		@Override
		public boolean testVanilla(ItemStack other) {
			return false;
		}

		@Override
		public boolean testVanillaItem(Item item) {
			return false;
		}

		@Override
		public Set<ItemStackJS> getStacks() {
			return Collections.emptySet();
		}

		@Override
		public Set<Item> getVanillaItems() {
			return Collections.emptySet();
		}

		@Override
		public ItemStackJS getFirst() {
			return this;
		}

		@Override
		public IngredientJS not() {
			return MatchAllIngredientJS.INSTANCE;
		}

		@Override
		public ItemStackJS withName(@Nullable Component displayName) {
			return this;
		}

		@Override
		public MapJS getEnchantments() {
			return new MapJS() {
				@Override
				protected boolean setChangeListener(@Nullable Object v) {
					return false;
				}
			};
		}

		@Override
		public boolean hasEnchantment(Enchantment enchantment, int level) {
			return false;
		}

		@Override
		public ItemStackJS enchant(MapJS map) {
			return this;
		}

		@Override
		public ItemStackJS enchant(Enchantment enchantment, int level) {
			return this;
		}

		@Override
		public String getMod() {
			return "minecraft";
		}

		@Override
		public boolean areItemsEqual(ItemStackJS other) {
			return other.isEmpty();
		}

		@Override
		public boolean areItemsEqual(ItemStack other) {
			return other.isEmpty();
		}

		@Override
		public boolean isNBTEqual(ItemStackJS other) {
			return !other.hasNBT();
		}

		@Override
		public boolean isNBTEqual(ItemStack other) {
			return !other.hasTag();
		}

		@Override
		public boolean equals(Object o) {
			return of(o).isEmpty();
		}

		@Override
		public boolean strongEquals(Object o) {
			return of(o).isEmpty();
		}

		@Override
		public int getHarvestLevel(ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
			return -1;
		}

		@Override
		public JsonElement toJson() {
			JsonObject json = new JsonObject();
			json.addProperty("item", "minecraft:air");
			return json;
		}

		@Override
		public JsonElement toRawResultJson() {
			JsonObject json = new JsonObject();
			json.addProperty("item", "minecraft:air");
			json.addProperty("count", 1);
			return json;
		}

		@Override
		public void onChanged(@Nullable Tag o) {
		}

		@Override
		public String getItemGroup() {
			return "";
		}
	};

	private static List<ItemStackJS> cachedItemList;
	private static ListJS cachedItemListJS;
	private static ListJS cachedItemTypeListJS;

	public static ItemStackJS of(@Nullable Object o) {
		if (o instanceof Wrapper) {
			o = ((Wrapper) o).unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR) {
			return EMPTY;
		} else if (o instanceof ItemStackJS) {
			return (ItemStackJS) o;
		} else if (o instanceof FluidStackJS) {
			return new DummyFluidItemStackJS((FluidStackJS) o);
		} else if (o instanceof IngredientJS) {
			return ((IngredientJS) o).getFirst();
		} else if (o instanceof ItemStack stack) {
			return stack.isEmpty() ? EMPTY : new ItemStackJS(stack);
		} else if (o instanceof ResourceLocation) {
			Item item = KubeJSRegistries.items().get((ResourceLocation) o);

			if (item == Items.AIR) {
				if (RecipeJS.itemErrors) {
					throw new RecipeExceptionJS("Item '" + o + "' not found!").error();
				}

				return EMPTY;
			}

			return new ItemStackJS(new ItemStack(item));
		} else if (o instanceof ItemLike) {
			return new ItemStackJS(new ItemStack(((ItemLike) o).asItem()));
		} else if (o instanceof JsonElement) {
			return resultFromRecipeJson((JsonElement) o);
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			Pattern reg = UtilsJS.parseRegex(o);

			if (reg != null) {
				return new RegexIngredientJS(reg).getFirst();
			}

			return EMPTY;
		} else if (o instanceof CharSequence) {
			String s = o.toString().trim();
			int count = 1;
			int spaceIndex = s.indexOf(' ');

			if (spaceIndex >= 2 && s.indexOf('x') == spaceIndex - 1) {
				count = Integer.parseInt(s.substring(0, spaceIndex - 1));
				s = s.substring(spaceIndex + 1);
			}

			if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
				return EMPTY;
			}

			if (s.startsWith("#")) {
				return TagIngredientJS.createTag(s.substring(1)).getFirst().withCount(count);
			} else if (s.startsWith("@")) {
				return new ModIngredientJS(s.substring(1)).getFirst().withCount(count);
			} else if (s.startsWith("%")) {
				CreativeModeTab group = findGroup(s.substring(1));

				if (group == null) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
					}

					return EMPTY;
				}

				return new GroupIngredientJS(group).getFirst().withCount(count);
			}

			Pattern reg = UtilsJS.parseRegex(s);

			if (reg != null) {
				return new RegexIngredientJS(reg).getFirst().withCount(count);
			}

			Item item = KubeJSRegistries.items().get(new ResourceLocation(s));

			if (item == Items.AIR) {
				if (RecipeJS.itemErrors) {
					throw new RecipeExceptionJS("Item '" + s + "' not found!").error();
				}

				return EMPTY;
			}

			return new ItemStackJS(new ItemStack(item, count));
		}

		MapJS map = MapJS.of(o);

		if (map != null) {
			if (map.containsKey("item")) {
				String id = KubeJS.appendModId(map.get("item").toString());
				Item item = KubeJSRegistries.items().get(new ResourceLocation(id));

				if (item == Items.AIR) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item '" + id + "' not found!").error();
					}

					return EMPTY;
				}

				ItemStack stack = new ItemStack(item);

				if (map.get("count") instanceof Number) {
					stack.setCount(((Number) map.get("count")).intValue());
				}

				if (map.containsKey("nbt")) {
					stack.setTag(MapJS.nbt(map.get("nbt")));
				}

				return new ItemStackJS(stack);
			} else if (map.get("tag") instanceof CharSequence) {
				ItemStackJS stack = TagIngredientJS.createTag(map.get("tag").toString()).getFirst();

				if (map.containsKey("count")) {
					stack.setCount(UtilsJS.parseInt(map.get("count"), 1));
				}

				return stack;
			} else if (map.get("fluid") instanceof CharSequence) {
				return new DummyFluidItemStackJS(FluidStackJS.of(map));
			}
		}

		return EMPTY;
	}

	public static Item getRawItem(@Nullable Object o) {
		if (o == null) {
			return Items.AIR;
		} else if (o instanceof Item) {
			return (Item) o;
		} else if (o instanceof CharSequence) {
			String s = o.toString();

			if (s == null || s.isEmpty()) {
				return Items.AIR;
			} else if (s.charAt(0) != '#') {
				return KubeJSRegistries.items().get(UtilsJS.getMCID(s));
			}
		}

		return of(o).getItem();
	}

	// Use ItemStackJS.of(object)
	public static ItemStackJS resultFromRecipeJson(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return EMPTY;
		} else if (json.isJsonPrimitive()) {
			return of(json.getAsString());
		} else if (json.isJsonObject()) {
			JsonObject o = json.getAsJsonObject();

			if (RecipeJS.currentRecipe != null) {
				ItemStackJS is = RecipeJS.currentRecipe.resultFromRecipeJson(o);

				if (is != null) {
					return is;
				}
			}

			if (o.has("item")) {
				ItemStackJS stack = of(o.get("item").getAsString());

				if (o.has("count")) {
					stack.setCount(o.get("count").getAsInt());
				}

				if (o.has("nbt")) {
					JsonElement element = o.get("nbt");

					if (element.isJsonObject()) {
						stack = stack.withNBT(MapJS.nbt(element));
					} else {
						stack = stack.withNBT(MapJS.nbt(element.getAsString()));
					}
				}

				if (o.has("chance")) {
					boolean locked = o.has("locked") && o.get("locked").getAsBoolean();
					double c = o.get("chance").getAsDouble();
					stack.setChance(locked ? -c : c);
				}

				return stack;
			} else if (o.has("tag")) {
				int c = 1;

				if (o.has("count")) {
					c = o.get("count").getAsInt();
				} else if (o.has("amount")) {
					c = o.get("amount").getAsInt();
				}

				return TagIngredientJS.createTag(o.get("tag").getAsString()).getFirst().withCount(c);
			}
		}

		return EMPTY;
	}

	public static List<ItemStackJS> getList() {
		if (cachedItemList != null) {
			return cachedItemList;
		}

		LinkedHashSet<ItemStackJS> set = new LinkedHashSet<>();
		NonNullList<ItemStack> stackList = NonNullList.create();

		for (var item : KubeJSRegistries.items()) {
			try {
				item.fillItemCategory(CreativeModeTab.TAB_SEARCH, stackList);
			} catch (Throwable ignored) {
			}
		}

		for (var stack : stackList) {
			if (!stack.isEmpty()) {
				set.add(new ItemStackJS(stack).withCount(1));
			}
		}

		cachedItemList = List.of(set.toArray(new ItemStackJS[0]));
		return cachedItemList;
	}

	public static ListJS getListJS() {
		if (cachedItemListJS == null) {
			cachedItemListJS = Objects.requireNonNull(ListJS.of(getList()));
		}

		return cachedItemListJS;
	}

	public static void clearListCache() {
		cachedItemList = null;
		cachedItemListJS = null;
	}

	public static ListJS getTypeList() {
		if (cachedItemTypeListJS == null) {
			cachedItemTypeListJS = new ListJS();

			for (var id : KubeJSRegistries.items().getIds()) {
				cachedItemTypeListJS.add(id.toString());
			}
		}

		return cachedItemTypeListJS;
	}

	@Nullable
	public static CreativeModeTab findGroup(String id) {
		for (var group : CreativeModeTab.TABS) {
			if (id.equals(group.getRecipeFolderName())) {
				return group;
			}
		}

		return null;
	}

	private final ItemStack stack;
	private double chance = Double.NaN;

	public ItemStackJS(ItemStack s) {
		stack = s;
	}

	public Item getItem() {
		return stack.getItem();
	}

	public ItemStack getItemStack() {
		return stack;
	}

	public String getId() {
		return Registries.getId(getItem(), Registry.ITEM_REGISTRY).toString();
	}

	public Collection<ResourceLocation> getTags() {
		return Tags.byItem(getItem());
	}

	public boolean hasTag(ResourceLocation tag) {
		return Tags.items().getTagOrEmpty(tag).contains(getItem());
	}

	@Override
	public ItemStackJS copy() {
		ItemStackJS s = new ItemStackJS(stack.copy());
		s.chance = chance;

		if (!hasNBT()) {
			((ItemStackKJS) (Object) s.stack).removeTagKJS();
		}

		return s;
	}

	public void setCount(int count) {
		stack.setCount(count);
	}

	@Override
	public int getCount() {
		return stack.getCount();
	}

	@Override
	public ItemStackJS withCount(int c) {
		if (c <= 0) {
			return EMPTY;
		}

		ItemStackJS is = copy();
		is.setCount(c);
		return is;
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public boolean isInvalidRecipeIngredient() {
		return stack.isEmpty();
	}

	public boolean isBlock() {
		return stack.getItem() instanceof BlockItem;
	}

	@Nullable
	public CompoundTag getNbt() {
		return stack.getTag();
	}

	public void setNbt(@Nullable CompoundTag tag) {
		stack.setTag(tag);
	}

	public boolean hasNBT() {
		return stack.hasTag();
	}

	public String getNbtString() {
		return String.valueOf(getNbt());
	}

	public ItemStackJS removeNBT() {
		ItemStackJS s = copy();
		((ItemStackKJS) (Object) s.stack).removeTagKJS();
		return s;
	}

	public ItemStackJS withNBT(CompoundTag nbt) {
		ItemStack is = stack.copy();

		if (is.getTag() == null) {
			is.setTag(nbt);
		} else {
			if (nbt != null && !nbt.isEmpty()) {
				for (var key : nbt.getAllKeys()) {
					is.getTag().put(key, nbt.get(key));
				}
			}
		}

		return new ItemStackJS(is).withChance(getChance());
	}

	public boolean hasChance() {
		return !Double.isNaN(chance);
	}

	public void removeChance() {
		setChance(Double.NaN);
	}

	public void setChance(double c) {
		chance = c;
	}

	public double getChance() {
		return chance;
	}

	public final ItemStackJS withChance(double c) {
		if (Double.isNaN(chance) && Double.isNaN(c) || chance == c) {
			return this;
		}

		ItemStackJS is = copy();
		is.setChance(c);
		return is;
	}

	public Text getName() {
		return Text.of(getItemStack().getHoverName());
	}

	public ItemStackJS withName(@Nullable Component displayName) {
		ItemStack is = stack.copy();

		if (displayName != null) {
			is.setHoverName(displayName);
		} else {
			is.resetHoverName();
		}

		return new ItemStackJS(is);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		int count = getCount();
		boolean hasChanceOrNbt = hasChance() || hasNBT();

		if (count > 1 && !hasChanceOrNbt) {
			builder.append('\'');
			builder.append(count);
			builder.append("x ");
			builder.append(getId());
			builder.append('\'');
		} else if (hasChanceOrNbt) {
			builder.append("Item.of('");
			builder.append(getId());
			builder.append('\'');
			List<Pair<String, Integer>> enchants = null;

			if (count > 1) {
				builder.append(", ");
				builder.append(count);
			}

			if (hasNBT()) {
				CompoundTag t = getNbt();

				if (t != null && !t.isEmpty()) {
					String key = getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";

					if (t.contains(key, NbtType.LIST)) {
						ListTag l = t.getList(key, NbtType.COMPOUND);
						enchants = new ArrayList<>(l.size());

						for (int i = 0; i < l.size(); i++) {
							CompoundTag t1 = l.getCompound(i);
							enchants.add(Pair.of(t1.getString("id"), t1.getInt("lvl")));
						}

						t = t.copy();
						t.remove(key);

						if (t.isEmpty()) {
							t = null;
						}
					}
				}

				if (t != null) {
					builder.append(", ");
					NBTUtils.quoteAndEscapeForJS(builder, t.toString());
				}
			}

			builder.append(')');

			if (enchants != null) {
				for (Pair<String, Integer> e : enchants) {
					builder.append(".enchant('");
					builder.append(e.getKey());
					builder.append("', ");
					builder.append(e.getValue());
					builder.append(')');
				}
			}

			if (hasChance()) {
				builder.append(".withChance(");
				builder.append(getChance());
				builder.append(')');
			}
		} else {
			builder.append('\'');
			builder.append(getId());
			builder.append('\'');
		}

		return builder.toString();
	}

	@Override
	public boolean test(ItemStackJS other) {
		return testVanilla(other.stack);
	}

	@Override
	public boolean testVanilla(ItemStack other) {
		if (stack.getItem() == other.getItem()) {
			CompoundTag nbt = stack.getTag();
			CompoundTag nbt2 = other.getTag();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	@Override
	public boolean testVanillaItem(Item item) {
		return item == getItem();
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		return Collections.singleton(this);
	}

	@Override
	public Set<Item> getVanillaItems() {
		return Collections.singleton(getItem());
	}

	@Override
	public ItemStackJS getFirst() {
		return copy();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getItem(), hasNBT() ? getNbt() : 0);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(UtilsJS.getID(o.toString()));
		} else if (o instanceof ItemStack s) {
			return !s.isEmpty() && areItemsEqual(s) && isNBTEqual(s);
		}

		ItemStackJS s = of(o);
		return !s.isEmpty() && areItemsEqual(s) && isNBTEqual(s);
	}

	public boolean strongEquals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(UtilsJS.getID(o.toString())) && getCount() == 1 && !hasNBT();
		} else if (o instanceof ItemStack s) {
			return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
		}

		ItemStackJS s = of(o);
		return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
	}

	public MapJS getEnchantments() {
		MapJS map = new MapJS();

		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(getItemStack()).entrySet()) {
			ResourceLocation id = KubeJSRegistries.enchantments().getId(entry.getKey());

			if (id != null) {
				map.put(id.toString(), entry.getValue());
			}
		}

		return map;
	}

	public boolean hasEnchantment(Enchantment enchantment, int level) {
		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) >= level;
	}

	public ItemStackJS enchant(MapJS enchantments) {
		ItemStackJS is = this;

		for (Map.Entry<String, Object> entry : enchantments.entrySet()) {
			Enchantment enchantment = KubeJSRegistries.enchantments().get(UtilsJS.getMCID(entry.getKey()));

			if (enchantment != null && entry.getValue() instanceof Number) {
				is = is.enchant(enchantment, ((Number) entry.getValue()).intValue());
			}
		}

		return is;
	}

	public ItemStackJS enchant(Enchantment enchantment, int level) {
		ItemStack is = stack.copy();

		if (is.getItem() == Items.ENCHANTED_BOOK) {
			EnchantedBookItem.addEnchantment(is, new EnchantmentInstance(enchantment, level));
		} else {
			is.enchant(enchantment, level);
		}

		return new ItemStackJS(is).withChance(getChance());
	}

	public String getMod() {
		return Registries.getId(getItem(), Registry.ITEM_REGISTRY).getNamespace();
	}

	/*
	public ListJS getLore() {
		final MapJS nbt = getNbt();
		final ListJS lore = new ListJS();

		lore.changeListener = o ->
		{
			if (lore.isEmpty()) {
				nbt.remove("Lore");
			} else {
				ListJS lore1 = new ListJS(lore.size());

				for (var o1 : lore) {
					lore1.add(Component.Serializer.toJson(Text.componentOf(o1)));
				}

				nbt.put("Lore", lore1);
			}
		};

		ListJS list = ListJS.of(nbt.get("Lore"));

		if (list != null) {
			for (var o : list) {
				try {
					lore.add(Component.Serializer.fromJson(o.toString()));
				} catch (JsonParseException var19) {
				}
			}
		}

		return lore;
	}
	 */

	public IngredientJS ignoreNBT() {
		return new IgnoreNBTIngredientJS(this);
	}

	public IngredientJS weakNBT() {
		return new WeakNBTIngredientJS(this);
	}

	public boolean areItemsEqual(ItemStackJS other) {
		return getItem() == other.getItem();
	}

	public boolean areItemsEqual(ItemStack other) {
		return getItem() == other.getItem();
	}

	public boolean isNBTEqual(ItemStackJS other) {
		if (hasNBT() == other.hasNBT()) {
			CompoundTag nbt = stack.getTag();
			CompoundTag nbt2 = other.getNbt();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	public boolean isNBTEqual(ItemStack other) {
		if (hasNBT() == other.hasTag()) {
			CompoundTag nbt = stack.getTag();
			CompoundTag nbt2 = other.getTag();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	public int getHarvestLevel(ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
		return _getHarvestLevel(getItemStack(), tool, player, block);
	}

	@ExpectPlatform
	private static int _getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
		throw new AssertionError();
	}

	public int getHarvestLevel(ToolType tool) {
		return getHarvestLevel(tool, null, null);
	}

	public float getHarvestSpeed(@Nullable BlockContainerJS block) {
		return getItemStack().getDestroySpeed(block == null ? Blocks.AIR.defaultBlockState() : block.getBlockState());
	}

	public float getHarvestSpeed() {
		return getHarvestSpeed(null);
	}

	@Override
	public JsonElement toJson() {
		int c = getCount();

		if (c == 1) {
			return new DummyItemStackJSIngredient(this).toJson();
		} else {
			return new IngredientStackJS(new DummyItemStackJSIngredient(this), c).toJson();
		}
	}

	public JsonElement toResultJson() {
		if (RecipeJS.currentRecipe != null) {
			JsonElement e = RecipeJS.currentRecipe.serializeItemStack(this);

			if (e != null) {
				return e;
			}
		}

		return toRawResultJson();
	}

	public JsonElement toRawResultJson() {
		JsonObject json = new JsonObject();
		json.addProperty("item", getId());
		json.addProperty("count", getCount());

		CompoundTag nbt = getNbt();

		if (nbt != null) {
			if (RecipeJS.currentRecipe != null && RecipeJS.currentRecipe.serializeNBTAsJson()) {
				json.add("nbt", new CompoundTagWrapper(nbt).toJson());
			} else {
				json.addProperty("nbt", nbt.toString());
			}
		}

		if (hasChance()) {
			json.addProperty("chance", getChance());
		}

		return json;
	}

	@Override
	public CompoundTag toNBT() {
		return getItemStack().save(new CompoundTag());
	}

	@Override
	public void onChanged(@Nullable Tag o) {
		if (o == null || o instanceof CompoundTag) {
			stack.setTag((CompoundTag) o);
		}
	}

	public String getItemGroup() {
		if (getItem().getItemCategory() == null) {
			return "";
		}

		return getItem().getItemCategory().getRecipeFolderName();
	}

	@Override
	public Set<String> getItemIds() {
		return Collections.singleton(getId());
	}

	@Nullable
	public FluidStackJS getFluidStack() {
		return null;
	}
}