package com.hannesdorfmann.parcelableplease.sample.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class Banana extends Fruit implements Parcelable {

  public Banana() {
    super("Banana", FruitType.B);
  }


  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    BananaParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Banana> CREATOR = new Creator<Banana>() {
    public Banana createFromParcel(Parcel source) {
      Banana target = new Banana();
      BananaParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Banana[] newArray(int size) {
      return new Banana[size];
    }
  };
}
