package com.latmod.mods.worldjs.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @author LatvianModder
 */
public abstract class Text
{
	private TextColor color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private String insertion;
	private String click;
	private Text hover;

	public abstract ITextComponent rawComponent();

	public abstract Text rawCopy();

	public final ITextComponent component()
	{
		ITextComponent component = rawComponent();

		if (color != null)
		{
			component.getStyle().setColor(color.color);
		}

		component.getStyle().setBold(bold);
		component.getStyle().setItalic(italic);
		component.getStyle().setUnderlined(underlined);
		component.getStyle().setStrikethrough(strikethrough);
		component.getStyle().setObfuscated(obfuscated);
		component.getStyle().setInsertion(insertion);

		if (click != null)
		{
			component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
		}

		if (hover != null)
		{
			component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.component()));
		}

		return component;
	}

	public final Text copy()
	{
		Text t = rawCopy();
		t.color = color;
		t.bold = bold;
		t.italic = italic;
		t.underlined = underlined;
		t.strikethrough = strikethrough;
		t.obfuscated = obfuscated;
		t.insertion = insertion;
		t.click = click;
		t.hover = hover == null ? null : hover.copy();
		return t;
	}

	public Text color(TextColor value)
	{
		color = value;
		return this;
	}

	public Text bold(Boolean value)
	{
		bold = value;
		return this;
	}

	public Text italic(Boolean value)
	{
		italic = value;
		return this;
	}

	public Text underlined(Boolean value)
	{
		underlined = value;
		return this;
	}

	public Text strikethrough(Boolean value)
	{
		strikethrough = value;
		return this;
	}

	public Text obfuscated(Boolean value)
	{
		obfuscated = value;
		return this;
	}

	public Text insertion(String value)
	{
		insertion = value;
		return this;
	}

	public Text click(String value)
	{
		click = value;
		return this;
	}

	public Text hover(Object... value)
	{
		hover = value.length == 0 ? null : TextUtils.INSTANCE.of(value);
		return this;
	}
}