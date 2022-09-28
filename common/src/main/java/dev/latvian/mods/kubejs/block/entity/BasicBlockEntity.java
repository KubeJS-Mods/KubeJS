package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.block.entity.ablities.BlockAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class BasicBlockEntity extends BlockEntity {
	public transient BlockEntityBuilder blockEntityBuilder;
	public transient Map<String, BlockAbility<?>> blockAbilities = new HashMap<>();

	public BasicBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		for (var ele : RegistryObjectBuilderTypes.BLOCK_ENTITY_TYPE.objects.values()) {
			if (ele instanceof BlockEntityBuilder e) {
				if (e.get() == blockEntityType) {
					init(e);
					break;
				}
			}
		}
	}

	public BasicBlockEntity(BlockPos blockPos, BlockState blockState, BlockEntityBuilder blockEntityBuilder) {
		super(blockEntityBuilder.get(), blockPos, blockState);
		init(blockEntityBuilder);
	}

	public void init(BlockEntityBuilder blockEntityBuilder) {
		if (this.blockEntityBuilder == null) {
			this.blockEntityBuilder = blockEntityBuilder;
			for (var pair : blockEntityBuilder.blockAbilities.entrySet()) {
				blockAbilities.put(pair.getKey(), pair.getValue().getB().apply(pair.getValue().getA()));
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag compoundTag) {
		super.saveAdditional(compoundTag);
		CompoundTag blockAbilitiesNbt = new CompoundTag();
		for (var pair : blockAbilities.entrySet()) {
			blockAbilitiesNbt.put(pair.getKey(), pair.getValue().toTag());
		}
		compoundTag.put("abilities", blockAbilitiesNbt);
	}



	@Override
	public void load(CompoundTag compoundTag) {
		super.load(compoundTag);
		CompoundTag blockAbilitiesNbt = compoundTag.getCompound("abilities");
		for (var key : blockAbilitiesNbt.getAllKeys()) {
			CompoundTag abilityNbt = blockAbilitiesNbt.getCompound(key);
			blockAbilities.get(key).fromTag(abilityNbt);
		}
	}

	public BlockAbility<?> getAbility(String id) {
		return blockAbilities.get(id);
	}
}