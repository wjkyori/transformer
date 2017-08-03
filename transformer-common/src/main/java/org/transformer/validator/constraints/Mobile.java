package org.transformer.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { MobileValidator.class })
public @interface Mobile {

  /** 错误信息. */
  String message() default "无效的手机号码.";

  /**
   * @return 正则表达式.
   */
  String regexp() default "^1[34578]\\d{9}$";

  /** 分组.*/
  Class<?>[] groups() default {};

  /** 参数.*/
  Class<? extends Payload>[] payload() default {};

  /**
   * Defines several {@link Size} annotations on the same element.
   *
   * @see Size
   */
  @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
      ElementType.CONSTRUCTOR, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface List {

    Mobile[] value();
  }

}