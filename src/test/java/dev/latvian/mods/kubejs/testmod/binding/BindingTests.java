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
