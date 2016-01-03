package com.hannesdorfmann.parcelableplease.sample.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class Orange extends Fruit implements Parcelable {

  public Orange() {
    super("Orange", FruitType.C);
  }

  public List<String> content;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    OrangeParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Orange> CREATOR = new Creator<Orange>() {
    public Orange createFromParcel(Parcel source) {
      Orange target = new Orange();
      OrangeParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Orange[] newArray(int size) {
      return new Orange[size];
    }
  };

  @Override public String toString() {
    return super.toString() + " "+content;
  }
}
