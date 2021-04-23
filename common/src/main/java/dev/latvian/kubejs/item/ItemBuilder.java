package dev.latvian.kubejs.item;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.BuilderBase;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemBuilder extends BuilderBase {
	public int maxStackSize;
	public int maxDamage;
	public String containerItem;
	public Map<ToolType, Integer> tools;
	public float miningSpeed;
	public Float attackDamage;
	public Float attackSpeed;
	public RarityWrapper rarity;
	public boolean glow;
	public final List<Text> tooltip;
	public CreativeModeTab group;
	public Int2IntOpenHashMap color;
	public String texture;
	public String parentModel;
	public FoodBuilder foodBuilder;

	public ItemJS item;

	private JsonObject modelJson;

	public ItemBuilder(String i) {
		super(i);
		maxStackSize = 64;
		maxDamage = 0;
		containerItem = "minecraft:air";
		tools = new HashMap<>();
		miningSpeed = 1.0F;
		rarity = RarityWrapper.COMMON;
		glow = false;
		tooltip = new ArrayList<>();
		group = KubeJS.tab;
		color = new Int2IntOpenHashMap();
		color.defaultReturnValue(0xFFFFFFFF);
		texture = id.getNamespace() + ":item/" + id.getPath();
		parentModel = "item/generated";
		foodBuilder = null;
		displayName = "";
	}

	@Override
	public String getType() {
		return "item";
	}

	public ItemBuilder maxStackSize(int v) {
		maxStackSize = v;
		return this;
	}

	public ItemBuilder unstackable() {
		return maxStackSize(1);
	}

	public ItemBuilder maxDamage(int v) {
		maxDamage = v;
		return this;
	}

	public ItemBuilder containerItem(String id) {
		containerItem = id;
		return this;
	}

	public ItemBuilder tool(String type, int level) {
		return tool(ToolType.byName(type), level);
	}

	public ItemBuilder tool(ToolType type, int level) {
		tools.put(type, level);
		return this;
	}

	public ItemBuilder miningSpeed(float miningSpeed) {
		this.miningSpeed = miningSpeed;
		return this;
	}

	public ItemBuilder attackDamage(float attackDamage) {
		this.attackDamage = attackDamage;
		return this;
	}

	public ItemBuilder attackSpeed(float attackSpeed) {
		this.attackSpeed = attackSpeed;
		return this;
	}

	public ItemBuilder rarity(RarityWrapper v) {
		rarity = v;
		return this;
	}

	public ItemBuilder glow(boolean v) {
		glow = v;
		return this;
	}

	public ItemBuilder tooltip(Object text) {
		tooltip.add(Text.of(text));
		return this;
	}

	public ItemBuilder group(String g) {
		for (CreativeModeTab ig : CreativeModeTab.TABS) {
			if (ig.getRecipeFolderName().equals(g)) {
				group = ig;
				return this;
			}
		}

		return this;
	}

	public ItemBuilder color(int index, int c) {
		color.put(index, 0xFF000000 | c);
		return this;
	}

	public ItemBuilder texture(String tex) {
		texture = tex;
		return this;
	}

	public ItemBuilder parentModel(String m) {
		parentModel = m;
		return this;
	}

	public ItemBuilder food(Consumer<FoodBuilder> b) {
		foodBuilder = new FoodBuilder();
		b.accept(foodBuilder);
		return this;
	}

	public Map<ToolType, Integer> getToolsMap() {
		return tools;
	}

	public float getMiningSpeed() {
		return miningSpeed;
	}

	public Float getAttackDamage() {
		return attackDamage;
	}

	public Float getAttackSpeed() {
		return attackSpeed;
	}

	public Item.Properties createItemProperties() {
		Item.Properties properties = new Item.Properties();

		properties.tab(group);

		if (maxDamage > 0) {
			properties.durability(maxDamage);
		} else {
			properties.stacksTo(maxStackSize);
		}

		properties.rarity(rarity.rarity);

		for (Map.Entry<ToolType, Integer> entry : tools.entrySet()) {
			appendToolType(properties, entry.getKey(), entry.getValue());
		}

		Item item = KubeJSRegistries.items().get(new ResourceLocation(containerItem));

		if (item != Items.AIR) {
			properties.craftRemainder(item);
		}

		if (foodBuilder != null) {
			properties.food(foodBuilder.build());
		}

		return properties;
	}

	@ExpectPlatform
	private static void appendToolType(Item.Properties properties, ToolType type, Integer level) {
		throw new AssertionError();
	}

	public void setModelJson(JsonObject o) {
		modelJson = o;
	}

	public JsonObject getModelJson() {
		if (modelJson == null) {
			modelJson = new JsonObject();
			modelJson.addProperty("parent", parentModel);

			if (parentModel.equals("item/generated")) {
				JsonObject textures = new JsonObject();
				textures.addProperty("layer0", texture);
				modelJson.add("textures", textures);
			}
		}

		return modelJson;
	}
}