package dev.latvian.mods.kubejs.web;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import dev.latvian.apps.tinyserver.http.HTTPRequest;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class KJSHTTPRequest extends HTTPRequest {
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

	public DataComponentPatch components(DynamicOps<Tag> ops) throws CommandSyntaxException {
		return DataComponentWrapper.urlDecodePatch(ops, query().getOrDefault("components", ""));
	}
}
