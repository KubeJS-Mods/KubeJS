package dev.latvian.mods.kubejs.web.ws;

@FunctionalInterface
public interface WSSessionFactory {
	WSSession create();
}
