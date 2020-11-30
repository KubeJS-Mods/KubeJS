package dev.latvian.kubejs.server;

/**
 * @author LatvianModder
 */
public class ServerSettings
{
	public static ServerSettings instance;

	public boolean dataPackOutput = false;
	public boolean logAddedRecipes = false;
	public boolean logRemovedRecipes = false;
	public boolean betterRecipeErrorLogging = true;
	public boolean logSkippedRecipes = false;
	public boolean logErroringRecipes = true;
	public boolean logRecipeParseErrors = true;
}