package dev.latvian.kubejs.block;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
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
	public String renderType;
	@Ignore
	public Int2IntOpenHashMap color;
	@Ignore
	public final JsonObject textures;
	@Ignore
	public String model;

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
		renderType = "solid";
		color = new Int2IntOpenHashMap();
		color.defaultReturnValue(0xFFFFFFFF);
		textures = new JsonObject();
		texture(id.getNamespace() + ":block/" + id.getPath());
		model = id.getNamespace() + ":block/" + id.getPath();
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

	public BlockBuilder renderType(@P("layer") String l)
	{
		renderType = l;
		return this;
	}

	public BlockBuilder color(int index, int c)
	{
		color.put(index, 0xFF000000 | c);
		return this;
	}

	public BlockBuilder texture(String tex)
	{
		for (Direction direction : Direction.values())
		{
			textures.addProperty(direction.getName(), tex);
		}

		textures.addProperty("particle", tex);
		return this;
	}

	public BlockBuilder texture(Direction direction, String tex)
	{
		textures.addProperty(direction.getName(), tex);
		return this;
	}

	public BlockBuilder model(String m)
	{
		model = m;
		return this;
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