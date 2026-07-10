// Category 3 - JS-driven binding tests. Verify bindings are reachable from JS and behave, and that
// the file-writing bindings (JsonIO, NBTIO) actually create files. BindingTests verifies the
// captured assertions and that the files exist on disk after the run.
//
// Registry-independent bindings (ID, Text, NBT, JsonUtils, JsonIO, NBTIO) are checked at script load.
// Item and Ingredient parsing is registry-backed, so those run on the first server tick (see the
// bottom of this file) - the same deferral wrapper_checks.js uses.

TestRuntime.check('binding.id', () => {
	TestRuntime.assertThat(ID.namespace('thing')).isEqualTo('minecraft');
	TestRuntime.assertThat(ID.string('air')).isEqualTo('minecraft:air');
	TestRuntime.assertThat(ID.kjsString('x')).isEqualTo('kubejs:x');
});

TestRuntime.check('binding.text', () => {
	TestRuntime.assertThat(Text.ofString('hello').getString()).isEqualTo('hello');
});

TestRuntime.check('binding.text.builders', () => {
	TestRuntime.assertThat(Text.string('plain').getString()).isEqualTo('plain');
	TestRuntime.assertThat(Text.literal('lit').getString()).isEqualTo('lit');
	TestRuntime.assertThat(Text.of('wrapped').getString()).isEqualTo('wrapped');
	TestRuntime.assertThat(Text.string('a').append(Text.string('b')).getString()).isEqualTo('ab');
	TestRuntime.assertThat(Text.isEmpty(Text.empty())).isTrue();
	// A translatable component falls back to its key when no translation is loaded (server side).
	TestRuntime.assertThat(Text.translate('kubejs.test.missing.key').getString()).isEqualTo('kubejs.test.missing.key');
});

TestRuntime.check('binding.text.styled', () => {
	let styled = Text.of({ text: 'hi', bold: true, color: 'red' });
	TestRuntime.assertThat(styled.getString()).isEqualTo('hi');
	TestRuntime.assertThat(styled.getStyle().isBold()).isTrue();
	TestRuntime.assertThat(styled.getStyle().getColor() !== null).isTrue();
	// Color helpers apply a color to their input component.
	TestRuntime.assertThat(Text.red(Text.string('x')).getStyle().getColor() !== null).isTrue();
});

TestRuntime.check('binding.nbt.compound', () => {
	let tag = NBT.compoundTag({ a: 1, b: 'two' });
	TestRuntime.assertThat(tag.contains('a')).isTrue();
	TestRuntime.assertThat(tag.contains('b')).isTrue();
	TestRuntime.assertThat(tag.size() === 2).isTrue();
	TestRuntime.assertThat(NBT.compoundTag().isEmpty()).isTrue();
});

TestRuntime.check('binding.nbt.typed', () => {
	TestRuntime.assertThat(NBT.intTag(5).toString()).isEqualTo('5');
	TestRuntime.assertThat(NBT.longTag(7).toString()).isEqualTo('7L');
	TestRuntime.assertThat(NBT.stringTag('hi').toString()).isEqualTo('"hi"');
	TestRuntime.assertThat(NBT.listTag(['a', 'b']).toString()).isEqualTo('["a","b"]');
});

TestRuntime.check('binding.nbt.json', () => {
	TestRuntime.assertThat(NBT.toJson(NBT.compoundTag({ k: 'v' })).toString()).isEqualTo('{"k":"v"}');
});

TestRuntime.check('binding.json.roundtrip', () => {
	TestRuntime.assertThat(JsonUtils.toString(JsonUtils.fromString('{"msg":"hello"}'))).isEqualTo('{"msg":"hello"}');
	TestRuntime.assertThat(JsonUtils.toObject(JsonUtils.fromString('{"msg":"hi"}')).get('msg') === 'hi').isTrue();
});

