package com.hannesdorfmann.parcelableplease.annotation;

import com.hannesdorfmann.parcelableplease.ParcelBagger;

/**
 * Annotate Fields you want to put in a Parcel which is not Parcelable.
 *
 * @author Hannes Dorfmann
 */
public @interface Bagger {
  Class<? extends ParcelBagger<?>> value();
}
