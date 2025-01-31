package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.BaseProperties;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

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
	public String publicAddress;
	public String auth;

	private WebServerProperties() {
		super(KubeJSPaths.WEB_SERVER_PROPERTIES, "KubeJS Web Server Properties");
	}

	@Override
	protected void load() {
		enabled = get("enabled", true);
		port = get("port", 61423);
		publicAddress = get("public_address", "");

		var randomAuth = new byte[33];
		new Random().nextBytes(randomAuth);
		auth = get("auth", new String(Base64.getUrlEncoder().encode(randomAuth), StandardCharsets.UTF_8));
	}
}