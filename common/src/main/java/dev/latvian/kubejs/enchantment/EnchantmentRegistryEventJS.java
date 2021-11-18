package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.event.StartupEventJS;

import java.util.function.Consumer;

/**
 * @author ILIKEPIEFOO2
 */
public class EnchantmentRegistryEventJS extends StartupEventJS {

	// As of 1.16 KubeJS, this is the normal way to do builders.
	public EnchantmentBuilder create(String name){
		EnchantmentBuilder builder = new EnchantmentBuilder(name);
		KubeJSObjects.ENCHANTMENTS.put(builder.id,builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}

	// Implementation style for 1.18+ KubeJS.
	public EnchantmentBuilder create(String name, Consumer<EnchantmentBuilder> builderConsumer){
		EnchantmentBuilder builder = create(name);
		try {
			builderConsumer.accept(builder);
		}catch (Exception e){
			KubeJS.LOGGER.error("Error while creating enchantment: " + name, e);
		}
		return builder;
	}

	public EnchantmentBuilder create(String name, Object categoryWrapper, Consumer<EnchantmentBuilder> builderConsumer){
		EnchantmentBuilder builder = create(name, builderConsumer);
		builder.setCategory(categoryWrapper);
		return builder;
	}

	public EnchantmentBuilder create(String name, Object categoryWrapper, Object rarityWrapper, Consumer<EnchantmentBuilder> builderConsumer){
		EnchantmentBuilder builder = create(name, builderConsumer);
		builder.setCategory(categoryWrapper);
		builder.setRarity(rarityWrapper);
		return builder;
	}

}
