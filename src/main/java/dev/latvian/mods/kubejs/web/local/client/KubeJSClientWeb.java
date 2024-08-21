package dev.latvian.mods.kubejs.web.local.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.JsonOps;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.UUIDWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.CachedComponentObject;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.web.JsonContent;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import dev.latvian.mods.kubejs.web.LocalWebServerRegistry;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class KubeJSClientWeb {
	public static final Lazy<Map<UUID, CachedComponentObject<Item, ItemStack>>> CACHED_ITEM_SEARCH = Lazy.of(() -> {
		var map = new LinkedHashMap<UUID, CachedComponentObject<Item, ItemStack>>();

		for (var stack : BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.SEARCH).getSearchTabDisplayItems()) {
			var patch = stack.getComponentsPatch();
			map.put(UUID.randomUUID(), new CachedComponentObject<>(UUID.randomUUID(), stack.getItem(), stack, patch));
		}

		return map;
	});

	public static final Lazy<Map<CachedComponentObject, UUID>> REVERSE_CACHED_ITEM_SEARCH = Lazy.of(() -> {
		var map = new HashMap<CachedComponentObject, UUID>();
		CACHED_ITEM_SEARCH.get().forEach((uuid, obj) -> map.put(obj, uuid));
		return map;
	});

	public static void register(LocalWebServerRegistry registry) {
		KubeJSWeb.addScriptTypeEndpoints(registry, ScriptType.CLIENT, KubeJS.getClientScriptManager()::reload);

		registry.get("/api/client/search/items", KubeJSClientWeb::getSearchItems);
		registry.get("/api/client/search/blocks", KubeJSClientWeb::getSearchBlocks);
		registry.get("/api/client/search/fluids", KubeJSClientWeb::getSearchFluids);

		registry.get("/api/client/assets/list/<prefix>", KubeJSClientWeb::getAssetList);
		registry.get("/api/client/assets/get/{namespace}/<path>", KubeJSClientWeb::getAssetContent);

		registry.get("/img/screenshot", KubeJSClientWeb::getScreenshot);

		registry.get("/img/{size}/item/{namespace}/{path}", ImageGenerator::item);
		registry.get("/img/{size}/block/{namespace}/{path}", ImageGenerator::block);
		registry.get("/img/{size}/fluid/{namespace}/{path}", ImageGenerator::fluid);
		registry.get("/img/{size}/item-tag/{namespace}/{path}", ImageGenerator::itemTag);
		registry.get("/img/{size}/block-tag/{namespace}/{path}", ImageGenerator::blockTag);
		registry.get("/img/{size}/fluid-tag/{namespace}/{path}", ImageGenerator::fluidTag);
	}

	private static HTTPResponse getScreenshot(KJSHTTPRequest req) {
		var bytes = req.supplyInMainThread(() -> {
			var mc = Minecraft.getInstance();
			mc.getMainRenderTarget().bindRead();

			try (var image = new NativeImage(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false)) {
				image.downloadTexture(0, true);
				image.flipY();
				return image.asByteArray();
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			} finally {
				mc.getMainRenderTarget().unbindRead();
			}
		});

		return HTTPResponse.ok().content(bytes, "image/png");
	}

	private static HTTPResponse getSearchItems(KJSHTTPRequest req) {
		return HTTPResponse.ok().content(JsonContent.object(json -> {
			var jsonOps = Minecraft.getInstance().level == null ? req.registries().json() : Minecraft.getInstance().level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
			var nbtOps = Minecraft.getInstance().level == null ? req.registries().nbt() : Minecraft.getInstance().level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
			var results = new JsonArray();
			var iconPathRoot = LocalWebServer.instance().url() + "/img/64/item/";

			for (var item : CACHED_ITEM_SEARCH.get().values()) {
				var o = new JsonObject();
				o.addProperty("cache_key", UUIDWrapper.toString(item.cacheKey()));
				o.addProperty("id", item.value().kjs$getId());
				o.addProperty("name", item.stack().getHoverName().getString());
				o.addProperty("icon_path", item.stack().kjs$getWebIconURL(nbtOps, 64).substring(iconPathRoot.length()));

				var patch = item.components();

				if (!patch.isEmpty()) {
					var p = new JsonObject();

					try {
						for (var entry : patch.entrySet()) {
							var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey()).toString();

							if (entry.getValue().isEmpty()) {
								p.add(key, JsonNull.INSTANCE);
							} else if (entry.getKey().codec() != null) {
								p.add(key, entry.getKey().codec().encodeStart(jsonOps, Cast.to(entry.getValue().get())).getOrThrow());
							}
						}

						o.add("components", p);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				var tags = new JsonArray();

				for (var t : item.value().builtInRegistryHolder().tags().toList()) {
					tags.add(t.location().toString());
				}

				if (!tags.isEmpty()) {
					o.add("tags", tags);
				}

				results.add(o);
			}

			json.addProperty("icon_path_root", iconPathRoot);
			json.add("results", results);
		}));
	}

	private static HTTPResponse getSearchBlocks(KJSHTTPRequest req) {
		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var block : BuiltInRegistries.BLOCK) {
				var o = new JsonObject();
				o.addProperty("id", block.kjs$getId());
				o.addProperty("name", Component.translatable(block.getDescriptionId()).getString());

				var tags = new JsonArray();

				for (var t : block.builtInRegistryHolder().tags().toList()) {
					tags.add(t.location().toString());
				}

				if (!tags.isEmpty()) {
					o.add("tags", tags);
				}

				json.add(o);
			}
		}));
	}

	private static HTTPResponse getSearchFluids(KJSHTTPRequest req) {
		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var fluid : BuiltInRegistries.FLUID) {
				var o = new JsonObject();
				o.addProperty("id", fluid.kjs$getId());
				o.addProperty("name", fluid.getFluidType().getDescription().getString());

				var tags = new JsonArray();

				for (var t : fluid.builtInRegistryHolder().tags().toList()) {
					tags.add(t.location().toString());
				}

				if (!tags.isEmpty()) {
					o.add("tags", tags);
				}

				json.add(o);
			}
		}));
	}

	private static HTTPResponse getAssetList(KJSHTTPRequest req) {
		var prefix = req.variable("prefix");

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
