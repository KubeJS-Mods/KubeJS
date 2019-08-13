package dev.latvian.kubejs.world;

import net.minecraft.world.GameRules;

import java.util.Arrays;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GameRulesJS
{
	private final GameRules rules;

	public GameRulesJS(GameRules r)
	{
		rules = r;
	}

	public String[] array()
	{
		return rules.getRules();
	}

	public List<String> list()
	{
		return Arrays.asList(array());
	}

	public String getString(String rule)
	{
		return rules.getString(rule);
	}

	public boolean getBoolean(String rule)
	{
		return rules.getBoolean(rule);
	}

	public int getInt(String rule)
	{
		return rules.getInt(rule);
	}

	public void set(String rule, Object value)
	{
		rules.setOrCreateGameRule(rule, String.valueOf(value));
	}
}