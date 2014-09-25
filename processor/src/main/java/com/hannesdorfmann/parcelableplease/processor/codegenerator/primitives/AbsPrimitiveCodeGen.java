package com.hannesdorfmann.parcelableplease.processor.codegenerator.primitives;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import java.io.IOException;
import repacked.com.squareup.javawriter.JavaWriter;

import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_PARCEL;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_SOURCE;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_TARGET;

/**
 * @author Hannes Dorfmann
 */
public class AbsPrimitiveCodeGen implements FieldCodeGen {

  private String methodSuffix;

  public AbsPrimitiveCodeGen(String methodSuffix) {
    this.methodSuffix = methodSuffix;
  }

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException {

    javaWriter.emitStatement("%s.write%s(%s.%s)", PARAM_PARCEL, methodSuffix, PARAM_SOURCE,
        field.getFieldName());
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException {

    javaWriter.emitStatement("%s.%s = %s.read%s()", PARAM_TARGET, field.getFieldName(), PARAM_PARCEL, methodSuffix);
  }
}
