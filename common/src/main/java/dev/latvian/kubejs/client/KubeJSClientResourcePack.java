package dev.latvian.kubejs.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.block.DetectorInstance;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;

public class KubeJSClientResourcePack extends KubeJSResourcePack {
	public KubeJSClientResourcePack() {
		super(PackType.CLIENT_RESOURCES);
	}

	@Override
	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
		JsonObject lang = new JsonObject();
		lang.addProperty("itemGroup.kubejs.kubejs", "KubeJS");

		for (BuilderBase builder : KubeJSObjects.ALL) {
			if (!builder.displayName.isEmpty()) {
				lang.addProperty(builder.translationKey, builder.displayName);
			}
		}

		for (DetectorInstance detector : KubeJSObjects.DETECTORS.values()) {
			lang.addProperty("block.kubejs.detector_" + detector.id, "KubeJS Detector [" + detector.id + "]");

			{
				JsonObject blockstate = new JsonObject();
				JsonObject variants = new JsonObject();
				JsonObject pf = new JsonObject();
				pf.addProperty("model", "kubejs:block/detector");
				variants.add("powered=false", pf);
				JsonObject pt = new JsonObject();
				pt.addProperty("model", "kubejs:block/detector_on");
				variants.add("powered=true", pt);
				blockstate.add("variants", variants);
				map.put(new ResourceLocation(KubeJS.MOD_ID, "blockstates/detector_" + detector.id), blockstate);
			}

			{
				JsonObject itemModel = new JsonObject();
				itemModel.addProperty("parent", KubeJS.MOD_ID + ":block/detector");
				map.put(new ResourceLocation(KubeJS.MOD_ID, "models/item/detector_" + detector.id), itemModel);
			}
		}

		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			map.put(new ResourceLocation(builder.id.getNamespace(), "blockstates/" + builder.id.getPath()), builder.getBlockstateJson());
			map.put(new ResourceLocation(builder.id.getNamespace(), "models/block/" + builder.id.getPath()), builder.getModelJson());

			if (builder.itemBuilder != null) {
				map.put(new ResourceLocation(builder.id.getNamespace(), "models/item/" + builder.id.getPath()), builder.itemBuilder.getModelJson());
			}
		}

		for (ItemBuilder builder : KubeJSObjects.ITEMS.values()) {
			map.put(new ResourceLocation(builder.id.getNamespace(), "models/item/" + builder.id.getPath()), builder.getModelJson());
		}

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values()) {
			map.put(new ResourceLocation(builder.id.getNamespace(), "blockstates/" + builder.id.getPath()), builder.getBlockstateJson());
			map.put(new ResourceLocation(builder.id.getNamespace(), "models/block/" + builder.id.getPath()), builder.getBlockModelJson());

			JsonObject bucketModel = new JsonObject();
			bucketModel.addProperty("parent", "kubejs:item/generated_bucket");
			map.put(new ResourceLocation(builder.id.getNamespace(), "models/item/" + builder.id.getPath() + "_bucket"), bucketModel);

			if (!builder.displayName.isEmpty()) {
				lang.addProperty(builder.bucketItem.getDescriptionId(), builder.displayName + " Bucket");
			}
		}

		map.put(new ResourceLocation(KubeJS.MOD_ID, "lang/en_us"), lang);
	}
}
