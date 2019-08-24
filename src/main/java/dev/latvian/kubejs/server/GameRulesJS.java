package dev.latvian.kubejs.server;

import net.minecraft.world.GameRules;

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