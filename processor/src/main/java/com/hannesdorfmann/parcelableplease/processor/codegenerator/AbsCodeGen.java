package com.hannesdorfmann.parcelableplease.processor.codegenerator;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import java.io.IOException;
import repacked.com.squareup.javawriter.JavaWriter;

import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_PARCEL;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_SOURCE;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_TARGET;

/**
 * @author Hannes Dorfmann
 */
public class AbsCodeGen implements FieldCodeGen {

  protected String methodSuffix;

  public AbsCodeGen(String methodSuffix) {
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
