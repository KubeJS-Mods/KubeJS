package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.bindings.ColorWrapper;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.type.TypeInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ItemTintFunction {
	TypeInfo TYPE_INFO = TypeInfo.of(ItemTintFunction.class);

	KubeColor getColor(ItemStack stack, int index);

	record Fixed(KubeColor color) implements ItemTintFunction {
		@Override
		public KubeColor getColor(ItemStack stack, int index) {
			return color;
		}
	}

	class Mapped implements ItemTintFunction {
		public final Int2ObjectMap<ItemTintFunction> map = new Int2ObjectArrayMap<>(1);

		@Override
		public KubeColor getColor(ItemStack stack, int index) {
			var f = map.get(index);
			return f == null ? null : f.getColor(stack, index);
		}
	}

	ItemTintFunction BLOCK = (stack, index) -> {
		if (stack.getItem() instanceof BlockItem block) {
			var s = block.getBlock().defaultBlockState();
			var internal = s.getBlock().kjs$getBlockBuilder();

			if (internal != null && internal.tint != null) {
				return internal.tint.getColor(s, null, null, index);
			}
		}

		return null;
	};

	ItemTintFunction POTION = (stack, index) -> {
		var potion = stack.get(DataComponents.POTION_CONTENTS);

		if (potion != null) {
			return new SimpleColor(potion.getColor());
		}

		return null;
	};

	ItemTintFunction MAP = (stack, index) -> {
		var map = stack.get(DataComponents.MAP_COLOR);

		if (map != null) {
			return new SimpleColor(map.rgb());
		}

		return null;
	};

	ItemTintFunction DISPLAY_COLOR_NBT = (stack, index) -> {
		var color = stack.get(DataComponents.DYED_COLOR);

		if (color != null) {
			return new SimpleColor(color.rgb());
		}

		return null;
	};

	@Nullable
	static ItemTintFunction wrap(Context cx, Object o) {
		if (o == null || Undefined.isUndefined(o)) {
			return null;
		} else if (o instanceof ItemTintFunction f) {
			return f;
		} else if (o instanceof List<?> list) {
			var map = new Mapped();

			for (int i = 0; i < list.size(); i++) {
				var f = wrap(cx, list.get(i));

				if (f != null) {
					map.map.put(i, f);
				}
			}

			return map;
		} else if (o instanceof CharSequence) {
			var f = switch (o.toString()) {
				case "block" -> BLOCK;
				case "potion" -> POTION;
				case "map" -> MAP;
				case "display_color_nbt" -> DISPLAY_COLOR_NBT;
				default -> null;
			};

			if (f != null) {
				return f;
			}
		} else if (o instanceof BaseFunction function) {
			return (ItemTintFunction) cx.createInterfaceAdapter(TYPE_INFO, function);
		}

		return new Fixed(ColorWrapper.wrap(o));
	}
}