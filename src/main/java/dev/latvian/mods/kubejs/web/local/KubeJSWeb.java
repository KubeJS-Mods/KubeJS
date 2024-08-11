package dev.latvian.mods.kubejs.web.local;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.web.KJSHTTPContext;
import dev.latvian.mods.kubejs.web.WebServerRegistry;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import dev.latvian.mods.kubejs.web.http.SimpleHTTPResponse;
import dev.latvian.mods.kubejs.web.ws.WSHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public class KubeJSWeb {
	public static WSHandler UPDATES = WSHandler.EMPTY;

	public static void addScriptTypeEndpoints(WebServerRegistry<KJSHTTPContext> registry, ScriptType s) {
		var path = "api/console/" + s.name;

		s.console.wsBroadcaster = registry.ws(path + "/stream", () -> new ConsoleWSSession(s.console));

		registry.acceptPostString(path + "/info", s.console::info);
		registry.acceptPostString(path + "/warn", s.console::warn);
		registry.acceptPostString(path + "/error", s.console::error);
		registry.get(path + "/errors", s.console::getErrorsResponse);
		registry.get(path + "/warnings", s.console::getWarningsResponse);
	}

	public static void register(WebServerRegistry<KJSHTTPContext> registry) {
		UPDATES = registry.ws("updates");

		addScriptTypeEndpoints(registry, ScriptType.STARTUP);
		addScriptTypeEndpoints(registry, ScriptType.SERVER);

		registry.acceptPostTask("api/reload/startup", KubeJS.getStartupScriptManager()::reload);
		registry.acceptPostTask("api/reload/server", KubeJSWeb::reloadInternalServer);

		registry.get("api/registries", KubeJSWeb::getRegistriesResponse); // List of all registries
		registry.get("api/registries/{namespace}/{path}/keys", KubeJSWeb::getRegistryKeysResponse); // List of all IDs in registry

		registry.get("api/tags/{namespace}/{path}", KubeJSWeb::getTagsResponse); // List of all tags in registry
		registry.get("api/tags/{namespace}/{path}/values/{tag-namespace}/{tag-path}", KubeJSWeb::getTagValuesResponse); // List of all values in a tag
		registry.get("api/tags/{namespace}/{path}/keys/{value-namespace}/{value-path}", KubeJSWeb::getTagKeysResponse); // List of all tags for a value
	}

	private static void reloadInternalServer() {
		var mc = Minecraft.getInstance();

		if (mc.player != null) {
			mc.player.kjs$runCommand("/reload");
		}
	}

	private static HTTPResponse getRegistriesResponse(KJSHTTPContext ctx) {
		return SimpleHTTPResponse.lazyJson(() -> {
			var json = new JsonArray();

			for (var registry : ctx.registries().access().registries().toList()) {
				json.add(registry.key().location().toString());
			}

			return json;
		});
	}

	private static HTTPResponse getRegistryKeysResponse(KJSHTTPContext ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		return SimpleHTTPResponse.lazyJson(() -> {
			var json = new JsonArray();

			for (var key : registry.get().keySet()) {
				json.add(key.toString());
			}

			return json;
		});
	}

	private static HTTPResponse getTagsResponse(KJSHTTPContext ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		return SimpleHTTPResponse.lazyJson(() -> {
			var json = new JsonArray();

			for (var tag : registry.get().getTagNames().map(TagKey::location).toList()) {
				json.add(tag.toString());
			}

			return json;
		});
	}

	private static HTTPResponse getTagValuesResponse(KJSHTTPContext ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		var tagKey = registry.get().getTag(TagKey.create(registry.get().key(), ctx.id("tag-namespace", "tag-path")));

		if (tagKey.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		return SimpleHTTPResponse.lazyJson(() -> {
			var json = new JsonArray();

			for (var key : tagKey.get().stream().map(Holder::unwrapKey).filter(Optional::isPresent).map(Optional::get).map(ResourceKey::location).toList()) {
				json.add(key.toString());
			}

			return json;
		});
	}

	private static HTTPResponse getTagKeysResponse(KJSHTTPContext ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		var value = registry.get().getHolder(ctx.id("value-namespace", "value-path"));

		if (value.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		return SimpleHTTPResponse.lazyJson(() -> {
			var json = new JsonArray();

			for (var key : value.get().tags().map(TagKey::location).toList()) {
				json.add(key.toString());
			}

			return json;
		});
	}
}
