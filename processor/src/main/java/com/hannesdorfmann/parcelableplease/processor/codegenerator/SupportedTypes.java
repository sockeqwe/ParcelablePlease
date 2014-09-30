package com.hannesdorfmann.parcelableplease.processor.codegenerator;

import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import com.hannesdorfmann.parcelableplease.processor.ProcessorMessage;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.android.BundleCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.android.ParcelableCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.android.SparseBooleanCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.collection.AbsListCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.collection.ParcelableArrayCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.collection.PrimitiveArrayCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.other.DateCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.other.SerializeableCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitives.BooleanCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.AbsPrimitiveWrapperCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.BooleanWrapperCodeGen;
import com.hannesdorfmann.parcelableplease.processor.util.CodeGenInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * The supported types and the corresponding code generator classes
 *
 * @author Hannes Dorfmann
 */
public class SupportedTypes {

  private static final String TYPE_KEY_PARCELABLE = "android.os.Parcelable";
  private static final String TYPE_KEY_SERIALIZABLE = "java.io.Serializable";
  private static final String TYPE_KEY_PARCELABLE_ARRAYLIST = "Parcelable-ArrayList";
  private static final String TYPE_KEY_PARCELABLE_LINKEDLIST = "Parcelable-LinkedList";
  private static final String TYPE_KEY_PARCELABLE_LIST = "Parcelable-List";
  private static final String TYPE_KEY_BUNDLE = "android.os.Bundle";
  private static final String TYPE_KEY_DOUBLE_ARRAY = "Array-Double";
  private static final String TYPE_KEY_FLOAT_ARRAY = "Array-Float";
  private static final String TYPE_KEY_INT_ARRAY = "Array-Int";
  private static final String TYPE_KEY_BOOL_ARRAY = "Array-Boolean";
  private static final String TYPE_KEY_BYTE_ARRAY = "Array-Byte";
  private static final String TYPE_KEY_CHAR_ARRAY = "Array-Char";
  private static final String TYPE_KEY_LONG_ARRAY = "Array-Long";
  private static final String TYPE_KEY_STRING_ARRAY = "Array-String";
  private static final String TYPE_KEY_PARCELABLE_ARRAY = "Array-Parcelable";
  private static final String TYPE_KEY_SPARSE_BOOLEAN_ARRAY = "android.util.SparseBooleanArray";
  private static final String TYPE_KEY_SPARSE_ARRAY = "android.util.SparseArray";

  private static final String TYPE_KEY_PARCELABLE_COPYONWRITEARRAYLIST =
      "Parcelable-CopyOnWriteArrayList";

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

    // Android
    typeMap.put(TYPE_KEY_PARCELABLE, new ParcelableCodeGen());
    typeMap.put(TYPE_KEY_BUNDLE, new BundleCodeGen());
    typeMap.put(TYPE_KEY_SPARSE_BOOLEAN_ARRAY, new SparseBooleanCodeGen());
    // typeMap.put(TYPE_KEY_SPARSE_ARRAY, new SparseArrayCodeGen()); // TODO implement

    // Lists
    typeMap.put(TYPE_KEY_PARCELABLE_LIST, new AbsListCodeGen(ArrayList.class.getName()));
    typeMap.put(TYPE_KEY_PARCELABLE_ARRAYLIST, new AbsListCodeGen(ArrayList.class.getName()));
    typeMap.put(TYPE_KEY_PARCELABLE_LINKEDLIST, new AbsListCodeGen(LinkedList.class.getName()));
    typeMap.put(TYPE_KEY_PARCELABLE_COPYONWRITEARRAYLIST,
        new AbsListCodeGen(CopyOnWriteArrayList.class.getName()));

    // Arrays
    typeMap.put(TYPE_KEY_BOOL_ARRAY, new PrimitiveArrayCodeGen("BooleanArray", "boolean"));
    typeMap.put(TYPE_KEY_BYTE_ARRAY, new PrimitiveArrayCodeGen("ByteArray", "byte"));
    typeMap.put(TYPE_KEY_CHAR_ARRAY, new PrimitiveArrayCodeGen("CharArray", "char"));
    typeMap.put(TYPE_KEY_DOUBLE_ARRAY, new PrimitiveArrayCodeGen("DoubleArray", "double"));
    typeMap.put(TYPE_KEY_FLOAT_ARRAY, new PrimitiveArrayCodeGen("FloatArray", "float"));
    typeMap.put(TYPE_KEY_LONG_ARRAY, new PrimitiveArrayCodeGen("LongArray", "long"));
    typeMap.put(TYPE_KEY_INT_ARRAY, new PrimitiveArrayCodeGen("IntArray", "int"));
    typeMap.put(TYPE_KEY_STRING_ARRAY, new PrimitiveArrayCodeGen("StringArray", "String"));
    typeMap.put(TYPE_KEY_PARCELABLE_ARRAY, new ParcelableArrayCodeGen());

