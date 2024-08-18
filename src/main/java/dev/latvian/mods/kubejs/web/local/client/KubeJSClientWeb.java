package dev.latvian.mods.kubejs.web.local.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.CachedComponentObject;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.web.JsonContent;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KubeJSClientWeb {
	public static final Lazy<Map<UUID, CachedComponentObject<Item>>> CACHED_ITEM_SEARCH = Lazy.of(() -> {
		var map = new HashMap<UUID, CachedComponentObject<Item>>();

		for (var stack : BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.SEARCH).getSearchTabDisplayItems()) {

			var patch = stack.getComponentsPatch();

			if (!patch.isEmpty()) {
				map.put(UUID.randomUUID(), new CachedComponentObject<>(UUID.randomUUID(), stack.getItem(), patch));
			}
		}

		return map;
	});

	public static final Lazy<Map<CachedComponentObject, UUID>> REVERSE_CACHED_ITEM_SEARCH = Lazy.of(() -> {
		var map = new HashMap<CachedComponentObject, UUID>();
		CACHED_ITEM_SEARCH.get().forEach((uuid, obj) -> map.put(obj, uuid));
		return map;
	});

	public static void register(ServerRegistry<KJSHTTPRequest> registry) {
		KubeJSWeb.addScriptTypeEndpoints(registry, ScriptType.CLIENT, KubeJS.getClientScriptManager()::reload);

		registry.get("/api/client/search/items", KubeJSClientWeb::getItemsResponse);
		registry.get("/api/client/search/blocks", KubeJSClientWeb::getBlocksResponse);
		registry.get("/api/client/search/fluids", KubeJSClientWeb::getFluidsResponse);

		registry.get("/api/client/assets/list/<prefix>", KubeJSClientWeb::getAssetList);
		registry.get("/api/client/assets/get/{namespace}/<path>", KubeJSClientWeb::getAssetContent);

		registry.get("/img/{size}/item/{namespace}/{path}", ImageGenerator::item);
		registry.get("/img/{size}/block/{namespace}/{path}", ImageGenerator::block);
		registry.get("/img/{size}/fluid/{namespace}/{path}", ImageGenerator::fluid);
		registry.get("/img/{size}/item-tag/{namespace}/{path}", ImageGenerator::itemTag);
		registry.get("/img/{size}/block-tag/{namespace}/{path}", ImageGenerator::blockTag);
		registry.get("/img/{size}/fluid-tag/{namespace}/{path}", ImageGenerator::fluidTag);
	}

	private static HTTPResponse getItemsResponse(KJSHTTPRequest req) {
		return HTTPResponse.ok().content(JsonContent.array(json -> {
			var ops = Minecraft.getInstance().level == null ? req.registries().json() : Minecraft.getInstance().level.registryAccess().createSerializationContext(JsonOps.INSTANCE);

			for (var stack : BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.SEARCH).getSearchTabDisplayItems()) {
				var o = new JsonObject();
				o.addProperty("id", stack.kjs$getId());
				o.addProperty("name", stack.getHoverName().getString());

				var patch = stack.getComponentsPatch();

				if (!patch.isEmpty()) {
					var p = new JsonObject();

					try {
						for (var entry : patch.entrySet()) {
							var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey()).toString();

							if (entry.getValue().isEmpty()) {
								p.add(key, JsonNull.INSTANCE);
							} else if (entry.getKey().codec() != null) {
								p.add(key, entry.getKey().codec().encodeStart(ops, Cast.to(entry.getValue().get())).getOrThrow());
							}
						}

						o.add("patch", p);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				json.add(o);
			}
		}));
	}

	private static HTTPResponse getBlocksResponse(KJSHTTPRequest req) {
		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var block : BuiltInRegistries.BLOCK) {
				var o = new JsonObject();
				o.addProperty("id", block.kjs$getId());
				o.addProperty("name", Component.translatable(block.getDescriptionId()).getString());
				json.add(o);
			}
		}));
	}

	private static HTTPResponse getFluidsResponse(KJSHTTPRequest req) {
		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var fluid : BuiltInRegistries.FLUID) {
				var o = new JsonObject();
				o.addProperty("id", fluid.kjs$getId());
				o.addProperty("name", fluid.getFluidType().getDescription().getString());
				json.add(o);
			}
		}));
	}

	private static HTTPResponse getAssetList(KJSHTTPRequest req) {
		var prefix = req.variables().get("prefix");

		if (prefix.isEmpty()) {
			return HTTPStatus.BAD_REQUEST;
		}

		return HTTPResponse.ok().content(JsonContent.object(json -> {
			for (var id : Minecraft.getInstance().getResourceManager().listResources(prefix, id -> true).keySet()) {
				var arr = (JsonArray) json.get(id.getNamespace());

				if (arr == null) {
					arr = new JsonArray();
					json.add(id.getNamespace(), arr);
				}

				arr.add(id.getPath().substring(prefix.length() + 1));
			}
		}));
	}

	private static HTTPResponse getAssetContent(KJSHTTPRequest req) throws Exception {
		var id = req.id();
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
