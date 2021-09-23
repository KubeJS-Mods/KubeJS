package dev.latvian.kubejs.fluid;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.util.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * @author LatvianModder
 */
public class FluidBuilder extends BuilderBase {
	public String stillTexture;
	public String flowingTexture;
	public int color = 0xFFFFFFFF;
	public int bucketColor = 0xFFFFFFFF;
	public int luminosity = 0;
	public int density = 1000;
	public int temperature = 300;
	public int viscosity = 1000;
	public boolean isGaseous;
	public RarityWrapper rarity = RarityWrapper.COMMON;
	public Object extraPlatformInfo;

	public FlowingFluid stillFluid;
	public FlowingFluid flowingFluid;
	public BucketItem bucketItem;
	public LiquidBlock block;

	private JsonObject blockstateJson;
	private JsonObject blockModelJson;

	public FluidBuilder(String i) {
		super(i);
		textureStill(KubeJS.id("fluid/fluid_thin"));
		textureFlowing(KubeJS.id("fluid/fluid_thin_flow"));
	}

	@Override
	public String getBuilderType() {
		return "fluid";
	}

	public FluidBuilder color(int c) {
		color = c;

		if ((color & 0xFFFFFF) == color) {
			color |= 0xFF000000;
		}

		return bucketColor(color);
	}

	public FluidBuilder bucketColor(int c) {
		bucketColor = c;

		if ((bucketColor & 0xFFFFFF) == bucketColor) {
			bucketColor |= 0xFF000000;
		}

		return this;
	}

	public FluidBuilder textureStill(ResourceLocation id) {
		stillTexture = id.toString();
		return this;
	}

	public FluidBuilder textureFlowing(ResourceLocation id) {
		flowingTexture = id.toString();
		return this;
	}

	public FluidBuilder textureThick(int color) {
		return textureStill(KubeJS.id("fluid/fluid_thick")).textureFlowing(KubeJS.id("fluid/fluid_thick_flow")).color(color);
	}

	public FluidBuilder textureThin(int color) {
		return textureStill(KubeJS.id("fluid/fluid_thin")).textureFlowing(KubeJS.id("fluid/fluid_thin_flow")).color(color);
	}

	public FluidBuilder luminosity(int luminosity) {
		this.luminosity = luminosity;
		return this;
	}

	public FluidBuilder density(int density) {
		this.density = density;
		return this;
	}

	public FluidBuilder temperature(int temperature) {
		this.temperature = temperature;
		return this;
	}

	public FluidBuilder viscosity(int viscosity) {
		this.viscosity = viscosity;
		return this;
	}

	public FluidBuilder gaseous() {
		isGaseous = true;
		return this;
	}

	public FluidBuilder rarity(RarityWrapper rarity) {
		this.rarity = rarity;
		return this;
	}

	public void setBlockstateJson(JsonObject o) {
		blockstateJson = o;
	}

	public JsonObject getBlockstateJson() {
		if (blockstateJson == null) {
			blockstateJson = new JsonObject();
			JsonObject variants = new JsonObject();
			JsonObject modelo = new JsonObject();
			modelo.addProperty("model", newID("block/", "").toString());
			variants.add("", modelo);
			blockstateJson.add("variants", variants);
		}

		return blockstateJson;
	}

	public void setBlockModelJson(JsonObject o) {
		blockModelJson = o;
	}

	public JsonObject getBlockModelJson() {
		if (blockModelJson == null) {
			blockModelJson = new JsonObject();
			JsonObject textures = new JsonObject();
			textures.addProperty("particle", stillTexture);
			blockModelJson.add("textures", textures);
			return blockModelJson;
		}

		return blockModelJson;
	}
}