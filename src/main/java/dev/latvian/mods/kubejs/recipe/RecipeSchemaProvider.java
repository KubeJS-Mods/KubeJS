package dev.latvian.mods.kubejs.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeOptional;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaData;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeSchemaFunction;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A base provider for generating recipe schemas.
 * <p>
 * <strong>Important!</strong> KubeJS must be added as an existing mod via
 * {@code programArguments.addAll('--existing-mod', 'kubejs')} in your data gen runs block for this to work!
 */
public abstract class RecipeSchemaProvider implements DataProvider {

    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final String name;
    private final RegistryAccessContainer registryAccessContainer;
    private final PackOutput.PathProvider path;
    private final ImmutableMap.Builder<ResourceLocation, RecipeSchemaData> map;
    private final ServerScriptManager scriptManager;
    private final RecipeTypeRegistryContext regCtx;
    private final Codec<RecipeSchemaData> codec;

    public RecipeSchemaProvider(String name, GatherDataEvent event) {
        this(name, event, RegistryAccessContainer.BUILTIN);
    }

    public RecipeSchemaProvider(String name, GatherDataEvent event, RegistryAccessContainer registryAccessContainer) {
        this.lookupProvider = event.getLookupProvider();
        this.name = name;
        this.registryAccessContainer = registryAccessContainer;
        path = event.getGenerator().getPackOutput().createPathProvider(PackOutput.Target.DATA_PACK, "kubejs/recipe_schema");
        map = ImmutableMap.builder();
        scriptManager = ServerScriptManager.createForDataGen();
        regCtx = new RecipeTypeRegistryContext(
                registryAccessContainer,
                scriptManager.recipeSchemaStorage
        );
        scriptManager.recipeSchemaStorage.fireEvents(registryAccessContainer, event.getResourceManager(PackType.SERVER_DATA));
        codec = RecipeSchemaData.CODEC.apply(regCtx);
    }

    public final RegistryAccessContainer registryAccessContainer() {
        return registryAccessContainer;
    }

    public final ServerScriptManager serverScriptManager() {
        return scriptManager;
    }

    public final RecipeTypeRegistryContext recipeTypeRegistryContext() {
        return regCtx;
    }

    public abstract void add(HolderLookup.Provider lookup);

    public void add(ResourceLocation id, RecipeSchemaData schema) {
        map.put(id, schema);
    }

    public void add(ResourceLocation id, Consumer<SchemaDataBuilder> builder) {
        add(id, Util.make(new SchemaDataBuilder(), builder).build());
    }

    public void onlyKeys(ResourceLocation id, RecipeKey<?>... keys) {
        add(id, b -> b.keys(keys));
    }

