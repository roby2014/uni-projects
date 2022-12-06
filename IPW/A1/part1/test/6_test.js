import assert from 'node:assert/strict'
import Spy from '../src/6.js'

describe('"Array.prototype.associateWith" tests', () => {
  it('test spy console.error', () => {
    const spy = Spy(console, 'error')
    console.error('calling console.error')
    console.error('calling console.error')
    console.error('calling console.error')
    assert.equal(spy.count, 3)
  })

  it('test spy console.log', () => {
    const spy = Spy(console, 'log')
    console.log('calling console.log')
    assert.equal(spy.count, 1)
  })

  it('test spy console.log 0 calls', () => {
    const spy = Spy(console, 'log')
    assert.equal(spy.count, 0)
  })
})
