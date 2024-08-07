package dev.latvian.mods.kubejs.web;

import org.jetbrains.annotations.Nullable;

public record CompiledPath(Part[] parts, int variables, boolean wildcard) {
	public static final CompiledPath EMPTY = new CompiledPath(new Part[0], 0, false);

	public record Part(String name, boolean variable) {
		public boolean matches(String string) {
			return variable || name.equals(string);
		}
	}

	public static CompiledPath compile(String string) {
		var ostring = string;

		while (string.startsWith("/")) {
			string = string.substring(1);
		}

		while (string.endsWith("/")) {
			string = string.substring(0, string.length() - 1);
		}

		if (string.isEmpty()) {
			return EMPTY;
		}

		var partsStr = string.split("/");
		var parts = new Part[partsStr.length];
		boolean wildcard = false;
		int variables = 0;

		for (int i = 0; i < partsStr.length; i++) {
			var s = partsStr[i];

			if (wildcard) {
				throw new IllegalArgumentException("<wildcard> argument must be the last part of the path '" + ostring + "'");
			}

			if (s.startsWith("{") && s.endsWith("}")) {
				parts[i] = new Part(s.substring(1, s.length() - 1), true);
				variables++;
			} else if (s.startsWith("<") && s.endsWith(">")) {
				parts[i] = new Part(s.substring(1, s.length() - 1), true);
				variables++;
				wildcard = true;
			} else {
				parts[i] = new Part(s, false);
			}
		}

		return new CompiledPath(parts, variables, wildcard);
	}

	@Nullable
	public String[] matches(String[] path) {
		if (wildcard) {
			if (path.length >= parts.length) {
				for (int i = 0; i < parts.length; i++) {
					if (!parts[i].matches(path[i])) {
						return null;
					}
				}

				if (path.length == parts.length) {
					return path;
				} else {
					var joinedPath = new String[parts.length];
					System.arraycopy(path, 0, joinedPath, 0, parts.length);

					for (int i = parts.length; i < path.length; i++) {
						joinedPath[parts.length - 1] += "/" + path[i];
					}

					return joinedPath;
				}
			}
		} else {
			if (path.length == parts.length) {
				for (int i = 0; i < parts.length; i++) {
					if (!parts[i].matches(path[i])) {
						return null;
					}
				}

				return path;
			}
		}

		return null;
	}
}
