package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.NBTUtils;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
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

public interface KubeJSTypeWrappers {
	@SuppressWarnings("unchecked")
	static IntProvider intProviderOf(Context cx, Object o) {
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
				var source = intProviderOf(cx, m.get("clamped"));
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

			var decoded = IntProvider.CODEC.parse(((KubeJSContext) cx).getNbtOps(), NBTUtils.toTagCompound(cx, m)).result();
			if (decoded.isPresent()) {
				return decoded.get();
			}
		}

		return ConstantInt.of(0);
	}

	@SuppressWarnings("unchecked")
	static NumberProvider numberProviderOf(Object o) {
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

	static Vec3 vec3Of(@Nullable Object o) {
		if (o instanceof Vec3 vec) {
			return vec;
		} else if (o instanceof Entity entity) {
			return entity.position();
		} else if (o instanceof List<?> list && list.size() >= 3) {
			return new Vec3(UtilsJS.parseDouble(list.get(0), 0), UtilsJS.parseDouble(list.get(1), 0), UtilsJS.parseDouble(list.get(2), 0));
		} else if (o instanceof BlockPos pos) {
			return new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		} else if (o instanceof BlockContainerJS block) {
			return new Vec3(block.getPos().getX() + 0.5D, block.getPos().getY() + 0.5D, block.getPos().getZ() + 0.5D);
		}

		return Vec3.ZERO;
	}

	static BlockPos blockPosOf(@Nullable Object o) {
		if (o instanceof BlockPos pos) {
			return pos;
		} else if (o instanceof List<?> list && list.size() >= 3) {
			return new BlockPos(UtilsJS.parseInt(list.get(0), 0), UtilsJS.parseInt(list.get(1), 0), UtilsJS.parseInt(list.get(2), 0));
		} else if (o instanceof BlockContainerJS block) {
			return block.getPos();
		} else if (o instanceof Vec3 vec) {
			return BlockPos.containing(vec.x, vec.y, vec.z);
		}

		return BlockPos.ZERO;
	}

	private static UniformInt parseIntBounds(Map<String, Object> m) {
		if (m.get("bounds") instanceof List bounds) {
			return UniformInt.of(UtilsJS.parseInt(bounds.get(0), 0), UtilsJS.parseInt(bounds.get(1), 0));
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

	@Nullable
	static Path pathOf(Object o) {
		try {
			if (o instanceof Path) {
				return KubeJS.verifyFilePath((Path) o);
			} else if (o == null || o.toString().isEmpty()) {
				return null;
			}

			return KubeJS.verifyFilePath(KubeJSPaths.GAMEDIR.resolve(o.toString()));
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	static File fileOf(Object o) {
		try {
			if (o instanceof File) {
				return KubeJS.verifyFilePath(((File) o).toPath()).toFile();
			} else if (o == null || o.toString().isEmpty()) {
				return null;
			}

			return KubeJS.verifyFilePath(KubeJSPaths.GAMEDIR.resolve(o.toString())).toFile();
		} catch (Exception ex) {
			return null;
		}
	}
}
