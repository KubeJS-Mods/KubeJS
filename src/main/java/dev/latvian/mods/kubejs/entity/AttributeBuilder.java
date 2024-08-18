package dev.latvian.mods.kubejs.entity;

import com.google.common.base.Predicates;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.common.BooleanAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@ReturnsSelf
public class AttributeBuilder extends BuilderBase<Attribute> {
	public record Range(double defaultValue, double min, double max) {
	}

	public final List<Predicate<EntityType<?>>> predicateList = new ArrayList<>();
	public Either<Range, Boolean> defaultValue;
	public boolean syncable = true;
	public Attribute.Sentiment sentiment;

	public AttributeBuilder(ResourceLocation id) {
		super(id);
	}

	public AttributeBuilder bool(boolean defaultValue) {
		this.defaultValue = Either.right(defaultValue);
		return this;
	}

	public AttributeBuilder range(double defaultValue, double min, double max) {
		this.defaultValue = Either.left(new Range(defaultValue, min, max));
		return this;
	}

	public AttributeBuilder syncable(boolean watch) {
		this.syncable = watch;
		return this;
	}

	public AttributeBuilder sentiment(Attribute.Sentiment sentiment) {
		this.sentiment = sentiment;
		return this;
	}

	public AttributeBuilder negativeSentiment() {
		return sentiment(Attribute.Sentiment.NEGATIVE);
	}

	public AttributeBuilder neutralSentiment() {
		return sentiment(Attribute.Sentiment.NEUTRAL);
	}

	public AttributeBuilder attachTo(Predicate<EntityType<?>> entityType) {
		predicateList.add(entityType);
		return this;
	}

	public AttributeBuilder attachToPlayers() {
		predicateList.add(entityType -> entityType == EntityType.PLAYER);
		return this;
	}

	public AttributeBuilder attachToMonsters() {
		predicateList.add(entityType -> entityType.getCategory() == MobCategory.MONSTER);
		return this;
	}

	public AttributeBuilder attachToCategory(MobCategory category) {
		predicateList.add(entityType -> entityType.getCategory() == category);
		return this;
	}

	@HideFromJS
	public List<Predicate<EntityType<?>>> getPredicateList() {
		return Collections.unmodifiableList(predicateList);
	}

	@Override
	public Attribute createObject() {
		if (defaultValue == null) {
			throw new IllegalArgumentException("Not possible to create a Boolean or Ranged Attribute. Use bool() or range() methods.");
		}

		return Either.unwrap(defaultValue.mapBoth(
			l -> new RangedAttribute(this.getBuilderTranslationKey(), l.defaultValue, l.min, l.max),
			r -> new BooleanAttribute(this.getBuilderTranslationKey(), r))
		);
	}

	@Override
	public Attribute transformObject(Attribute attribute) {
		if (syncable) {
			attribute.setSyncable(true);
		}

		if (sentiment != null) {
			attribute.setSentiment(sentiment);
		}

		if (predicateList.isEmpty()) {
			predicateList.add(Predicates.alwaysTrue());
		}
		return attribute;
	}
}
