/// Returns true if it confirms the existence and validity of a given property in obj
export default function validateProperty (obj, propValidator) {
  // will count how many validators succeed, if its equal to the validator's count, it means success
  let successCount = 0

  // loop obj's properties
  for (const prop in obj) {
    if (prop != propValidator.name) {
      continue
    }
    propValidator.validators.forEach(fn => {
      if (fn(obj[prop])) {
        successCount++
      }
    })
  }

  return successCount == propValidator.validators.length
}
