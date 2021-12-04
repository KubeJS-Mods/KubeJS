# [![KubeJS](https://repository-images.githubusercontent.com/46427577/d2446680-c366-11ea-8e6b-8a4da776b475)](https://kubejs.com)

**Note: If you are a script developer (i.e. pack dev, server admin, etc.), you likely want to visit our [website](https://kubejs.com) or the [wiki](https://mods.latvian.dev/books/kubejs) instead.**

(For a Table Of Contents, click the menu icon in the top left!)

## Introduction

KubeJS is a multi-modloader Minecraft mod which lets you create scripts in the JavaScript programming language to manage your server using events, change recipes, add ~~and edit~~ (coming soon!) loot tables, customise your world generation, add new blocks and items, or use custom integration with other mods like [FTB Quests](https://mods.latvian.dev/books/kubejs/page/ftb-quests-integration) for even more advanced features!

## Issues and Feedback

If you think you've found a bug with the mod or want to ask for a new feature, feel free to [open an issue](https://github.com/KubeJS-Mods/KubeJS/issues) here on GitHub, we'll try to get on it as soon as we can! Alternatively, you can also discuss your feature requests and suggestions with others using the [Discussions](https://github.com/KubeJS-Mods/KubeJS/discussions) tab.

And if you're just looking for help with KubeJS overall and the wiki didn't have the answer for what you were looking for, you can join our [Discord server](https://discord.gg/bPFfH6P) and ask for help in the support channels, as well!

## License

KubeJS is distributed under the GNU Lesser General Public License v3.0, or LGPLv3. See our [LICENSE](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/LICENSE.txt) file for more information.

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
            includeGroup "dev.architectury"
        }
    }

    maven {
        // saps.dev Maven (KubeJS and Rhino)
        url = "https://maven.saps.dev/minecraft"
        content {
            includeGroup "dev.latvian.mods"
        }
    }
}
```

You can then declare KubeJS as a regular `compile`-time dependency in your `dependencies` block:

```groovy
// Loom (Fabric / Quilt / Architectury)
modImplementation("dev.latvian.mods:kubejs-<loader>:${kubejs_version}")

// ForgeGradle
implementation fg.deobf("dev.latvian.mods:kubejs-forge:${kubejs_version}")

// these two are unfortunately needed since fg.deobf doesn't respect transitive dependencies as of yet
implementation fg.deobf("dev.latvian.mods:rhino:${rhino_version}")
implementation fg.deobf("me.shedaniel:architectury-forge:${architectury_version}")
```

Just set the versions with most up-to-date version of the required mod(s), which you also find using these badges:

<p align="center">
    <a href="https://vers.saps.dev">
        <img src="https://flat.badgen.net/maven/v/metadata-url/https/mvn.saps.dev/minecraft/dev/latvian/mods/kubejs/maven-metadata.xml?color=C186E6&label=KubeJS" alt="KubeJS Latest Version">
    </a>
	<a href="https://vers.saps.dev">
        <img src="https://flat.badgen.net/maven/v/metadata-url/https/mvn.saps.dev/minecraft/dev/latvian/mods/rhino/maven-metadata.xml?color=3498DB&label=Rhino" alt="Rhino Latest Version">
    </a>
		<a href="https://niceme.me">
        <img src="https://flat.badgen.net/maven/v/metadata-url/https/maven.architectury.dev/me/shedaniel/architectury/maven-metadata.xml?color=F95F1E&label=Architectury" alt="Architectury Latest Version">
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

### Creating a plugin

KubeJS [plugins](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/KubeJSPlugin.java) are a new feature introduced in KubeJS `1605.3.7` designed to ease the process of adding KubeJS integration to mods. They contain convenient hooks for addon developers to:

- perform certain actions during or after plugin initialisation (`init` / `afterInit` - [Example](https://github.com/FTBTeam/FTB-Chunks/blob/1.18/main/common/src/main/java/dev/ftb/mods/ftbchunks/integration/kubejs/FTBChunksKubeJSPlugin.java#L15-L24))
- add custom recipe handlers for modded recipe types (`addRecipes` - [Example](https://github.com/KubeJS-Mods/KubeJS-Create/blob/1.18/main/src/main/java/dev/latvian/mods/kubejs/create/KubeJSCreatePlugin.java#L19-L29)) *(this replaces `RegisterRecipeHandlersEvent` listeners)*
- add classes to the class filter (with the option to add to the filter for a certain script type only, as well as to add `Class` objects directly rather than using strings) (`addClasses` - [Example](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/BuiltinKubeJSPlugin.java#L68-L120))
- add global bindings (`addBindings` - [Example](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/forge/src/main/java/dev/latvian/mods/kubejs/forge/BuiltinKubeJSForgePlugin.java#L27-L31)) *(this replaces `BindingsEvent` listeners)* 
- add type wrappers for automatic native type conversion, for example to allow `String`s to be automatically converted to `ResourceLocation`s. (`addTypeWrappers` - [Example](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/BuiltinKubeJSPlugin.java#L211-L252))
- attach data to [players](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/player/AttachPlayerDataEvent.java), [worlds](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/world/AttachWorldDataEvent.java) or the [server](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/server/AttachServerDataEvent.java) that may then be used by script makers (`attach(Player|World|Server)Data` - [Example](https://github.com/FTBTeam/FTB-Quests/blob/1.18/main/common/src/main/java/dev/ftb/mods/ftbquests/integration/kubejs/KubeJSIntegration.java#L40-L42), please note this example currently uses the deprecated way of listening to `AttachPlayerDataEvent` itself)

A newer, more convenient way to add bindings, set class filters and even add **Native Type Wrappers** (which can convert an untyped JavaScript input (most likely a String) to a corresponding Java object automatically) comes in the form of KubeJS , which is a very simple class you can extend in your own mod to:

### Adding recipe handlers

To add custom recipe handlers for your own modded recipe types, use KubeJS plugins as noted above. A concrete example of this can be found [here](https://github.com/KubeJS-Mods/KubeJS-Thermal/blob/1.18/main/src/main/java/dev/latvian/mods/kubejs/thermal/KubeJSThermalPlugin.java) for integration with the Thermal series, but we'll give you a simple outline of the process here as well:

```java
public class MyExamplePlugin extends KubeJSPlugin {
    // for custom recipe types based on shaped recipes, like non-mirrored or copying NBT
    event.registerShaped("mymod:shapedbutbetter");        // analogue: registerShapeless

    // this is what you usually want to use for custom machine recipe types and the like
    event.register("mymod:customtype", MyRecipeJS::new);
}

public class MyRecipeJS extends RecipeJS {
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
}
```

### Adding bindings

Similarly to adding custom recipe types, you may also add **custom bindings** to KubeJS (see [Simply Seasons](https://github.com/Harvest-Festival/Simply-Seasons/blob/1.18/main/src/main/java/uk/joshiejack/simplyseasons/plugins/KubeJSPlugin.java#L17-L21) for a *very* simple example). Bindings can be anything from [single value constants](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/BuiltinKubeJSPlugin.java#L188) to Java [class](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/BuiltinKubeJSPlugin.java#L170) and [method wrappers](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/BuiltinKubeJSPlugin.java#L167), and can be constrained to individual scopes, contexts and script types, as well!

### Setting class filters

KubeJS offers native Java type access in script files, meaning that basic Java types can be referenced directly by using for example `java("package.class")`. This access is by default limited to only specifically allowed classes, with the default setting being to deny **anything else** unless [explicitly specified](https://github.com/KubeJS-Mods/KubeJS/blob/1.18/main/common/src/main/java/dev/latvian/mods/kubejs/CommonProperties.java#L51) by the user, however you may still want to explicitly allow (or explicitly deny, to prevent users from using it even with the above setting toggled on) access to certain classes in your mod. To do this, you may either provide a class filter using a KubeJS plugin (more on that later!) *or* you can avoid adding KubeJS as a dependency entirely by providing a simple `kubejs.classfilter.txt` file in your mod's `resources` with the following format (Note that comments aren't allowed in the actual file):

```diff
+mymod.api.MyModAPI // This will *explicitly allow* your class to be used in KubeJS
-mymod.internal.HttpUtil // This will *explicitly deny* your class from being used in KubeJS
```

For any unset classes, the default setting is once again determined by the user.

## Contributing to KubeJS

### Getting Started

If you want to contribute to KubeJS, you will first need to set up a development environment, which should be fairly simple. Just clone the repository using Git:

```sh
git clone https://github.com/KubeJS-Mods/KubeJS.git
```

and import the gradle project using an IDE of your choice! (Note: Eclipse *may* have some problems with Architectury's runs, but IDEA and VS Code should work fine.)

### Building

Building KubeJS from source should be rather straightforward, as well, just run `gradlew build` in the root project, and the corresponding jars for Forge and Fabric should be in the respective module's `build/libs` directory (`kubejs-<loader>-<version>.jar`). The project will also produce a common jar; that one however should not be used in production.

### Creating Pull Requests

When creating a pull request to KubeJS, please make sure you acknowledge the following:

- We will *usually* not accept pull requests that add mod-specific support; those things are better suited as KubeJS plugins (optimally within the target mods themselves) or integration mods.
- Please be sure to test your feature **before** creating a pull request. We are a rather small team and as such, can't spend all too much time on reviewing pull requests.
- While not *strictly* required, please try to adhere by the repository's code style and conventions. A sample `.editorconfig` file exists to make this even easier for you to achieve (see the relevant EditorConfig plugin for your IDE)
- If you are unsure whether your feature fits into KubeJS and would like to hear additional opinions on the matter, feel free to open an `issue` or a `discussion` to discuss it further with us first!
