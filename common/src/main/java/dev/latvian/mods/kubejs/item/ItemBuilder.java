package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonObject;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.util.BuilderBase;
import dev.latvian.mods.rhino.mod.util.color.Color;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
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

	public transient int maxStackSize;
	public transient int maxDamage;
	public transient int burnTime;
	public transient String containerItem;
	public transient Function<ItemStackJS, Collection<ItemStackJS>> subtypes;
	public transient Rarity rarity;
	public transient boolean glow;
	public transient final List<Component> tooltip;
	public transient CreativeModeTab group;
	public transient Int2IntOpenHashMap color;
	public String texture;
	public String parentModel;
	public transient FoodBuilder foodBuilder;

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
		color = new Int2IntOpenHashMap();
		color.defaultReturnValue(0xFFFFFFFF);
		texture = "";
		parentModel = "";
		foodBuilder = null;
		modelJson = null;
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
		generator.itemModel(id, m -> {
			if (!parentModel.isEmpty()) {
				m.parent(parentModel);
			} else {
				m.parent("minecraft:item/generated");
			}

			m.texture("layer0", texture.isEmpty() ? newID("item/", "").toString() : texture);
		});
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void clientRegistry(Minecraft minecraft) {
		if (!color.isEmpty()) {
			ColorHandlerRegistry.registerItemColors((stack, index) -> color.get(index), this);
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

	public ItemBuilder group(String g) {
		for (var ig : CreativeModeTab.TABS) {
			if (ig.getRecipeFolderName().equals(g)) {
				group = ig;
				return this;
			}
		}

		return this;
	}

	public ItemBuilder color(int index, Color c) {
		color.put(index, c.getArgbKJS());
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

	public Item.Properties createItemProperties() {
		var properties = new Item.Properties();

		properties.tab(group);

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
}