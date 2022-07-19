package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.level.gen.AddWorldgenEventJS;
import dev.latvian.mods.kubejs.level.gen.RemoveWorldgenEventJS;

public interface WorldgenEvents {
	EventGroup GROUP = EventGroup.of("WorldgenEvents");
	EventHandler ADD = GROUP.startup("add", () -> AddWorldgenEventJS.class).legacy("worldgen.add");
	EventHandler REMOVE = GROUP.startup("remove", () -> RemoveWorldgenEventJS.class).legacy("worldgen.remove");
}
