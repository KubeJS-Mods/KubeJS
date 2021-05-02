# [![KubeJS](https://repository-images.githubusercontent.com/46427577/d2446680-c366-11ea-8e6b-8a4da776b475)](https://kubejs.com)

**Note: If you are a script developer (i.e. pack dev, server admin, etc.), you likely want to visit our [website](https://kubejs.com) or the [wiki](https://mods.latvian.dev/books/kubejs) instead.**

(For a Table Of Contents, click the <img src="https://raw.githubusercontent.com/primer/octicons/main/icons/list-unordered-16.svg?sanitize=true" alt="menu"> icon in the top left!)

## Introduction

KubeJS is a multi-modloader Minecraft mod which lets you create scripts in the JavaScript programming language to manage your server using events, change recipes, add ~~and edit~~ (coming soon!) loot tables, customise your world generation, add new blocks and items, or use custom integration with other mods like [FTB Quests](https://mods.latvian.dev/books/kubejs/page/ftb-quests-integration) for even more advanced features!

## Issues and Feedback

If you think you've found a bug with the mod or want to ask for a new feature, feel free to [open an issue](https://github.com/KubeJS-Mods/KubeJS/issues) here on GitHub, we'll try to get on it as soon as we can! Alternatively, you can also discuss your feature requests and suggestions with others using the [Discussions](https://github.com/KubeJS-Mods/KubeJS/discussions) tab.

And if you're just looking for help with KubeJS overall and the wiki didn't have the answer for what you were looking for, you can join our [Discord server](https://discord.gg/bPFfH6P) and ask for help in the `#kubejs-and-code` channel, as well!

## License

KubeJS is distributed under the GNU Lesser General Public License v3.0, or LGPLv3. See our [LICENSE](https://github.com/KubeJS-Mods/KubeJS/blob/master/LICENSE.txt) file for more information.

## Creating addons

Creating addon mods for KubeJS is easy! Just follow the following steps to your own liking, depending on how deep you want your integration to go!

### Initial setup

To add a Gradle dependency on KubeJS, you will need to add the following repositories to your `build.gradle`'s `repositories`:

```groovy
repositories {
    maven {
        // Shedaniel's maven (Architectury API)
        url = "https://maven.architectury.dev"
        content {
            includeGroup "me.shedaniel"
        }
    }

    maven {
        // saps.dev Maven (KubeJS and Rhino)
        // you can also use Lat's Maven @ https://maven.latvian.dev/
        url = "https://maven.saps.dev/minecraft"
        content {
            includeGroup "dev.latvian.mods"
        }
    }
}
```

You can then declare KubeJS as a regular `compile`-time dependency in your `dependencies` block:

```groovy
// Fabric/Quilt Loom and Architectury's "forgeloom"
modImplementation("dev.latvian.mods:kubejs-<loader>:<version>")

// ForgeGradle
implementation fg.deobf("dev.latvian.mods:kubejs-<loader>:<version>")
```

Just replace `<version>` with the latest version of KubeJS, which you also find using this badge:

<p align="center">
    <a href="https://maven.saps.dev/versions">
        <img src="https://img.shields.io/maven-metadata/v?color=%23fcb95b&label=KubeJS&metadataUrl=https%3A%2F%2Fmvn.saps.dev%2Fminecraft%2Fdev%2Flatvian%2Fmods%2Fkubejs%2Fmaven-metadata.xml&style=flat-square" alt="KubeJS Latest Version">
    </a>
</p>

You should of course use `kubejs-forge` for Forge projects and `kubejs-fabric` for Fabric projects. KubeJS' dependencies (notably, Rhino and Architectury) ***should*** all be downloaded automatically; otherwise, you may need to add them manually.

### Fixing refmaps (ForgeGradle only)

KubeJS uses the official mappings for Minecraft ("mojmap"). Since the refmap remapper for Mixins on ModLauncher **currently** doesn't support non-MCP mappings, you will need to add some extra lines to your runs to keep it from crashing, as detailed [here](https://github.com/SpongePowered/Mixin/issues/462#issuecomment-791370319) on the Mixin issue tracker. Be sure to regenerate your runs afterwards!

```groovy
minecraft {
    runs {
        client {
            property 'mixin.env.remapRefMap', 'true'
            property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
        }
        // should be analogue for any other runs you have
    }
}
```

### Adding recipe handlers

To add custom recipe handlers for your own modded recipe types, use the [`RegisterRecipeHandlersEvent`](https://github.com/KubeJS-Mods/KubeJS/blob/master/common/src/main/java/dev/latvian/kubejs/recipe/RegisterRecipeHandlersEvent.java). A concrete example of this can be found [here](https://github.com/KubeJS-Mods/KubeJS-Thermal/blob/main/src/main/java/dev/latvian/kubejs/thermal/KubeJSThermal.java) for integration with the Thermal series, but we'll give you a simple outline of the process here as well:

```java
// You may also use Forge's event bus here
RegisterRecipeHandlers.EVENT.register(event -> {
    // for custom recipe types based on shaped recipes, like non-mirrored or copying NBT
    event.registerShaped("mymod:shapedbutbetter");       // analogue: registerShapeless

    // this is what you usually want to use for custom machine recipe types and the like
    event.register("mymod:customtype", MyRecipeJS::new);

    // in MyRecipeJS.java (which extends RecipeJS)

    // Input is an IngredientStackJS, return value should be the
    // serialised JSON variant used in your recipe 
    @Override
    public JsonElement serializeIngredientStack(IngredientStackJS stack);

    // say your recipe had processing time, you would use builder
    // methods like these to add these properties to the JSON
    public MyRecipeJS time(int ticks) {
        json.addProperty("time", ticks);
        save();
        return this;
    }

    // Similar to inputs, if you use custom parsing to determine your
    // result item, use this method to override the parsing of said item.
    @Override
    public ItemStackJS parseResultItem(@Nullable Object o) {
        if(o instanceof JsonObject) {
            // parse the item yourself if it's a JsonObject
        }
        return super.parseResultItem(o); // fallback to default parsing otherwise
    }
});
```

### Adding bindings / wrappers

Similarly to adding custom recipe types, there is a [`BindingsEvent`](https://github.com/KubeJS-Mods/KubeJS/blob/master/common/src/main/java/dev/latvian/kubejs/script/BindingsEvent.java) which you can use to add custom bindings to KubeJS (see [FTBQuests](https://github.com/FTBTeam/FTB-Quests/blob/master/common/src/main/java/dev/ftb/mods/ftbquests/integration/kubejs/KubeJSIntegration.java) for a simple example). Bindings can be anything from single value constants to Java class and method wrappers, and can be constrained to individual scopes, contexts and script types, as well!

### Setting class filters

KubeJS offers native Java type access in script files, meaning that basic Java types can be referenced directly by using for example `java("package.class")`. This access is by default limited to only specifically allowed classes, with the default setting being to deny **anything else** unless [explicitly specified](https://github.com/KubeJS-Mods/KubeJS/blob/master/common/src/main/java/dev/latvian/kubejs/CommonProperties.java#L51) by the user, however you may still want to explicitly allow (or explicitly deny, to prevent users from using it even with the above setting toggled on) access to certain classes in your mod. To do this, you may either provide a class filter using a KubeJS plugin (more on that later!) *or* you can avoid adding KubeJS as a dependency entirely by providing a simple `kubejs.classfilter.txt` file in your mod's `resources` with the following format (Note that comments aren't allowed in the actual file):

```diff
+mymod.api.MyModAPI // This will *explicitly allow* your class to be used in KubeJS
-mymod.internal.HttpUtil // This will *explicitly deny* your class from being used in KubeJS
```

For any unset classes, the default setting is once again determined by the user.

### **NEW!** KubeJS plugins

WIP (Lat pls help)

## Contributing to KubeJS

### WIP
