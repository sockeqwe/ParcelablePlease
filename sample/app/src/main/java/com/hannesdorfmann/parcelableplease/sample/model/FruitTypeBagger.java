package com.hannesdorfmann.parcelableplease.sample.model;

import android.os.Parcel;
import com.hannesdorfmann.parcelableplease.ParcelBagger;

/**
 * @author Hannes Dorfmann
 */
public class FruitTypeBagger implements ParcelBagger<FruitType> {

  @Override public void write(FruitType value, Parcel out, int flags) {
    out.writeString(value.toString());
  }

  @Override public FruitType read(Parcel in) {
    return FruitType.valueOf(in.readString());
  }
}
