package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentCodecFactory;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeFunctionInstance;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeSchemaFunction;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.JsonUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class JsonRecipeSchemaLoader {
	private record Merge(boolean keys, boolean constructors, boolean unique) {
		public static final Merge DEFAULT = new Merge(false, false, false);
	}

	private static final class RecipeSchemaBuilder {
		private final ResourceLocation id;
		private final RecipeSchemaData data;
		private RecipeSchema schema;

		private RecipeSchemaBuilder parent;
		private ResourceLocation overrideType;
		private List<RecipeKey<?>> keys;
		private List<RecipeSchemaData.ConstructorData> constructors;
		private Map<String, RecipeSchemaFunction> functions;
		private KubeRecipeFactory recipeFactory;
		private List<String> unique;
		private Boolean hidden;
		private Map<String, JsonElement> overrideKeys;

		private RecipeSchemaBuilder(ResourceLocation id, RecipeSchemaData data) {
			this.id = id;
			this.data = data;
		}

		private List<RecipeKey<?>> getKeys() {
			if (keys != null) {
				if (data.merge().keys()) {
					var mergedKeys = new LinkedHashMap<String, RecipeKey<?>>();

					if (parent != null) {
						for (var key : parent.getKeys()) {
							mergedKeys.put(key.name, key);
						}
					}

					for (var key : keys) {
						mergedKeys.put(key.name, key);
					}

					return List.copyOf(mergedKeys.values());
				}

				return keys;
			} else if (parent != null) {
				return parent.getKeys();
			} else {
				return List.of();
			}
		}

		private List<RecipeSchemaData.ConstructorData> getConstructors() {
			if (constructors != null) {
				if (data.merge().constructors()) {
					var list = new ArrayList<RecipeSchemaData.ConstructorData>();

					if (parent != null) {
						list.addAll(parent.getConstructors());
					}

					list.addAll(constructors);
					return list;
				}

				return constructors;
			} else if (parent != null) {
				return parent.getConstructors();
			} else {
				return List.of();
			}
		}

		private void gatherFunctions(Map<String, RecipeSchemaFunction> list) {
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
				if (data.merge().unique()) {
					var u = new LinkedHashSet<String>();

					if (parent != null) {
						u.addAll(parent.getUnique());
					}

					u.addAll(unique);
					return List.copyOf(u);
				}

				return unique;
			} else if (parent != null) {
				return parent.getUnique();
			} else {
				return List.of();
			}
		}

		private boolean isHidden() {
			if (hidden != null) {
				return hidden;
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

					var functionMap = new HashMap<String, RecipeSchemaFunction>();
					gatherFunctions(functionMap);

					var keyOverrides = new Reference2ObjectOpenHashMap<RecipeKey<?>, RecipeOptional<?>>(overrideKeys == null ? 0 : overrideKeys.size());

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

							for (var keyName : c.keys()) {
								var key = keyMap.get(keyName);

								if (key != null) {
									cKeys.add(key);
								} else {
									throw new NullPointerException("Key '" + keyName + "' not found in constructor of recipe schema '" + id + "'");
								}
							}

							var constructor = new RecipeConstructor(List.copyOf(cKeys));

							if (!c.overrides().isEmpty()) {
								for (var entry : c.overrides().entrySet()) {
									var key = keyMap.get(entry.getKey());

									if (key != null) {
										try {
											constructor.overrideValue(key, Cast.to(key.codec.parse(jsonOps, entry.getValue()).getOrThrow()));
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

					for (var entry : functionMap.entrySet()) {
						var func = entry.getValue().resolve(jsonOps, schema);

						if (func.isSuccess()) {
							schema.function(new RecipeFunctionInstance(entry.getKey(), func.getOrThrow()));
						} else {
							throw new NullPointerException("Failed to parse function '" + entry.getKey() + "' of recipe schema '" + id + "': " + func.error().map(DataResult.Error::message).orElse("Unknown Error"));
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

	public static void load(RecipeSchemaStorage storage, RecipeComponentCodecFactory.Context recipeComponentCodecFactoryContext, DynamicOps<JsonElement> jsonOps, RecipeSchemaRegistry event, ResourceManager resourceManager) {
		var map = new HashMap<ResourceLocation, RecipeSchemaBuilder>();

		for (var entry : resourceManager.listResources("kubejs/recipe_schema", path -> path.getPath().endsWith(".json")).entrySet()) {
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);
				var id = entry.getKey().withPath(entry.getKey().getPath().substring("kubejs/recipe_schema/".length(), entry.getKey().getPath().length() - ".json".length()));
				var data = RecipeSchemaData.CODEC.parse(jsonOps, json);

				if (data.isSuccess()) {
					map.put(id, new RecipeSchemaBuilder(id, data.getOrThrow()));
				} else {
					ConsoleJS.SERVER.error("Error parsing recipe schema json " + entry.getKey() + ": " + data.error().map(DataResult.Error::message).orElse("Unknown Error"));
				}
			} catch (Exception ex) {
				ConsoleJS.SERVER.error("Error reading recipe schema json " + entry.getKey(), ex);
			}
		}

		for (var builder : map.values()) {
			for (var m : builder.data.mappings()) {
				storage.mappings.put(m, builder.id);
			}
		}

		for (var builder : map.values()) {
			builder.hidden = builder.data.hidden().orElse(null);
			builder.parent = builder.data.parent().map(map::get).orElse(null);
			builder.overrideType = builder.data.overrideType().orElse(null);

			if (builder.data.recipeFactory().isPresent()) {
				var fname = builder.data.recipeFactory().get();
				builder.recipeFactory = storage.recipeTypes.get(fname);

				if (builder.recipeFactory == null) {
					throw new NullPointerException("Recipe factory '" + fname + "' not found for recipe schema '" + builder.id + "'");
				}
			}

			if (builder.data.keys().isPresent()) {
				builder.keys = new ArrayList<>();

				for (var keyData : builder.data.keys().get()) {
					try {
						var type = recipeComponentCodecFactoryContext.codec().decode(jsonOps, keyData.type()).getOrThrow().getFirst();
						var key = type.key(keyData.name(), keyData.role());

						if (keyData.defaultOptional()) {
							key.defaultOptional();
						} else if (keyData.optional().isPresent()) {
							var optionalJson = keyData.optional().get();

							try {
								key.optional = new RecipeOptional.Constant(key.codec.decode(jsonOps, optionalJson).getOrThrow().getFirst());
							} catch (Exception ex) {
								throw new IllegalArgumentException("Failed to create optional value for key '" + key + "' of '" + builder.id + "' from " + optionalJson, ex);
							}
						}

						if (!keyData.alternativeNames().isEmpty()) {
							key.names.addAll(keyData.alternativeNames());
						}

						key.excluded = keyData.excluded();

						if (!keyData.functionNames().isEmpty()) {
							key.functionNames = keyData.functionNames();
						}

						key.alwaysWrite = keyData.alwaysWrite();
						builder.keys.add(key);
					} catch (Exception ex) {
						ConsoleJS.SERVER.error("Error parsing recipe schema '" + builder.id + "' key " + keyData.name(), ex);
					}
				}
			}

			if (builder.data.constructors().isPresent()) {
				builder.constructors = builder.data.constructors().get();
			}

			if (builder.data.unique().isPresent()) {
				builder.unique = builder.data.unique().get();
			}

			if (builder.data.functions().isPresent()) {
				builder.functions = builder.data.functions().get();
			}

			if (!builder.data.overrideKeys().isEmpty()) {
				builder.overrideKeys = builder.data.overrideKeys();
			}
		}

		for (var builder : map.values()) {
			var schema = builder.getSchema(jsonOps);
			// System.out.println("SCHEMA " + builder.id);

			for (var constructor : schema.constructors().values()) {
				for (var key : schema.keys) {
					if (key.optional != null && !constructor.keys.contains(key) && !constructor.overrides.containsKey(key)) {
						constructor.defaultValue(key, Cast.to(key.optional));
						// System.out.println("- V DEF " + key.toString());
					} else {
						// System.out.println("- X NOP " + key.toString());
					}
				}

				// System.out.println("^ " + constructor);
			}
		}

		for (var builder : map.values()) {
			var schema = builder.getSchema(jsonOps);
			event.namespace(builder.id.getNamespace()).register(builder.id.getPath(), schema);
		}
	}
}
