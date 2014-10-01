package com.hannesdorfmann.parcelableplease.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate fields not to be parcelable (transient)
 *
 * @author Hannes Dorfmann
 */
@Target(ElementType.FIELD) @Retention(RetentionPolicy.CLASS) @Documented
public @interface ParcelableNoThanks {
}
