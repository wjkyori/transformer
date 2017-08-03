package org.transformer.validator.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<Mobile, String> {

  private String regexp;

  @Override
  public void initialize(Mobile constraintAnnotation) {
    this.regexp = constraintAnnotation.regexp();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    if (value.matches(regexp)) {
      return true;
    }
    return false;
  }

}
