package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.web.http.HTTPContext;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class KJSHTTPContext extends HTTPContext {
	public RegistryAccessContainer registries() {
		return RegistryAccessContainer.current;
	}

	public void runInRenderThread(Runnable task) {
		Minecraft.getInstance().executeBlocking(task);
	}

	public <T> T supplyInRenderThread(Supplier<T> task) {
		return CompletableFuture.supplyAsync(task, Minecraft.getInstance()).join();
	}

	public ResourceLocation id(String ns, String path) {
		return ResourceLocation.fromNamespaceAndPath(variables().get(ns), variables().get(path));
	}

	public ResourceLocation id() {
		return id("namespace", "path");
	}
}
