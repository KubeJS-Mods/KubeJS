package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.core.ClientPacketListenerKJS;
import dev.latvian.mods.kubejs.net.KubeServerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RemoteRecipeViewerDataUpdatedEvent;
import dev.latvian.mods.kubejs.tooltip.ItemTooltipData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KubeSessionData {
	@Nullable
	public static KubeSessionData of(ClientPacketListener listener) {
		return listener == null ? null : ((ClientPacketListenerKJS) listener).kjs$sessionData();
	}

	@Nullable
	public static KubeSessionData of(Minecraft mc) {
		return mc == null ? null : of(mc.getConnection());
	}

	public ResourceLocation activePostShader = null;
	public RecipeViewerData recipeViewerData = null;
	public List<ItemTooltipData> itemTooltips = List.of();

	public void sync(KubeServerData data) {
		recipeViewerData = data.recipeViewerData().orElse(null);
		itemTooltips = List.copyOf(data.itemTooltipData());

		NeoForge.EVENT_BUS.post(new RemoteRecipeViewerDataUpdatedEvent(recipeViewerData));
	}
}
