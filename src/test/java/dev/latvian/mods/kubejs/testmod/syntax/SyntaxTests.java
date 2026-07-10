package dev.latvian.mods.kubejs.testmod.syntax;

import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertFired;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertVerified;

/// Category 4. Verifies the JS-level syntaxes exercised by `syntax_checks.js` still evaluate under the
/// current Rhino, guarding against language regressions between versions.
@ForEachTest(groups = "kubejs.syntax")
public class SyntaxTests {
	// syntax.default_params and syntax.spread_rest are disabled in syntax_checks.js - the current Rhino
	// rejects default/rest parameters at parse time. Add their ids here if that support ever lands.
	private static final String[] IDS = {
		"syntax.arrow",
		"syntax.destructuring",
		"syntax.template_literals",
		"syntax.for_of",
		"syntax.array_methods",
		"syntax.map_and_set",
	};

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "syntax_features", description = "Core JS language features still evaluate under the current Rhino")
	static void syntaxFeatures(final DynamicTest test) {
		test.onGameTest(helper -> {
			for (var id : IDS) {
				assertFired(helper, id);
				assertVerified(helper, id);
			}

			helper.succeed();
		});
	}
}
