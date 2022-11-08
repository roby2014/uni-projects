import '../src/4.js'

import assert from 'node:assert/strict'
import validateProperties from '../src/2.js'

describe('"Array.prototype.associateWith" tests', () => {
  it('test with string length transformation', () => {
    const numbers = ['one', 'two', 'three', 'four']
    const result = numbers.associateWith(str => str.length)
    assert.deepEqual(result, { one: 3, two: 3, three: 5, four: 4 })
  })

  it('test with string transformation', () => {
    const numbers = ['one', 'two', 'three', 'four']
    const result = numbers.associateWith(str => str)
    assert.deepEqual(result, {
      one: 'one',
      two: 'two',
      three: 'three',
      four: 'four'
    })
  })
})
