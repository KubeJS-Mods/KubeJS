package dev.latvian.kubejs.util;

/**
 * @author LatvianModder
 */
public interface Copyable {
	Copyable copy();

	@Deprecated
	default Copyable getCopy() {
		return copy();
	}
}