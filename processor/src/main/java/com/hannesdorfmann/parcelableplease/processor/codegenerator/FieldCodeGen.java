package com.hannesdorfmann.parcelableplease.processor.codegenerator;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import java.io.IOException;
import repacked.com.squareup.javawriter.JavaWriter;

/**
 * @author Hannes Dorfmann
 */
public interface FieldCodeGen {

  public void generateWriteToParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException;

  public void generateReadFromParcel(ParcelableField field, JavaWriter javaWriter)
      throws IOException;
}
