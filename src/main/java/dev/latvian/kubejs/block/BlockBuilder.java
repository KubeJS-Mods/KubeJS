package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockBuilder
{
	public final ResourceLocation id;
	private final Consumer<BlockBuilder> callback;
	@Ignore
	public MaterialJS material;
	@Ignore
	public float hardness;
	@Ignore
	public float resistance;
	@Ignore
	public float lightLevel;
	@Ignore
	public ToolType harvestTool;
	@Ignore
	public int harvestLevel;
	@Ignore
	public boolean opaque;
	@Ignore
	public boolean fullBlock;
	@Ignore
	public BlockRenderLayer layer;

	public BlockBuilder(String i, Consumer<BlockBuilder> c)
	{
		id = UtilsJS.getID(KubeJS.appendModId(i));
		callback = c;
		material = MaterialListJS.INSTANCE.map.get("wood");
		hardness = 0.5F;
		resistance = -1F;
		lightLevel = 0F;
		harvestTool = null;
		harvestLevel = -1;
		opaque = true;
		fullBlock = false;
		layer = BlockRenderLayer.SOLID;
	}

	public BlockBuilder material(@P("material") MaterialJS m)
	{
		material = m;
		return this;
	}

	public BlockBuilder hardness(@P("hardness") float h)
	{
		hardness = h;
		return this;
	}

	public BlockBuilder resistance(@P("resistance") float r)
	{
		resistance = r;
		return this;
	}

	public BlockBuilder unbreakable()
	{
		hardness = -1F;
		resistance = Float.MAX_VALUE;
		return this;
	}

	public BlockBuilder lightLevel(@P("light") float light)
	{
		lightLevel = light;
		return this;
	}

	public BlockBuilder harvestTool(@P("tool") ToolType tool, @P("level") int level)
	{
		harvestTool = tool;
		harvestLevel = level;
		return this;
	}

	public BlockBuilder opaque(@P("opaque") boolean o)
	{
		opaque = o;
		return this;
	}

	public BlockBuilder fullBlock(@P("fullBlock") boolean f)
	{
		fullBlock = f;
		return this;
	}

	public BlockBuilder layer(@P("layer") String l)
	{
		switch (l.toLowerCase())
		{
			case "cutout":
				layer = BlockRenderLayer.CUTOUT;
				return this;
			case "cutout_mipped":
			case "mipped_cutout":
				layer = BlockRenderLayer.CUTOUT_MIPPED;
				return this;
			case "translucent":
				layer = BlockRenderLayer.TRANSLUCENT;
				return this;
			default:
				layer = BlockRenderLayer.SOLID;
				return this;
		}
	}

	public void add()
	{
		callback.accept(this);
	}

	public Block.Properties createProperties()
	{
		Block.Properties properties = Block.Properties.create(material.getMinecraftMaterial());
		properties.sound(material.getSound());

		if (resistance >= 0F)
		{
			properties.hardnessAndResistance(hardness, resistance);
		}
		else
		{
			properties.hardnessAndResistance(hardness);
		}

		properties.lightValue((int) (lightLevel * 15F));

		if (harvestTool != null)
		{
			properties.harvestTool(harvestTool);
		}

		if (harvestLevel >= 0)
		{
			properties.harvestLevel(harvestLevel);
		}

		return properties;
	}
}