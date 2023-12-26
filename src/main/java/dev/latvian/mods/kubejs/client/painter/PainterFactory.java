package dev.latvian.mods.kubejs.client.painter;

@FunctionalInterface
public interface PainterFactory {
	PainterObject create(Painter painter);
}
