package com.hannesdorfmann.parcelableplease.processor.codegenerator.collection;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import repacked.com.squareup.javawriter.JavaWriter;

import java.io.IOException;

import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.*;

/**
 * String list
 *
 * @author Yaroslav Heriatovych
 */
public class StringListCodeGen implements FieldCodeGen {

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    jw.emitStatement("%s.writeStringList(%s.%s)", PARAM_PARCEL, PARAM_SOURCE,
        field.getFieldName());
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    jw.emitStatement("%s.%s = %s.createStringArrayList()", PARAM_TARGET, field.getFieldName(), PARAM_PARCEL);
  }
}
