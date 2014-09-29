package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.processor.codegenerator.AbsCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.other.DateCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.other.ParcelableCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitives.BooleanCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.AbsPrimitiveWrapperCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.BooleanWrapperCodeGen;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Hannes Dorfmann
 */
public class SupportedTypes {

  /**
   * Used for something that extends / implements parcelable
   */
  private static final String TYPE_KEY_PARCELABLE = "android.os.Parcelable";

  private static Map<String, FieldCodeGen> typeMap;

  static {
    typeMap = new HashMap<String, FieldCodeGen>();

    // primitives
    typeMap.put(byte.class.getCanonicalName(), new AbsCodeGen("Byte"));
    typeMap.put(boolean.class.getCanonicalName(), new BooleanCodeGen());
    typeMap.put(double.class.getCanonicalName(), new AbsCodeGen("Double"));
    typeMap.put(float.class.getCanonicalName(), new AbsCodeGen("Float"));
    typeMap.put(int.class.getCanonicalName(), new AbsCodeGen("Int"));
    typeMap.put(long.class.getCanonicalName(), new AbsCodeGen("Long"));
    typeMap.put(String.class.getCanonicalName(), new AbsCodeGen("String"));

    // Wrapper classes
    typeMap.put(Byte.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Byte"));
    typeMap.put(Boolean.class.getCanonicalName(), new BooleanWrapperCodeGen());
    typeMap.put(Double.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Double"));
    typeMap.put(Float.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Float"));
    typeMap.put(Integer.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Int"));
    typeMap.put(Long.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Long"));

    // TODO arrays, parcelable , Lists, Map, etc.
    typeMap.put(TYPE_KEY_PARCELABLE, new ParcelableCodeGen());

    // Other common classes
    typeMap.put(Date.class.getCanonicalName(), new DateCodeGen());
  }

  /**
   * Get the field Code Generator for a certain field
   *
   * @return null if no Generator is available for the given field. That mens that that type of the
   * field is not suppored by this library.
   */
  public static FieldCodeGen getGenerator(ParcelableField field) {
    return typeMap.get(field.getTypeKey());
  }

  /**
   * The typekey is used as key for {@link #typeMap} to get the corresponding FieldCodeGen instance
   */
  public static String getTypeKey(VariableElement element, Elements elements, Types types) {

    // Check if its a simple parcelable
    if (types.isAssignable(element.asType(),
        elements.getTypeElement("android.os.Parcelable").asType())) {
      return TYPE_KEY_PARCELABLE;
    }

    return element.asType().toString();
  }
}
