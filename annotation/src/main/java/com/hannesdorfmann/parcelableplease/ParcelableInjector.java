package com.hannesdorfmann.parcelableplease;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Hannes Dorfmann
 */
public interface ParcelableInjector<T extends Parcelable> {

  /**
   * Write the value into the parcelable
   */
  public void writeToParcel(T value, Parcel out, int flags);

  /**
   * Read the value from parcelable
   */
  public T readFromParcel(Parcel in);

}
