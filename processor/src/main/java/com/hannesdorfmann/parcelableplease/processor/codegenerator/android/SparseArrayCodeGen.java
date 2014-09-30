package com.hannesdorfmann.parcelableplease.processor.codegenerator.android;

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
public class SparseArrayCodeGen implements FieldCodeGen {

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException {

    javaWriter.emitStatement("%s.writeSparseArray(%s.%s)", PARAM_PARCEL, PARAM_SOURCE,
        field.getFieldName());
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException {

    javaWriter.emitStatement("%s.%s = %s.readSparseArray(%s.class.getClassLoader())", PARAM_TARGET,
        field.getFieldName(), PARAM_PARCEL, field.getGenericsTypeArgument().toString());
  }
}
