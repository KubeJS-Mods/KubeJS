package dev.latvian.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedWriter;
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

	public static transient CommandSourceStack source;
	public static transient JsonObject dataExport;

	public static void exportData() {
		if (dataExport != null) {
			Util.ioPool().execute(ServerSettings::exportDataBlocking);
		}
	}

	private static <T> void addRegistry(JsonObject o, String name, Registrar<T> r) {
		JsonArray a = new JsonArray();

		for (ResourceLocation id : r.getIds()) {
			a.add(id.toString());
		}

		o.add(name, a);
	}

	private static void exportDataBlocking() {
		JsonObject registries = new JsonObject();
		addRegistry(registries, "items", KubeJSRegistries.items());
		addRegistry(registries, "blocks", KubeJSRegistries.blocks());
		addRegistry(registries, "fluids", KubeJSRegistries.fluids());
		addRegistry(registries, "entity_types", KubeJSRegistries.entityTypes());
		dataExport.add("registries", registries);

		JsonArray errors = new JsonArray();

		for (String s : ScriptType.SERVER.errors) {
			errors.add(s);
		}

		dataExport.add("errors", errors);

		JsonArray warnings = new JsonArray();

		for (String s : ScriptType.SERVER.warnings) {
			warnings.add(s);
		}

		dataExport.add("warnings", warnings);

		try (BufferedWriter writer = Files.newBufferedWriter(KubeJSPaths.EXPORTED.resolve("kubejs-server-export.json"))) {
			JsonUtilsJS.GSON.toJson(dataExport, writer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		source.sendSuccess(new TextComponent("Done! Export in kubejs/exported/kubejs-server-export.json"), false);
		source.sendSuccess(new TextComponent("You can now upload it on ").append(new TextComponent("https://export.kubejs.com/").withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://export.kubejs.com/")))), false);
		source = null;
		dataExport = null;
	}
}