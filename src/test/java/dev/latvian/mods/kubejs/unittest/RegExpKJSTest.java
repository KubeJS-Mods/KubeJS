package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.RegExpKJS;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegExpKJSTest {
	@Test
	void parsesPatternWithFlag() {
		var pattern = RegExpKJS.ofString("/foo/i");
		assertNotNull(pattern);
		assertEquals("foo", pattern.pattern());
		assertTrue((pattern.flags() & Pattern.CASE_INSENSITIVE) != 0);
	}

	@Test
	void rejectsNonRegExpStrings() {
		assertNull(RegExpKJS.ofString("foo"));
		assertNull(RegExpKJS.ofString("/x"));
	}

	@Test
	void roundTripsThroughString() {
		assertEquals("/a.b/is", RegExpKJS.toRegExpString(RegExpKJS.ofString("/a.b/is")));
		assertEquals("/plain/", RegExpKJS.toRegExpString(RegExpKJS.ofString("/plain/")));
	}

	@Test
	void getFlagsCombinesBits() {
		assertEquals(Pattern.CASE_INSENSITIVE, RegExpKJS.getFlags("i"));
		assertEquals(
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
			RegExpKJS.getFlags("ims")
		);
	}

	@Test
	void flagValidation() {
		assertTrue(RegExpKJS.isValidFlag('i'));
		assertFalse(RegExpKJS.isValidFlag('z'));
	}
}
