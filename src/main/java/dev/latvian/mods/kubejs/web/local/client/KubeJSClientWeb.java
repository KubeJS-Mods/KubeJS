package dev.latvian.mods.kubejs.web.local.client;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.web.KJSHTTPContext;
import dev.latvian.mods.kubejs.web.WebServerRegistry;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import dev.latvian.mods.kubejs.web.http.SimpleHTTPResponse;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import net.minecraft.client.Minecraft;

public class KubeJSClientWeb {
	public static void register(WebServerRegistry<KJSHTTPContext> registry) {
		KubeJSWeb.addScriptTypeEndpoints(registry, ScriptType.CLIENT);

		registry.acceptPostTask("api/reload/client", KubeJS.getClientScriptManager()::reload);

		registry.get("assets", KubeJSClientWeb::getAssets);
		registry.get("assets/{namespace}/<path>", KubeJSClientWeb::getAsset);

		registry.get("img/{size}/item/{namespace}/{path}", ImageGenerator::item);
		registry.get("img/{size}/block/{namespace}/{path}", ImageGenerator::block);
		registry.get("img/{size}/fluid/{namespace}/{path}", ImageGenerator::fluid);
		registry.get("img/{size}/item-tag/{namespace}/{path}", ImageGenerator::itemTag);
		registry.get("img/{size}/block-tag/{namespace}/{path}", ImageGenerator::blockTag);
		registry.get("img/{size}/fluid-tag/{namespace}/{path}", ImageGenerator::fluidTag);
	}

	private static HTTPResponse getAssets(KJSHTTPContext ctx) {
		return SimpleHTTPResponse.lazyJson(() -> {
			var json = new JsonArray();

			for (var id : Minecraft.getInstance().getResourceManager().listPacks().toList()) {
				json.add(id.toString());
			}

			return json;
		});
	}

	private static HTTPResponse getAsset(KJSHTTPContext ctx) throws Exception {
		var id = ctx.id();
		var asset = Minecraft.getInstance().getResourceManager().getResource(id);

		if (asset.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		try (var in = asset.get().open()) {
			if (id.getPath().endsWith(".png")) {
				return new SimpleHTTPResponse(200, in.readAllBytes(), "image/png");
			} else if (id.getPath().endsWith(".json") || id.getPath().endsWith(".mcmeta")) {
				return new SimpleHTTPResponse(200, in.readAllBytes(), "application/json; charset=utf-8");
			} else if (id.getPath().endsWith(".ogg")) {
				return new SimpleHTTPResponse(200, in.readAllBytes(), "audio/ogg");
			} else {
				return new SimpleHTTPResponse(200, in.readAllBytes(), "text/plain");
			}
		}
	}
}
