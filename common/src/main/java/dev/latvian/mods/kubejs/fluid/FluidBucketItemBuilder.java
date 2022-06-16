package dev.latvian.mods.kubejs.fluid;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BucketItem;

import java.util.function.Supplier;

public class FluidBucketItemBuilder extends ItemBuilder {
	public final FluidBuilder fluidBuilder;

	public FluidBucketItemBuilder(FluidBuilder b) {
		super(b.newID("", "_bucket"));
		fluidBuilder = b;
		maxStackSize(1);
	}

	@Override
	public BucketItem createObject() {
		return new ArchitecturyBucketItem(fluidBuilder, createItemProperties());
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.itemModel(id, m -> m.parent("kubejs:item/generated_bucket"));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void clientRegistry(Supplier<Minecraft> minecraft) {
		if (fluidBuilder.bucketColor != 0xFFFFFFFF) {
			ColorHandlerRegistry.registerItemColors((stack, index) -> index == 1 ? fluidBuilder.bucketColor : 0xFFFFFFFF, this);
		}
	}
}
