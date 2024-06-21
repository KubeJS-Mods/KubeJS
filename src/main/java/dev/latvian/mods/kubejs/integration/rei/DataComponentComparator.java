package dev.latvian.mods.kubejs.integration.rei;

import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;

import java.util.List;
import java.util.Objects;

public record DataComponentComparator(List<DataComponentType<?>> components) implements EntryComparator<DataComponentHolder> {
	public static DataComponentComparator EMPTY = new DataComponentComparator(List.of());

	public static DataComponentComparator of(List<DataComponentType<?>> components) {
		return components.isEmpty() ? EMPTY : new DataComponentComparator(components);
	}

	@Override
	public long hash(ComparisonContext context, DataComponentHolder holder) {
		long hash = 1L;

		if (components.isEmpty()) {
			for (var component : holder.getComponents()) {
				hash = hash * 31 + (Objects.hashCode(component.type()) ^ Objects.hashCode(component.value()));
			}
		} else {
			for (var type : components) {
				hash = hash * 31 + (Objects.hashCode(type) ^ Objects.hashCode(holder.get(type)));
			}
		}

		return hash;
	}
}
