package com.hannesdorfmann.parcelableplease;

import android.os.Parcel;

/**
 * A way to put a not Parcelable object into a Parcel and vice versa.
 *
 * @author Hannes Dorfmann
 */
public interface ParcelBagger<T> {

  /**
   * Write the value into the parcelable
   */
  public void write(T value, Parcel out, int flags);

  /**
   * Read the value from parcelable
   */
  public T read(Parcel in);
}
