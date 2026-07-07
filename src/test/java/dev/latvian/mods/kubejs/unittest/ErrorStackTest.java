package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.ErrorStack;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorStackTest {
	@Test
	void emptyStackRendersNothing() {
		var stack = new ErrorStack();
		assertThat(stack.toString()).isEmpty();
		assertThat(stack.atString()).isEmpty();
	}

	@Test
	void nestedKeysRenderInOrder() {
		var stack = new ErrorStack();
		stack.push("first");
		stack.setKey("a");
		stack.push("second");
		stack.setKey("b");
		assertThat(stack.toString()).isEqualTo("[a][b]");
		assertThat(stack.atString()).isEqualTo(" @ [a][b]");
		assertThat(stack.stringAt()).isEqualTo("[a][b] @ ");
	}

	@Test
	void poppingUnwindsTheStack() {
		var stack = new ErrorStack();
		stack.push("first");
		stack.setKey("a");
		stack.push("second");
		stack.setKey("b");
		stack.pop();
		assertThat(stack.toString()).isEmpty();
	}

	@Test
	void noneIsANoOp() {
		ErrorStack.NONE.push("x");
		ErrorStack.NONE.setKey("y");
		assertThat(ErrorStack.NONE.toString()).isEmpty();
	}
}
