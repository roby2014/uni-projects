import assert from 'node:assert/strict'
import validateProperties from '../src/2.js'

describe('validateProperties Tests', () => {
  const validators = [
    {
      name: 'p1',
      validators: [s => typeof s == 'string' && s.length > 2, s => s[0] == 'a']
    },
    {
      name: 'p2',
      validators: [s => Number.isInteger(s)]
    }
  ]

  it('test #1', () => {
    const obj1 = { p1: 'a' }
    const result = validateProperties(obj1, validators)
    assert.deepEqual(result, ['p1', 'p2'])
  })

  it('test #2', () => {
    const obj2 = { p1: 123 }
    const result = validateProperties(obj2, validators)
    assert.deepEqual(result, ['p1', 'p2'])
  })

  it('test #3', () => {
    const obj3 = { p1: 'abc', p2: 123 }
    const result = validateProperties(obj3, validators)
    assert.deepEqual(result, [])
  })
})
