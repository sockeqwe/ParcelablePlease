package com.hannesdorfmann.parcelableplease.processor.codegenerator.collection;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import java.io.IOException;
import repacked.com.squareup.javawriter.JavaWriter;

import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_PARCEL;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_SOURCE;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_TARGET;

/**
 * It also handles null values
 *
 * @author Hannes Dorfmann
 */
public class PrimitiveArrayCodeGen implements FieldCodeGen {

  private String methodSuffix;
  private String type;

  public PrimitiveArrayCodeGen(String methodSuffix, String type) {
    this.methodSuffix = methodSuffix;
    this.type = type;
  }

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    // -1 means that it array is null. Otherwise its array.length

    jw.emitStatement("%s.writeInt( (%s.%s != null ? %s.%s.length : -1) )",
        CodeGenerator.PARAM_PARCEL, CodeGenerator.PARAM_SOURCE, field.getFieldName(),
        CodeGenerator.PARAM_SOURCE, field.getFieldName());

    jw.beginControlFlow("if (%s.%s != null)", CodeGenerator.PARAM_SOURCE, field.getFieldName());
    jw.emitStatement("%s.write%s(%s.%s)", PARAM_PARCEL, methodSuffix, PARAM_SOURCE,
        field.getFieldName());
    jw.endControlFlow();
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    jw.emitStatement("int %sLengthHelper = -1", field.getFieldName());
    jw.emitStatement("%sLengthHelper = %s.readInt()", field.getFieldName(),
        CodeGenerator.PARAM_PARCEL);

    jw.beginControlFlow("if (%sLengthHelper >= 0)", field.getFieldName());
    jw.emitStatement("%s[] %sArrayHelper = new %s[%sLengthHelper]", type, field.getFieldName(),
        type, field.getFieldName());
    jw.emitStatement("%s.read%s(%sArrayHelper)", PARAM_PARCEL, methodSuffix, field.getFieldName());
    jw.emitStatement("%s.%s = %sArrayHelper", PARAM_TARGET, field.getFieldName(),
        field.getFieldName());
    jw.nextControlFlow("else");
    jw.emitStatement("%s.%s = null", PARAM_TARGET, field.getFieldName());
    jw.endControlFlow();
  }
}
