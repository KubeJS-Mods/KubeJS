package dev.latvian.mods.kubejs.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ItemBuilder extends BuilderBase<Item> {
	public static final Map<String, Tier> TOOL_TIERS = new HashMap<>();
	public static final Map<String, ArmorMaterial> ARMOR_TIERS = new HashMap<>();

	static {
		for (var tier : Tiers.values()) {
			TOOL_TIERS.put(tier.toString().toLowerCase(), tier);
		}

		for (var tier : ArmorMaterials.values()) {
			ARMOR_TIERS.put(tier.toString().toLowerCase(), tier);
		}
	}

	public static ArmorMaterial toArmorMaterial(Object o) {
		if (o instanceof ArmorMaterial armorMaterial) {
			return armorMaterial;
		}

		String asString = String.valueOf(o);

		ArmorMaterial armorMaterial = ItemBuilder.ARMOR_TIERS.get(asString);
		if (armorMaterial != null) {
			return armorMaterial;
		}

		String withKube = KubeJS.appendModId(asString);
		return ItemBuilder.ARMOR_TIERS.getOrDefault(withKube, ArmorMaterials.IRON);
	}

	public static Tier toToolTier(Object o) {
		if (o instanceof Tier tier) {
			return tier;
		}

		String asString = String.valueOf(o);

		Tier toolTier = ItemBuilder.TOOL_TIERS.get(asString);
		if (toolTier != null) {
			return toolTier;
		}

		String withKube = KubeJS.appendModId(asString);
		return ItemBuilder.TOOL_TIERS.getOrDefault(withKube, Tiers.IRON);
	}

	public transient int maxStackSize;
	public transient int maxDamage;
	public transient int burnTime;
	private ResourceLocation containerItem;
	public transient Function<ItemStack, Collection<ItemStack>> subtypes;
	public transient Rarity rarity;
	public transient boolean fireResistant;
	public transient boolean glow;
	public transient final List<Component> tooltip;
	@Nullable
	public transient ItemTintFunction tint;
	public transient FoodBuilder foodBuilder;
	public transient Function<ItemStack, Color> barColor;
	public transient ToIntFunction<ItemStack> barWidth;
	public transient NameCallback nameGetter;

	public transient Multimap<ResourceLocation, AttributeModifier> attributes;
	public transient UseAnim anim;
	public transient ToIntFunction<ItemStack> useDuration;
	public transient UseCallback use;
	public transient FinishUsingCallback finishUsing;
	public transient ReleaseUsingCallback releaseUsing;
	public transient Predicate<HurtEnemyContext> hurtEnemy;

	public String texture;
	public String parentModel;
	public JsonObject textureJson;
	public JsonObject modelJson;

	public ItemBuilder(ResourceLocation i) {
		super(i);
		maxStackSize = 64;
		maxDamage = 0;
		burnTime = 0;
		containerItem = null;
		subtypes = null;
		rarity = Rarity.COMMON;
		glow = false;
		tooltip = new ArrayList<>();
		textureJson = new JsonObject();
		parentModel = "";
		foodBuilder = null;
		modelJson = null;
		attributes = ArrayListMultimap.create();
		anim = null;
		useDuration = null;
		use = null;
		finishUsing = null;
		releaseUsing = null;
		fireResistant = false;
		hurtEnemy = null;
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.ITEM;
	}

	@Override
	public Item transformObject(Item obj) {
		obj.kjs$setItemBuilder(this);
		return obj;
	}

	@Override
	public void generateDataJsons(DataJsonGenerator generator) {
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		if (modelJson != null) {
			generator.json(AssetJsonGenerator.asItemModelLocation(id), modelJson);
			return;
		}

		generator.itemModel(id, m -> {
			if (!parentModel.isEmpty()) {
				m.parent(parentModel);
			} else {
				m.parent("minecraft:item/generated");
			}

			if (textureJson.size() == 0) {
				texture(newID("item/", "").toString());
			}

			m.textures(textureJson);
		});
	}

	@Info("Sets the item's max stack size. Default is 64.")
	public ItemBuilder maxStackSize(int v) {
		maxStackSize = v;
		return this;
	}

	@Info("Makes the item not stackable, equivalent to setting the item's max stack size to 1.")
	public ItemBuilder unstackable() {
		return maxStackSize(1);
	}

	@Info("Sets the item's max damage. Default is 0 (No durability).")
	public ItemBuilder maxDamage(int v) {
		maxDamage = v;
		return this;
	}

	@Info("Sets the item's burn time. Default is 0 (Not a fuel).")
	public ItemBuilder burnTime(int v) {
		burnTime = v;
		return this;
	}

	@Info("Sets the item's container item, e.g. a bucket for a milk bucket.")
	public ItemBuilder containerItem(ResourceLocation id) {
		containerItem = id;
		return this;
	}

	@Info("""
		Adds subtypes to the item. The function should return a collection of item stacks, each with a different subtype.
					
		Each subtype will appear as a separate item in JEI and the creative inventory.
		""")
	public ItemBuilder subtypes(Function<ItemStack, Collection<ItemStack>> fn) {
		subtypes = fn;
		return this;
	}

	@Info("Sets the item's rarity.")
	public ItemBuilder rarity(Rarity v) {
		rarity = v;
		return this;
	}

	@Info("Makes the item glow like enchanted, even if it's not enchanted.")
	public ItemBuilder glow(boolean v) {
		glow = v;
		return this;
	}

	@Info("Adds a tooltip to the item.")
	public ItemBuilder tooltip(Component text) {
		tooltip.add(text);
		return this;
	}

	@Deprecated
	public ItemBuilder group(@Nullable String g) {
		ConsoleJS.STARTUP.error("Item builder .group() is no longer supported, use StartupEvents.modifyCreativeTab!");
		return this;
	}

	@Info("Colorizes item's texture of the given index. Index is used when you have multiple layers, e.g. a crushed ore (of rock + ore).")
	public ItemBuilder color(int index, ItemTintFunction color) {
		if (!(tint instanceof ItemTintFunction.Mapped)) {
			tint = new ItemTintFunction.Mapped();
		}

		((ItemTintFunction.Mapped) tint).map.put(index, color);
		return this;
	}

	@Info("Colorizes item's texture of the given index. Useful for coloring items, like GT ores ore dusts.")
	public ItemBuilder color(ItemTintFunction callback) {
		tint = callback;
		return this;
	}

	@Info("Sets the item's texture (layer0).")
	public ItemBuilder texture(String tex) {
		textureJson.addProperty("layer0", tex);
		return this;
	}

	@Info("Sets the item's texture by given key.")
	public ItemBuilder texture(String key, String tex) {
		textureJson.addProperty(key, tex);
		return this;
	}

	@Info("Directlys set the item's texture json.")
	public ItemBuilder textureJson(JsonObject json) {
		textureJson = json;
		return this;
	}

	@Info("Directly set the item's model json.")
	public ItemBuilder modelJson(JsonObject json) {
		modelJson = json;
		return this;
	}

	@Info("Sets the item's model (parent).")
	public ItemBuilder parentModel(String m) {
		parentModel = m;
		return this;
	}

	@Info("Determines the color of the item's durability bar. Defaulted to vanilla behavior.")
	public ItemBuilder barColor(Function<ItemStack, Color> barColor) {
		this.barColor = barColor;
		return this;
	}

	@Info("""
		Determines the width of the item's durability bar. Defaulted to vanilla behavior.
					
		The function should return a value between 0 and 13 (max width of the bar).
		""")
	public ItemBuilder barWidth(ToIntFunction<ItemStack> barWidth) {
		this.barWidth = barWidth;
		return this;
	}

	@Info("""
		Sets the item's name dynamically.
		""")
	public ItemBuilder name(NameCallback name) {
		this.nameGetter = name;
		return this;
	}

	@Info("""
		Set the food properties of the item.
		""")
	public ItemBuilder food(Consumer<FoodBuilder> b) {
		foodBuilder = new FoodBuilder();
		b.accept(foodBuilder);
		return this;
	}

	@Info("Makes the item fire resistant like netherite tools (or not).")
	public ItemBuilder fireResistant(boolean isFireResistant) {
		fireResistant = isFireResistant;
		return this;
	}


	@Info("Makes the item fire resistant like netherite tools.")
	public ItemBuilder fireResistant() {
		return fireResistant(true);
	}

	public Item.Properties createItemProperties() {
		var properties = new KubeJSItemProperties(this);

		if (maxDamage > 0) {
			properties.durability(maxDamage);
		} else {
			properties.stacksTo(maxStackSize);
		}

		properties.rarity(rarity);

		var item = containerItem == null ? Items.AIR : ItemWrapper.getItem(containerItem);

		if (item != Items.AIR) {
			properties.craftRemainder(item);
		}

		if (foodBuilder != null) {
			properties.food(foodBuilder.build());
		}

		if (fireResistant) {
			properties.fireResistant();
		}

		return properties;
	}

	@Info(value = """
		Adds an attribute modifier to the item.
					
		An attribute modifier is something like a damage boost or a speed boost.
		On tools, they're applied when the item is held, on armor, they're
		applied when the item is worn.
		""",
		params = {
			@Param(name = "attribute", value = "The resource location of the attribute, e.g. 'generic.attack_damage'"),
			@Param(name = "identifier", value = "A unique identifier for the modifier. Modifiers are considered the same if they have the same identifier."),
			@Param(name = "d", value = "The amount of the modifier."),
			@Param(name = "operation", value = "The operation to apply the modifier with. Can be ADDITION, MULTIPLY_BASE, or MULTIPLY_TOTAL.")
		})
	public ItemBuilder modifyAttribute(ResourceLocation attribute, String identifier, double d, AttributeModifier.Operation operation) {
		attributes.put(attribute, new AttributeModifier(new UUID(identifier.hashCode(), identifier.hashCode()), identifier, d, operation));
		return this;
	}

	@Info("Determines the animation of the item when used, e.g. eating food.")
	public ItemBuilder useAnimation(UseAnim animation) {
		this.anim = animation;
		return this;
	}

	@Info("""
		The duration when the item is used.
					
		For example, when eating food, this is the time it takes to eat the food.
		This can change the eating speed, or be used for other things (like making a custom bow).
		""")
	public ItemBuilder useDuration(ToIntFunction<ItemStack> useDuration) {
		this.useDuration = useDuration;
		return this;
	}

	@Info("""
		Determines if player will start using the item.
					
		For example, when eating food, returning true will make the player start eating the food.
		""")
	public ItemBuilder use(UseCallback use) {
		this.use = use;
		return this;
	}

	@Info("""
		When players finish using the item.
					
		This is called only when `useDuration` ticks have passed.
					
		For example, when eating food, this is called when the player has finished eating the food, so hunger is restored.
		""")
	public ItemBuilder finishUsing(FinishUsingCallback finishUsing) {
		this.finishUsing = finishUsing;
		return this;
	}

	@Info("""
		When players did not finish using the item but released the right mouse button halfway through.
					
		An example is the bow, where the arrow is shot when the player releases the right mouse button.
					
		To ensure the bow won't finish using, Minecraft sets the `useDuration` to a very high number (1h).
		""")
	public ItemBuilder releaseUsing(ReleaseUsingCallback releaseUsing) {
		this.releaseUsing = releaseUsing;
		return this;
	}

	@Info("""
		Gets called when the item is used to hurt an entity.
		
		For example, when using a sword to hit a mob, this is called.
		""")
	public ItemBuilder hurtEnemy(Predicate<HurtEnemyContext> context) {
		this.hurtEnemy = context;
		return this;
	}

	@FunctionalInterface
	public interface UseCallback {
		boolean use(Level level, Player player, InteractionHand interactionHand);
	}

	@FunctionalInterface
	public interface FinishUsingCallback {
		ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity);
	}

	@FunctionalInterface
	public interface ReleaseUsingCallback {
		void releaseUsing(ItemStack itemStack, Level level, LivingEntity user, int tick);
	}

	@FunctionalInterface
	public interface NameCallback {
		Component apply(ItemStack itemStack);
	}

	public record HurtEnemyContext(ItemStack getItem, LivingEntity getTarget, LivingEntity getAttacker) {}
}