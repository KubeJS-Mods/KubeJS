package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class JsonRecipeSchemaLoader {
	private record ConstructorBuilder(List<String> keys, Map<String, JsonElement> overrides) {
	}

	private record FunctionBuilder(String name, JsonObject json) {
	}

	private static final class RecipeSchemaBuilder {
		private final ResourceLocation id;
		private final JsonObject json;
		private RecipeSchema schema;

		private RecipeSchemaBuilder parent;
		private ResourceLocation overrideType;
		private List<RecipeKey<?>> keys;
		private List<ConstructorBuilder> constructors;
		private Map<String, FunctionBuilder> functions;
		private KubeRecipeFactory recipeFactory;
		private List<String> unique;
		private boolean hidden;
		private Map<String, JsonElement> overrideKeys;

		private RecipeSchemaBuilder(ResourceLocation id, JsonObject json) {
			this.id = id;
			this.json = json;
		}

		private List<RecipeKey<?>> getKeys() {
			if (keys != null) {
				return keys;
			} else if (parent != null) {
				return parent.getKeys();
			} else {
				return List.of();
			}
		}

		private List<ConstructorBuilder> getConstructors() {
			if (constructors != null) {
				return constructors;
			} else if (parent != null) {
				return parent.getConstructors();
			} else {
				return List.of();
			}
		}

		private void gatherFunctions(Map<String, FunctionBuilder> list) {
			if (parent != null) {
				parent.gatherFunctions(list);
			}

			if (functions != null) {
				list.putAll(functions);
			}
		}

		private KubeRecipeFactory getRecipeFactory() {
			if (recipeFactory != null) {
				return recipeFactory;
			} else if (parent != null) {
				return parent.getRecipeFactory();
			} else {
				return null;
			}
		}

		private List<String> getUnique() {
			if (unique != null) {
				return unique;
			} else if (parent != null) {
				return parent.getUnique();
			} else {
				return List.of();
			}
		}

		private boolean isHidden() {
			if (hidden) {
				return true;
			} else if (parent != null) {
				return parent.isHidden();
			} else {
				return false;
			}
		}

		private RecipeSchema getSchema(DynamicOps<JsonElement> jsonOps) {
			if (schema == null) {
				if (overrideType != null || keys != null || constructors != null || functions != null || recipeFactory != null || unique != null || overrideKeys != null) {
					var keys = getKeys();
					var keyMap = new HashMap<String, RecipeKey<?>>();

					for (var key : keys) {
						keyMap.put(key.name, key);
					}

					var functionMap = new HashMap<String, FunctionBuilder>();
					gatherFunctions(functionMap);

					var keyOverrides = new IdentityHashMap<RecipeKey<?>, RecipeOptional<?>>(overrideKeys == null ? 0 : overrideKeys.size());

					if (overrideKeys != null) {
						for (var entry : overrideKeys.entrySet()) {
							var key = keyMap.get(entry.getKey());

							if (key != null) {
								try {
									keyOverrides.put(key, new RecipeOptional.Constant(key.codec.decode(jsonOps, entry.getValue()).getOrThrow().getFirst()));
								} catch (Exception ex) {
									throw new IllegalArgumentException("Failed to create optional value for key '" + key + "' of '" + id + "' from " + entry.getValue(), ex);
								}
							} else {
								throw new NullPointerException("Key '" + entry.getKey() + "' not found in key overrides of recipe schema '" + id + "'");
							}
						}
					}

					schema = new RecipeSchema(keyOverrides, getKeys());
					schema.typeOverride = overrideType;

					var rf = getRecipeFactory();

					if (rf != null) {
						schema.recipeFactory = rf;
					}

					var constructors = getConstructors();

					if (!constructors.isEmpty()) {
						for (var c : constructors) {
							var cKeys = new ArrayList<RecipeKey<?>>();

							for (var keyName : c.keys) {
								var key = keyMap.get(keyName);

								if (key != null) {
									cKeys.add(key);
								} else {
									throw new NullPointerException("Key '" + keyName + "' not found in constructor of recipe schema '" + id + "'");
								}
							}

							var constructor = new RecipeConstructor(cKeys.toArray(new RecipeKey[0]));

							if (!c.overrides.isEmpty()) {
								constructor.overrides = new IdentityHashMap<>(c.overrides.size());

								for (var entry : c.overrides.entrySet()) {
									var key = keyMap.get(entry.getKey());

									if (key != null) {
										try {
											constructor.overrides.put(key, new RecipeOptional.Constant(key.codec.decode(jsonOps, entry.getValue()).getOrThrow().getFirst()));
										} catch (Exception ex) {
											throw new IllegalArgumentException("Failed to create optional value for key '" + key + "' of '" + id + "' from " + entry.getValue(), ex);
										}
									} else {
										throw new NullPointerException("Key '" + entry.getKey() + "' not found in overrides of constructor of recipe schema '" + id + "'");
									}
								}
							}

							schema.constructor(constructor);
						}
					}

					// schema.constructors(constructors.toArray(new RecipeConstructor[0]));

					for (var entry : functionMap.entrySet()) {
						var funcName = entry.getKey();
						var funcJson = entry.getValue().json;

						if (funcJson.has("set")) {
							Map<RecipeKey<?>, Object> map = new HashMap<>(1);

							for (var entry1 : funcJson.getAsJsonObject("set").entrySet()) {
								var keyName = entry1.getKey();
								var key = keyMap.get(keyName);

								if (key != null) {
									map.put(key, key.codec.decode(jsonOps, entry1.getValue()).getOrThrow().getFirst());
								} else {
									throw new NullPointerException("Key '" + keyName + "' not found in function '" + funcName + "' of recipe schema '" + id + "'");
								}
							}

							if (map.size() == 1) {
								schema.function(funcName, new RecipeSchemaFunction.SetFunction(map.keySet().iterator().next(), map.values().iterator().next()));
							} else if (!map.isEmpty()) {
								schema.function(funcName, new RecipeSchemaFunction.SetManyFunction(map));
							}
						}

						if (funcJson.has("add_to_list")) {
							var keyName = funcJson.get("add_to_list").getAsString();
							var key = keyMap.get(keyName);

							if (key != null) {
								schema.function(funcName, new RecipeSchemaFunction.AddToListFunction<>((RecipeKey) key));
							} else {
								throw new NullPointerException("Key '" + keyName + "' not found in function '" + funcName + "' of recipe schema '" + id + "'");
							}
						}
					}

					var uniqueKeyNames = getUnique();

					if (!uniqueKeyNames.isEmpty()) {
						var uniqueKeys = new ArrayList<RecipeKey<?>>();

						for (var keyName : uniqueKeyNames) {
							var key = keyMap.get(keyName);

							if (key != null) {
								uniqueKeys.add(key);
							} else {
								throw new NullPointerException("Key '" + keyName + "' not found in unique keys of recipe schema '" + id + "'");
							}
						}

						schema.uniqueIds(uniqueKeys);
					}

					schema.hidden = isHidden();
				} else if (parent != null) {
					schema = parent.getSchema(jsonOps);
				} else {
					schema = new RecipeSchema(Map.of(), List.of());
					schema.constructor();
				}
			}

			return schema;
		}
	}

	public static void load(RecipeSchemaStorage storage, RegistryAccessContainer registries, RecipeSchemaRegistry event, ResourceManager resourceManager) {
		var map = new HashMap<ResourceLocation, RecipeSchemaBuilder>();

		for (var entry : resourceManager.listResources("kubejs/recipe_schema", path -> path.getPath().endsWith(".json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);
				var holder = new RecipeSchemaBuilder(ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), entry.getKey().getPath().substring("kubejs/recipe_schema/".length(), entry.getKey().getPath().length() - ".json".length())), json);
				map.put(holder.id, holder);

				if (holder.json.has("mappings")) {
					for (var m : holder.json.getAsJsonArray("mappings")) {
						storage.mappings.put(m.getAsString(), holder.id);
					}
				}
			} catch (Exception ex) {
				ConsoleJS.SERVER.error("Error reading recipe schema json " + entry.getKey(), ex);
			}
		}

		for (var holder : map.values()) {
			if (holder.json.has("hidden")) {
				holder.hidden = holder.json.get("hidden").getAsBoolean();
			}

			holder.parent = holder.json.has("parent") ? map.get(ResourceLocation.parse(holder.json.get("parent").getAsString())) : null;

			if (holder.json.has("override_type")) {
				holder.overrideType = ResourceLocation.parse(holder.json.get("override_type").getAsString());
			}

			if (holder.json.has("factory")) {
				var fname = ResourceLocation.parse(holder.json.get("factory").getAsString());
				holder.recipeFactory = storage.recipeTypes.get(fname);

				if (holder.recipeFactory == null) {
					throw new NullPointerException("Recipe factory '" + fname + "' not found for recipe schema '" + holder.id + "'");
				}
			}

			if (holder.json.has("keys")) {
				holder.keys = new ArrayList<>();

				for (var entry : holder.json.getAsJsonArray("keys")) {
					try {
						var keyJson = entry.getAsJsonObject();
						var name = keyJson.get("name").getAsString();
						var role = switch (keyJson.has("role") ? keyJson.get("role").getAsString() : "") {
							case "input" -> ComponentRole.INPUT;
							case "output" -> ComponentRole.OUTPUT;
							default -> ComponentRole.OTHER;
						};

						var type = storage.getComponent(registries, keyJson.get("type").getAsString());
						var key = type.key(name, role);

						if (keyJson.has("optional")) {
							var optionalJson = keyJson.get("optional");

							if (optionalJson == null || optionalJson.isJsonNull()) {
								key.defaultOptional();
							} else {
								try {
									key.optional = new RecipeOptional.Constant(key.codec.decode(registries.json(), optionalJson).getOrThrow().getFirst());
								} catch (Exception ex) {
									throw new IllegalArgumentException("Failed to create optional value for key '" + key + "' of '" + holder.id + "' from " + optionalJson, ex);
								}
							}
						}

						if (keyJson.has("alternative_names")) {
							var arr = keyJson.getAsJsonArray("alternative_names");

							for (var e : arr) {
								key.names.add(e.getAsString());
							}
						}

						if (keyJson.has("excluded")) {
							key.excluded = keyJson.get("excluded").getAsBoolean();
						}

						if (keyJson.has("function_names")) {
							var arr = keyJson.getAsJsonArray("function_names");
							key.functionNames = new ArrayList<>(arr.size());

							for (var e : arr) {
								key.functionNames.add(e.getAsString());
							}
						}

						if (keyJson.has("allow_empty")) {
							key.allowEmpty = keyJson.get("allow_empty").getAsBoolean();
						}

						if (keyJson.has("always_write")) {
							key.alwaysWrite = keyJson.get("always_write").getAsBoolean();
						}

						holder.keys.add(key);
					} catch (Exception ex) {
						ConsoleJS.SERVER.error("Error parsing recipe schema '" + holder.id + "' key " + entry, ex);
					}
				}
			}

			if (holder.json.has("constructors")) {
				for (var entry : holder.json.getAsJsonArray("constructors")) {
					var c = entry.getAsJsonObject();
					var constructor = new ConstructorBuilder(new ArrayList<>(3), new HashMap<>(0));

					for (var e : c.getAsJsonArray("keys")) {
						constructor.keys.add(e.getAsString());
					}

					if (c.has("overrides")) {
						for (var e : c.getAsJsonObject("overrides").entrySet()) {
							constructor.overrides.put(e.getKey(), e.getValue());
						}
					}

					if (holder.constructors == null) {
						holder.constructors = new ArrayList<>();
					}

					holder.constructors.add(constructor);
				}
			}

			if (holder.json.has("unique")) {
				var arr = holder.json.getAsJsonArray("unique");
				holder.unique = new ArrayList<>(arr.size());

				for (var e : arr) {
					holder.unique.add(e.getAsString());
				}
			}

			if (holder.json.has("functions")) {
				holder.functions = new HashMap<>();

				for (var entry : holder.json.getAsJsonObject("functions").entrySet()) {
					holder.functions.put(entry.getKey(), new FunctionBuilder(entry.getKey(), entry.getValue().getAsJsonObject()));
				}
			}

			if (holder.json.has("override_keys")) {
				holder.overrideKeys = new HashMap<>();

				for (var e : holder.json.getAsJsonObject("override_keys").entrySet()) {
					holder.overrideKeys.put(e.getKey(), e.getValue());
				}
			}
		}

		for (var holder : map.values()) {
			var schema = holder.getSchema(registries.json());
			event.namespace(holder.id.getNamespace()).register(holder.id.getPath(), schema);
		}
	}
}
