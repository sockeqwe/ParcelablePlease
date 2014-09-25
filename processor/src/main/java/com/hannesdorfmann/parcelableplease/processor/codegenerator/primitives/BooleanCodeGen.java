package com.hannesdorfmann.parcelableplease.processor.codegenerator.primitives;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import java.io.IOException;
import repacked.com.squareup.javawriter.JavaWriter;

/**
 * For boolean primitives
 *
 * @author Hannes Dorfmann
 */
public class BooleanCodeGen implements FieldCodeGen {

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException {

    javaWriter.emitStatement("%s.writeByte( (byte) (%s.%s? 1 : 0))", CodeGenerator.PARAM_PARCEL,
        CodeGenerator.PARAM_SOURCE, field.getFieldName());
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException {

    javaWriter.emitStatement("%s.%s = ( %s.readByte() == 1 )", CodeGenerator.PARAM_TARGET,
        field.getFieldName(), CodeGenerator.PARAM_PARCEL);
  }
}
