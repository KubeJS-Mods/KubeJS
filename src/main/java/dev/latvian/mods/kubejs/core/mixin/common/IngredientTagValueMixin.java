package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(targets = "net/minecraft/world/item/crafting/Ingredient$TagValue")
public abstract class IngredientTagValueMixin {
	@Shadow
	@Final
	private TagKey<Item> tag;

	@Inject(method = "getItems", at = @At("HEAD"), cancellable = true)
	private void kjs$getItems(CallbackInfoReturnable<Collection<ItemStack>> info) {
		if (RecipesKubeEvent.instance != null) {
			info.setReturnValue(TagContext.INSTANCE.getValue().patchIngredientTags(tag));
		}
	}
}
