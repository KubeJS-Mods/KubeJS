package dev.latvian.kubejs.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ScriptManager {
	private static final String[] BLACKLISTED_PACKAGES = {
			"java.io", // IO and network
			"java.nio",
			"java.net",
			"sun",
			"com.sun",
			"io.netty",
			"java.lang",

			"dev.latvian.mods.rhino", // Rhino itself
			"dev.latvian.kubejs.script", // KubeJS itself

			"cpw.mods.modlauncher", // Forge / FML internal stuff
			"cpw.mods.gross",
			"net.minecraftforge.fml",
			"net.minecraftforge.accesstransformer",
			"net.minecraftforge.coremod",
			"org.openjdk.nashorn",
			"jdk.nashorn",

			"net.fabricmc.accesswidener", // Fabric internal stuff
			"net.fabricmc.devlaunchinjector",
			"net.fabricmc.loader",
			"net.fabricmc.tinyremapper",

			"org.objectweb.asm", // ASM
			"org.spongepowered.asm", // Sponge ASM
			"me.shedaniel.architectury", // Architectury

			"com.chocohead.mm", // Manningham Mills
	};

	private static final String[] BLACKLISTED_PACKAGES_START = new String[BLACKLISTED_PACKAGES.length];

	static {
		for (int i = 0; i < BLACKLISTED_PACKAGES.length; i++) {
			BLACKLISTED_PACKAGES_START[i] = BLACKLISTED_PACKAGES[i] + ".";
		}
	}

	private static final Predicate<String> CLASS_WHITELIST_FUNCTION = s -> {
		for (String s1 : BLACKLISTED_PACKAGES) {
			if (s.equals(s1)) {
				return false;
			}
		}

		for (String s1 : BLACKLISTED_PACKAGES_START) {
			if (s.startsWith(s1)) {
				return false;
			}
		}

		return true;
	};

	public final ScriptType type;
	public final Path directory;
	public final String exampleScript;
	public final EventsJS events;
	public final Map<String, ScriptPack> packs;
	private final Object2BooleanOpenHashMap<String> classWhitelistCache;
	public boolean firstLoad;

	public ScriptManager(ScriptType t, Path p, String e) {
		type = t;
		directory = p;
		exampleScript = e;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		classWhitelistCache = new Object2BooleanOpenHashMap<>();
		firstLoad = true;
	}

	public void unload() {
		events.clear();
		packs.clear();
		type.errors.clear();
		type.warnings.clear();
		type.console.resetFile();
	}

	public void loadFromDirectory() {
		if (Files.notExists(directory)) {
			UtilsJS.tryIO(() -> Files.createDirectories(directory));

			try (InputStream in = KubeJS.class.getResourceAsStream(exampleScript);
				 OutputStream out = Files.newOutputStream(directory.resolve("script.js"))) {
				out.write(IOUtils.toByteArray(in));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		ScriptPack pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");

		for (ScriptFileInfo fileInfo : pack.info.scripts) {
			ScriptSource.FromPath scriptSource = info -> directory.resolve(info.file);

			Throwable error = fileInfo.preload(scriptSource);

			if (fileInfo.isIgnored()) {
				continue;
			}

			if (error == null) {
				pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
			} else {
				KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
			}
		}

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public void load() {
		classWhitelistCache.clear();
		Context context = Context.enter();
		context.setLanguageVersion(Context.VERSION_ES6);
		context.setClassShutter((fullClassName, type) -> type != ClassShutter.TYPE_CLASS_IN_PACKAGE || classWhitelistCache.computeBooleanIfAbsent(fullClassName, CLASS_WHITELIST_FUNCTION));
		context.getTypeWrappers().removeAll();

		// Java / Minecraft //
		context.getTypeWrappers().register(String.class, String::valueOf);
		context.getTypeWrappers().register(CharSequence.class, String::valueOf);
		context.getTypeWrappers().register(ResourceLocation.class, o -> UtilsJS.getMCID(o == null ? null : o.toString()));
		context.getTypeWrappers().register(JsonObject.class, MapJS::json);
		context.getTypeWrappers().register(JsonArray.class, ListJS::json);
		context.getTypeWrappers().register(ItemStack.class, o -> ItemStackJS.of(o).getItemStack());
		context.getTypeWrappers().register(CompoundTag.class, MapJS::nbt);
		context.getTypeWrappers().register(CollectionTag.class, ListJS::nbt);
		context.getTypeWrappers().register(ListTag.class, o -> (ListTag) ListJS.nbt(o));
		context.getTypeWrappers().register(UUID.class, UUIDUtilsJS::fromString);
		context.getTypeWrappers().register(Pattern.class, UtilsJS::parseRegex);
		context.getTypeWrappers().register(Component.class, Text::componentOfObject);
		context.getTypeWrappers().register(MutableComponent.class, o -> new TextComponent("").append(Text.componentOfObject(o)));
		context.getTypeWrappers().register(BlockPos.class, o -> {
			if (o instanceof BlockPos) {
				return (BlockPos) o;
			} else if (o instanceof List && ((List<?>) o).size() >= 3) {
				return new BlockPos(((Number) ((List<?>) o).get(0)).intValue(), ((Number) ((List<?>) o).get(1)).intValue(), ((Number) ((List<?>) o).get(2)).intValue());
			}

			return BlockPos.ZERO;
		});

		context.getTypeWrappers().register(Item.class, o -> ItemStackJS.of(o).getItem());
		wrapRegistry(context.getTypeWrappers(), Block.class, KubeJSRegistries.blocks());
		wrapRegistry(context.getTypeWrappers(), Fluid.class, KubeJSRegistries.fluids());
		wrapRegistry(context.getTypeWrappers(), SoundEvent.class, KubeJSRegistries.soundEvents());

		// KubeJS //
		context.getTypeWrappers().register(MapJS.class, MapJS::of);
		context.getTypeWrappers().register(ListJS.class, ListJS::of);
		context.getTypeWrappers().register(ItemStackJS.class, ItemStackJS::of);
		context.getTypeWrappers().register(IngredientJS.class, IngredientJS::of);
		context.getTypeWrappers().register(IngredientStackJS.class, o -> IngredientJS.of(o).asIngredientStack());
		context.getTypeWrappers().register(Text.class, Text::of);
		context.getTypeWrappers().register(BlockStatePredicate.class, BlockStatePredicate::of);
		context.getTypeWrappers().register(FluidStackJS.class, FluidStackJS::of);
		context.getTypeWrappers().register(RecipeFilter.class, RecipeFilter::of);
		context.getTypeWrappers().register(MaterialJS.class, MaterialListJS.INSTANCE::of);

		long startAll = System.currentTimeMillis();

		int i = 0;
		int t = 0;

		for (ScriptPack pack : packs.values()) {
			try {
				pack.context = context;
				pack.scope = context.initStandardObjects();

				BindingsEvent event = new BindingsEvent(type, pack.scope);
				BindingsEvent.EVENT.invoker().accept(event);
				DefaultBindings.init(this, event);

				for (ScriptFile file : pack.scripts) {
					t++;
					long start = System.currentTimeMillis();

					if (file.load()) {
						i++;
						type.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
					} else if (file.getError() != null) {
						if (file.getError() instanceof RhinoException) {
							type.console.error("Error loading KubeJS script: " + file.getError().getMessage());
						} else {
							type.console.error("Error loading KubeJS script: " + file.info.location + ": " + file.getError());
							file.getError().printStackTrace();
						}
					}
				}
			} catch (Throwable ex) {
				type.console.error("Failed to read script pack " + pack.info.namespace + ": ", ex);
				ex.printStackTrace();
			}
		}

		type.console.info("Loaded " + i + "/" + t + " KubeJS " + type.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s");
		Context.exit();

		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new EventJS());

		if (i != t && type == ScriptType.STARTUP) {
			throw new RuntimeException("There were startup script syntax errors! See logs/kubejs/startup.txt for more info");
		}

		firstLoad = false;
	}

	private static <T> void wrapRegistry(TypeWrappers typeWrappers, Class<T> c, Registry<T> registry) {
		typeWrappers.register(c, o -> {
			if (o == null) {
				return null;
			} else if (c.isAssignableFrom(o.getClass())) {
				return (T) o;
			}

			return registry.get(new ResourceLocation(o.toString()));
		});
	}
}