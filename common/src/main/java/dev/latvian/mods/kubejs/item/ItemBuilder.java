package dev.latvian.mods.kubejs.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * @author LatvianModder
 */
public abstract class ItemBuilder extends BuilderBase<Item> {

	@FunctionalInterface
	public interface UseCallback {
		boolean use(LevelJS level, PlayerJS<?> player, InteractionHand interactionHand);
	}

	@FunctionalInterface
	public interface FinishUsingCallback {
		ItemStackJS finishUsingItem(ItemStackJS itemStack, LevelJS level, LivingEntityJS livingEntity);
	}

	@FunctionalInterface
	public interface ReleaseUsingCallback {
		void releaseUsing(ItemStackJS itemStack, LevelJS level, LivingEntityJS user, int tick);
	}


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

	public static ArmorMaterial ofArmorMaterial(Object o) {
		String asString = String.valueOf(o);

		ArmorMaterial armorMaterial = ItemBuilder.ARMOR_TIERS.get(asString);
		if (armorMaterial != null) {
			return armorMaterial;
		}

		String withKube = KubeJS.appendModId(asString);
		return ItemBuilder.ARMOR_TIERS.getOrDefault(withKube, ArmorMaterials.IRON);
	}

	public transient int maxStackSize;
	public transient int maxDamage;
	public transient int burnTime;
	public transient String containerItem;
	public transient Function<ItemStackJS, Collection<ItemStackJS>> subtypes;
	public transient Rarity rarity;
	public transient boolean glow;
	public transient final List<Component> tooltip;
	@Nullable
	public transient CreativeModeTab group;
	@Nullable
	public transient ItemColor colorCallback;
	public JsonObject textureJson;
	public String texture;
	public String parentModel;
	public transient FoodBuilder foodBuilder;
	public transient Function<ItemStackJS, Color> barColor;
	public transient ToIntFunction<ItemStackJS> barWidth;
	public transient Multimap<ResourceLocation, AttributeModifier> attributes;
	public transient UseAnim anim;
	public transient ToIntFunction<ItemStackJS> useDuration;
	public transient UseCallback use;
	public transient FinishUsingCallback finishUsing;
	public transient ReleaseUsingCallback releaseUsing;


	public JsonObject modelJson;

	public ItemBuilder(ResourceLocation i) {
		super(i);
		maxStackSize = 64;
		maxDamage = 0;
		burnTime = 0;
		containerItem = "minecraft:air";
		subtypes = null;
		rarity = Rarity.COMMON;
		glow = false;
		tooltip = new ArrayList<>();
		group = KubeJS.tab;
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
	}

	@Override
	public final RegistryObjectBuilderTypes<Item> getRegistryType() {
		return RegistryObjectBuilderTypes.ITEM;
	}

	@Override
	public Item transformObject(Item obj) {
		if (obj instanceof ItemKJS itemKJS) {
			itemKJS.setItemBuilderKJS(this);
		}

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

	@Override
	@Environment(EnvType.CLIENT)
	public void clientRegistry(Minecraft minecraft) {
		if (colorCallback != null) {
			ColorHandlerRegistry.registerItemColors(colorCallback, this);
		}
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

	public ItemBuilder rarity(Rarity v) {
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

	public ItemBuilder group(@Nullable String g) {
		if (g == null) {
			group = null;
			return this;
		}

		for (var ig : CreativeModeTab.TABS) {
			if (ig.getRecipeFolderName().equals(g)) {
				group = ig;
				return this;
			}
		}

		return this;
	}

	public ItemBuilder color(int index, Color c) {
		if (!(colorCallback instanceof IndexedItemColor indexed)) {
			if (colorCallback != null) {
				ConsoleJS.STARTUP.warnf("Overwriting existing dynamic item color for {} with an indexed color", id);
			}
			colorCallback = Util.make(new IndexedItemColor(), col -> col.add(index, c));
		} else {
			indexed.add(index, c);
		}
		return this;
	}

	public ItemBuilder color(DynamicItemColor callback) {
		colorCallback = callback;
		return this;
	}

	public ItemBuilder texture(String tex) {
		textureJson.addProperty("layer0", tex);
		return this;
	}

	public ItemBuilder texture(String key, String tex) {
		textureJson.addProperty(key, tex);
		return this;
	}

	public ItemBuilder textureJson(JsonObject json) {
		textureJson = json;
		return this;
	}

	public ItemBuilder modelJson(JsonObject json) {
		modelJson = json;
		return this;
	}

	public ItemBuilder parentModel(String m) {
		parentModel = m;
		return this;
	}

	public ItemBuilder barColor(Function<ItemStackJS, Color> barColor) {
		this.barColor = barColor;
		return this;
	}

	public ItemBuilder barWidth(ToIntFunction<ItemStackJS> barWidth) {
		this.barWidth = barWidth;
		return this;
	}

	public ItemBuilder food(Consumer<FoodBuilder> b) {
		foodBuilder = new FoodBuilder();
		b.accept(foodBuilder);
		return this;
	}

	public Item.Properties createItemProperties() {
		var properties = new Item.Properties();

		if (group != null) {
			properties.tab(group);
		}

		if (maxDamage > 0) {
			properties.durability(maxDamage);
		} else {
			properties.stacksTo(maxStackSize);
		}

		properties.rarity(rarity);

		var item = KubeJSRegistries.items().get(new ResourceLocation(containerItem));

		if (item != Items.AIR) {
			properties.craftRemainder(item);
		}

		if (foodBuilder != null) {
			properties.food(foodBuilder.build());
		}

		return properties;
	}

	public ItemBuilder modifyAttribute(ResourceLocation attribute, String identifier, double d, AttributeModifier.Operation operation) {
		attributes.put(attribute, new AttributeModifier(new UUID(identifier.hashCode(), identifier.hashCode()), identifier, d, operation));
		return this;
	}

	public ItemBuilder useAnimation(UseAnim animation) {
		this.anim = animation;
		return this;
	}

	public ItemBuilder useDuration(ToIntFunction<ItemStackJS> useDuration) {
		this.useDuration = useDuration;
		return this;
	}

	public ItemBuilder use(UseCallback use) {
		this.use = use;
		return this;
	}

	public ItemBuilder finishUsing(FinishUsingCallback finishUsing) {
		this.finishUsing = finishUsing;
		return this;
	}

	public ItemBuilder releaseUsing(ReleaseUsingCallback releaseUsing) {
		this.releaseUsing = releaseUsing;
		return this;

	public static class IndexedItemColor implements ItemColor {
		Int2IntOpenHashMap colors = new Int2IntOpenHashMap();

		public IndexedItemColor() {
			colors.defaultReturnValue(0xFFFFFFFF);
		}

		@Override
		public int getColor(@NotNull ItemStack stack, int tintIndex) {
			return colors.get(tintIndex);
		}

		public void add(int tintIndex, Color color) {
			colors.put(tintIndex, color.getRgbKJS());
		}
	}

	@FunctionalInterface
	public interface DynamicItemColor extends ItemColor {
		Color getColor(ItemStackJS stackJS, int tintIndex);

		default int getColor(@NotNull ItemStack stack, int tintIndex) {
			return getColor(new ItemStackJS(stack), tintIndex).getRgbKJS();
		}
	}
}