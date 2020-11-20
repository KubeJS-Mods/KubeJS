package dev.latvian.kubejs.player;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class AdvancementJS
{
	public final Advancement advancement;

	public AdvancementJS(Advancement a)
	{
		advancement = a;
	}

	@Override
	public boolean equals(Object o)
	{
		return o == this || o instanceof AdvancementJS && advancement.equals(((AdvancementJS) o).advancement);
	}

	@Override
	public int hashCode()
	{
		return advancement.hashCode();
	}

	@Override
	public String toString()
	{
		return advancement.getId().toString();
	}

	public ResourceLocation id()
	{
		return advancement.getId();
	}

	@Nullable
	public AdvancementJS getParent()
	{
		return advancement.getParent() == null ? null : new AdvancementJS(advancement.getParent());
	}

	public Set<AdvancementJS> getChildren()
	{
		Set<AdvancementJS> set = new LinkedHashSet<>();

		for (Advancement a : advancement.getChildren())
		{
			set.add(new AdvancementJS(a));
		}

		return set;
	}

	public void addChild(AdvancementJS a)
	{
		advancement.addChild(a.advancement);
	}

	public Text getDisplayText()
	{
		return Text.of(advancement.getChatComponent());
	}

	public boolean hasDisplay()
	{
		return advancement.getDisplay() != null;
	}

	public Text getTitle()
	{
		return Text.of(advancement.getDisplay() != null ? advancement.getDisplay().getTitle() : new TextString(""));
	}

	public Text getDescription()
	{
		return Text.of(advancement.getDisplay() != null ? advancement.getDisplay().getDescription() : new TextString(""));
	}
}