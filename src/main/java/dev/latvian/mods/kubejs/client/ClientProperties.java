package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.BaseProperties;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

@NullUnmarked
public class ClientProperties extends BaseProperties {
	private static @Nullable ClientProperties instance;

	public static ClientProperties get() {
		if (instance == null) {
			instance = new ClientProperties();
		}

		return instance;
	}

	public static void reload() {
		instance = new ClientProperties();
	}


	public String windowTitle;
	public boolean disableRecipeBook;
	public boolean exportAtlases;
	public boolean blurScaledPackIcon;
	public boolean customStackSizeText;
	public boolean shrinkStackSizeText;

	private ClientProperties() {
		super(KubeJSPaths.CLIENT_PROPERTIES, "KubeJS Client Properties");
	}

	@Override
	protected void load() {
		windowTitle = get("window_title", "");
		disableRecipeBook = get("disable_recipe_book", false);
		exportAtlases = get("export_atlases", false);
		blurScaledPackIcon = get("blur_scaled_pack_icon", true);
		customStackSizeText = get("custom_stack_size_text", true);
		shrinkStackSizeText = get("shrink_stack_size_text", true);
	}
}