package dev.latvian.kubejs.documentation.tags;

/**
 * @author LatvianModder
 */
public class TextTag extends TagBase
{
	private String text;

	public TextTag(String s)
	{
		text = s;
	}

	@Override
	public void build(StringBuilder builder)
	{
		if (!text.isEmpty())
		{
			builder.append(text.replace("<", "&lt;").replace(">", "&gt;"));
		}
	}

	@Override
	public String getAttribute(String key)
	{
		return "";
	}
}