package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;

/**
 * @author LatvianModder
 */
public class LootBuilderPool
{
	private RandomIntGenerator rolls = new ConstantIntValue(1);
	private final JsonArray conditions = new JsonArray();
	private final JsonArray entries = new JsonArray();

	public void toJson(LootEventJS<?> event, JsonArray array)
	{
		JsonObject json = new JsonObject();
		json.add("rolls", event.gsonConditions.toJsonTree(rolls));

		if (conditions.size() > 0)
		{
			json.add("conditions", conditions);
		}

		if (entries.size() > 0)
		{
			json.add("entries", entries);
		}

		array.add(json);
	}

	public void setRolls(int r)
	{
		rolls = new ConstantIntValue(r);
	}

	public void setUniformRolls(float min, float max)
	{
		rolls = new RandomValueBounds(min, max);
	}

	public void setBinomialRolls(int n, float p)
	{
		rolls = new BinomialDistributionGenerator(n, p);
	}

	public void addCondition(Object o)
	{
		conditions.add(MapJS.json(o));
	}

	public void addEntry(Object o)
	{
		entries.add(MapJS.json(o));
	}

	public void addItem(ResourceLocation item)
	{
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:item");
		json.addProperty("name", item.toString());
		addEntry(json);
	}
}
