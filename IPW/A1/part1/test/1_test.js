import assert from 'node:assert/strict'
import validateProperty from '../src/1.js'

describe('Operations Test', () => {
  const validator = {
    name: 'p1',
    validators: [s => typeof s == 'string' && s.length > 2, s => s[0] == 'a']
  }

  it('testing "1.js" function', () => {
    const obj1 = { p1: 'abc' }
    const result = validateProperty(obj1, validator)
    assert.equal(result, true)
  })

  it('testing "1.js" function', () => {
    const obj2 = { p2: 123 }
    const result = validateProperty(obj2, validator)
    assert.equal(result, false)
  })

  it('testing "1.js" function', () => {
    const obj3 = { p1: 'a', p2: 123 }
    const result = validateProperty(obj3, validator)
    assert.equal(result, false)
  })

  it('testing "1.js" function', () => {
    const obj4 = { p3: 'abc' }
    const result = validateProperty(obj4, validator)
    assert.equal(result, false)
  })
})
