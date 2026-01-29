package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerType;

public class VillagerTypeBuilder extends BuilderBase<VillagerType> {
	public VillagerTypeBuilder(Identifier i) {
		super(i);
	}

	@Override
	public VillagerType createObject() {
		return new VillagerType();
	}

}
