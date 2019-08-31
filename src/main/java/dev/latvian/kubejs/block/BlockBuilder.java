package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.ID;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockBuilder
{
	public final ID id;
	private final Consumer<BlockBuilder> callback;
	public MaterialJS material;
	public String translationKey;
	public float hardness;
	public float resistance;
	public float lightLevel;
	public String harvestTool;
	public int harvestLevel;
	public boolean opaque;
	public boolean fullBlock;

	public BlockBuilder(String i, Consumer<BlockBuilder> c)
	{
		id = new ID(KubeJS.appendModId(i));
		callback = c;
		material = MaterialListJS.INSTANCE.map.get("wood");
		translationKey = id.namespace + "." + id.path;
		hardness = 0.5F;
		resistance = -1F;
		lightLevel = 0F;
		harvestTool = "";
		harvestLevel = 0;
		opaque = true;
		fullBlock = false;
	}

	public BlockBuilder material(MaterialJS m)
	{
		material = m;
		return this;
	}

	public BlockBuilder translationKey(String key)
	{
		translationKey = key;
		return this;
	}

	public BlockBuilder hardness(float h)
	{
		hardness = h;
		return this;
	}

	public BlockBuilder resistance(float r)
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

	public BlockBuilder lightLevel(float light)
	{
		lightLevel = light;
		return this;
	}

	public BlockBuilder harvestTool(String tool, int level)
	{
		harvestTool = tool;
		harvestLevel = level;
		return this;
	}

	public BlockBuilder opaque(boolean o)
	{
		opaque = o;
		return this;
	}

	public BlockBuilder fullBlock(boolean f)
	{
		fullBlock = f;
		return this;
	}

	public void add()
	{
		callback.accept(this);
	}
}