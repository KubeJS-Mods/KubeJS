package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(Ingredient.TagValue.class)
public abstract class IngredientTagValueMixin {
	@Shadow
	@Final
	private TagKey<Item> tag;

	@Inject(method = "getItems", at = @At("HEAD"), cancellable = true)
	private void kjs$getItems(CallbackInfoReturnable<Collection<ItemStack>> info) {
		var lookup = RecipesKubeEvent.TEMP_ITEM_TAG_LOOKUP.getValue();

		if (lookup != null) {
			var values = lookup.values(tag);

			if (values.isEmpty()) {
				throw new RecipeExceptionJS("Empty tag: " + tag.location());
			} else {
				info.setReturnValue(values.stream().map(ItemStack::new).toList());
			}
		}
	}
}
