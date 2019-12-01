package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import mezz.jei.api.registration.ISubtypeRegistration;

import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class AddJEISubtypesEventJS extends EventJS
{
	@FunctionalInterface
	public interface Interpreter extends Function<ItemStackJS, String>
	{
	}

	public final ISubtypeRegistration registration;

	public AddJEISubtypesEventJS(ISubtypeRegistration r)
	{
		registration = r;
	}

	public void register(Object id, Interpreter interpreter)
	{
		registration.registerSubtypeInterpreter(ItemStackJS.of(id).getItem(), stack -> interpreter.apply(ItemStackJS.of(stack)));
	}
}