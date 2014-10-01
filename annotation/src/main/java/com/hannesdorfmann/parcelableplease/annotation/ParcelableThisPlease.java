package com.hannesdorfmann.parcelableplease.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate fields with that if you don't want to use all
 * @author Hannes Dorfmann
 */
@Target(ElementType.FIELD) @Retention(RetentionPolicy.CLASS) @Documented
public @interface ParcelableThisPlease {
}
