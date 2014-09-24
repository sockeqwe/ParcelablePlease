package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.ParcelableBagger;
import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import javax.lang.model.element.VariableElement;

/**
 * @author Hannes Dorfmann
 */
public class ParcelableField {

  private String fieldName;
  private Class<? extends ParcelableBagger> baggerClass;


  public ParcelableField(VariableElement element) {

    element.asType();

    fieldName = element.getSimpleName().toString();

    Bagger baggerAnnotation = element.getAnnotation(Bagger.class);

    if (baggerAnnotation != null){
      // has a bagger annotation
      baggerClass = baggerAnnotation.value();
    }



  }
}
