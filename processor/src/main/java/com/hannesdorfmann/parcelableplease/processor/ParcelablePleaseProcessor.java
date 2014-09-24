package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.annotation.NoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ThisPlease;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Hannes Dorfmann
 */
@SupportedAnnotationTypes("com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease")
public class ParcelablePleaseProcessor extends AbstractProcessor {

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    elementUtils = env.getElementUtils();
    typeUtils = env.getTypeUtils();
    filer = env.getFiler();
    ProcessorMessage.init(env);
  }

  @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

    for (Element element : env.getElementsAnnotatedWith(ParcelablePlease.class)) {

      if (!isClass(element)) {
        continue;
      }

      ParcelablePlease annotation = element.getAnnotation(ParcelablePlease.class);
      boolean allFields = annotation.allFields();

      List<ParcelableField> fields = new ArrayList<ParcelableField>();

      if (element.getEnclosedElements() != null) {
        for (Element enclosed : element.getEnclosedElements()) {
          // Search for fields

          if (enclosed.getKind() != ElementKind.FIELD && !(enclosed instanceof VariableElement)) {
            continue; // Not a field, so go on
          }

          // it's a field, so go on

          NoThanks skipFieldAnnotation = enclosed.getAnnotation(NoThanks.class);

          if (skipFieldAnnotation != null) {
            // Field is marked as not parcelabel, so continue with the next
            continue;
          }

          if (!allFields) {
            ThisPlease fieldAnnotated = enclosed.getAnnotation(ThisPlease.class);
            if (fieldAnnotated == null) {
              // Not all fields should parcelable,
              // but this field is not annotated as make parcelable
              continue;
            }
          }

          // If we are here the field should be parcelable
          ParcelableField field = new ParcelableField();
          fields.add(field);
        }
      }
    }

    return false;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /**
   * Checks if the element is a class
   */
  private boolean isClass(Element element) {
    if (element.getKind() == ElementKind.CLASS) {
      return true;
    } else {
      ProcessorMessage.error(element,
          "Element %s is annotated with @%s but is not a class. Only Classes are supported",
          element.getSimpleName(), ParcelablePlease.class.getSimpleName());
      return false;
    }
  }
}
