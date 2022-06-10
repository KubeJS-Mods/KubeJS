package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;

/**
 * @author LatvianModder
 */
public class ServerSettings {
	public static ServerSettings instance;

	public boolean dataPackOutput = false;
	public boolean logAddedRecipes = false;
	public boolean logRemovedRecipes = false;
	public boolean logOverrides = false;
	public boolean logSkippedRecipes = false;
	public boolean logErroringRecipes = true;
	public boolean logInvalidRecipeHandlers = true;
	public boolean useOriginalRecipeForFilters = true;

	@HideFromJS
	public static CommandSourceStack source;
	@HideFromJS
	public static JsonObject dataExport;

	public static void exportData() {
		if (dataExport != null) {
			Util.ioPool().execute(ServerSettings::exportDataBlocking);
		}
	}

	private static <T> void addRegistry(JsonObject o, String name, Registrar<T> r) {
		var a = new JsonArray();

		for (var id : r.getIds()) {
			a.add(id.toString());
		}

		o.add(name, a);
	}

	private static void exportDataBlocking() {
		var registries = new JsonObject();
		addRegistry(registries, "items", KubeJSRegistries.items());
		addRegistry(registries, "blocks", KubeJSRegistries.blocks());
		addRegistry(registries, "fluids", KubeJSRegistries.fluids());
		addRegistry(registries, "entity_types", KubeJSRegistries.entityTypes());
		dataExport.add("registries", registries);

		var errors = new JsonArray();

		for (var s : ScriptType.SERVER.errors) {
			errors.add(s);
		}

		dataExport.add("errors", errors);

		var warnings = new JsonArray();

		for (var s : ScriptType.SERVER.warnings) {
			warnings.add(s);
		}

		dataExport.add("warnings", warnings);

		try (var writer = Files.newBufferedWriter(KubeJSPaths.EXPORTED.resolve("kubejs-server-export.json"))) {
			JsonIO.GSON.toJson(dataExport, writer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		source.sendSuccess(Component.literal("Done! Export in kubejs/exported/kubejs-server-export.json"), false);
		source.sendSuccess(Component.literal("You can now upload it on ").append(Component.literal("https://export.kubejs.com/").withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://export.kubejs.com/")))), false);
		source = null;
		dataExport = null;
	}
}