package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.kubejs.item.ingredient.GroupIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IgnoreNBTIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.RegexIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.WeakNBTIngredientJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.ChangeListener;
import dev.latvian.mods.rhino.mod.util.NBTSerializable;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.mod.util.NbtType;
import dev.latvian.mods.rhino.mod.util.TagUtils;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
@SuppressWarnings("unused")
public class ItemStackJS implements IngredientJS, NBTSerializable, ChangeListener<Tag>, SpecialEquality {
	public static final ItemStackJS EMPTY = new ItemStackJS(ItemStack.EMPTY) {
		@Override
		public String getId() {
			return "minecraft:air";
		}

		@Override
		public Collection<ResourceLocation> getTags() {
			return Set.of();
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
			return Set.of();
		}

		@Override
		public Set<Item> getVanillaItems() {
			return Set.of();
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
		public Map<String, Integer> getEnchantments() {
			return Map.of();
		}

		@Override
		public boolean hasEnchantment(Enchantment enchantment, int level) {
			return false;
		}

		@Override
		public ItemStackJS enchant(Map<?, ?> map) {
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
		public JsonElement toJson() {
			var json = new JsonObject();
			json.addProperty("item", "minecraft:air");
			return json;
		}

		@Override
		public JsonElement toRawResultJson() {
			var json = new JsonObject();
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
	private static List<String> cachedItemTypeList;

	public static ItemStackJS of(@Nullable Object o) {
		if (o instanceof Wrapper w) {
			o = w.unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR) {
			return EMPTY;
		} else if (o instanceof ItemStackJS js) {
			return js;
		} else if (o instanceof IngredientJS ingr) {
			return ingr.getFirst();
		} else if (o instanceof ItemStack stack) {
			return stack.isEmpty() ? EMPTY : new ItemStackJS(stack);
		} else if (o instanceof ResourceLocation id) {
			var item = KubeJSRegistries.items().get(id);

			if (item == Items.AIR) {
				if (RecipeJS.itemErrors) {
					throw new RecipeExceptionJS("Item '" + id + "' not found!").error();
				}

				return EMPTY;
			}

			return new ItemStackJS(new ItemStack(item));
		} else if (o instanceof ItemLike itemLike) {
			return new ItemStackJS(new ItemStack(itemLike.asItem()));
		} else if (o instanceof JsonElement json) {
			return resultFromRecipeJson(json);
		} else if (o instanceof StringTag tag) {
			return of(tag.getAsString());
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			var reg = UtilsJS.parseRegex(o);

			if (reg != null) {
				return new RegexIngredientJS(reg).getFirst();
			}

			return EMPTY;
		} else if (o instanceof CharSequence) {
			var s = o.toString().trim();
			var count = 1;
			var spaceIndex = s.indexOf(' ');

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
				var group = findGroup(s.substring(1));

				if (group == null) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
					}

					return EMPTY;
				}

				return new GroupIngredientJS(group).getFirst().withCount(count);
			}

			var reg = UtilsJS.parseRegex(s);

			if (reg != null) {
				return new RegexIngredientJS(reg).getFirst().withCount(count);
			}

			var item = KubeJSRegistries.items().get(new ResourceLocation(s));

			if (item == Items.AIR) {
				if (RecipeJS.itemErrors) {
					throw new RecipeExceptionJS("Item '" + s + "' not found!").error();
				}

				return EMPTY;
			}

			return new ItemStackJS(new ItemStack(item, count));
		}

		var map = MapJS.of(o);

		if (map != null) {
			if (map.containsKey("item")) {
				var id = KubeJS.appendModId(map.get("item").toString());
				var item = KubeJSRegistries.items().get(new ResourceLocation(id));

				if (item == Items.AIR) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item '" + id + "' not found!").error();
					}

					return EMPTY;
				}

				var stack = new ItemStack(item);

				if (map.get("count") instanceof Number number) {
					stack.setCount(number.intValue());
				}

				if (map.containsKey("nbt")) {
					stack.setTag(NBTUtils.toTagCompound(map.get("nbt")));
				}

				return new ItemStackJS(stack);
			} else if (map.get("tag") instanceof CharSequence s) {
				var stack = TagIngredientJS.createTag(s.toString()).getFirst();

				if (map.containsKey("count")) {
					stack.setCount(UtilsJS.parseInt(map.get("count"), 1));
				}

				return stack;
			}
		}

