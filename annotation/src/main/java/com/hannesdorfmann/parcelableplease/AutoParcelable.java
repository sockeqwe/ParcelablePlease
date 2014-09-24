package com.hannesdorfmann.parcelableplease;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hannes Dorfmann
 */
public class AutoParcelable {

  private Map<String, ParcelableInjector<? extends Parcelable>> injectorMap =
      new HashMap<String, ParcelableInjector<? extends Parcelable>>();

  private static AutoParcelable INSTANCE = null;

  private AutoParcelable() {

  }

  private static void init() {
    if (INSTANCE == null) {
      // Read generated list
    }
  }

  private static ParcelableInjector<? extends Parcelable> getInjector(Parcelable source) {
    init();
    ParcelableInjector<? extends Parcelable> injector =
        INSTANCE.injectorMap.get(source.getClass().getName());
    if (injector == null) {
      throw new NullPointerException("Could not find a ParcelInjector for "
          + source.getClass().getName()
          + ". Have you annotated this class with @"
          + ParcelablePlease.class.getSimpleName()
          + " ?");
    }

    return injector;
  }

  public static void writeToParcel(Parcelable source, Parcel out, int flags) {

    //ParcelableInjector<? extends Parcelable> injector = getInjector(source);
    //injector.writeToParcel(source, out, flags);
  }

  public static void readFromParcel(Parcelable target, Parcel in) {

  }
}
