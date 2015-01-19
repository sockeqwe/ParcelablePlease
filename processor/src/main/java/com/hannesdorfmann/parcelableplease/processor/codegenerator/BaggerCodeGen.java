package com.hannesdorfmann.parcelableplease.processor.codegenerator;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import java.io.IOException;
import repacked.com.squareup.javawriter.JavaWriter;

import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_FLAGS;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_PARCEL;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_SOURCE;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_TARGET;

/**
 * A code generator for Baggers.
 *
 * @author Hannes Dorfmann
 */
public class BaggerCodeGen implements FieldCodeGen {

  public BaggerCodeGen() {

  }

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    jw.emitStatement("new %s().write(%s.%s, %s, %s)", field.getFullQualifiedBaggerName(),
        PARAM_SOURCE, field.getFieldName(), PARAM_PARCEL, PARAM_FLAGS);
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    jw.emitStatement("%s.%s = new %s().read(%s)", PARAM_TARGET, field.getFieldName(),
        field.getFullQualifiedBaggerName(), PARAM_PARCEL);
  }
}
