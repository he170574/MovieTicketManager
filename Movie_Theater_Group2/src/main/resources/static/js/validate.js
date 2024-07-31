function Validator(options) {

  // Validate function
  function validate(inputElement, rule) {
    var errorMessage;
    var errorElement = inputElement.parentElement.querySelector(options.errorSelector)
    var rules = selectorRules[rule.selector];

    for (let i = 0; i < rules.length; i++) {
      errorMessage = rules[i](inputElement.value)
      if (errorMessage) break;
    }

    if (errorMessage) {
      errorElement.innerHTML = "<i class='bi bi-exclamation-triangle-fill me-2'></i>" + errorMessage;
      inputElement.parentElement.classList.add(options.invalidClass);
    } else {
      errorElement.innerText = ''
      inputElement.parentElement.classList.remove(options.invalidClass);
    }
  }

  // constants
  var formElement = document.querySelector(options.form);

  // submit form
  formElement.onsubmit = function(e) {
    e.preventDefault();
    options.rules.forEach(function(rule) {
      var inputElement = document.querySelector(rule.selector);
      validate(inputElement, rule)
    })
  }

  var selectorRules = {}

  // onblur && on input
  if (formElement) {

    options.rules.forEach(function(rule) {
      var inputElement = document.querySelector(rule.selector);
      console.log(inputElement);

      if (Array.isArray(selectorRules[rule.selector])) {
        selectorRules[rule.selector].push(rule.test);
      } else {
        selectorRules[rule.selector] = [rule.test]
      }

      if (inputElement) {
        inputElement.onblur = function() {
          validate(inputElement, rule)
        };

        inputElement.oninput = function() {
          var errorElement = inputElement.parentElement.querySelector(options.errorSelector);
          errorElement.innerText = ''
          inputElement.parentElement.classList.remove(options.invalidClass);
        }
      }

    });
  }
}

Validator.isRequired = function(selector) {
  return {
    selector: selector,
    test: function(value) {
      return value.trim() ? undefined : 'Please enter this field';
    }
  }
}

Validator.isWrong = function(selector) {
  return {
    selector: selector,
    test: function(value) {
      // const apiUrl = "";
      // fetch(apiUrl)
      //   .then(response => {
      //     if (!response.ok) {
      //       throw new Error('Network response was not ok');
      //     }
      //     return response.json();
      //   })
      //   .then(data => {
      //     return data;
      //   })
      //   .catch(error => {
      //     console.error('Error:', error);
      //   });
      return value.trim() ? undefined : 'Please enter this field';
    }
  }
}

Validator.isLocked = function(selector) {
  return {
    selector: selector,
    test: function(value) {
      const apiUrl = "";
      fetch(apiUrl)
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(data => {
          return data;
        })
        .catch(error => {
          console.error('Error:', error);
        });
    }
  }
}

Validator.isPhoneNumber = function(selector) {
  return {
    selector: selector,
    test: function(value) {
      if (value.match("^\\d{10}$")) {
        return undefined;
      } else {
        return 'Invalid phone number'
      }
    }
  }
}

Validator.isEmail = function(selector) {
  return {
    selector: selector,
    test: function(value) {
      if (value.match("^\\w+@gmail.com$")) {
        return undefined;
      } else {
        return 'Invalid email'
      }
    }
  }
}

Validator.isPassword = function(selector) {
  return {
    selector: selector,
    test: function(value) {
      if (value.length >= 6 && value.match('\\d') && value.match('[a-z]') && value.match('[A-Z]')) {
        return undefined
      } else {
        return "Password must include uppercase, lowercase and number."
      }
    }
  }
}