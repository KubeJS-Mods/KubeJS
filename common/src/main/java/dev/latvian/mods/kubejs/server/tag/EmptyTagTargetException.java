package dev.latvian.mods.kubejs.server.tag;

import java.util.NoSuchElementException;

public class EmptyTagTargetException extends NoSuchElementException {
	public EmptyTagTargetException(String message) {
		super(message);
	}
}
