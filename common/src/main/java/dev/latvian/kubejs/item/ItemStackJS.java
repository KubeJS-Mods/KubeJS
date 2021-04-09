package dev.latvian.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ingredient.GroupIngredientJS;
import dev.latvian.kubejs.item.ingredient.IgnoreNBTIngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.kubejs.item.ingredient.RegexIngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.JSObjectType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.NBTSerializable;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WrappedJSObjectChangeListener;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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
public abstract class ItemStackJS implements IngredientJS, NBTSerializable, WrappedJSObjectChangeListener<MapJS> {
	private static List<ItemStackJS> cachedItemList;
	private static ListJS cachedItemListJS;
	private static ListJS cachedItemTypeListJS;

	public static ItemStackJS of(@Nullable Object o) {
		if (o instanceof Wrapper) {
			o = ((Wrapper) o).unwrap();
		}

		if (o == null) {
			return EmptyItemStackJS.INSTANCE;
		} else if (o instanceof ItemStackJS) {
			return (ItemStackJS) o;
		} else if (o instanceof IngredientJS) {
			return ((IngredientJS) o).getFirst();
		} else if (o instanceof ItemStack) {
			ItemStack stack = (ItemStack) o;
			return stack.isEmpty() ? EmptyItemStackJS.INSTANCE : new BoundItemStackJS(stack);
		} else if (o instanceof ResourceLocation) {
			return new UnboundItemStackJS((ResourceLocation) o);
		} else if (o instanceof Item) {
			return new UnboundItemStackJS(Registries.getId((Item) o, Registry.ITEM_REGISTRY));
		} else if (o instanceof JsonElement) {
			return resultFromRecipeJson((JsonElement) o);
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			Pattern reg = UtilsJS.parseRegex(o);

			if (reg != null) {
				return new RegexIngredientJS(reg).getFirst();
			}

			return EmptyItemStackJS.INSTANCE;
		} else if (o instanceof CharSequence) {
			String s = o.toString().trim();
			int count = 1;
			int spaceIndex = s.indexOf(' ');

			if (spaceIndex >= 2 && s.indexOf('x') == spaceIndex - 1) {
				count = Integer.parseInt(s.substring(0, spaceIndex - 1));
				s = s.substring(spaceIndex + 1);
			}

			if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
				return EmptyItemStackJS.INSTANCE;
			}

			if (s.startsWith("#")) {
				return TagIngredientJS.createTag(s.substring(1)).getFirst().withCount(count);
			} else if (s.startsWith("@")) {
				return new ModIngredientJS(s.substring(1)).getFirst().withCount(count);
			} else if (s.startsWith("%")) {
				CreativeModeTab group = ItemStackJS.findGroup(s.substring(1));

				if (group == null) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
					}

					return EmptyItemStackJS.INSTANCE;
				}

