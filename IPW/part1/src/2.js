import validateProperty from './1.js'

/// Returns an array with the name of the properties whose validation failed.
export default function validateProperties (obj, propValidators) {
  let arr = []

  const v1 = propValidators[0]
  if (!validateProperty(obj, v1)) arr.push(v1.name)

  const v2 = propValidators[1]
  if (!validateProperty(obj, v2)) arr.push(v2.name)

  return arr
}