    // Other common classes
    typeMap.put(TYPE_KEY_SERIALIZABLE, new SerializeableCodeGen());
    typeMap.put(Date.class.getCanonicalName(), new DateCodeGen());
  }

  /**
   * Collect information about the element and the corresponding code generator
   */
  public static CodeGenInfo getCodeGenInfo(VariableElement element, Elements elements,
      Types types) {

    // Special classes, primitive, wrappers, etc.
    String typeKey = element.asType().toString();
    if (typeMap.get(typeKey) != null) {
      return new CodeGenInfo(typeMap.get(typeKey));
    }

    // Check if its a simple parcelable
    if (isOfType(element, "android.os.Parcelable", elements, types)) {
      return new CodeGenInfo(typeMap.get(TYPE_KEY_PARCELABLE));
    }

    // Lists
    if (isOfWildCardType(element, ArrayList.class.getName(), "android.os.Parcelable", elements,
        types)) {

      return new CodeGenInfo(typeMap.get(TYPE_KEY_PARCELABLE_ARRAYLIST),
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    if (isOfWildCardType(element, LinkedList.class.getName(), "android.os.Parcelable", elements,
        types)) {
      return new CodeGenInfo(typeMap.get(TYPE_KEY_PARCELABLE_LINKEDLIST),
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    if (isOfWildCardType(element, CopyOnWriteArrayList.class.getName(), "android.os.Parcelable",
        elements, types)) {
      return new CodeGenInfo(typeMap.get(TYPE_KEY_PARCELABLE_COPYONWRITEARRAYLIST),
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    if (isOfWildCardType(element, List.class.getName(), "android.os.Parcelable", elements, types)) {
      return new CodeGenInfo(typeMap.get(TYPE_KEY_PARCELABLE_LIST),
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    // Arrays
    if (element.asType().getKind() == TypeKind.ARRAY) {

      ArrayType arrayType = (ArrayType) element.asType();

      TypeMirror arrayOf = arrayType.getComponentType();

      if (arrayOf.getKind() == TypeKind.CHAR) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_CHAR_ARRAY));
      }

      if (arrayOf.getKind() == TypeKind.BOOLEAN) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_BOOL_ARRAY));
      }

      if (arrayOf.getKind() == TypeKind.BYTE) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_BYTE_ARRAY));
      }

      if (arrayOf.getKind() == TypeKind.DOUBLE) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_DOUBLE_ARRAY));
      }

      if (arrayOf.getKind() == TypeKind.FLOAT) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_FLOAT_ARRAY));
      }

      if (arrayOf.getKind() == TypeKind.INT) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_INT_ARRAY));
      }

      if (arrayOf.getKind() == TypeKind.LONG) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_LONG_ARRAY));
      }

      if (arrayOf.toString().equals(String.class.getName())) {
        return new CodeGenInfo(typeMap.get(TYPE_KEY_STRING_ARRAY));
      }

      if (isOfType(arrayOf, "android.os.Parcelable", elements, types)) {
        // It's an array of parcelable
        return new CodeGenInfo(typeMap.get(TYPE_KEY_PARCELABLE_ARRAY), arrayOf);
      }

      // Unsupported Array Type
      ProcessorMessage.error(element, "Unsuppored type %s as Array type for field %s. "
              + "You could write your own Serialization mechanism by using @%s ",
          element.asType().toString(), element.getSimpleName(), Bagger.class.getSimpleName());
    }


    // TODO SparseArray
    // if (isOfWildCardType(element, "android.util.SparseArray", "android.os.Parcelable", elements,
    //     types)) {
    //  return new CodeGenInfo(typeMap.get(TYPE_KEY_SPARSE_ARRAY),
    //      hasGenericsTypeArgumentOf(element, null, elements, types));
    // }



    // Serializable as last
    if (isOfType(element, "java.io.Serializable", elements, types)) {
      return new CodeGenInfo(typeMap.get(TYPE_KEY_SERIALIZABLE));
    }

    // Unsupported type
    ProcessorMessage.error(element, "Unsuppored type %s for field %s. "
            + "You could write your own Serialization mechanism by using @%s ",
        element.asType().toString(), element.getSimpleName(), Bagger.class.getSimpleName());

    return new CodeGenInfo(null);
  }

  /**
   * Get the wildcardType
   */
  public static TypeMirror getWildcardType(String type, String elementType, Elements elements,
      Types types) {
    TypeElement arrayList = elements.getTypeElement(type);
    TypeMirror elType = elements.getTypeElement(elementType).asType();
    return types.getDeclaredType(arrayList, types.getWildcardType(elType, null));
  }

  private static boolean isOfWildCardType(Element element, String type, String wildcardtype,
      Elements elements, Types types) {
    return types.isAssignable(element.asType(),
        getWildcardType(type, wildcardtype, elements, types));
  }

  private static boolean isOfType(Element element, String type, Elements elements, Types types) {
    return isOfType(element.asType(), type, elements, types);
  }

  private static boolean isOfType(TypeMirror typeMirror, String type, Elements elements,
      Types types) {
    return types.isAssignable(typeMirror, elements.getTypeElement(type).asType());
  }

  /**
   * Checks if the variabel element has generics arguments that matches the expected type
   */
  public static TypeMirror hasGenericsTypeArgumentOf(Element element, String typeToCheck,
      Elements elements, Types types) {

    if (element.asType().getKind() != TypeKind.DECLARED
        || !(element.asType() instanceof DeclaredType)) {
      ProcessorMessage.error(element, "The field %s in %s doesn't have generic type arguments!",
          element.getSimpleName(), element.asType().toString());
    }

    DeclaredType declaredType = (DeclaredType) element.asType();
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

    if (typeArguments.isEmpty()) {
      ProcessorMessage.error(element, "The field %s in %s doesn't have generic type arguments!",
          element.getSimpleName(), element.asType().toString());
    }

    if (typeArguments.size() > 1) {
      ProcessorMessage.error(element, "The field %s in %s has more than 1 generic type argument!",
          element.getSimpleName(), element.asType().toString());
    }

    // Ok it has a generic argument, check if this extends Parcelable
    TypeMirror argument = typeArguments.get(0);

    if (typeToCheck != null) {
      if (!isOfType(argument, typeToCheck, elements, types)) {
        ProcessorMessage.error(element,
            "The fields %s  generic type argument is not of type  %s! (in %s )",
            element.getSimpleName(), typeToCheck, element.asType().toString());
      }
    }

    // everything is like expected
    return argument;
  }
}
