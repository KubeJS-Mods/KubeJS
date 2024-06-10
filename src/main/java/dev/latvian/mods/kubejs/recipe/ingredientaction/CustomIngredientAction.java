package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public record CustomIngredientAction(String id) implements IngredientAction {
	public static final Map<String, CustomIngredientActionCallback> MAP = new HashMap<>();

	public static final IngredientActionType TYPE = new IngredientActionType("custom", RecordCodecBuilder.<CustomIngredientAction>mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("id").forGetter(CustomIngredientAction::id)
	).apply(instance, CustomIngredientAction::new)));

	@Override
	public IngredientActionType getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		var callback = MAP.get(id);
		return callback == null ? ItemStack.EMPTY : callback.transform(old, index, container).copy();
	}
}
