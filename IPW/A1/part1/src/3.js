import validateProperties from './2.js'

/// Object function that calls [validateProperties] from 2.js
Object.prototype.validateProperties = function (propValidators) {
  return validateProperties(this, propValidators)
}
