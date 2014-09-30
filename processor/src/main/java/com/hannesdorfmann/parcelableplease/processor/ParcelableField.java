package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.ParcelableBagger;
import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import com.hannesdorfmann.parcelableplease.processor.util.TypeKeyResult;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

// TODO refactor this class in interfacees and sublacsses for BaggerAnnotated, normal and Generic fields

/**
 * @author Hannes Dorfmann
 */
public class ParcelableField {

  private String fieldName;
  private String type;
  private Class<? extends ParcelableBagger> baggerClass;
  private Element element;
  private String typeKey;
  private TypeMirror genericsTypeArgument;

  public ParcelableField(VariableElement element, Elements elementUtils, Types typeUtils) {

    this.element = element;
    fieldName = element.getSimpleName().toString();
    type = element.asType().toString();

    // Check for Bagger
    Bagger baggerAnnotation = element.getAnnotation(Bagger.class);
    if (baggerAnnotation != null) {
      // has a bagger annotation
      baggerClass = baggerAnnotation.value();
      // Check if the bagger class has a default constructor
      Constructor<?>[] constructors = baggerClass.getConstructors();

      boolean foundDefaultConstructor = false;
      for (Constructor c : constructors) {
        boolean isPublicConstructor = Modifier.isPublic(c.getModifiers());
        Class<?>[] pType = c.getParameterTypes();

        if (pType.length == 0 && isPublicConstructor) {
          foundDefaultConstructor = true;
          break;
        }
      }

      if (!foundDefaultConstructor) {
        ProcessorMessage.error(element, "The %s must provide a public empty default constructor",
            baggerClass.getSimpleName());
      }
    } else {

      // Check if type is supported
      TypeKeyResult res = SupportedTypes.getTypeKey(element, elementUtils, typeUtils);
      typeKey = res.getTypeKey();

      FieldCodeGen gen = SupportedTypes.getGenerator(this);
      if (gen == null) {
        ProcessorMessage.error(getElement(),
            "The field %s is not Parcelable or is of unsupported type. Use a @%s", getFieldName(),
            Bagger.class.getSimpleName() + " to provide your own serialisation mechanism");

        throw new IllegalArgumentException("Unparcelable Field " + getFieldName());
      }

      genericsTypeArgument = res.getGenericsType();
    }
  }

  public Element getElement() {
    return element;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getType() {
    return type;
  }

  public Class<? extends ParcelableBagger> getBaggerClass() {
    return baggerClass;
  }

  public String getTypeKey() {
    return typeKey;
  }

  public TypeMirror getGenericsTypeArgument() {
    return genericsTypeArgument;
  }
}
