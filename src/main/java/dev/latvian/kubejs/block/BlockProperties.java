package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.ID;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockProperties
{
	public final ID id;
	public final transient Consumer<BlockProperties> callback;
	public MaterialJS material;
	public String translationKey;
	public float hardness;
	public float resistance;
	public float lightLevel;
	public String harvestTool;
	public int harvestLevel;
	public boolean opaque;
	public boolean fullBlock;

	public BlockProperties(String i, Consumer<BlockProperties> c)
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

	public BlockProperties material(MaterialJS m)
	{
		material = m;
		return this;
	}

	public BlockProperties translationKey(String key)
	{
		translationKey = key;
		return this;
	}

	public BlockProperties hardness(float h)
	{
		hardness = h;
		return this;
	}

	public BlockProperties resistance(float r)
	{
		resistance = r;
		return this;
	}

	public BlockProperties unbreakable()
	{
		hardness = -1F;
		resistance = Float.MAX_VALUE;
		return this;
	}

	public BlockProperties lightLevel(float light)
	{
		lightLevel = light;
		return this;
	}

	public BlockProperties harvestTool(String tool, int level)
	{
		harvestTool = tool;
		harvestLevel = level;
		return this;
	}

	public BlockProperties opaque(boolean o)
	{
		opaque = o;
		return this;
	}

	public BlockProperties fullBlock(boolean f)
	{
		fullBlock = f;
		return this;
	}

	public void register()
	{
		callback.accept(this);
	}
}