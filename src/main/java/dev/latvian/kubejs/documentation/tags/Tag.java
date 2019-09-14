package dev.latvian.kubejs.documentation.tags;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class Tag extends TagBase
{
	public final String name;
	protected Map<String, String> attributes;

	public Tag(String n)
	{
		name = n;
	}

	@Override
	public String getAttribute(String key)
	{
		return attributes == null || attributes.isEmpty() ? "" : attributes.getOrDefault(key, "");
	}

	public Tag attr(String key, String value)
	{
		if (attributes == null)
		{
			attributes = new LinkedHashMap<>();
		}

		attributes.put(key, value);
		return this;
	}

	public Tag title(String title)
	{
		return attr("title", title);
	}

	public Tag style(String key, String value)
	{
		attr("style", getAttribute("style") + key + ':' + value + ';');
		return this;
	}

	public Tag addClass(String c)
	{
		if (c.isEmpty())
		{
			return this;
		}

		String s = getAttribute("class");
		attr("class", s.isEmpty() ? c : (s + " " + c));
		return this;
	}

	public <T extends TagBase> T append(T child)
	{
		return child;
	}

	public Tag text(Object txt)
	{
		String text = String.valueOf(txt);

		if (!text.isEmpty())
		{
			append(new TextTag(text));
		}

		return this;
	}

	public Tag paired(String tag, String text)
	{
		return append(new PairedTag(tag, text));
	}

	public Tag paired(String tag)
	{
		return paired(tag, "");
	}

	public Tag unpaired(String tag)
	{
		return append(new UnpairedTag(tag));
	}

	public Tag h1(String text)
	{
		return paired("h1", text);
	}

	public Tag h2(String text)
	{
		return paired("h2", text);
	}

	public Tag h3(String text)
	{
		return paired("h3", text);
	}

	public Tag p(String text)
	{
		return paired("p", text);
	}

	public Tag p()
	{
		return p("");
	}

	public Tag a(String text, String url)
	{
		return paired("a", text).attr("href", url);
	}

	public Tag img(String img)
	{
		return unpaired("img").attr("src", img);
	}

	public Tag span(String text, String c)
	{
		return paired("span", text).addClass(c);
	}

	public Tag br()
	{
		return unpaired("br");
	}

	public Tag ul()
	{
		return paired("ul");
	}

	public Tag ol()
	{
		return paired("ol");
	}

	public Tag li()
	{
		return paired("li");
	}

	public Tag table()
	{
		return paired("table");
	}

	public Tag tr()
	{
		return paired("tr");
	}

	public Tag th()
	{
		return paired("th");
	}

	public Tag td()
	{
		return paired("td");
	}
}