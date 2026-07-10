// Category 2 - JS-driven wrapper tests. Each block passes a raw JS value across a typed Java
// boundary (TestRuntime.as*), forcing the registered type wrapper to run, then asserts the coerced
// object. These run once on the first server tick - not at script load - so registry-backed
// conversions (item components) are bound; WrapperTests waits for and verifies the captured results.

let wrapperChecksRun = false;

ServerEvents.tick(event => {
	if (wrapperChecksRun) {
		return;
	}

	wrapperChecksRun = true;

	TestRuntime.check('wrapper.vec3', () => {
		let v = TestRuntime.asVec3([1, 2, 3]);
		TestRuntime.assertThat(v.x()).isEqualTo(1.0);
		TestRuntime.assertThat(v.y()).isEqualTo(2.0);
		TestRuntime.assertThat(v.z()).isEqualTo(3.0);
	});

	TestRuntime.check('wrapper.blockpos', () => {
		let p = TestRuntime.asBlockPos([4, 5, 6]);
		TestRuntime.assertThat(p.toShortString()).isEqualTo('4, 5, 6');
	});

	TestRuntime.check('wrapper.itemstack', () => {
		let stack = TestRuntime.asItemStack('minecraft:diamond');
		TestRuntime.assertThat(stack.id).isEqualTo('minecraft:diamond');
	});

	TestRuntime.check('wrapper.component', () => {
		let component = TestRuntime.asComponent('hello');
		TestRuntime.assertThat(component.getString()).isEqualTo('hello');
	});

	TestRuntime.check('wrapper.nbt', () => {
		let tag = TestRuntime.asCompoundTag({ a: 1, b: 'two' });
		TestRuntime.assertThat(tag.contains('a')).isTrue();
		TestRuntime.assertThat(tag.contains('b')).isTrue();
	});

	TestRuntime.check('wrapper.color', () => {
		let color = TestRuntime.asColor('#ff0000');
		TestRuntime.assertThat(color.toHexString()).isEqualTo('#FF0000');
	});

	TestRuntime.check('wrapper.id', () => {
		let id = TestRuntime.asId('kubejs:test');
		TestRuntime.assertThat(id.toString()).isEqualTo('kubejs:test');
	});

	TestRuntime.check('wrapper.uuid', () => {
		let uuid = TestRuntime.asUUID('12345678-1234-1234-1234-123456789abc');
		TestRuntime.assertThat(uuid.toString()).isEqualTo('12345678-1234-1234-1234-123456789abc');
	});

	TestRuntime.check('wrapper.tristate', () => {
		let tristate = TestRuntime.asTristate('true');
		TestRuntime.assertThat(tristate.getSerializedName()).isEqualTo('true');
	});

	TestRuntime.check('wrapper.duration', () => {
		let duration = TestRuntime.asDuration('20t');
		TestRuntime.assertThat(duration.toMillis() === 1000).isTrue();
	});

	TestRuntime.check('wrapper.regexp', () => {
		let pattern = TestRuntime.asPattern('/foo/i');
		TestRuntime.assertThat(pattern.pattern()).isEqualTo('foo');
	});

	// Number-based wrappers: a bare number is a constant provider, a [min, max] list a uniform range.
	TestRuntime.check('wrapper.int_provider', () => {
		let constant = TestRuntime.asIntProvider(3);
		TestRuntime.assertThat(constant.minInclusive() === 3 && constant.maxInclusive() === 3).isTrue();

		let range = TestRuntime.asIntProvider([2, 8]);
		TestRuntime.assertThat(range.minInclusive() === 2 && range.maxInclusive() === 8).isTrue();
	});

	// Map-based wrapper: a color name resolves to that vanilla map color.
	TestRuntime.check('wrapper.map_color', () => {
		let color = TestRuntime.asMapColor('color_red');
		TestRuntime.assertThat(color.col).isEqualTo(10040115);
	});
});
