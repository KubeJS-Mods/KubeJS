package dev.latvian.mods.kubejs.web.http;

public enum HTTPMethod {
	GET,
	POST,
	PUT,
	PATCH,
	DELETE;

	public static HTTPMethod fromString(String method) {
		return switch (method) {
			case "get", "GET" -> GET;
			case "post", "POST" -> POST;
			case "put", "PUT" -> PUT;
			case "patch", "PATCH" -> PATCH;
			case "delete", "DELETE" -> DELETE;
			default -> throw new IllegalArgumentException("Invalid HTTP method: " + method);
		};
	}
}
