package dev.latvian.mods.kubejs.web.local.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.JsonOps;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.apps.tinyserver.http.response.error.client.BadRequestError;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UUIDWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.CachedComponentObject;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.NameProvider;
import dev.latvian.mods.kubejs.web.JsonContent;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import dev.latvian.mods.kubejs.web.LocalWebServerAPIRegistry;
import dev.latvian.mods.kubejs.web.LocalWebServerRegistry;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class KubeJSClientWeb {
	private static final Lazy<CreativeModeTab> SEARCH_TAB = Lazy.of(() -> BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.SEARCH));

	private static final Lazy<Map<Item, NameProvider<ItemStack>>> ITEM_NAME_PROVIDERS = Lazy.identityMap(map -> KubeJSPlugins.forEachPlugin(map::put, KubeJSPlugin::registerItemNameProviders));

	public static Map<UUID, CachedComponentObject<Item, ItemStack>> createItemSearch(boolean useSearchTab) {
		var map = new LinkedHashMap<UUID, CachedComponentObject<Item, ItemStack>>();

		if (useSearchTab) {
			for (var stack : SEARCH_TAB.get().getSearchTabDisplayItems()) {
				var obj = CachedComponentObject.ofItemStack(stack, false);
				map.put(obj.cacheKey(), obj);
			}
		} else {
			for (var item : BuiltInRegistries.ITEM) {
				if (item != Items.AIR) {
					var obj = CachedComponentObject.ofItemStack(item.getDefaultInstance(), false);
					map.put(obj.cacheKey(), obj);
				}
			}
		}

		return map;
	}

	public static Map<CachedComponentObject<Item, ItemStack>, UUID> createReverseItemSearch(Map<UUID, CachedComponentObject<Item, ItemStack>> original) {
		var map = new HashMap<CachedComponentObject<Item, ItemStack>, UUID>();
		original.forEach((uuid, obj) -> map.put(obj, uuid));
		return map;
	}

	public static void registerAPIs(LocalWebServerAPIRegistry registry) {
		registry.register(KubeJS.id("translate"), 1);
		registry.register(KubeJS.id("translate"), 1);
		registry.register(KubeJS.id("translate"), 1);
		registry.register(KubeJS.id("translate"), 1);
		registry.register(KubeJS.id("translate"), 1);
		registry.register(KubeJS.id("translate"), 1);
	}

	public static void register(LocalWebServerRegistry registry) {
		registry.get("/api/client/translate/{key}", KubeJSClientWeb::getTranslate);
		registry.get("/api/client/component-string/{json}", KubeJSClientWeb::getComponentString);

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

	public static void registerWithAuth(LocalWebServerRegistry registry) {
		KubeJSWeb.addScriptTypeEndpoints(registry, ScriptType.CLIENT, KubeJSClientWeb::reloadClientScripts);
	}

	private static void reloadClientScripts() {
		KubeJS.getClientScriptManager().reload();
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

	private static HTTPResponse getTranslate(KJSHTTPRequest req) {
		return HTTPResponse.ok().text(I18n.get(req.variable("key").asString()));
	}

	private static HTTPResponse getComponentString(KJSHTTPRequest req) {
		return HTTPResponse.ok().text(ComponentSerialization.FLAT_CODEC.decode(req.registries().java(), req.variable("json")).getOrThrow().getFirst().getString());
	}

	private static HTTPResponse getSearchItems(KJSHTTPRequest req) {
		var level = Minecraft.getInstance().level;
		var jsonOps = level == null ? req.registries().json() : level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
		var nbtOps = level == null ? req.registries().nbt() : level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
		var results = new JsonArray();
		var itemSearch = createItemSearch(level != null);
		var search = req.query("search").asString().toLowerCase(Locale.ROOT);
		var includeTags = req.query("tags").asBoolean(false);
		var renderIcons = req.query("render-icons").asInt(0);

		if (renderIcons > 1024) {
			throw new BadRequestError("render-icons value too large, max 1024!");
		}

		var localPath = "local/kubejs/cache/web/img/item/";
		var iconPathRoot = renderIcons > 0 ? (KubeJSPaths.GAMEDIR.toUri() + localPath) : (LocalWebServer.instance().url() + "/img/64/item/");

		if (renderIcons > 0) {
			var futures = new ArrayList<CompletableFuture<Void>>(itemSearch.size());

			try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
				for (var item : itemSearch.values()) {
					futures.add(CompletableFuture.runAsync(() -> item.iconPath().setValue(ImageGenerator.renderItem(req, renderIcons, item.stack(), false).pathStr()), executor));
				}

				CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			}
		}

		var registries = level != null ? level.registryAccess() : RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

		return HTTPResponse.ok().content(JsonContent.object(json -> {
			json.addProperty("world", level != null);

			for (var item : itemSearch.values()) {
				var o = new JsonObject();

				var nameProvider = ITEM_NAME_PROVIDERS.get().get(item.value());
				var nameProviderName = nameProvider == null ? null : nameProvider.getName(registries, item.stack());
				var name = (nameProviderName == null ? item.stack().getHoverName() : nameProviderName).getString();

				if (!search.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(search)) {
					continue;
				}

				o.addProperty("cache_key", UUIDWrapper.toString(item.cacheKey()));
				o.addProperty("id", item.value().kjs$getId());
				o.addProperty("name", name);
				o.addProperty("icon_path", renderIcons > 0 ? item.iconPath().getValue().substring(localPath.length()) : item.stack().kjs$getWebIconURL(nbtOps, 64).fullString().substring(iconPathRoot.length()));

				var block = item.value() instanceof BlockItem b ? b.getBlock() : Blocks.AIR;

				if (block != Blocks.AIR) {
					o.addProperty("block", block.kjs$getId());
				}

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

				if (includeTags) {
					var tags = new JsonArray();

					for (var t : item.value().builtInRegistryHolder().tags().toList()) {
						tags.add(t.location().toString());
					}

					if (!tags.isEmpty()) {
						o.add("tags", tags);
					}
				}

				results.add(o);
			}

			json.addProperty("icon_path_root", iconPathRoot);
			json.add("results", results);
		}));
	}

	private static HTTPResponse getSearchBlocks(KJSHTTPRequest req) {
		var includeTags = req.query("tags").asBoolean(false);

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var block : BuiltInRegistries.BLOCK) {
				var o = new JsonObject();
				o.addProperty("id", block.kjs$getId());
				o.addProperty("name", Component.translatable(block.getDescriptionId()).getString());

				if (includeTags) {
					var tags = new JsonArray();

					for (var t : block.builtInRegistryHolder().tags().toList()) {
						tags.add(t.location().toString());
					}

					if (!tags.isEmpty()) {
						o.add("tags", tags);
					}
				}

				json.add(o);
			}
		}));
	}

	private static HTTPResponse getSearchFluids(KJSHTTPRequest req) {
		var includeTags = req.query("tags").asBoolean(false);

		return HTTPResponse.ok().content(JsonContent.array(json -> {
			for (var fluid : BuiltInRegistries.FLUID) {
				var o = new JsonObject();
				o.addProperty("id", fluid.kjs$getId());
				o.addProperty("name", fluid.getFluidType().getDescription().getString());

				if (includeTags) {
					var tags = new JsonArray();

					for (var t : fluid.builtInRegistryHolder().tags().toList()) {
						tags.add(t.location().toString());
					}

					if (!tags.isEmpty()) {
						o.add("tags", tags);
					}
				}

				json.add(o);
			}
		}));
	}

	private static HTTPResponse getAssetList(KJSHTTPRequest req) {
		var prefix = req.variable("prefix").asString();

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
