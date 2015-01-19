package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.ParcelBagger;
import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.BaggerCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.FieldCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.SupportedTypes;
import com.hannesdorfmann.parcelableplease.processor.util.CodeGenInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

// TODO refactor this class in interfaces and sublacsses for BaggerAnnotated, normal and Generic fields

/**
 * Scans a field and collects information for the java code generating process
 *
 * @author Hannes Dorfmann
 */
public class ParcelableField {

  private String fieldName;
  private String type;
  private Element element;
  private FieldCodeGen codeGenerator;
  private TypeMirror genericsTypeArgument;
  private String baggerFullyQualifiedName;

  public ParcelableField(VariableElement element, Elements elementUtils, Types typeUtils) {

    this.element = element;
    fieldName = element.getSimpleName().toString();
    type = element.asType().toString();

    // Check for Bagger
    Bagger baggerAnnotation = element.getAnnotation(Bagger.class);
    if (baggerAnnotation != null) {
      // has a bagger annotation
      try {
        Class<? extends ParcelBagger> clazz = baggerAnnotation.value();
        baggerFullyQualifiedName = getFullQualifiedNameByClass(clazz);
      } catch (MirroredTypeException mte) {
        TypeMirror baggerClass = mte.getTypeMirror();
        baggerFullyQualifiedName = getFullQualifiedNameByTypeMirror(baggerClass);
      }

      // Everything is fine, so use the bagger
      codeGenerator = new BaggerCodeGen();
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
      }
    }
  }

  private String getFullQualifiedNameByClass(Class<? extends ParcelBagger> clazz) {

    // Check public
    if (!Modifier.isPublic(clazz.getModifiers())) {

      ProcessorMessage.error(element, "The %s must be a public class to be a valid Bagger",
          clazz.getCanonicalName());
      return null;
    }

    // Check constructors
    Constructor<?>[] constructors = clazz.getConstructors();

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
          clazz.getCanonicalName());
      return null;
    }

    return clazz.getCanonicalName();
  }

  /**
   * Checks if a class is public
   */
  private boolean isPublicClass(DeclaredType type) {
    Element element = type.asElement();

    return element.getModifiers().contains(javax.lang.model.element.Modifier.PUBLIC);
  }

  /**
   * Checks if an public empty constructor is available
   */
  private boolean hasPublicEmptyConstructor(DeclaredType type) {
    Element element = type.asElement();

    List<? extends Element> containing = element.getEnclosedElements();

    for (Element e : containing) {
      if (e.getKind() == ElementKind.CONSTRUCTOR) {
        ExecutableElement c = (ExecutableElement) e;

        if ((c.getParameters() == null || c.getParameters().isEmpty()) && c.getModifiers()
            .contains(javax.lang.model.element.Modifier.PUBLIC)) {
          return true;
        }
      }
    }

    return false;
  }

  private String getFullQualifiedNameByTypeMirror(TypeMirror baggerClass) {
    if (baggerClass == null) {
      ProcessorMessage.error(element, "Could not get the bagger class");
      return null;
    }

    if (baggerClass.getKind() != TypeKind.DECLARED) {
      ProcessorMessage.error(element, "@ %s  is not a class in %s ", Bagger.class.getSimpleName(),
          element.getSimpleName());
      return null;
    }

    if (!isPublicClass((DeclaredType) baggerClass)) {
      ProcessorMessage.error(element, "The %s must be a public class to be a valid Bagger",
          baggerClass.toString());
      return null;
    }

    // Check if the bagger class has a default constructor
    if (!hasPublicEmptyConstructor((DeclaredType) baggerClass)) {
      ProcessorMessage.error(element,
          "The %s must provide a public empty default constructor to be a valid Bagger",
          baggerClass.toString());
      return null;
    }

    return baggerClass.toString();
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

  public String getFullQualifiedBaggerName() {
    return baggerFullyQualifiedName;
  }

  public FieldCodeGen getCodeGenerator() {
    return codeGenerator;
  }

  public TypeMirror getGenericsTypeArgument() {
    return genericsTypeArgument;
  }
}
