// Category 4 - syntax-driven tests. Assert the JS syntaxes KubeJS scripts rely on still parse and run
// under the current Rhino. SyntaxTests verifies the captured results. Syntaxes the current Rhino does
// not support are kept below, disabled, so they can be re-enabled the moment support lands (uncomment
// the block and add its id to SyntaxTests.IDS).

TestRuntime.check('syntax.arrow', () => {
	let add = (a, b) => a + b;
	TestRuntime.assertThat(add(3, 4) === 7).isTrue();
});

TestRuntime.check('syntax.destructuring', () => {
	let [a, b] = [1, 2];
	let { x } = { x: 3 };
	TestRuntime.assertThat(a === 1 && b === 2 && x === 3).isTrue();
});

TestRuntime.check('syntax.template_literals', () => {
	let name = 'world';
	TestRuntime.assertThat(`hello ${name}`).isEqualTo('hello world');
});

TestRuntime.check('syntax.for_of', () => {
	let total = 0;
	for (let n of [1, 2, 3, 4]) {
		total += n;
	}
	TestRuntime.assertThat(total === 10).isTrue();
});

TestRuntime.check('syntax.array_methods', () => {
	let result = [1, 2, 3, 4].filter(n => n % 2 === 0).map(n => n * 10);
	TestRuntime.assertThat(result.join(',')).isEqualTo('20,40');
});

TestRuntime.check('syntax.map_and_set', () => {
	let map = new Map();
	map.set('a', 1);
	let set = new Set([1, 1, 2]);
	TestRuntime.assertThat(map.get('a') === 1 && set.size === 2).isTrue();
});

// Disabled - the current Rhino rejects these at parse time ("missing formal parameter"). Re-enable
// (and add the id to SyntaxTests.IDS) if default/rest parameter support is ever added.

// TestRuntime.check('syntax.default_params', () => {
// 	let add = (a, b = 3) => a + b;
// 	TestRuntime.assertThat(add(4) === 7).isTrue();
// });

// TestRuntime.check('syntax.spread_rest', () => {
// 	let sum = (...xs) => xs.reduce((total, n) => total + n, 0);
// 	TestRuntime.assertThat(sum(...[1, 2, 3], 4) === 10).isTrue();
// });
