package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerTypeBuilder extends BuilderBase<VillagerType> {
	public VillagerTypeBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public VillagerType createObject() {
		return new VillagerType(id.getPath());
	}
}
