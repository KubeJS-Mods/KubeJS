package dev.latvian.mods.kubejs.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;

public class KubeJSBlockProperties extends BlockBehaviour.Properties {
	public final BlockBuilder blockBuilder;

	public KubeJSBlockProperties(BlockBuilder blockBuilder, @Nullable Block copyPropertiesFrom) {
		super();
		this.blockBuilder = blockBuilder;

		if (copyPropertiesFrom != null) {
			// incredibly cursed but alternative is ATing every field

			try {
				var from = copyPropertiesFrom.properties();

				for (var field : BlockBehaviour.Properties.class.getDeclaredFields()) {
					if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
						field.setAccessible(true);
						field.set(this, field.get(from));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