    public RecipeSchemaData.RecipeKeyData keyData(RecipeKey<?> key) {
        if (key.functionNames == null) {
            key.noFunctions();
        }
        return new RecipeSchemaData.RecipeKeyData(
                key.name,
                key.role,
                key.component,
                Optional.ofNullable(key.optional)
                        .map(o -> key.codec.encodeStart(
                                registryAccessContainer.json(),
                                Cast.to(o.getValueForDataGeneration())
                        ).getOrThrow()),
                key.optional == RecipeOptional.DEFAULT,
                new ArrayList<>(key.names),
                key.excluded,
                key.functionNames,
                key.alwaysWrite
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return lookupProvider.thenCompose(p -> {
            add(p);
            return CompletableFuture.allOf(
                    map.buildOrThrow().entrySet().stream()
                            .map(e -> DataProvider.saveStable(output, p, codec, e.getValue(), path.json(e.getKey())))
                            .toArray(CompletableFuture[]::new)
            );
        });
    }

    @Override
    public String getName() {
        return name;
    }

    public class SchemaDataBuilder {

        private ResourceLocation parent, overrideType, recipeFactory;
        private List<RecipeSchemaData.RecipeKeyData> keys;
        private List<RecipeSchemaData.ConstructorData> constructors;
        private Map<String, RecipeSchemaFunction> functions;
        private final Map<String, JsonElement> overrideKeys = new HashMap<>();
        boolean hidden = false;
        private final List<String> mappings = new ArrayList<>();
        private List<String> unique;
        private List<RecipePostProcessor> postProcessors;
        private RecipeSchemaData.MergeData mergeData = RecipeSchemaData.MergeData.DEFAULT;

        /**
         * Sets the parent recipe type, which acts as a fallback/proxy for recipe methods. See vanilla's smoking &
         * blasting recipe types for examples.
         */
        public SchemaDataBuilder parent(ResourceLocation parent) {
            this.parent = parent;
            return this;
        }

        /**
         * Specifies an alternative recipe serializer to use instead of the one associated with this recipe
         */
        public SchemaDataBuilder overrideType(ResourceLocation type) {
            overrideType = type;
            return this;
        }

        /**
         * Set the {@link dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory recipe factory} to be used for this recipe
         * type. See {@link dev.latvian.mods.kubejs.plugin.KubeJSPlugin#registerRecipeFactories(RecipeFactoryRegistry) #registerRecipeFactories}
         * for registering custom factories
         */
        public SchemaDataBuilder recipeFactory(ResourceLocation factory) {
            recipeFactory = factory;
            return this;
        }

        /**
         * Adds the {@code RecipeKey}s, automatically converting them to {@code RecipeKeyData}s
         */
        public SchemaDataBuilder keys(RecipeKey<?>... keys) {
            return keys(List.of(keys));
        }

        /**
         * Adds the {@code RecipeKey}s, automatically converting them to {@code RecipeKeyData}s
         */
        public SchemaDataBuilder keys(List<RecipeKey<?>> keys) {
            return keyDatas(keys.stream().map(RecipeSchemaProvider.this::keyData).toList());
        }

        /**
         * Adds the raw {@code RecipeKeyData}s
         */
        public SchemaDataBuilder keyDatas(RecipeSchemaData.RecipeKeyData... keys) {
            return keyDatas(List.of(keys));
        }

        /**
         * Adds the raw {@code RecipeKeyData}s
         */
        public SchemaDataBuilder keyDatas(List<RecipeSchemaData.RecipeKeyData> keys) {
            if (this.keys == null) {
                this.keys = new ArrayList<>(keys);
            } else {
                this.keys.addAll(keys);
            }
            return this;
        }

        /**
         * Add custom constructors which can be used instead of the key-based default
         */
        public SchemaDataBuilder constructors(RecipeSchemaData.ConstructorData... constructors) {
            return constructors(List.of(constructors));
        }

        /**
         * Add custom constructors which can be used instead of the key-based default
         */
        public SchemaDataBuilder constructors(List<RecipeSchemaData.ConstructorData> constructors) {
            if (this.constructors == null) {
                this.constructors = new ArrayList<>(constructors);
            } else {
                this.constructors.addAll(constructors);
            }
            return this;
        }

        /**
         * Add the function to the recipe, used like {@code event.recipes.my.recipe(<recipe args>).<name>(<function args>)}
         */
        public SchemaDataBuilder function(String name, RecipeSchemaFunction function) {
            return functions(Map.of(name, function));
        }

        /**
         * Add the functions to the recipe, see {@link #function(String, RecipeSchemaFunction)}
         */
        public SchemaDataBuilder functions(Map<String, RecipeSchemaFunction> functions) {
            if (this.functions == null) {
                this.functions = new HashMap<>(functions);
            } else {
                this.functions.putAll(functions);
            }
            return this;
        }

        /**
         * Specify the optional value of a key
         */
        public SchemaDataBuilder overrideKey(String key, JsonElement optionalValue) {
            overrideKeys.put(key, optionalValue);
            return this;
        }

        /**
         * Specify specific overrides for the optional values of keys
         */
        public SchemaDataBuilder overrideKeys(Map<String, JsonElement> overrideKeys) {
            this.overrideKeys.putAll(overrideKeys);
            return this;
        }

        /**
         * Nominally, if this recipe type should be hidden... but this value appears to be unused
         */
        public SchemaDataBuilder hidden() {
            return hidden(true);
        }

        /**
         * Nominally, if this recipe type should be hidden... but this value appears to be unused
         */
        public SchemaDataBuilder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        /**
         * The names for accessing this recipe via a special method, e.g. {@code event.recipes.myMapping(...}
         */
        public SchemaDataBuilder mappings(String... mappings) {
            return mappings(List.of(mappings));
        }

        /**
         * The names for accessing this recipe via a special method, e.g. {@code event.recipes.myMapping(...}
         */
        public SchemaDataBuilder mappings(List<String> mappings) {
            this.mappings.addAll(mappings);
            return this;
        }

        /**
         * The keys to use when generating a unique id for the recipe
         */
        public SchemaDataBuilder keysForUniqueId(String... keys) {
            return keysForUniqueId(List.of(keys));
        }

        /**
         * The keys to use when generating a unique id for the recipe
         */
        public SchemaDataBuilder keysForUniqueId(List<String> keys) {
            if (unique == null) {
                unique = new ArrayList<>(keys);
            } else {
                unique.addAll(keys);
            }
            return this;
        }

        /**
         * Post processors to apply to recipes, see {@link dev.latvian.mods.kubejs.recipe.schema.postprocessing.KeyPatternCleanupPostProcessor KeyPatternCleanupPostProcessor}
         */
        public SchemaDataBuilder postProcessors(RecipePostProcessor... processors) {
            return postProcessors(List.of(processors));
        }

        /**
         * Post processors to apply to recipes, see {@link dev.latvian.mods.kubejs.recipe.schema.postprocessing.KeyPatternCleanupPostProcessor KeyPatternCleanupPostProcessor}
         */
        public SchemaDataBuilder postProcessors(List<RecipePostProcessor> processors) {
            if (postProcessors == null) {
                postProcessors = new ArrayList<>(processors);
            } else {
                postProcessors.addAll(processors);
            }
            return this;
        }

        /**
         * If values from {@link #parent(ResourceLocation) parent schemas} should be merged when baking the recipe schema
         */
        public SchemaDataBuilder mergeData(boolean keys, boolean constructors, boolean unique, boolean postProcessors) {
            mergeData = new RecipeSchemaData.MergeData(keys, constructors, unique, postProcessors);
            return this;
        }

        RecipeSchemaData build() {
            return new RecipeSchemaData(
                    Optional.ofNullable(parent),
                    Optional.ofNullable(overrideType),
                    Optional.ofNullable(recipeFactory),
                    Optional.ofNullable(keys),
                    Optional.ofNullable(constructors),
                    Optional.ofNullable(functions),
                    overrideKeys,
                    Optional.of(hidden),
                    mappings,
                    Optional.ofNullable(unique),
                    Optional.ofNullable(postProcessors),
                    mergeData
            );
        }
    }
}
