package dev.latvian.mods.kubejs.integration.jei;

/**
 * @author LatvianModder
 */

/*
public class InformationJEIEventJS extends EventJS {
	private final IIngredientManager manager;
	private final List<IngredientInfoRecipe<?>> recipes;

	public InformationJEIEventJS(IIngredientManager m, List<IngredientInfoRecipe<?>> l) {
		manager = m;
		recipes = l;
	}

	public void add(Object o, Text[] s) {
		recipes.addAll(IngredientInfoRecipe.create(
				IngredientJS.of(o).getStacks().stream().map(ItemStackJS::getItemStack).collect(Collectors.toList()),
				VanillaTypes.ITEM,
				Arrays.stream(s).map(Text::component).toArray(Component[]::new)));
	}

	public <T> void addForType(IIngredientType<T> type, Object o, Text[] s) {
		Set<String> targets = ListJS.orSelf(o).stream().map(String::valueOf).collect(Collectors.toSet());
		IIngredientHelper<T> helper = manager.getIngredientHelper(type);
		recipes.addAll(IngredientInfoRecipe.create(
				manager.getAllIngredients(type)
						.stream()
						.filter(t -> targets.contains(helper.getWildcardId(t)))
						.collect(Collectors.toList()),
				type, Arrays.stream(s).map(Text::component).toArray(Component[]::new)));
	}

}

*/