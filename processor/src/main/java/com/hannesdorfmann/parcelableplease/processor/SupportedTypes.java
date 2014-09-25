package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitives.AbsPrimitiveCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitives.BooleanCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.AbsPrimitiveWrapperCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.BooleanWrapperCodeGen;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hannes Dorfmann
 */
public class SupportedTypes {

  private static Map<String, FieldCodeGen> typeMap;

  static {
    typeMap = new HashMap<String, FieldCodeGen>();

    // primitives
    typeMap.put(byte.class.getCanonicalName(), new AbsPrimitiveCodeGen("Byte"));
    typeMap.put(boolean.class.getCanonicalName(), new BooleanCodeGen());
    typeMap.put(double.class.getCanonicalName(), new AbsPrimitiveCodeGen("Double"));
    typeMap.put(float.class.getCanonicalName(), new AbsPrimitiveCodeGen("Float"));
    typeMap.put(int.class.getCanonicalName(), new AbsPrimitiveCodeGen("Int"));
    typeMap.put(long.class.getCanonicalName(), new AbsPrimitiveCodeGen("Long"));
    typeMap.put(String.class.getCanonicalName(), new AbsPrimitiveCodeGen("String"));

    // Wrapper classes
    typeMap.put(Byte.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Byte"));
    typeMap.put(Boolean.class.getCanonicalName(), new BooleanWrapperCodeGen());
    typeMap.put(Double.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Double"));
    typeMap.put(Float.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Float"));
    typeMap.put(Integer.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Int"));
    typeMap.put(Long.class.getCanonicalName(), new AbsPrimitiveWrapperCodeGen("Long"));

    // TODO arrays, parcelable , Lists, Map, etc.

  }



  public static FieldCodeGen getGenerator(ParcelableField field){
   return typeMap.get(field.getType());
  }
}