				return new GroupIngredientJS(group).getFirst().withCount(count);
			}

			Pattern reg = UtilsJS.parseRegex(s);

			if (reg != null) {
				return new RegexIngredientJS(reg).getFirst().withCount(count);
			}

			return new UnboundItemStackJS(new ResourceLocation(s)).withCount(count);
		}

		MapJS map = MapJS.of(o);

		if (map != null) {
			if (map.containsKey("item")) {
				ItemStackJS stack = new UnboundItemStackJS(new ResourceLocation(KubeJS.appendModId(map.get("item").toString())));

				if (map.get("count") instanceof Number) {
					stack.setCount(((Number) map.get("count")).intValue());
				}

				if (map.containsKey("nbt")) {
					stack = stack.withNBT(map.getOrNewMap("nbt"));
				}

				return stack;
			} else if (map.get("tag") instanceof CharSequence) {
				ItemStackJS stack = TagIngredientJS.createTag(map.get("tag").toString()).getFirst();

				if (map.containsKey("count")) {
					stack.setCount(UtilsJS.parseInt(map.get("count"), 1));
				}

				return stack;
			}
		}

		return EmptyItemStackJS.INSTANCE;
	}

	public static ItemStackJS of(ItemStackJS stack, @Nullable Object countOrNBT) {
		Object n = UtilsJS.wrap(countOrNBT, JSObjectType.ANY);

		if (n instanceof Number) {
			stack.setCount(((Number) n).intValue());
		} else if (n instanceof MapJS) {
			stack = stack.withNBT((MapJS) n);
		}

		return stack;
	}

	public static ItemStackJS of(ItemStackJS stack, int count, MapJS nbt) {
		return stack.withCount(count).withNBT(nbt);
	}

	// Use ItemStackJS.of(object)
	public static ItemStackJS resultFromRecipeJson(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return EmptyItemStackJS.INSTANCE;
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
				ItemStackJS stack = ItemStackJS.of(o.get("item").getAsString());

				if (o.has("count")) {
					stack.setCount(o.get("count").getAsInt());
				}

				if (o.has("nbt")) {
					JsonElement element = o.get("nbt");

					if (element.isJsonObject()) {
						stack = stack.withNBT(MapJS.of(element));
					} else {
						try {
							stack = stack.withNBT(MapJS.of(TagParser.parseTag(GsonHelper.convertToString(element, "nbt"))));
						} catch (CommandSyntaxException ex) {
							ex.printStackTrace();
						}
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

		return EmptyItemStackJS.INSTANCE;
	}

	public static List<ItemStackJS> getList() {
		if (cachedItemList != null) {
			return cachedItemList;
		}

		LinkedHashSet<ItemStackJS> set = new LinkedHashSet<>();
		NonNullList<ItemStack> stackList = NonNullList.create();

		for (Item item : KubeJSRegistries.items()) {
			try {
				item.fillItemCategory(CreativeModeTab.TAB_SEARCH, stackList);
			} catch (Throwable ex) {
			}
		}

		for (ItemStack stack : stackList) {
			if (!stack.isEmpty()) {
				set.add(new BoundItemStackJS(stack).getCopy().withCount(1));
			}
		}

		cachedItemList = Collections.unmodifiableList(Arrays.asList(set.toArray(new ItemStackJS[0])));
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

			for (ResourceLocation id : KubeJSRegistries.items().getIds()) {
				cachedItemTypeListJS.add(id.toString());
			}
		}

		return cachedItemTypeListJS;
	}

	@Nullable
	public static CreativeModeTab findGroup(String id) {
		for (CreativeModeTab group : CreativeModeTab.TABS) {
			if (id.equals(group.getRecipeFolderName())) {
				return group;
			}
		}

		return null;
	}

	private double chance = Double.NaN;

	public abstract Item getItem();

	@MinecraftClass
	public abstract ItemStack getItemStack();

	public String getId() {
		return Registries.getId(getItem(), Registry.ITEM_REGISTRY).toString();
	}

	@Override
	public abstract ItemStackJS getCopy();

	public abstract void setCount(int count);

	@Override
	public abstract int getCount();

	@Override
	public ItemStackJS withCount(int c) {
		if (c <= 0) {
			return EmptyItemStackJS.INSTANCE;
		}

		ItemStackJS is = getCopy();
		is.setCount(c);
		return is;
	}

	@Override
	public final ItemStackJS x(int c) {
		return withCount(c);
	}

	@Override
	public boolean isEmpty() {
		return getCount() <= 0;
	}

	@Override
	public boolean isInvalidRecipeIngredient() {
		return isEmpty();
	}

	public boolean isBlock() {
		return getItem() instanceof BlockItem;
	}

	public abstract MapJS getNbt();

	public ItemStackJS withNBT(MapJS nbt) {
		if (isEmpty()) {
			return this;
		}

		if (nbt != null) {
			ItemStackJS is = getCopy();
			is.getNbt().putAll(nbt);
			return is;
		}

		return this;
	}

	@Deprecated
	public final ItemStackJS nbt(MapJS nbt) {
		return withNBT(nbt);
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

		ItemStackJS is = getCopy();
		is.setChance(c);
		return is;
	}

	@Deprecated
	public final ItemStackJS chance(double c) {
		return withChance(c);
	}

	public Text getName() {
		return Text.of(getItemStack().getHoverName());
	}

	public void setName(@Nullable Object displayName) {
		if (displayName == null || displayName instanceof String && displayName.toString().isEmpty()) {
			return;
		}

		Text t = Text.of(displayName);
		MapJS nbt = getNbt();
		nbt.getOrNewMap("display").put("Name", t.toJson());
	}

	public final ItemStackJS name(String displayName) {
		setName(displayName);
		return this;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		int count = getCount();
		MapJS nbt = getNbt();

		if (count > 1 || hasChance() || !nbt.isEmpty()) {
			builder.append("Item.of('");
			builder.append(getId());
			builder.append('\'');

			if (count > 1) {
				builder.append(", ");
				builder.append(count);
			}

			if (!nbt.isEmpty()) {
				builder.append(", ");
				nbt.toString(builder);
			}

			builder.append(')');

			if (hasChance()) {
				builder.append(".chance(");
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
	public boolean test(ItemStackJS stack) {
		return areItemsEqual(stack) && isNBTEqual(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return areItemsEqual(stack) && isNBTEqual(stack);
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
		return getCopy();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getItem(), getNbt());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(UtilsJS.getID(o.toString()));
		} else if (o instanceof ItemStack) {
			ItemStack s = (ItemStack) o;
			return !s.isEmpty() && areItemsEqual(s) && isNBTEqual(s);
		}

		ItemStackJS s = of(o);
		return !s.isEmpty() && areItemsEqual(s) && isNBTEqual(s);
	}

	public boolean strongEquals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(UtilsJS.getID(o.toString())) && getCount() == 1 && getNbt().isEmpty();
		} else if (o instanceof ItemStack) {
			ItemStack s = (ItemStack) o;
			return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
		}

		ItemStackJS s = of(o);
		return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
	}

	public MapJS getEnchantments() {
		final MapJS nbt = getNbt();
		final String key = getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";
		final MapJS enchantments = new MapJS();

		enchantments.changeListener = o ->
		{
			ListJS list = new ListJS(o.size());

			for (Map.Entry<String, Object> entry : o.entrySet()) {
				if (entry.getValue() instanceof Number && ((Number) entry.getValue()).intValue() > 0) {
					MapJS ench = new MapJS(2);
					ench.put("id", new ResourceLocation(entry.getKey()).toString());
					ench.put("lvl", ((Number) entry.getValue()).intValue());
					list.add(ench);
				}
			}

			if (list.isEmpty()) {
				nbt.remove(key);
			} else {
				nbt.put(key, list);
			}
		};

		ListJS list = ListJS.of(nbt.get(key));

		if (list != null) {
			for (Object o : list) {
				MapJS m = MapJS.of(o);

				if (m != null && m.containsKey("id") && m.containsKey("lvl")) {
					enchantments.put(m.get("id").toString(), m.get("lvl"));
				}
			}
		}

		return enchantments;
	}

	public ItemStackJS enchant(Object enchantments) {
		getEnchantments().putAll(MapJS.of(enchantments));
		return this;
	}

	public ItemStackJS enchant(String id, int level) {
		getEnchantments().put(id, level);
		return this;
	}

	public String getMod() {
		return Registries.getId(getItem(), Registry.ITEM_REGISTRY).getNamespace();
	}

	public ListJS getLore() {
		final MapJS nbt = getNbt();
		final ListJS lore = new ListJS();

		lore.changeListener = o ->
		{
			if (lore.isEmpty()) {
				nbt.remove("Lore");
			} else {
				ListJS lore1 = new ListJS(lore.size());

				for (Object o1 : lore) {
					lore1.add(Component.Serializer.toJson(Text.of(o1).component()));
				}

				nbt.put("Lore", lore1);
			}
		};

		ListJS list = ListJS.of(nbt.get("Lore"));

		if (list != null) {
			for (Object o : list) {
				try {
					lore.add(Component.Serializer.fromJson(o.toString()));
				} catch (JsonParseException var19) {
				}
			}
		}

		return lore;
	}

	public IgnoreNBTIngredientJS ignoreNBT() {
		return new IgnoreNBTIngredientJS(this);
	}

	public boolean areItemsEqual(ItemStackJS stack) {
		return getItem() == stack.getItem();
	}

	public boolean areItemsEqual(ItemStack stack) {
		return getItem() == stack.getItem();
	}

	public boolean isNBTEqual(ItemStackJS stack) {
		return Objects.equals(getNbt(), stack.getNbt());
	}

	public boolean isNBTEqual(ItemStack stack) {
		MapJS nbt = getNbt();
		CompoundTag nbt1 = stack.getTag();

		if (nbt1 == null) {
			return nbt.isEmpty();
		}

		return Objects.equals(MapJS.nbt(nbt), nbt1);
	}

	public int getHarvestLevel(ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
		return _getHarvestLevel(this, tool, player, block);
	}

	@ExpectPlatform
	private static int _getHarvestLevel(ItemStackJS stack, ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
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

		JsonObject json = new JsonObject();
		json.addProperty("item", getId());
		json.addProperty("count", getCount());

		MapJS nbt = getNbt();

		if (!nbt.isEmpty()) {
			json.addProperty("nbt", nbt.toNBT().toString());
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
	public void onChanged(@Nullable MapJS o) {
	}

	public String getItemGroup() {
		if (getItem().getItemCategory() == null) {
			return "";
		}

		return getItem().getItemCategory().getRecipeFolderName();
	}
}