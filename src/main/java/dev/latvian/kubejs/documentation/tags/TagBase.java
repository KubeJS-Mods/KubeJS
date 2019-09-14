package dev.latvian.kubejs.documentation.tags;

/**
 * @author LatvianModder
 */
public abstract class TagBase
{
	public abstract void build(StringBuilder builder);

	public abstract String getAttribute(String key);
}