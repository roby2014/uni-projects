/// This function returns an Object in which the elements values of the original Array are properties names,
/// and the corresponding values values are produced from them by the given transformation function.
Array.prototype.associateWith = function (transformation) {
  let obj = {}
  this.forEach(it => (obj[it] = transformation(it)))
  return obj
}

let numbers = ['one', 'two', 'three', 'four']
console.log(numbers.associateWith(str => str.length))

// { one: 3, two: 3, three: 5, four: 4}