TestRuntime.check('binding.json.build', () => {
	TestRuntime.assertThat(JsonUtils.toString(JsonUtils.of({ msg: 'hi' }))).isEqualTo('{"msg":"hi"}');
	TestRuntime.assertThat(JsonUtils.toString(JsonUtils.arrayOf(['x', 'y']))).isEqualTo('["x","y"]');
	TestRuntime.assertThat(JsonUtils.toPrettyString(JsonUtils.fromString('{"a":"b"}')).contains('\n')).isTrue();

	let original = JsonUtils.fromString('{"a":"b"}');
	let copy = JsonUtils.copy(original);
	TestRuntime.assertThat(copy !== original).isTrue();
	TestRuntime.assertThat(JsonUtils.toString(copy)).isEqualTo('{"a":"b"}');
});

TestRuntime.check('binding.jsonio', () => {
	JsonIO.write('kubejs/test_binding.json', { hello: 'world', count: 5 });
	TestRuntime.assertThat(JsonIO.readString('kubejs/test_binding.json')).contains('world');
});

TestRuntime.check('binding.nbtio', () => {
	NBTIO.write('kubejs/test_binding.nbt', { a: 1 });
	let tag = NBTIO.read('kubejs/test_binding.nbt');
	TestRuntime.assertThat(tag.contains('a')).isTrue();
});

let bindingChecksRun = false;

ServerEvents.tick(event => {
	if (bindingChecksRun) {
		return;
	}

	bindingChecksRun = true;

	TestRuntime.check('binding.item.of', () => {
		let stack = Item.of('minecraft:diamond');
		TestRuntime.assertThat(stack.id).isEqualTo('minecraft:diamond');
		TestRuntime.assertThat(stack.getCount() === 1).isTrue();
	});

	TestRuntime.check('binding.item.count', () => {
		TestRuntime.assertThat(Item.of('minecraft:diamond', 4).getCount() === 4).isTrue();
		TestRuntime.assertThat(Item.of('4x minecraft:diamond').getCount() === 4).isTrue();
	});

	TestRuntime.check('binding.item.meta', () => {
		TestRuntime.assertThat(Item.getEmpty().isEmpty()).isTrue();
		TestRuntime.assertThat(Item.isItem(Item.of('minecraft:diamond'))).isTrue();
		TestRuntime.assertThat(Item.isItem('minecraft:diamond')).isFalse();
		TestRuntime.assertThat(Item.exists('minecraft:diamond')).isTrue();
		TestRuntime.assertThat(Item.exists('minecraft:totally_not_a_real_item')).isFalse();
	});

	TestRuntime.check('binding.ingredient.match', () => {
		let diamonds = Ingredient.of('minecraft:diamond');
		TestRuntime.assertThat(diamonds.test(Item.of('minecraft:diamond'))).isTrue();
		TestRuntime.assertThat(diamonds.test(Item.of('minecraft:dirt'))).isFalse();
	});

	TestRuntime.check('binding.ingredient.tag', () => {
		let planks = Ingredient.of('#minecraft:planks');
		TestRuntime.assertThat(planks.test(Item.of('minecraft:oak_planks'))).isTrue();
		TestRuntime.assertThat(planks.test(Item.of('minecraft:diamond'))).isFalse();
	});

	TestRuntime.check('binding.ingredient.compound', () => {
		let either = Ingredient.of(['minecraft:diamond', 'minecraft:dirt']);
		TestRuntime.assertThat(either.test(Item.of('minecraft:diamond'))).isTrue();
		TestRuntime.assertThat(either.test(Item.of('minecraft:dirt'))).isTrue();
		TestRuntime.assertThat(either.test(Item.of('minecraft:stone'))).isFalse();
	});

	TestRuntime.check('binding.ingredient.meta', () => {
		TestRuntime.assertThat(Ingredient.isIngredient(Ingredient.of('minecraft:diamond'))).isTrue();
		TestRuntime.assertThat(Ingredient.isIngredient('minecraft:diamond')).isFalse();
		TestRuntime.assertThat(Ingredient.of('minecraft:diamond').first.id).isEqualTo('minecraft:diamond');
	});
});
