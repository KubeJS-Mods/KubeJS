package dev.latvian.mods.kubejs.testmod.binding;

import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertFired;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertVerified;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertj;
import static org.assertj.core.api.Assertions.assertThat;

/// Category 3. Verifies that script bindings are reachable and work (`binding_checks.js`), including
/// that the file-writing bindings actually create files on disk.
///
/// ID/Text/NBT/JsonUtils checks fire as the script loads, so they are asserted directly. Item and
/// Ingredient parsing is registry-backed and runs on the first server tick, so those are awaited.
@ForEachTest(groups = "kubejs.binding")
public class BindingTests {
	@GameTest
	@EmptyTemplate
	@TestHolder(value = "binding_reachable", description = "ID/Text bindings are reachable from JS and behave as expected")
	static void bindingReachable(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "binding.id");
			assertVerified(helper, "binding.id");
			assertFired(helper, "binding.text");
			assertVerified(helper, "binding.text");
			helper.succeed();
		});
	}

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "binding_text", description = "Text binding builders and styling behave as expected")
	static void bindingText(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "binding.text.builders");
			assertVerified(helper, "binding.text.builders");
			assertFired(helper, "binding.text.styled");
			assertVerified(helper, "binding.text.styled");
			helper.succeed();
		});
	}

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "binding_nbt", description = "NBT binding builds compound/typed/list tags and converts to JSON")
	static void bindingNbt(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "binding.nbt.compound");
			assertVerified(helper, "binding.nbt.compound");
			assertFired(helper, "binding.nbt.typed");
			assertVerified(helper, "binding.nbt.typed");
			assertFired(helper, "binding.nbt.json");
			assertVerified(helper, "binding.nbt.json");
			helper.succeed();
		});
	}

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "binding_json", description = "JsonUtils binding round-trips, builds and copies JSON")
	static void bindingJson(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "binding.json.roundtrip");
			assertVerified(helper, "binding.json.roundtrip");
			assertFired(helper, "binding.json.build");
			assertVerified(helper, "binding.json.build");
			helper.succeed();
		});
	}

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "binding_item", description = "Item binding parses ids, counts and metadata helpers")
	static void bindingItem(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence()
			.thenWaitUntil(() -> assertFired(helper, "binding.item.of"))
			.thenExecute(() -> assertVerified(helper, "binding.item.of"))
			.thenExecute(() -> assertVerified(helper, "binding.item.count"))
			.thenExecute(() -> assertVerified(helper, "binding.item.meta"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "binding_ingredient", description = "Ingredient binding matches items, tags, compounds and metadata helpers")
	static void bindingIngredient(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence()
			.thenWaitUntil(() -> assertFired(helper, "binding.ingredient.match"))
			.thenExecute(() -> assertVerified(helper, "binding.ingredient.match"))
			.thenExecute(() -> assertVerified(helper, "binding.ingredient.tag"))
			.thenExecute(() -> assertVerified(helper, "binding.ingredient.compound"))
			.thenExecute(() -> assertVerified(helper, "binding.ingredient.meta"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "binding_file_io", description = "JsonIO/NBTIO bindings write files that exist on disk afterwards")
	static void bindingFileIo(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "binding.jsonio");
			assertVerified(helper, "binding.jsonio");
			assertj(helper, () -> assertThat(Files.isRegularFile(Path.of("kubejs/test_binding.json"))).as("JsonIO should have created the json file").isTrue());

			assertFired(helper, "binding.nbtio");
			assertVerified(helper, "binding.nbtio");
			assertj(helper, () -> assertThat(Files.isRegularFile(Path.of("kubejs/test_binding.nbt"))).as("NBTIO should have created the nbt file").isTrue());

			helper.succeed();
		});
	}
}
