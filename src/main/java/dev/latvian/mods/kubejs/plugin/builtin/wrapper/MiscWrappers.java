package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface MiscWrappers {
	@SuppressWarnings("unchecked")
	static IntProvider wrapIntProvider(Context cx, Object o) {
		if (o instanceof Number n) {
			return ConstantInt.of(n.intValue());
		} else if (o instanceof List l && !l.isEmpty()) {
			var min = (Number) l.get(0);
			var max = l.size() >= 2 ? (Number) l.get(1) : min;
			return UniformInt.of(min.intValue(), max.intValue());
		} else if (o instanceof Map) {
			var m = (Map<String, Object>) o;

			var intBounds = parseIntBounds(m);
			if (intBounds != null) {
				return intBounds;
			} else if (m.containsKey("clamped")) {
				var source = wrapIntProvider(cx, m.get("clamped"));
				var clampTo = parseIntBounds(m);
				if (clampTo != null) {
					return ClampedInt.of(source, clampTo.getMinValue(), clampTo.getMaxValue());
				}
			} else if (m.containsKey("clamped_normal")) {
				var clampTo = parseIntBounds(m);
				var mean = ((Number) m.get("mean")).intValue();
				var deviation = ((Number) m.get("deviation")).intValue();
				if (clampTo != null) {
					return ClampedNormalInt.of(mean, deviation, clampTo.getMinValue(), clampTo.getMaxValue());
				}
			}

			var decoded = IntProvider.CODEC.parse(RegistryAccessContainer.of(cx).nbt(), NBTWrapper.wrapCompound(cx, m)).result();
			if (decoded.isPresent()) {
				return decoded.get();
			}
		}

		return ConstantInt.of(0);
	}

	@SuppressWarnings("unchecked")
	static FloatProvider wrapFloatProvider(Context cx, Object o) {
		if (o instanceof Number n) {
			return ConstantFloat.of(n.floatValue());
		} else if (o instanceof List l && !l.isEmpty()) {
			var min = (Number) l.get(0);
			var max = l.size() >= 2 ? (Number) l.get(1) : min;
			return UniformFloat.of(min.floatValue(), max.floatValue());
		} else if (o instanceof Map) {
			var m = (Map<String, Object>) o;

			var floatBounds = parseFloatBounds(m);
			if (floatBounds != null) {
				return floatBounds;
			} else if (m.containsKey("clamped_normal")) {
				var clampTo = parseFloatBounds(m);
				var mean = ((Number) m.get("mean")).intValue();
				var deviation = ((Number) m.get("deviation")).intValue();
				if (clampTo != null) {
					return ClampedNormalFloat.of(mean, deviation, clampTo.getMinValue(), clampTo.getMaxValue());
				}
			}

			var decoded = FloatProvider.CODEC.parse(RegistryAccessContainer.of(cx).nbt(), NBTWrapper.wrapCompound(cx, m)).result();

			if (decoded.isPresent()) {
				return decoded.get();
			}
		}

		return ConstantFloat.of(0F);
	}

	@SuppressWarnings("unchecked")
	static NumberProvider wrapNumberProvider(Object o) {
		if (o instanceof Number n) {
			var f = n.floatValue();
			return UniformGenerator.between(f, f);
		} else if (o instanceof List l && !l.isEmpty()) {
			var min = (Number) l.get(0);
			var max = l.size() >= 2 ? (Number) l.get(1) : min;
			return UniformGenerator.between(min.floatValue(), max.floatValue());
		} else if (o instanceof Map) {
			var m = (Map<String, Object>) o;
			if (m.containsKey("min") && m.containsKey("max")) {
				return UniformGenerator.between(((Number) m.get("min")).intValue(), ((Number) m.get("max")).floatValue());
			} else if (m.containsKey("n") && m.containsKey("p")) {
				return BinomialDistributionGenerator.binomial(((Number) m.get("n")).intValue(), ((Number) m.get("p")).floatValue());
			} else if (m.containsKey("value")) {
				var f = ((Number) m.get("value")).floatValue();
				return UniformGenerator.between(f, f);
			}
		}

		return ConstantValue.exactly(0);
	}

	static Vec3 wrapVec3(@Nullable Object o) {
		return switch (o) {
			case Vec3 vec -> vec;
			case Entity entity -> entity.position();
			case List<?> list when list.size() >= 3 -> new Vec3(StringUtilsWrapper.parseDouble(list.get(0), 0), StringUtilsWrapper.parseDouble(list.get(1), 0), StringUtilsWrapper.parseDouble(list.get(2), 0));
			case BlockPos pos -> new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			case LevelBlock block -> new Vec3(block.getCenterX(), block.getCenterY(), block.getCenterZ());
			case null, default -> Vec3.ZERO;
		};

	}

	static BlockPos wrapBlockPos(@Nullable Object o) {
		return switch (o) {
			case BlockPos pos -> pos;
			case List<?> list when list.size() >= 3 -> new BlockPos(StringUtilsWrapper.parseInt(list.get(0), 0), StringUtilsWrapper.parseInt(list.get(1), 0), StringUtilsWrapper.parseInt(list.get(2), 0));
			case LevelBlock block -> block.getPos();
			case Vec3 vec -> BlockPos.containing(vec.x, vec.y, vec.z);
			case null, default -> BlockPos.ZERO;
		};

	}

	private static UniformInt parseIntBounds(Map<String, Object> m) {
		if (m.get("bounds") instanceof List bounds) {
			return UniformInt.of(StringUtilsWrapper.parseInt(bounds.get(0), 0), StringUtilsWrapper.parseInt(bounds.get(1), 0));
		} else if (m.containsKey("min") && m.containsKey("max")) {
			return UniformInt.of(((Number) m.get("min")).intValue(), ((Number) m.get("max")).intValue());
		} else if (m.containsKey("min_inclusive") && m.containsKey("max_inclusive")) {
			return UniformInt.of(((Number) m.get("min_inclusive")).intValue(), ((Number) m.get("max_inclusive")).intValue());
		} else if (m.containsKey("value")) {
			var f = ((Number) m.get("value")).intValue();
			return UniformInt.of(f, f);
		}
		return null;
	}

	private static UniformFloat parseFloatBounds(Map<String, Object> m) {
		if (m.get("bounds") instanceof List bounds) {
			return UniformFloat.of((float) StringUtilsWrapper.parseDouble(bounds.get(0), 0), (float) StringUtilsWrapper.parseDouble(bounds.get(1), 0));
		} else if (m.containsKey("min") && m.containsKey("max")) {
			return UniformFloat.of(((Number) m.get("min")).floatValue(), ((Number) m.get("max")).floatValue());
		} else if (m.containsKey("min_inclusive") && m.containsKey("max_inclusive")) {
			return UniformFloat.of(((Number) m.get("min_inclusive")).floatValue(), ((Number) m.get("max_inclusive")).floatValue());
		} else if (m.containsKey("value")) {
			var f = ((Number) m.get("value")).floatValue();
			return UniformFloat.of(f, f);
		}
		return null;
	}

	@Nullable
	static Path wrapPath(Object o) {
		try {
			if (o instanceof Path) {
				return KubeJSPaths.verifyFilePath((Path) o);
			} else if (o == null || o.toString().isEmpty()) {
				return null;
			}

			return KubeJSPaths.verifyFilePath(KubeJSPaths.GAMEDIR.resolve(o.toString()));
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	static File wrapFile(Object o) {
		try {
			if (o instanceof File) {
				return KubeJSPaths.verifyFilePath(((File) o).toPath()).toFile();
			} else if (o == null || o.toString().isEmpty()) {
				return null;
			}

			return KubeJSPaths.verifyFilePath(KubeJSPaths.GAMEDIR.resolve(o.toString())).toFile();
		} catch (Exception ex) {
			return null;
		}
	}
}
