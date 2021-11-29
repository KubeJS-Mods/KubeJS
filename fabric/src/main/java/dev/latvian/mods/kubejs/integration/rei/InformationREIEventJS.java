package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.text.Text;
import dev.latvian.mods.kubejs.util.ListJS;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;

/**
 * @author shedaniel
 */
public class InformationREIEventJS extends EventJS {
	/**
	 * Registers an information display.
	 *
	 * @param stacks      The target stacks
	 * @param title       The title of the information display
	 * @param description The information to be provided
	 */
	public void add(Object stacks, Component title, Object description) {
		BuiltinClientPlugin.getInstance().registerInformation(
				EntryIngredients.ofItemStacks(CollectionUtils.map(IngredientJS.of(stacks).getStacks(), ItemStackJS::getItemStack)),
				title,
				components -> {
					for (var o : ListJS.orSelf(description)) {
						components.add(Text.of(o).component());
					}
					return components;
				}
		);
	}
}