package dev.latvian.mods.kubejs.web.local.client;

import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.web.JsonContent;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import net.minecraft.client.Minecraft;

public class KubeJSClientWeb {
	public static void register(ServerRegistry<KJSHTTPRequest> registry) {
		KubeJSWeb.addScriptTypeEndpoints(registry, ScriptType.CLIENT);

		registry.acceptPostTask("api/reload/client", KubeJS.getClientScriptManager()::reload);

		registry.get("assets/list/<prefix>", KubeJSClientWeb::getAssetList);
		registry.get("assets/get/{namespace}/<path>", KubeJSClientWeb::getAssetContent);

		registry.get("img/{size}/item/{namespace}/{path}", ImageGenerator::item);
		registry.get("img/{size}/block/{namespace}/{path}", ImageGenerator::block);
		registry.get("img/{size}/fluid/{namespace}/{path}", ImageGenerator::fluid);
		registry.get("img/{size}/item-tag/{namespace}/{path}", ImageGenerator::itemTag);
		registry.get("img/{size}/block-tag/{namespace}/{path}", ImageGenerator::blockTag);
		registry.get("img/{size}/fluid-tag/{namespace}/{path}", ImageGenerator::fluidTag);
	}

	private static HTTPResponse getAssetList(KJSHTTPRequest ctx) {
		var prefix = ctx.variables().get("prefix");

		if (prefix.isEmpty()) {
			return HTTPStatus.BAD_REQUEST;
		}

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var id : Minecraft.getInstance().getResourceManager().listResources(prefix, id -> true).keySet()) {
				json.add(id.toString());
			}
		}));
	}

	private static HTTPResponse getAssetContent(KJSHTTPRequest ctx) throws Exception {
		var id = ctx.id();
		var asset = Minecraft.getInstance().getResourceManager().getResource(id);

		if (asset.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		try (var in = asset.get().open()) {
			if (id.getPath().endsWith(".png")) {
				return HTTPResponse.ok().content(in.readAllBytes(), "image/png");
			} else if (id.getPath().endsWith(".json") || id.getPath().endsWith(".mcmeta")) {
				return HTTPResponse.ok().content(in.readAllBytes(), "application/json; charset=utf-8");
			} else if (id.getPath().endsWith(".ogg")) {
				return HTTPResponse.ok().content(in.readAllBytes(), "audio/ogg");
			} else {
				return HTTPResponse.ok().content(in.readAllBytes(), "text/plain");
			}
		}
	}
}
