package com.hannesdorfmann.parcelableplease.processor.util;

import com.hannesdorfmann.parcelableplease.processor.SupportedTypes;
import javax.lang.model.type.TypeMirror;

/**
 * A simple class (compareable to a pair) that contains the typeKey (see {@link SupportedTypes} )
 * and a TypeMirror for Generics Type for the same inspected element. Note that Generic Type key
 * can
 * be null, or in other words is only available if the inspected element uses generics like
 * ArrayList<Foo> does. In this example the genericsType mirror will be of type Foo.
 *
 * @author Hannes Dorfmann
 */
public class TypeKeyResult {

  private String typeKey;
  private TypeMirror genericsType;

  public TypeKeyResult(String typeKey) {
    this.typeKey = typeKey;
  }

  public TypeKeyResult(String typeKey, TypeMirror genericsType) {
    this.typeKey = typeKey;
    this.genericsType = genericsType;
  }

  public String getTypeKey() {
    return typeKey;
  }

  public TypeMirror getGenericsType() {
    return genericsType;
  }
}
