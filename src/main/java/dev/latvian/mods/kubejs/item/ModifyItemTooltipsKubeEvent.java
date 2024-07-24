package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.text.action.TextActionBuilder;
import dev.latvian.mods.kubejs.text.tooltip.ItemTooltipData;
import dev.latvian.mods.kubejs.text.tooltip.TooltipRequirements;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ModifyItemTooltipsKubeEvent implements KubeEvent {
	private final Consumer<ItemTooltipData> callback;

	public ModifyItemTooltipsKubeEvent(Consumer<ItemTooltipData> callback) {
		this.callback = callback;
	}

	private void modify(@Nullable Ingredient filter, Optional<TooltipRequirements> requirements, Consumer<TextActionBuilder> consumer) {
		var builder = new TextActionBuilder();
		consumer.accept(builder);
		callback.accept(new ItemTooltipData(filter == null || filter.isEmpty() || filter.kjs$isWildcard() ? Optional.empty() : Optional.of(filter), requirements, List.copyOf(builder.actions)));
	}

	public void modify(Ingredient filter, TooltipRequirements requirements, Consumer<TextActionBuilder> consumer) {
		modify(filter, Optional.ofNullable(requirements), consumer);
	}

	public void modify(Ingredient filter, Consumer<TextActionBuilder> consumer) {
		modify(filter, Optional.empty(), consumer);
	}

	public void modifyAll(TooltipRequirements requirements, Consumer<TextActionBuilder> consumer) {
		modify(null, Optional.ofNullable(requirements), consumer);
	}

	public void modifyAll(Consumer<TextActionBuilder> consumer) {
		modify(null, Optional.empty(), consumer);
	}

	public void add(Ingredient filter, List<Component> text) {
		modify(filter, builder -> builder.add(text));
	}

	public void add(Ingredient filter, TooltipRequirements requirements, List<Component> text) {
		modify(filter, requirements, builder -> builder.add(text));
	}
}