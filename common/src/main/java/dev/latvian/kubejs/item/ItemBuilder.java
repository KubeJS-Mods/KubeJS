package dev.latvian.kubejs.item;

import com.google.gson.JsonObject;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.item.custom.ArmorItemType;
import dev.latvian.kubejs.item.custom.BasicItemType;
import dev.latvian.kubejs.item.custom.ItemType;
import dev.latvian.kubejs.util.BuilderBase;
import dev.latvian.kubejs.util.ConsoleJS;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ItemBuilder extends BuilderBase {
	public static final Map<String, Tier> TOOL_TIERS = new HashMap<>();
	public static final Map<String, ArmorMaterial> ARMOR_TIERS = new HashMap<>();

	static {
		for (Tier tier : Tiers.values()) {
			TOOL_TIERS.put(tier.toString().toLowerCase(), tier);
		}

		for (ArmorMaterial tier : ArmorMaterials.values()) {
			ARMOR_TIERS.put(tier.toString().toLowerCase(), tier);
		}
	}

	public transient ItemType type;
	public transient int maxStackSize;
	public transient int maxDamage;
	public transient int burnTime;
	public transient String containerItem;
	public transient Function<ItemStackJS, Collection<ItemStackJS>> subtypes;
	public transient Map<ToolType, Integer> tools;
	public transient float miningSpeed;
	public transient Float attackDamage;
	public transient Float attackSpeed;
	public transient RarityWrapper rarity;
	public transient boolean glow;
	public transient final List<Component> tooltip;
	public transient CreativeModeTab group;
	public transient Int2IntOpenHashMap color;
	public String texture;
	public String parentModel;
	public transient FoodBuilder foodBuilder;
	public transient Set<String> defaultTags;

	// Tools //
	public transient Tier toolTier;
	public transient float attackDamageBaseline;
	public transient float attackSpeedBaseline;

	// Armor //
	public transient ArmorMaterial armorTier;

	public transient Item item;

	public JsonObject modelJson;

	public ItemBuilder(String i) {
		super(i);
		type = BasicItemType.INSTANCE;
		maxStackSize = 64;
		maxDamage = 0;
		burnTime = 0;
		containerItem = "minecraft:air";
		subtypes = null;
		tools = new HashMap<>();
		miningSpeed = 1.0F;
		rarity = RarityWrapper.COMMON;
		glow = false;
		tooltip = new ArrayList<>();
		group = KubeJS.tab;
		color = new Int2IntOpenHashMap();
		color.defaultReturnValue(0xFFFFFFFF);
		texture = "";
		parentModel = "";
		foodBuilder = null;
		defaultTags = new HashSet<>();
		toolTier = Tiers.IRON;
		armorTier = ArmorMaterials.IRON;
		displayName = "";
		modelJson = null;
	}

	@Override
	public String getBuilderType() {
		return "item";
	}

	public ItemBuilder type(ItemType t) {
		type = t;
		type.applyDefaults(this);
		return this;
	}

	public ItemBuilder tier(String t) {
		if (type == BasicItemType.INSTANCE) {
			return this;
		} else if (type instanceof ArmorItemType) {
			armorTier = ARMOR_TIERS.getOrDefault(t, ArmorMaterials.IRON);
			return this;
		}

		toolTier = TOOL_TIERS.getOrDefault(t, Tiers.IRON);
		return this;
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

	public ItemBuilder burnTime(int v) {
		burnTime = v;
		return this;
	}

	public ItemBuilder containerItem(String id) {
		containerItem = id;
		return this;
	}

	public ItemBuilder subtypes(Function<ItemStackJS, Collection<ItemStackJS>> fn) {
		subtypes = fn;
		return this;
	}

	public ItemBuilder tool(ToolType type, int level) {
		tools.put(type, level);
		return this;
	}

	public ItemBuilder miningSpeed(float f) {
		miningSpeed = f;
		ConsoleJS.STARTUP.warn("You should be using a 'pickaxe' or other tool type item if you want to modify mining speed!");
		return this;
	}

	public ItemBuilder attackDamage(float f) {
		attackDamage = f;
		ConsoleJS.STARTUP.warn("You should be using a 'sword' type item if you want to modify attack damage!");
		return this;
	}

	public ItemBuilder attackSpeed(float f) {
		attackSpeed = f;
		ConsoleJS.STARTUP.warn("You should be using a 'sword' type item if you want to modify attack speed!");
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

	public ItemBuilder tooltip(Component text) {
		tooltip.add(text);
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

	@Nullable
	public Float getAttackDamage() {
		return attackDamage;
	}

	@Nullable
	public Float getAttackSpeed() {
		return attackSpeed;
	}

	public ItemBuilder tag(String tag) {
		defaultTags.add(tag);
		return this;
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
}