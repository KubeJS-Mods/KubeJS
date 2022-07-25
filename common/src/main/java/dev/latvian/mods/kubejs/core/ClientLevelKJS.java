package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.multiplayer.ClientLevel;

@RemapPrefixForJS("kjs$")
public interface ClientLevelKJS extends LevelKJS {
	@Override
	default ClientLevel kjs$self() {
		return (ClientLevel) this;
	}

	@Override
	default ScriptType kjs$getSide() {
		return ScriptType.CLIENT;
	}

	@Override
	default EntityArrayList kjs$getEntities() {
		return new EntityArrayList(kjs$self(), kjs$self().entitiesForRendering());
	}
}
