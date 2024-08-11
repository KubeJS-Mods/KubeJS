package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.BaseProperties;
import dev.latvian.mods.kubejs.KubeJSPaths;

public class WebServerProperties extends BaseProperties {
	private static WebServerProperties instance;

	public static WebServerProperties get() {
		if (instance == null) {
			instance = new WebServerProperties();
		}

		return instance;
	}

	public static void reload() {
		instance = new WebServerProperties();
	}

	public boolean enabled;
	public int port;
	public boolean isPublic;
	public String publicAddress;

	private WebServerProperties() {
		super(KubeJSPaths.CLIENT_PROPERTIES, "KubeJS Web Server Properties");
	}

	@Override
	protected void load() {
		enabled = get("enabled", true);
		port = get("port", 61423);
		isPublic = get("public", false);
		publicAddress = get("public_address", "");
	}
}