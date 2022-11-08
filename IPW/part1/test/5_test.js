import assert from 'node:assert/strict'
import checkUsersValid from '../src/5.js'

describe('"checkUsersValid" tests', () => {
  const testAllValid = checkUsersValid([{ id: 1 }, { id: 2 }, { id: 3 }])

  it('test #1', () => {
    assert.equal(testAllValid([{ id: 2 }, { id: 1 }]), true)
  })

  it('test #2', () => {
    assert.equal(testAllValid([{ id: 2 }, { id: 4 }, { id: 1 }]), false)
  })

  it('test #3', () => {
    assert.equal(testAllValid([{ id: 2 }]), true)
  })

  it('test #4', () => {
    assert.equal(testAllValid([{ id: 6 }]), false)
  })
})
