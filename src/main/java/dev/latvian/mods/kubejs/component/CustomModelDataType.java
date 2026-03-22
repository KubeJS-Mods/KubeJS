package dev.latvian.mods.kubejs.component;

import net.minecraft.world.item.component.CustomModelData;

import java.util.List;

public enum CustomModelDataType {
	FLOAT {
		@Override
		public CustomModelData apply(CustomModelData existing, List<?> values) {
			var floats = values.stream().map(v -> ((Number) v).floatValue()).toList();
			return new CustomModelData(floats, existing.flags(), existing.strings(), existing.colors());
		}
	},
	BOOLEAN {
		@Override
		public CustomModelData apply(CustomModelData existing, List<?> values) {
			return new CustomModelData(existing.floats(), List.copyOf((List<Boolean>) values), existing.strings(), existing.colors());
		}
	},
	STRING {
		@Override
		public CustomModelData apply(CustomModelData existing, List<?> values) {
			return new CustomModelData(existing.floats(), existing.flags(), List.copyOf((List<String>) values), existing.colors());
		}
	},
	COLOR {
		@Override
		public CustomModelData apply(CustomModelData existing, List<?> values) {
			var colors = values.stream().map(v -> ((Number) v).intValue()).toList();
			return new CustomModelData(existing.floats(), existing.flags(), existing.strings(), colors);
		}
	};

	public static final CustomModelData EMPTY = new CustomModelData(List.of(), List.of(), List.of(), List.of());

	public abstract CustomModelData apply(CustomModelData existing, List<?> values);
}
