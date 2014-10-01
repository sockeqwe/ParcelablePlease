package com.hannesdorfmann.parcelableplease.processor.codegenerator;

import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import repacked.com.squareup.javawriter.JavaWriter;

import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_FLAGS;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_PARCEL;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_SOURCE;
import static com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator.PARAM_TARGET;

/**
 * A code generator for Baggers. It's a singleton instance.
 *
 * @author Hannes Dorfmann
 */
public class BaggerCodeGen implements FieldCodeGen {

  private static BaggerCodeGen INSTANCE = new BaggerCodeGen();

  public static final String BAGGERS_PACKAGE = "com.hannesdorfmann.parcelableplease";

  public static final String BAGGERS_CLASS_NAME = "Baggers";

  public static final String BAGGERS_QUALIFIED_NAME = BAGGERS_PACKAGE + "." + BAGGERS_CLASS_NAME;

  /**
   * list of all used Baggers classes
   */
  public static final Set<String> usedBaggers = new HashSet<String>();

  /**
   * Singleton
   */
  private BaggerCodeGen() {

  }

  @Override public void generateWriteToParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    String baggersFieldName = classToFieldName(field.getFullQualifiedBaggerName());

    jw.emitStatement("%s.%s.write(%s.%s, %s, %s)", BAGGERS_QUALIFIED_NAME, baggersFieldName,
        PARAM_SOURCE, field.getFieldName(), PARAM_PARCEL, PARAM_FLAGS);

    // No need to to that in read method again
    usedBaggers.add(field.getFullQualifiedBaggerName());
  }

  @Override public void generateReadFromParcel(ParcelableField field, JavaWriter jw)
      throws IOException {

    String baggersFieldName = classToFieldName(field.getFullQualifiedBaggerName());

    jw.emitStatement("%s.%s = %s.%s.read(%s)", PARAM_TARGET, field.getFieldName(),
        BAGGERS_QUALIFIED_NAME, baggersFieldName, PARAM_PARCEL);
  }

  public static BaggerCodeGen getInstance() {
    return INSTANCE;
  }

  /**
   * Make a java field name out of a class name
   */
  public static String classToFieldName(String fullQualifiedName) {
    return fullQualifiedName.replace('.', '_');
  }
}
