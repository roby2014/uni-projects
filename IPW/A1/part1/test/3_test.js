import assert from 'node:assert/strict'
import validateProperties from '../src/2.js'
import '../src/3.js'

describe('Operations Test', () => {
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

  it('testing "3.js" function', () => {
    const obj1 = { p1: 'a' }
    const result = obj1.validateProperties(validators)
    assert.deepEqual(result, ['p1', 'p2'])
  })

  it('testing "3.js" function', () => {
    const obj2 = { p1: 123 }
    const result = obj2.validateProperties(validators)
    assert.deepEqual(result, ['p1', 'p2'])
  })

  it('testing "3.js" function', () => {
    const obj3 = { p1: 'abc', p2: 123 }
    const result = obj3.validateProperties(validators)
    assert.deepEqual(result, [])
  })
})
