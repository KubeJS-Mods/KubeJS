package dev.latvian.kubejs.world.gen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.Tags;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.gen.filter.BiomeFilter;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.registry.BiomeModifications;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.UniformInt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.function.Consumer;

// TODO: MAJOR cleanup needed!

/**
 * @author LatvianModder
 */
public class WorldgenAddEventJS extends StartupEventJS {

	private static MessageDigest messageDigest;

	protected void addFeature(BiomeFilter filter, GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature) {
		ResourceLocation id = new ResourceLocation("kjs_" + getUniqueId(feature));
		registerFeature0(id, feature);
		BiomeModifications.postProcessProperties(filter, (ctx, properties) -> {
			properties.getGenerationProperties().addFeature(decoration, feature);
		});
	}

	@ExpectPlatform
	public static void registerFeature0(ResourceLocation id, ConfiguredFeature<?,?> feature) {
	}

	protected void addEntitySpawn(BiomeFilter filter, MobCategory category, MobSpawnSettings.SpawnerData spawnerData) {
		BiomeModifications.postProcessProperties(filter, (ctx, properties) -> {
			properties.getSpawnProperties().addSpawn(category, spawnerData);
		});
	}

	public void addOre(Consumer<AddOreProperties> p) {
		AddOreProperties properties = new AddOreProperties();
		p.accept(properties);

		if (properties._block == Blocks.AIR.defaultBlockState()) {
			return;
		}

		AnyRuleTest ruleTest = new AnyRuleTest();

		for (Object o : ListJS.orSelf(properties.spawnsIn.values)) {
			String s = String.valueOf(o);
			boolean invert = false;

			while (s.startsWith("!")) {
				s = s.substring(1);
				invert = !invert;
			}

			if (s.startsWith("#")) {
				ResourceLocation id = new ResourceLocation(s.substring(1));
				Tag<Block> tag = Tags.blocks().getTagOrEmpty(id);
				if (tag != null) {
					RuleTest tagTest = new TagMatchTest(tag);
					ruleTest.list.add(invert ? new InvertRuleTest(tagTest) : tagTest);
				} else {
					ScriptType.STARTUP.console.warn("Skipped tag rule test as tag " + id + " doesn't exist!");
				}
			} else {
				BlockState bs = UtilsJS.parseBlockState(s);
				RuleTest blockTest = s.indexOf('[') != -1 ? new BlockStateMatchTest(bs) : new BlockMatchTest(bs.getBlock());
				ruleTest.list.add(invert ? new InvertRuleTest(blockTest) : blockTest);
			}
		}

		RuleTest ruleTest1 = ruleTest.list.isEmpty() ? OreConfiguration.Predicates.NATURAL_STONE : ruleTest;

		ConfiguredFeature<OreConfiguration, ?> oreConfig = (properties.noSurface ? Feature.NO_SURFACE_ORE : Feature.ORE).configured(new OreConfiguration(properties.spawnsIn.blacklist ? new InvertRuleTest(ruleTest1) : ruleTest1, properties._block, properties.clusterMaxSize));

		oreConfig = UtilsJS.cast(oreConfig.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(properties.minHeight, 0, properties.maxHeight))));
		oreConfig = UtilsJS.cast(oreConfig.count(UniformInt.of(properties.clusterMinCount, properties.clusterMaxCount - properties.clusterMinCount)));

		if (properties.chance > 0) {
			oreConfig = UtilsJS.cast(oreConfig.chance(properties.chance));
		}

		if (properties.squared) {
			oreConfig = UtilsJS.cast(oreConfig.squared());
		}

		addFeature(properties._biomes, properties._worldgenLayer, oreConfig);
	}

	public void addLake(Consumer<AddLakeProperties> p) {
		AddLakeProperties properties = new AddLakeProperties();
		p.accept(properties);

		if (properties._block == Blocks.AIR.defaultBlockState()) {
			return;
		}

		addFeature(properties._biomes, properties._worldgenLayer, Feature.LAKE.configured(new BlockStateConfiguration(properties._block)).decorated((FeatureDecorator.WATER_LAKE).configured(new ChanceDecoratorConfiguration(properties.chance))));
	}

	public void addSpawn(Consumer<AddSpawnProperties> p) {
		AddSpawnProperties properties = new AddSpawnProperties();
		p.accept(properties);

		if (properties._entity == null || properties._category == null) {
			return;
		}

		addEntitySpawn(properties._biomes, properties._category, new MobSpawnSettings.SpawnerData(properties._entity, properties.weight, properties.minCount, properties.maxCount));
	}

	private static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
		if (element == null || element.isJsonNull()) {
			stream.writeByte('-');
		} else if (element instanceof JsonArray) {
			stream.writeByte('[');
			for (JsonElement e : (JsonArray) element) {
				writeJsonHash(stream, e);
			}
		} else if (element instanceof JsonObject) {
			stream.writeByte('{');
			for (Map.Entry<String, JsonElement> e : ((JsonObject) element).entrySet()) {
				stream.writeBytes(e.getKey());
				writeJsonHash(stream, e.getValue());
			}
		} else if (element instanceof JsonPrimitive) {
			stream.writeByte('=');
			if (((JsonPrimitive) element).isBoolean()) {
				stream.writeBoolean(element.getAsBoolean());
			} else if (((JsonPrimitive) element).isNumber()) {
				stream.writeDouble(element.getAsDouble());
			} else {
				stream.writeBytes(element.getAsString());
			}
		} else {
			stream.writeByte('?');
			stream.writeInt(element.hashCode());
		}
	}

	public byte[] getJsonHashBytes(JsonElement json) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writeJsonHash(new DataOutputStream(baos), json);
		} catch (IOException ex) {
			ex.printStackTrace();
			int h = json.hashCode();
			return new byte[]{(byte) (h >> 24), (byte) (h >> 16), (byte) (h >> 8), (byte) (h >> 0)};
		}

		return baos.toByteArray();
	}

	public String getUniqueId(ConfiguredFeature<?, ?> feature) {
		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				throw new InternalError("MD5 not supported", nsae);
			}
		}

		JsonElement json = ConfiguredFeature.DIRECT_CODEC.encodeStart(JsonOps.COMPRESSED, feature)
				.getOrThrow(false, str -> {
					throw new RuntimeException("Could not encode feature to JSON: " + str);
				});

		if (messageDigest == null) {
			return new BigInteger(Hex.encodeHexString(getJsonHashBytes(json)), 16).toString(36);
		} else {
			messageDigest.reset();
			return new BigInteger(Hex.encodeHexString(messageDigest.digest(getJsonHashBytes(json))), 16).toString(36);
		}
	}
}