// priority: 0

console.info('Hello, World! (You will see this line every time server resources reload)')

events.listen('recipes', event => {
  // Change recipes here
})

events.listen('item.tags', event => {
  // Change item tags here
})