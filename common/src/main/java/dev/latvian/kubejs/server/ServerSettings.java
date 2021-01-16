package dev.latvian.kubejs.server;

import com.google.gson.JsonObject;

/**
 * @author LatvianModder
 */
public class ServerSettings
{
	public static ServerSettings instance;

	public boolean dataPackOutput = false;
	public boolean logAddedRecipes = false;
	public boolean logRemovedRecipes = false;
	public boolean logSkippedRecipes = false;
	public boolean logErroringRecipes = true;

	public transient JsonObject dataExport = new JsonObject();
}