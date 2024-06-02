package dev.latvian.mods.kubejs.core.mixin;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/world/item/crafting/Ingredient$TagValue")
public abstract class IngredientTagValueMixin {
	/* FIXME
	@Shadow
	@Final
	private TagKey<Item> tag;

	@Inject(method = "getItems", at = @At("HEAD"), cancellable = true)
	private void kjs$getItems(CallbackInfoReturnable<Collection<ItemStack>> info) {
		if (RecipesKubeEvent.instance != null) {
			info.setReturnValue(TagContext.INSTANCE.getValue().patchIngredientTags(tag));
		}
	}
	 */
}
