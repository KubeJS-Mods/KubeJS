package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Note: predicateType has to be able to be cast to {@link java.util.function.Predicate} of entryType
 */
public class RecipeViewerEntryType {
	public static final RecipeViewerEntryType ITEM = new RecipeViewerEntryType("item", ItemStackJS.TYPE_INFO, ItemPredicate.TYPE_INFO, ItemStackJS.ITEM_TYPE_INFO) {
		@Override
		public Object wrapEntry(Context cx, Object from) {
			return ItemStackJS.wrap(((KubeJSContext) cx).getRegistries(), from);
		}

		@Override
		public Object wrapPredicate(Context cx, Object from) {
			return ItemPredicate.wrap(cx, from);
		}

		@Override
		public Object getBase(Object from) {
			return ((ItemStack) from).getItem();
		}
	};

	public static final RecipeViewerEntryType FLUID = new RecipeViewerEntryType("fluid", FluidWrapper.TYPE_INFO, FluidWrapper.INGREDIENT_TYPE_INFO, FluidWrapper.FLUID_TYPE_INFO) {
		@Override
		public Object wrapEntry(Context cx, Object from) {
			return FluidWrapper.wrap(((KubeJSContext) cx).getRegistries(), from);
		}

		@Override
		public Object wrapPredicate(Context cx, Object from) {
			return FluidWrapper.wrapIngredient(((KubeJSContext) cx).getRegistries(), from);
		}

		@Override
		public Object getBase(Object from) {
			return ((FluidStack) from).getFluid();
		}
	};

	public static Lazy<Map<String, RecipeViewerEntryType>> CUSTOM_TYPES = Lazy.of(() -> {
		var map = new HashMap<String, RecipeViewerEntryType>();
		KubeJSPlugins.forEachPlugin(t -> map.put(t.id, t), KubeJSPlugin::registerRecipeViewerEntryTypes);
		return Map.copyOf(map);
	});

	public static final Lazy<List<RecipeViewerEntryType>> ALL_TYPES = Lazy.of(() -> {
		var list = new ArrayList<RecipeViewerEntryType>();
		list.add(ITEM);
		list.add(FLUID);
		list.addAll(CUSTOM_TYPES.get().values());
		return List.copyOf(list);
	});

	public static RecipeViewerEntryType fromString(@Nullable Object id) {
		return switch (id == null ? "" : id.toString()) {
			case null -> null;
			case "" -> null;
			case "item" -> ITEM;
			case "fluid" -> FLUID;
			default -> CUSTOM_TYPES.get().get(String.valueOf(id));
		};
	}

	public static final Extra<RecipeViewerEntryType> EXTRA = Extra.create(RecipeViewerEntryType.class).transformer(RecipeViewerEntryType::fromString).identity();

	public final String id;
	public final TypeInfo entryType;
	public final TypeInfo predicateType;
	public final TypeInfo baseClass;

	public RecipeViewerEntryType(String id, TypeInfo entryType, TypeInfo predicateType, TypeInfo baseClass) {
		this.id = id;
		this.entryType = entryType;
		this.predicateType = predicateType;
		this.baseClass = baseClass;
	}

	public RecipeViewerEntryType(String id, TypeInfo entryType, TypeInfo predicateType) {
		this(id, entryType, predicateType, TypeInfo.NONE);
	}

	public Object wrapEntry(Context cx, Object from) {
		return cx.jsToJava(from, entryType);
	}

	public Object wrapPredicate(Context cx, Object from) {
		return cx.jsToJava(from, predicateType);
	}

	public Object getBase(Object from) {
		return from;
	}
}
