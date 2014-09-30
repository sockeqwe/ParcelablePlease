package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.ParcelableBagger;
import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.SupportedTypes;
import com.hannesdorfmann.parcelableplease.processor.util.CodeGenInfo;
import com.hannesdorfmann.parcelableplease.processor.util.UnsupportedTypeException;
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
  private FieldCodeGen codeGenerator;
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
      // Not using Bagger
      CodeGenInfo res = SupportedTypes.getCodeGenInfo(element, elementUtils, typeUtils);
      codeGenerator = res.getCodeGenerator();
      genericsTypeArgument = res.getGenericsType();

      // Check if type is supported
      if (codeGenerator == null) {
        ProcessorMessage.error(element,
            "Unsupported type %s for field %s. You could use @%s to provide your own serialization mechanism",
            element.asType().toString(), element.getSimpleName(), Bagger.class.getSimpleName());

        throw new UnsupportedTypeException("Unsupported type %s " + element.asType().toString());
      }
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

  public FieldCodeGen getCodeGenerator() {
    return codeGenerator;
  }

  public TypeMirror getGenericsTypeArgument() {
    return genericsTypeArgument;
  }
}
