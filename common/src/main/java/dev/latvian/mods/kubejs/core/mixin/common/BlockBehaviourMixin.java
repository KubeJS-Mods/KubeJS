package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockBehaviourMixin implements BlockKJS {
	private BlockBuilder kjs$blockBuilder;
	private CompoundTag kjs$typeData;
	private ResourceLocation kjs$id;
	private String kjs$idString;

	@Override
	public ResourceLocation kjs$getIdLocation() {
		if (kjs$id == null) {
			if ((Object) this instanceof Block block) {
				var id = KubeJSRegistries.blocks().getId(block);
				kjs$id = id == null ? UtilsJS.UNKNOWN_ID : id;
			} else {
				kjs$id = UtilsJS.UNKNOWN_ID;
			}
		}

		return kjs$id;
	}

	@Override
	public String kjs$getId() {
		if (kjs$idString == null) {
			kjs$idString = kjs$getIdLocation().toString();
		}

		return kjs$idString;
	}

	@Override
	@Nullable
	public BlockBuilder kjs$getBlockBuilder() {
		return kjs$blockBuilder;
	}

	@Override
	public void kjs$setBlockBuilder(BlockBuilder b) {
		kjs$blockBuilder = b;
	}

	@Override
	public CompoundTag kjs$getTypeData() {
		if (kjs$typeData == null) {
			kjs$typeData = new CompoundTag();
		}

		return kjs$typeData;
	}

	@Override
	@Accessor("hasCollision")
	@Mutable
	public abstract void kjs$setHasCollision(boolean v);

	@Override
	@Accessor("explosionResistance")
	@Mutable
	public abstract void kjs$setExplosionResistance(float v);

	@Override
	@Accessor("isRandomlyTicking")
	@Mutable
	public abstract void kjs$setIsRandomlyTicking(boolean v);

	@Override
	@Accessor("soundType")
	@Mutable
	public abstract void kjs$setSoundType(SoundType v);

	@Override
	@Accessor("friction")
	@Mutable
	public abstract void kjs$setFriction(float v);

	@Override
	@Accessor("speedFactor")
	@Mutable
	public abstract void kjs$setSpeedFactor(float v);

	@Override
	@Accessor("jumpFactor")
	@Mutable
	public abstract void kjs$setJumpFactor(float v);
}
