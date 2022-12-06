// Returns an object that keeps track of how many times a function is called.
export default function Spy (target, method) {
  // create an object to store the data
  let spy = { count: 0 }

  // store original function to call later (maintain old behaviour)
  const original_function = target[method]

  // overwrite the function
  target[method] = function (...args) {
    spy.count++
    return original_function.apply(this, args)
  }

  return spy
}
