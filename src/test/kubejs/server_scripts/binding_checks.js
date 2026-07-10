// Category 3 - JS-driven binding tests. Verify bindings are reachable from JS and behave, and that
// the file-writing bindings (JsonIO, NBTIO) actually create files. BindingTests verifies the
// captured assertions and that the files exist on disk after the run.

TestRuntime.check('binding.id', () => {
	TestRuntime.assertThat(ID.namespace('thing')).isEqualTo('minecraft');
	TestRuntime.assertThat(ID.string('air')).isEqualTo('minecraft:air');
	TestRuntime.assertThat(ID.kjsString('x')).isEqualTo('kubejs:x');
});

TestRuntime.check('binding.text', () => {
	TestRuntime.assertThat(Text.ofString('hello').getString()).isEqualTo('hello');
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
