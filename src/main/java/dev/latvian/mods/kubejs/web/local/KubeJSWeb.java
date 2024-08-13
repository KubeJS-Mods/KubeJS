package dev.latvian.mods.kubejs.web.local;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.apps.tinyserver.ws.WSHandler;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.web.JsonContent;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import dev.latvian.mods.kubejs.web.KJSWSSession;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Supplier;

public class KubeJSWeb {
	public static WSHandler<KJSHTTPRequest, KJSWSSession> UPDATES = WSHandler.empty();

	public static void broadcastEvent(WSHandler<KJSHTTPRequest, KJSWSSession> ws, String event, Supplier<JsonElement> payload) {
		ws.broadcastText(() -> {
			var json = new JsonObject();
			json.addProperty("event", event);

			var p = payload == null ? null : payload.get();

			if (p != null && !p.isJsonNull()) {
				json.add("payload", p);
			}

			return json.toString();
		});
	}

	public static void broadcastUpdate(String event, Supplier<JsonElement> payload) {
		broadcastEvent(UPDATES, event, payload);
	}

	public static void addScriptTypeEndpoints(ServerRegistry<KJSHTTPRequest> registry, ScriptType s) {
		var path = "api/console/" + s.name;

		s.console.wsBroadcaster = registry.ws(path + "/stream", () -> new ConsoleWSSession(s.console));

		registry.acceptPostString(path + "/info", s.console::info);
		registry.acceptPostString(path + "/warn", s.console::warn);
		registry.acceptPostString(path + "/error", s.console::error);
		registry.get(path + "/errors", s.console::getErrorsResponse);
		registry.get(path + "/warnings", s.console::getWarningsResponse);
	}

	public static void register(ServerRegistry<KJSHTTPRequest> registry) {
		UPDATES = registry.ws("updates", KJSWSSession::new);

		addScriptTypeEndpoints(registry, ScriptType.STARTUP);
		addScriptTypeEndpoints(registry, ScriptType.SERVER);

		registry.acceptPostTask("api/reload/startup", KubeJS.getStartupScriptManager()::reload);
		registry.acceptPostTask("api/reload/server", KubeJSWeb::reloadInternalServer);

		registry.get("api/registries", KubeJSWeb::getRegistriesResponse); // List of all registries
		registry.get("api/registries/{namespace}/{path}/keys", KubeJSWeb::getRegistryKeysResponse); // List of all IDs in registry
		registry.get("api/registries/{namespace}/{path}/match/{regex}", KubeJSWeb::getRegistryMatchResponse); // List of RegEx matched IDs in registry

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

	private static HTTPResponse getRegistriesResponse(KJSHTTPRequest ctx) {
		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var registry : ctx.registries().access().registries().toList()) {
				json.add(registry.key().location().toString());
			}
		}));
	}

	private static HTTPResponse getRegistryKeysResponse(KJSHTTPRequest ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var key : registry.get().keySet()) {
				json.add(key.toString());
			}
		}));
	}

	private static HTTPResponse getRegistryMatchResponse(KJSHTTPRequest ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var regex = RegExpKJS.ofString(ctx.variables().get("regex"));

		if (regex == null) {
			return HTTPStatus.BAD_REQUEST;
		}

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var key : registry.get().keySet()) {
				var k = key.toString();

				if (regex.matcher(k).find()) {
					json.add(k);
				}
			}
		}));
	}

	private static HTTPResponse getTagsResponse(KJSHTTPRequest ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var tag : registry.get().getTagNames().map(TagKey::location).toList()) {
				json.add(tag.toString());
			}
		}));
	}

	private static HTTPResponse getTagValuesResponse(KJSHTTPRequest ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var tagKey = registry.get().getTag(TagKey.create(registry.get().key(), ctx.id("tag-namespace", "tag-path")));

		if (tagKey.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var key : tagKey.get().stream().map(Holder::unwrapKey).filter(Optional::isPresent).map(Optional::get).map(ResourceKey::location).toList()) {
				json.add(key.toString());
			}
		}));
	}

	private static HTTPResponse getTagKeysResponse(KJSHTTPRequest ctx) {
		var registry = ctx.registries().access().registry(ResourceKey.createRegistryKey(ctx.id()));

		if (registry.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var value = registry.get().getHolder(ctx.id("value-namespace", "value-path"));

		if (value.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var key : value.get().tags().map(TagKey::location).toList()) {
				json.add(key.toString());
			}
		}));
	}
}
