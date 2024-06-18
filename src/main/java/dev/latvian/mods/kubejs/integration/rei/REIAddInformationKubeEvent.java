package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.AddInformationKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;

public class REIAddInformationKubeEvent implements AddInformationKubeEvent {
	private final RecipeViewerEntryType type;

	public REIAddInformationKubeEvent(RecipeViewerEntryType type) {
		this.type = type;
	}

	@Override
	public void add(Context cx, Object filter, Component[] info) {
		if (info.length == 0) {
			return;
		}

		var in = REIIntegration.ingredientOf(cx, type, filter);

		if (in.isEmpty()) {
			return;
		}

		BuiltinClientPlugin.getInstance().registerInformation(in, info[0], components -> {
			//noinspection ManualArrayToCollectionCopy
			for (int i = 1; i < info.length; i++) {
				components.add(info[i]);
			}

			return components;
		});
	}
}