		return EMPTY;
	}

	public static Item getRawItem(@Nullable Object o) {
		if (o == null) {
			return Items.AIR;
		} else if (o instanceof Item item) {
			return item;
		} else if (o instanceof CharSequence) {
			var s = o.toString();
			if (s.isEmpty()) {
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
		} else if (json instanceof JsonObject jsonObj) {
			if (RecipeJS.currentRecipe != null) {
				var is = RecipeJS.currentRecipe.resultFromRecipeJson(jsonObj);

				if (is != null) {
					return is;
				}
			}

			ItemStackJS stack = null;
			if (jsonObj.has("item")) {
				stack = of(jsonObj.get("item").getAsString());
			} else if (jsonObj.has("tag")) {
				stack = TagIngredientJS.createTag(jsonObj.get("tag").getAsString()).getFirst();
			}

			if (stack != null) {
				if (jsonObj.has("count")) {
					stack.setCount(jsonObj.get("count").getAsInt());
				} else if (jsonObj.has("amount")) {
					stack.setCount(jsonObj.get("amount").getAsInt());
				}

				if (jsonObj.has("nbt")) {
					var element = jsonObj.get("nbt");

					if (element.isJsonObject()) {
						stack.setNbt(NBTUtils.toTagCompound(element));
					} else {
						stack.setNbt(NBTUtils.toTagCompound(element.getAsString()));
					}
				}

				if (jsonObj.has("chance")) {
					var locked = jsonObj.has("locked") && jsonObj.get("locked").getAsBoolean();
					var c = jsonObj.get("chance").getAsDouble();
					stack.setChance(locked ? -c : c);
				}

				return stack;
			}
		}

		return EMPTY;
	}

	public static List<ItemStackJS> getList() {
		if (cachedItemList != null) {
			return cachedItemList;
		}

		var set = new LinkedHashSet<ItemStackJS>();
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

	public static void clearListCache() {
		cachedItemList = null;
	}

	public static List<String> getTypeList() {
		if (cachedItemTypeList == null) {
			cachedItemTypeList = new ArrayList<>();

			for (var id : KubeJSRegistries.items().getIds()) {
				cachedItemTypeList.add(id.toString());
			}
		}

		return cachedItemTypeList;
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
		return String.valueOf(Registries.getId(getItem(), Registry.ITEM_REGISTRY));
	}

	public Collection<ResourceLocation> getTags() {
		return Tags.byItem(getItem()).map(TagKey::location).collect(Collectors.toSet());
	}

	public boolean hasTag(ResourceLocation tag) {
		return getItemStack().is(Tags.item(tag));
	}

	@Override
	public ItemStackJS copy() {
		var s = new ItemStackJS(stack.copy());
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

		var is = copy();
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
		var s = copy();
		((ItemStackKJS) (Object) s.stack).removeTagKJS();
		return s;
	}

	public ItemStackJS withNBT(CompoundTag nbt) {
		var is = stack.copy();

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

		var is = copy();
		is.setChance(c);
		return is;
	}

	public Component getName() {
		return getItemStack().getHoverName();
	}

	public ItemStackJS withName(@Nullable Component displayName) {
		var is = stack.copy();

		if (displayName != null) {
			is.setHoverName(displayName);
		} else {
			is.resetHoverName();
		}

		return new ItemStackJS(is);
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();

		var count = getCount();
		var hasChanceOrNbt = hasChance() || hasNBT();

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
				var t = getNbt();

				if (t != null && !t.isEmpty()) {
					var key = getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";

					if (t.contains(key, NbtType.LIST)) {
						var l = t.getList(key, NbtType.COMPOUND);
						enchants = new ArrayList<>(l.size());

						for (var i = 0; i < l.size(); i++) {
							var t1 = l.getCompound(i);
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
				for (var e : enchants) {
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
			var nbt = stack.getTag();
			var nbt2 = other.getTag();
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

		var s = of(o);
		return !s.isEmpty() && areItemsEqual(s) && isNBTEqual(s);
	}

	public boolean strongEquals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(UtilsJS.getID(o.toString())) && getCount() == 1 && !hasNBT();
		} else if (o instanceof ItemStack s) {
			return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
		}

		var s = of(o);
		return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
	}

	public Map<String, Integer> getEnchantments() {
		var map = new HashMap<String, Integer>();

		for (var entry : EnchantmentHelper.getEnchantments(getItemStack()).entrySet()) {
			var id = KubeJSRegistries.enchantments().getId(entry.getKey());

			if (id != null) {
				map.put(id.toString(), entry.getValue());
			}
		}

		return map;
	}

	public boolean hasEnchantment(Enchantment enchantment, int level) {
		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) >= level;
	}

	public ItemStackJS enchant(Map<?, ?> enchantments) {
		var is = this;

		for (var entry : enchantments.entrySet()) {
			var enchantment = KubeJSRegistries.enchantments().get(UtilsJS.getMCID(entry.getKey()));

			if (enchantment != null && entry.getValue() instanceof Number number) {
				is = is.enchant(enchantment, number.intValue());
			}
		}

		return is;
	}

	public ItemStackJS enchant(Enchantment enchantment, int level) {
		var is = stack.copy();

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
					lore1.add(Component.Serializer.toJson(Text.of(o1)));
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
			var nbt = stack.getTag();
			var nbt2 = other.getNbt();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	public boolean isNBTEqual(ItemStack other) {
		if (hasNBT() == other.hasTag()) {
			var nbt = stack.getTag();
			var nbt2 = other.getTag();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	public float getHarvestSpeed(@Nullable BlockContainerJS block) {
		return getItemStack().getDestroySpeed(block == null ? Blocks.AIR.defaultBlockState() : block.getBlockState());
	}

	public float getHarvestSpeed() {
		return getHarvestSpeed(null);
	}

	@Override
	public JsonElement toJson() {
		var c = getCount();

		if (c == 1) {
			return new DummyItemStackJSIngredient(this).toJson();
		} else {
			return new IngredientStackJS(new DummyItemStackJSIngredient(this), c).toJson();
		}
	}

	public JsonElement toResultJson() {
		if (RecipeJS.currentRecipe != null) {
			var e = RecipeJS.currentRecipe.serializeItemStack(this);

			if (e != null) {
				return e;
			}
		}

		return toRawResultJson();
	}

	public JsonElement toRawResultJson() {
		var json = new JsonObject();
		json.addProperty("item", getId());
		json.addProperty("count", getCount());

		var nbt = getNbt();

		if (nbt != null) {
			if (RecipeJS.currentRecipe != null && RecipeJS.currentRecipe.serializeNBTAsJson()) {
				json.add("nbt", TagUtils.toJson(nbt));
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

	public CompoundTag getTypeData() {
		return getItem().getTypeDataKJS();
	}
}