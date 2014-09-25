package com.hannesdorfmann.parcelableplease.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate classes that have
 *
 * @author Hannes Dorfmann
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.CLASS) @Documented
public @interface ParcelablePlease {

  /**
   * Should all fields be parcelable.
   * Default is true.
   */
  boolean allFields() default true;

  /**
   * Should private fields be ignored?
   * Otherwise an error while compiling will be thrown if the class annotated with this Annotation
   * contains private fields.
   */
  boolean ignorePrivateFields() default false;
}
