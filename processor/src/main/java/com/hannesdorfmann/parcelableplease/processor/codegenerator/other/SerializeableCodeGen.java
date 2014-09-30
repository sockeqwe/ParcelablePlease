package com.hannesdorfmann.parcelableplease.processor.codegenerator.other;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.AbsCodeGen;
import java.io.IOException;
import repacked.com.squareup.javawriter.JavaWriter;

import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_PARCEL;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_TARGET;

/**
 * Code generartor for Serializable
 *
 * @author Hannes Dorfmann
 */
public class SerializeableCodeGen extends AbsCodeGen {

  public SerializeableCodeGen() {
    super("Serializable");
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException {

    javaWriter.emitStatement("%s.%s = (%s) %s.readSerializable()", PARAM_TARGET,
        field.getFieldName(), field.getType(), PARAM_PARCEL);
  }
}
