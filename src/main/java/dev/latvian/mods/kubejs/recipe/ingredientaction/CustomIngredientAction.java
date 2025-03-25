package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.ModifyCraftingItemKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public record CustomIngredientAction(String id) implements IngredientAction {
	public static final IngredientActionType<CustomIngredientAction> TYPE = new IngredientActionType<>(KubeJS.id("custom"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("id").forGetter(CustomIngredientAction::id)
	).apply(instance, CustomIngredientAction::new)), ByteBufCodecs.STRING_UTF8.map(CustomIngredientAction::new, CustomIngredientAction::id));

	@Override
	public IngredientActionType<?> getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingInput input) {
		return ((ItemStack) ServerEvents.MODIFY_RECIPE_INGREDIENT.post(ScriptType.SERVER, id, new ModifyCraftingItemKubeEvent(input, old, index)).value()).copy();
	}
}
