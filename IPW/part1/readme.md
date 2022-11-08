# IPW A1 PART 1 (JavaScript functions)

## Part 1 - JavaScript functions

Implement the following JavaScript functions and create tests that confirm the described behaviour:

1. Implement the function `validateProperty(obj, propValidator)`, that returns `true` if it confirms the existence and validity of a given property in `obj`, or `false` otherwise. The validity specification is provided in `propValidator` argument.

   **Example**

   ```javascript
   const validator = {name : "p1" , validators: [s => typeof s == 'string' && s.length > 2, s => s[0]=="a"]  }
   const obj1 = { p1 : "abc" }
   const obj2 = { p2 : 123 }
   const obj3 = { p1 : "a" , p2 : 123 }

   validateProperty(obj1, validator) //true
   validateProperty(obj2, validator) //false
   validateProperty(obj3, validator) //false
   ```

   **Arguments**

   - `obj`: an object to validate a property
   - `propValidator`: an object that validates a property on `obj`, having the following properties:
     - `name` - the name of the property to validate
     - `validators` - an array of predicate functions that must be called with the property value. If any returns `false` the property is invalid.

   **Conditions**

   - Do not create any unnecessary functions e.g. helpers.

---

2. Implement the function `validateProperties(obj, propValidators)` that returns an array with the name of the properties whose validation failed. If an empty array is returned, the validation was successful.

   **Example**

   ```javascript
   const validators = [
      {
      name : "p1" , validators: [s => typeof s == 'string' && s.length > 2, s => s[0]=="a"] 
      },
      {
      name : "p2" , validators: [s => Number.isInteger(s)] 
      }
   ]
   const obj1 = { p1 : "a" }
   const obj2 = { p1 : 123  }
   const obj3 = { p1 : "abc" , p2 : 123 }

   validateProperties(obj1, validators) // ["p1", "p2"]
   validateProperties(obj2, validators) // ["p1", "p2"]
   validateProperties(obj3, validators) // []
   ```

   **Arguments**

   - `obj`: an object to validate the properties
  
   - `propValidators`: an array of property validators, as defined in the previous exercise to validate the properties in `obj`

   **Conditions**

      - Do not use any for/while loops or `Array#forEach`.
      - Do not create any unnecessary functions e.g. helpers.
  
   **Hints**

   - The function must call `validateProperty` for each validator

---

3. Add a `validateProperties` method to `Object` type. This method implementation should call the `validateProperties` function implemented in the previous exercise.  

**Example**

```javascript
   const validators = [
      {
      name : "p1" , validators: [s => typeof s == 'string' && s.length > 2, s => s[0]=="a"] 
      },
      {
      name : "p2" , validators: [s => Number.isInteger(s)] 
      }
   ]
   const obj1 = { p1 : "a" }
   const obj2 = { p1 : 123  }
   const obj3 = { p1 : "abc" , p2 : 123 }

   obj1.validateProperties(validators) // ["p1", "p2"]
   obj2.validateProperties(validators) // ["p1", "p2"]
   obj3.validateProperties(validators) // []
   ```

---

4. Add the `associateWith(transformation)` method to the Array type.
This function returns an `Object` in which the elements values of the original Array are properties names, and the corresponding values values are produced from them by the given `transformation` function. If two elements are equal, only the last one remains in the object.
**Example:**

   ```javascript
   let numbers = ["one", "two", "three", "four"]
   console.log(numbers.associateWith( str => str.length ))

   // { one: 3, two: 3, three: 5, four: 4}
   ```

---

1. Implement `checkUsersValid(goodUsers)`function that return a function that takes a list of valid users in `goodUsers`, and  returns `true` if all of the supplied users exist in the original list of users, or `false` otherwise.

   You only need to check that the user ids match.
   **Example:**

   ```javascript
   var goodUsers = [
      { id: 1 },
      { id: 2 },
      { id: 3 }
   ]

   // `checkUsersValid` is the function you'll define
   var testAllValid = checkUsersValid(goodUsers)

   testAllValid([
      { id: 2 },
      { id: 1 }
   ])
   // => true

   testAllValid([
      { id: 2 },
      { id: 4 },
      { id: 1 }
   ])
   // => false
   
   ```
  
   **Arguments**

   - `goodUsers`: a list of valid users
  
---

6. Override a specified method of an object with new functionality while still maintaining all of the old behaviour.

   Create a `Spy` function that returns an object that keeps track of how many times a function is called.

   **Example**

   ```javascript
   var spy = Spy(console, 'error')

   console.error('calling console.error')
   console.error('calling console.error')
   console.error('calling console.error')

   console.log(spy.count) // 3
   ```

   **Arguments**

   - `target`: an object containing the method `method`
   - `method`: a string with the name of the method on `target` to spy on.

   **Conditions**

   - Do not use any for/while loops or `Array#forEach`.
   - Do not create any unnecessary functions e.g. helpers.

   **Hint**

     - Functions have context, input and output. Make sure you consider the context, input to *and output from* the function you are spying on.


