package com.hannesdorfmann.parcelableplease.processor.codegenerator.other;

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
public class DateCodeGen implements FieldCodeGen {

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    jw.emitStatement("%s.writeByte( (byte) (%s.%s != null ? 1 : 0) )", CodeGenerator.PARAM_PARCEL,
        CodeGenerator.PARAM_SOURCE, field.getFieldName());

    jw.beginControlFlow("if (%s.%s != null)", CodeGenerator.PARAM_SOURCE, field.getFieldName());
    jw.emitStatement("%s.writeLong(%s.%s.getTime())", PARAM_PARCEL, PARAM_SOURCE,
        field.getFieldName());
    jw.endControlFlow();
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    jw.emitStatement("boolean %sNullHelper", field.getFieldName());
    jw.emitStatement("%sNullHelper = ( %s.readByte() == 1 )", field.getFieldName(),
        CodeGenerator.PARAM_PARCEL);

    jw.beginControlFlow("if (%sNullHelper)", field.getFieldName());
    jw.emitStatement("long %sLongHelper = %s.readLong()", field.getFieldName(), PARAM_PARCEL);
    jw.emitStatement("%s.%s = new java.util.Date(%sLongHelper)", PARAM_TARGET, field.getFieldName(),
        field.getFieldName());
    jw.nextControlFlow("else");
    jw.emitStatement("%s.%s = null", PARAM_TARGET, field.getFieldName());
    jw.endControlFlow();
  }
}
