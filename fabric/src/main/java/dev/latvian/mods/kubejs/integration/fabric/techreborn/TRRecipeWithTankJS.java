package dev.latvian.mods.kubejs.integration.fabric.techreborn;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;

/**
 * @author LatvianModder
 */
public class TRRecipeWithTankJS extends TRRecipeJS {
	@Override
	public void create(RecipeArguments args) {
		super.create(args);
		tank(args.getString(4, "minecraft:water"), args.getInt(5, 1000));
	}

	public TRRecipeWithTankJS tank(String fluidId, int amount) {
		var o = new JsonObject();
		o.addProperty("fluid", fluidId);
		o.addProperty("amount", amount);
		json.add("tank", o);
		save();
		return this;
	}
}
