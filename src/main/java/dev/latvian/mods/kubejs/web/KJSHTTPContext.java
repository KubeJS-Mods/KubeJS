package dev.latvian.mods.kubejs.web;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.web.http.HTTPContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
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

	public DataComponentPatch queryAsPatch(DynamicOps<Tag> ops) throws CommandSyntaxException {
		if (query().isEmpty()) {
			return DataComponentPatch.EMPTY;
		}

		var builder = DataComponentPatch.builder();

		for (var entry : query().entrySet()) {
			var dataComponentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse(entry.getKey()));

			if (dataComponentType != null && !dataComponentType.isTransient()) {
				var dataResult = dataComponentType.codecOrThrow().parse(ops, new TagParser(new StringReader(entry.getValue())).readValue());

				if (dataResult.isSuccess() && dataResult.result().isPresent()) {
					builder.set(dataComponentType, Cast.to(dataResult.result().get()));
				}
			}
		}

		return builder.build();
	}
}
