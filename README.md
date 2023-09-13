# [![KubeJS](https://repository-images.githubusercontent.com/46427577/d2446680-c366-11ea-8e6b-8a4da776b475)](https://kubejs.com)

**Note: If you are a script developer (i.e. pack dev, server admin, etc.), you likely want to visit our [website](https://kubejs.com) or the [wiki](https://mods.latvian.dev/books/kubejs) instead.**

(For a Table Of Contents, click the menu icon in the top left!)

## Introduction

KubeJS is a multi-modloader Minecraft mod which lets you create scripts in the JavaScript programming language to manage your server using events, change recipes, add ~~and edit~~ (coming soon!) loot tables, customise your world generation, add new blocks and items, or use custom integration with other mods like [FTB Quests](https://mods.latvian.dev/books/kubejs/page/ftb-quests-integration) for even more advanced features!

## Issues and Feedback

If you think you've found a bug with the mod or want to ask for a new feature, feel free to [open an issue](https://github.com/KubeJS-Mods/KubeJS/issues) here on GitHub, we'll try to get on it as soon as we can! Alternatively, you can also discuss your feature requests and suggestions with others using the [Discussions](https://github.com/KubeJS-Mods/KubeJS/discussions) tab.

And if you're just looking for help with KubeJS overall and the wiki didn't have the answer for what you were looking for, you can join our [Discord server](https://discord.gg/lat) and ask for help in the support channels, as well!

## License

KubeJS is distributed under the GNU Lesser General Public License v3.0, or LGPLv3. See our [LICENSE](/LICENSE.txt) file for more information.

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
		url = "https://maven.saps.dev/releases"
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

// these two are unfortunately needed since fg.deobf doesn't respect transitive dependencies yet
implementation fg.deobf("dev.latvian.mods:rhino-forge:${rhino_version}")
implementation fg.deobf("dev.architectury:architectury-forge:${architectury_version}")
```

Just set the versions with most up-to-date version of the required mod(s), which you also find using these badges:

<p align="center">
    <a href="https://maven.saps.dev/#/releases/dev/latvian/mods/kubejs">
        <img src="https://flat.badgen.net/maven/v/metadata-url/https/maven.saps.dev/releases/dev/latvian/mods/kubejs/maven-metadata.xml?color=C186E6&label=KubeJS" alt="KubeJS Latest Version">
    </a>
	<a href="https://maven.saps.dev/#/releases/dev/latvian/mods/rhino">
        <img src="https://flat.badgen.net/maven/v/metadata-url/https/maven.saps.dev/releases/dev/latvian/mods/rhino/maven-metadata.xml?color=3498DB&label=Rhino" alt="Rhino Latest Version">
    </a>
		<a href="https://linkie.shedaniel.dev/dependencies">
        <img src="https://flat.badgen.net/badge/Architectury/See%20this%20page%20for%20more%20information/F95F1E" alt="Architectury Latest Version">
    </a>
</p>

(Note: The above badges may not represent the *true* latest version of these mods. As a basic rule of thumb, for KubeJS and Rhino, you should always be using the latest version compiled against your version of Minecraft, for example `1802.+` for Minecraft 1.18.2, while for Architectury, the corresponding major version will be provided. You can also click on the badge to see all versions of each mod)

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

KubeJS [plugins](https://github.com/KubeJS-Mods/KubeJS/blob/1.19/main/common/src/main/java/dev/latvian/mods/kubejs/KubeJSPlugin.java) are the main way to add KubeJS integration to your mods through code. They contain various convenient hooks for addon developers, to allow things such as:

- performing certain actions during or after plugin or KubeJS initialisation (`init`, `afterInit`, etc.)
- adding classes to the class filter (`registerClasses`) as well as custom bindings (`registerBindings`) and type wrappers (`registerWrappers`) for easier interaction with native code in user scripts (See below for an explanation and example use cases)
- registering custom recipe handlers for modded recipe types (`registerRecipeTypes` - See below for an example)
- registering custom event handler groups for the KubeJS event system (`registerEvents`, this is **necessary** in order to have the event group be accessible from scripts)
- attaching extra data to players, worlds or the server, such that it can be accessed by script developers later (`attach(Player|World|Server)Data` - [Example](https://github.com/FTBTeam/FTB-Quests/blob/11311be070273008483d4c734ff9b96cc6a85b02/common/src/main/java/dev/ftb/mods/ftbquests/integration/kubejs/KubeJSIntegration.java#L40-L43))

### Adding recipe handlers

To add custom recipe handlers for your own modded recipe types, use KubeJS plugins as noted above. A concrete example of this can be found [here](https://github.com/KubeJS-Mods/KubeJS-Thermal/blob/1.19/main/src/main/java/dev/latvian/mods/kubejs/thermal/KubeJSThermalPlugin.java) for integration with the Thermal series, but we'll give you a simple outline of the process here as well:

```java
public class MyExamplePlugin extends KubeJSPlugin {
	@Override
	public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
		// for custom recipe types based on shaped recipes, like non-mirrored or copying NBT
		event.registerShaped("mymod:shapedbutbetter");        // analogue: registerShapeless

		// this is what you usually want to use for custom machine recipe types and the like
		event.register("mymod:customtype", MyRecipeJS::new);
	}
}

public class MyRecipeJS extends RecipeJS {
	public OutputItem result; // represents a single output item stack, which may have a chance attached to it 
	public InputItem ingredient; // represents an input item ingredient or ingredient stack

	// create is invoked when a recipe is created through script code,
	// with args being the list of parameters passed to the recipe constructor
	// if an args.get(i) call is out of bounds, it will return null instead, so
	// you don't need to worry about checking for out of bounds
	@Override
	public void create(RecipeArguments args) {
		result = parseOutputItem(args.get(0));
		ingredient = parseInputItem(args.get(1));
	}

	// example of a custom property that can be set through scripts
	// in this case, the experience property is saved to the recipe's JSON immediately,
	// rather than storing it in a field and serializing it all at once later;
	// this is recommended for "optional" properties that likely won't be supplied during `create`
	public CookingRecipeJS xp(float xp) {
		json.addProperty("experience", Math.max(0F, xp));
		save();
		return this;
	}

	// this is invoked when a recipe is loaded from JSON
	// (mostly used for modifying existing recipes, since new recipes
	// added by scripts are done through `create` instead)
	@Override
	public void deserialize() {
		result = parseOutputItem(json.get("result"));
		ingredient = parseInputItem(json.get("ingredient"));
	}

	// this is used both by modified and newly created recipes
	// to serialize them to JSON; currently, it is *required*
	// by default that your recipes are JSON-serializable,
	// and while you may be able to get away with code-only recipes
	// e.g. through overriding `createRecipe`, this is unsupported
	// since the assumed use case is that all recipes have some JSON representation
	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", outputToJson(result));
		}

		if (serializeInputs) {
			json.add("ingredient", inputToJson(ingredient));
		}
	}

	// the next two methods are used during bulk recipe modification
	// (through RecipeFilter) to find recipes that contain a certain in- or output
	@Override
	public boolean hasInput(IngredientMatch match) {
		return match.contains(ingredient);
	}

	@Override
	public boolean hasOutput(IngredientMatch match) {
		return match.contains(result);
	}

	// these two methods are used to replace a given in- or output item with another using the given transformer
	@Override
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		if (match.contains(ingredient)) {
			ingredient = transformer.transform(this, match, ingredient, with);
			return true;
		}

		return false;
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		if (match.contains(result)) {
			result = transformer.transform(this, match, result, with);
			return true;
		}

		return false;
	}
}
```

### Adding bindings

Similarly to adding custom recipe types, you may also add **custom bindings** to KubeJS (see [AntimatterAPI](https://github.com/GregTech-Intergalactical/AntimatterAPI/blob/dev-1.18/common/src/main/java/muramasa/antimatter/integration/kubejs/AntimatterKubeJS.java) for a simple example). Bindings can be anything from single value constants (like the global `HOUR = 3600000 (ms)`) to Java class and method wrappers (such as the builtin `Item` binding, which is wrapping the `ItemWrapper` class), and can be constrained to individual scopes, contexts and script types, as well!

### Setting class filters

KubeJS offers native Java type access in script files, meaning that basic Java types can be referenced directly by using for example `Java.loadClass("package.class")`. While builtin filters exist to prevent users from accessing any internal or potentially harmful packages added by Minecraft or its libraries directly, you may still want to explicitly deny access to certain classes or packages in your mod (or explicitly allow certain classes *within* a generally blacklisted package). To do this, you may either provide a class filter using a KubeJS plugin *or* you can avoid adding KubeJS as a dependency entirely by providing a simple `kubejs.classfilter.txt` file in your mod's `resources` with the following format (Note that comments aren't allowed in the actual file):

```diff
+mymod.api // This will *explicitly allow* anything from the mymod.api package to be used in KubeJS
-mymod.api.MyModAPIImpl // This will deny access to the MyModAPIImpl class, while keeping the rest of the package accessible
-mymod.internal.HttpUtil // This will *explicitly deny* your class from being used in KubeJS
```

## Contributing to KubeJS

### Getting Started

If you want to contribute to KubeJS, you will first need to set up a development environment, which should be fairly simple. Just clone the repository using Git:

```sh
git clone https://github.com/KubeJS-Mods/KubeJS.git
```

and import the gradle project using an IDE of your choice! (Note: Eclipse is likely to have problems with Architectury's runs, but IDEA and VS Code should work fine.)

### Building

Building KubeJS from source should be rather straightforward, as well, just run `gradlew build` in the root project, and the corresponding jars for Forge and Fabric should be in the respective module's `build/libs` directory (`kubejs-<loader>-<version>.jar`). The project will also produce a common jar; that one however should not be used in production.

### Creating Pull Requests

When creating a pull request to KubeJS, please make sure you acknowledge the following:

- We will *usually* not accept pull requests that add mod-specific support; those things are better suited as KubeJS plugins (optimally within the target mods themselves) or integration mods.
- Please be sure to test your feature **before** creating a pull request. We are a rather small team and as such, can't spend all too much time on reviewing pull requests.
- While not *strictly* required, please try to adhere by the repository's code style and conventions. A sample `.editorconfig` file exists to make this even easier for you to achieve (see the relevant EditorConfig plugin for your IDE)
- If you are unsure whether your feature fits into KubeJS and would like to hear additional opinions on the matter, feel free to open an `issue` or a `discussion` to discuss it further with us first!
