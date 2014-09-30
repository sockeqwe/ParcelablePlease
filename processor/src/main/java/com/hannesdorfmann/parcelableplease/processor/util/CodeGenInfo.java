package com.hannesdorfmann.parcelableplease.processor.util;

import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import javax.lang.model.type.TypeMirror;

/**
 * A simple class (comparable to a pair) that contains the  (see {@link FieldCodeGen} )
 * and a TypeMirror for Generics Type for the same inspected element. Note that Generic Type key
 * can
 * be null, or in other words is only available if the inspected element uses generics like
 * ArrayList<Foo> does. In this example the genericsType mirror will be of type Foo.
 *
 * @author Hannes Dorfmann
 */
public class CodeGenInfo {

  private TypeMirror genericsType;

  private FieldCodeGen generator;

  public CodeGenInfo(FieldCodeGen generator) {
    this.generator = generator;
  }

  public CodeGenInfo(FieldCodeGen generator, TypeMirror genericsType) {
    this.generator = generator;
    this.genericsType = genericsType;
  }

  public FieldCodeGen getCodeGenerator() {
    return generator;
  }

  public TypeMirror getGenericsType() {
    return genericsType;
  }
}
