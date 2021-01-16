package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public abstract class LootBuilder<P extends LootBuilderPool, F extends LootBuilderFunction>
{
	private final List<P> pools = new ArrayList<>();
	private final List<F> functions = new ArrayList<>();

	public abstract String getType();

	public abstract P newPool();

	public abstract F newFunction();

	public JsonObject toJson(LootEventJS<?> event)
	{
		JsonObject json = new JsonObject();
		json.addProperty("type", getType());

		if (!pools.isEmpty())
		{
			JsonArray p = new JsonArray();

			for (P pool : pools)
			{
				pool.toJson(event, p);
			}

			json.add("pools", p);
		}

		if (!functions.isEmpty())
		{
			JsonArray f = new JsonArray();

			for (F function : functions)
			{
				function.toJson(event, f);
			}

			json.add("functions", f);
		}

		return json;
	}

	public void pool(Consumer<P> p)
	{
		P pool = newPool();
		p.accept(pool);
		pools.add(pool);
	}

	public void function(Consumer<F> p)
	{
		F function = newFunction();
		p.accept(function);
		functions.add(function);
	}
}
