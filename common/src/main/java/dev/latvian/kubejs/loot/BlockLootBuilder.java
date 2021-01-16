package dev.latvian.kubejs.loot;

import com.google.gson.JsonObject;

/**
 * @author LatvianModder
 */
public class BlockLootBuilder extends LootBuilder<BlockLootBuilder.Pool, BlockLootBuilder.Function>
{
	@Override
	public String getType()
	{
		return "minecraft:block";
	}

	@Override
	public Pool newPool()
	{
		return new Pool();
	}

	@Override
	public Function newFunction()
	{
		return new Function();
	}

	public static class Pool extends LootBuilderPool
	{
		public void survivesExplosion()
		{
			JsonObject json = new JsonObject();
			json.addProperty("condition", "minecraft:survives_explosion");
			addCondition(json);
		}
	}

	public static class Function extends LootBuilderFunction
	{
	}
}
