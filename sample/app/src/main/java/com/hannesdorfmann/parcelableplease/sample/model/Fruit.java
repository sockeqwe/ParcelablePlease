package com.hannesdorfmann.parcelableplease.sample.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class Fruit implements Parcelable {

  String name;

  @Bagger(FruitTypeBagger.class) FruitType type;

  public Fruit(String name, FruitType type) {
    this.name = name;
    this.type = type;
  }

  protected Fruit() {

  }

  public String getName() {
    return name;
  }

  public FruitType getType() {
    return type;
  }

  @Override public String toString() {
    return name + "(" + type + ")";
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    FruitParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Fruit> CREATOR = new Creator<Fruit>() {
    public Fruit createFromParcel(Parcel source) {
      Fruit target = new Fruit();
      FruitParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Fruit[] newArray(int size) {
      return new Fruit[size];
    }
  };
}
