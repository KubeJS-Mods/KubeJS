package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabKubeEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistryKubeEvent;
import dev.latvian.mods.kubejs.registry.RegistryKubeEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface StartupEvents {
	EventGroup GROUP = EventGroup.of("StartupEvents");

	EventHandler INIT = GROUP.startup("init", () -> KubeStartupEvent.class);
	EventHandler POST_INIT = GROUP.startup("postInit", () -> KubeStartupEvent.class);
	SpecializedEventHandler<ResourceKey<Registry<?>>> REGISTRY = GROUP.startup("registry", Extra.REGISTRY, () -> RegistryKubeEvent.class).required();
	EventHandler RECIPE_SCHEMA_REGISTRY = GROUP.startup("recipeSchemaRegistry", () -> RecipeSchemaRegistryKubeEvent.class);
	SpecializedEventHandler<ResourceLocation> MODIFY_CREATIVE_TAB = GROUP.startup("modifyCreativeTab", Extra.ID, () -> CreativeTabKubeEvent.class).required();
}
