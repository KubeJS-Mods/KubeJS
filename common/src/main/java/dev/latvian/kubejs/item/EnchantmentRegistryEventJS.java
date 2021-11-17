package dev.latvian.kubejs.item;

import dev.latvian.kubejs.bindings.EnchantmentCategoryWrapper;
import dev.latvian.kubejs.bindings.EnchantmentRarityWrapper;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.event.StartupEventJS;

import java.util.function.Consumer;

public class EnchantmentRegistryEventJS extends StartupEventJS {

	public EnchantmentBuilder create(String name){
		EnchantmentBuilder builder = new EnchantmentBuilder(name);
		KubeJSObjects.ENCHANTMENTS.put(builder.id,builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}

	public EnchantmentBuilder create(String name, Consumer<EnchantmentBuilder> builderConsumer){
		EnchantmentBuilder builder = create(name);
		builderConsumer.accept(builder);
		return builder;
	}

	public EnchantmentBuilder create(String name, EnchantmentCategoryWrapper categoryWrapper, Consumer<EnchantmentBuilder> builderConsumer){
		EnchantmentBuilder builder = create(name);
		builder.categoryWrapper = categoryWrapper;
		builderConsumer.accept(builder);
		return builder;
	}

	public EnchantmentBuilder create(String name, EnchantmentCategoryWrapper categoryWrapper, EnchantmentRarityWrapper rarityWrapper, Consumer<EnchantmentBuilder> builderConsumer){
		EnchantmentBuilder builder = create(name);
		builder.rarityWrapper = rarityWrapper;
		builder.categoryWrapper = categoryWrapper;
		builderConsumer.accept(builder);
		return builder;
	}

	public EnchantmentBuilder create(String name, EnchantmentRarityWrapper rarityWrapper, Consumer<EnchantmentBuilder> builderConsumer){
		EnchantmentBuilder builder = create(name);
		builder.rarityWrapper = rarityWrapper;
		builderConsumer.accept(builder);
		return builder;
	}
}
