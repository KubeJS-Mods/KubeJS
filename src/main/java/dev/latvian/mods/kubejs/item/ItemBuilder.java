package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.registry.ModelledBuilderBase;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@ReturnsSelf
public class ItemBuilder extends ModelledBuilderBase<Item> implements ItemBehaviorFunctions {

	public transient Map<Object, Object> components;
	public transient int maxStackSize;
	public transient int maxDamage;
	public transient int burnTime;
	private ResourceLocation containerItem;
	public transient Function<ItemStack, Collection<ItemStack>> subtypes;
	public transient Rarity rarity;
	public transient boolean fireResistant;
	@Nullable
	public transient ItemTintFunction tint;
	public transient FoodBuilder foodBuilder;
	public transient JukeboxPlayable jukeboxPlayable;
	public transient final ItemBehavior behavior = new ItemBehavior();

	public transient Tool tool;
	public transient ItemAttributeModifiers itemAttributeModifiers;
	public transient boolean canRepair;

	public ItemBuilder(ResourceLocation id) {
		super(id);
		this.baseTexture = id.withPath(ID.ITEM).toString();

		this.maxStackSize = -1;
		this.maxDamage = 0;
		this.burnTime = 0;
		this.containerItem = null;
		this.subtypes = null;
		this.rarity = null;
		this.foodBuilder = null;
		this.fireResistant = false;

		this.tool = null;
		this.itemAttributeModifiers = null;
		this.canRepair = true;
	}

	@Override
	public Item createObject() {
		return new Item(createItemProperties());
	}

	@Override
	public Item transformObject(Item obj) {
		displayName(displayName, formattedDisplayName);
		obj.kjs$setItemBehavior(behavior);
		return obj;
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		generateItemModels(generator);
	}

	protected void generateItemModels(KubeAssetGenerator generator) {
		generator.itemModel(id, m -> {
			if (modelGenerator != null) {
				modelGenerator.accept(m);
				return;
			}

			m.parent(parentModel != null ? parentModel : KubeAssetGenerator.GENERATED_ITEM_MODEL);

			if (textures.isEmpty()) {
				m.texture("layer0", baseTexture);
			} else {
				m.textures(textures);
			}
		});
	}

	public <T> ItemBuilder component(DataComponentType<T> type, T value) {
		if (components == null) {
			components = new HashMap<>();
		}

		components.put(type, value);
		return this;
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
	public ItemBuilder burnTime(TickDuration v) {
		burnTime = v.intTicks();
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

	@Info("""
		Set the food properties of the item.
		""")
	public ItemBuilder food(Consumer<FoodBuilder> b) {
		if (foodBuilder == null) {
			foodBuilder = new FoodBuilder();
		}

		b.accept(foodBuilder);
		foodEaten(foodBuilder.eaten);
		return this;
	}

	@Info("""
		Set the food nutrition and saturation of the item.
		""")
	public ItemBuilder food(int nutrition, float saturation) {
		return food(b -> b.nutrition(nutrition).saturation(saturation));
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

	@Override
	public ItemBehavior kjs$getOrCreateBehavior() {
		return behavior;
	}

	public ItemBuilder jukeboxPlayable(ResourceKey<JukeboxSong> song, boolean showInTooltip) {
		this.jukeboxPlayable = new JukeboxPlayable(new EitherHolder<>(song), showInTooltip);
		return this;
	}

	public ItemBuilder jukeboxPlayable(ResourceKey<JukeboxSong> song) {
		return jukeboxPlayable(song, true);
	}

	public ItemBuilder disableRepair() {
		this.canRepair = false;
		return this;
	}

	public Item.Properties createItemProperties() {
		var properties = new KubeJSItemProperties(this);

		if (components != null && !components.isEmpty()) {
			for (var entry : components.entrySet()) {
				var type = DataComponentWrapper.wrapType(entry.getKey());

				if (type != null) {
					properties.component((DataComponentType) type, entry.getValue());
				} else {
					ConsoleJS.STARTUP.error("Component '" + entry.getKey() + "' not found for item " + id);
				}
			}
		}

		if (maxDamage > 0) {
			properties.durability(maxDamage);
		} else if (maxStackSize != -1) {
			properties.stacksTo(maxStackSize);
		}

		if (rarity != null) {
			properties.rarity(rarity);
		}

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

		if (tool != null) {
			properties.component(DataComponents.TOOL, tool);
		}

		if (itemAttributeModifiers != null) {
			properties.attributes(itemAttributeModifiers);
		}

		if (jukeboxPlayable != null) {
			properties.component(DataComponents.JUKEBOX_PLAYABLE, jukeboxPlayable);
		}

		if (!canRepair) {
			properties.setNoRepair();
		}

		return properties;
	}
}