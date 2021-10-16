package dev.latvian.kubejs.integration.rei;

/**
 * @author shedaniel
 */
public class REIPlugin {/* implements REIClientPlugin {
	private final Set<ResourceLocation> categoriesRemoved = new HashSet<>();

	@Override
	public ResourceLocation getPluginIdentifier() {
		return new ResourceLocation(KubeJS.MOD_ID, "rei");
	}

	@Override
	public void registerEntries(EntryRegistry entryRegistry) {
		Function<Object, Collection<EntryStack>> itemSerializer = o -> EntryStack.ofItemStacks(CollectionUtils.map(IngredientJS.of(o).getStacks(), ItemStackJS::getItemStack));

		new HideREIEventJS<>(entryRegistry, EntryStack.Type.ITEM, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_ITEMS);
		new AddREIEventJS(entryRegistry, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_ADD_ITEMS);
	}

	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		new InformationREIEventJS().post(ScriptType.CLIENT, REIIntegration.REI_INFORMATION);
	}

	@Override
	public void registerOthers(RecipeHelper recipeHelper) {
		recipeHelper.registerRecipeVisibilityHandler((category, display) -> {
			return categoriesRemoved.contains(category.getIdentifier()) ? InteractionResult.FAIL : InteractionResult.PASS;
		});
	}

	@Override
	public void postRegister() {
		categoriesRemoved.clear();
		new RemoveREICategoryEventJS(categoriesRemoved).post(ScriptType.CLIENT, REIIntegration.REI_REMOVE_CATEGORIES);
	}
		*/
}