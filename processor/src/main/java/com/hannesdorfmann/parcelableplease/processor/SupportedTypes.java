package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.processor.codegenerator.AbsCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.collection.AbsListCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.other.DateCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.other.ParcelableCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitives.BooleanCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.AbsPrimitiveWrapperCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.primitiveswrapper.BooleanWrapperCodeGen;
import com.hannesdorfmann.parcelableplease.processor.util.GenericsTypeArgmumentException;
import com.hannesdorfmann.parcelableplease.processor.util.TypeKeyResult;
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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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

  /**
   * Used for arraylist of parcelables
   */
  private static final String TYPE_KEY_PARCELABLE_ARRAYLIST = "Parcelable-ArrayList";

  /**
   * Used for LinkedList of parcelables
   */
  private static final String TYPE_KEY_PARCELABLE_LINKEDLIST = "Parcelable-LinkedList";

  private static final String TYPE_KEY_PARCELABLE_LIST = "Parcelable-List";

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

    // Parcelable , Serializeable
    typeMap.put(TYPE_KEY_PARCELABLE, new ParcelableCodeGen());

    // Lists
    typeMap.put(TYPE_KEY_PARCELABLE_LIST, new AbsListCodeGen(ArrayList.class.getName()));
    typeMap.put(TYPE_KEY_PARCELABLE_ARRAYLIST, new AbsListCodeGen(ArrayList.class.getName()));
    typeMap.put(TYPE_KEY_PARCELABLE_LINKEDLIST, new AbsListCodeGen(LinkedList.class.getName()));
    typeMap.put(TYPE_KEY_PARCELABLE_COPYONWRITEARRAYLIST,
        new AbsListCodeGen(CopyOnWriteArrayList.class.getName()));

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
  public static TypeKeyResult getTypeKey(VariableElement element, Elements elements, Types types) {

    // Check if its a simple parcelable
    if (isOfType(element, "android.os.Parcelable", elements, types)) {
      return new TypeKeyResult(TYPE_KEY_PARCELABLE);
    }

    // Lists
    if (isOfWildCardType(element, ArrayList.class.getName(), "android.os.Parcelable", elements,
        types)) {

      return new TypeKeyResult(TYPE_KEY_PARCELABLE_ARRAYLIST,
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    if (isOfWildCardType(element, LinkedList.class.getName(), "android.os.Parcelable", elements,
        types)) {
      return new TypeKeyResult(TYPE_KEY_PARCELABLE_LINKEDLIST,
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    if (isOfWildCardType(element, CopyOnWriteArrayList.class.getName(), "android.os.Parcelable",
        elements, types)) {
      return new TypeKeyResult(TYPE_KEY_PARCELABLE_COPYONWRITEARRAYLIST,
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    if (isOfWildCardType(element, List.class.getName(), "android.os.Parcelable", elements, types)) {
      return new TypeKeyResult(TYPE_KEY_PARCELABLE_LIST,
          hasGenericsTypeArgumentOf(element, "android.os.Parcelable", elements, types));
    }

    return new TypeKeyResult(element.asType().toString());
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

      throw new GenericsTypeArgmumentException(
          "The field " + element.getSimpleName() + " doesn't have generic type arguments!");
    }

    DeclaredType declaredType = (DeclaredType) element.asType();
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

    if (typeArguments.isEmpty()) {
      ProcessorMessage.error(element, "The field %s in %s doesn't have generic type arguments!",
          element.getSimpleName(), element.asType().toString());

      throw new GenericsTypeArgmumentException(
          "The field " + element.getSimpleName() + " doesn't have generic type arguments!");
    }

    if (typeArguments.size() > 1) {
      ProcessorMessage.error(element, "The field %s in %s has more than 1 generic type argument!",
          element.getSimpleName(), element.asType().toString());

      throw new GenericsTypeArgmumentException(
          "The field " + element.getSimpleName() + " has more than 1 generic type argument");
    }

    // Ok it has a generic argument, check if this extends Parcelable
    TypeMirror argument = typeArguments.get(0);

    if (!isOfType(argument, typeToCheck, elements, types)) {
      ProcessorMessage.error(element,
          "The fields %s  generic type argument is not of type  %s! (in %s )",
          element.getSimpleName(), typeToCheck, element.asType().toString());

      throw new GenericsTypeArgmumentException("The fields "
          + element.getSimpleName()
          + " generic type argument is not of type "
          + typeToCheck);
    }

    // everything is like expected
    return argument;
  }
}
