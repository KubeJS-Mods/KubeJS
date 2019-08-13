package dev.latvian.kubejs.function;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface LogFunction
{
	void func(String text, Object... objects);
}