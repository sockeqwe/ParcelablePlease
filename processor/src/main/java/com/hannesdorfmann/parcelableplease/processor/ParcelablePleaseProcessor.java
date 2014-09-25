package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.annotation.NoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ThisPlease;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator;
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
import javax.lang.model.element.Modifier;
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

    Element lastElement = null;

    for (Element element : env.getElementsAnnotatedWith(ParcelablePlease.class)) {

      if (!isClass(element)) {
        continue;
      }

      List<ParcelableField> fields = new ArrayList<ParcelableField>();

      lastElement = element;

      ParcelablePlease annotation = element.getAnnotation(ParcelablePlease.class);
      boolean allFields = annotation.allFields();
      boolean ignorePrivateFields = annotation.ignorePrivateFields();

      List<? extends Element> memberFields = elementUtils.getAllMembers((TypeElement) element);

      if (memberFields != null) {
        for (Element member : memberFields) {
          // Search for fields

          if (member.getKind() != ElementKind.FIELD || !(member instanceof VariableElement)) {
            continue; // Not a field, so go on
          }

          // it's a field, so go on

          NoThanks skipFieldAnnotation = member.getAnnotation(NoThanks.class);
          if (skipFieldAnnotation != null) {
            // Field is marked as not parcelabel, so continue with the next
            continue;
          }

          if (!allFields) {
            ThisPlease fieldAnnotated = member.getAnnotation(ThisPlease.class);
            if (fieldAnnotated == null) {
              // Not all fields should parcelable,
              // and this field is not annotated as parcelable, so skip this field
              continue;
            }
          }

          // Check the visibility of the field and modifiers
          Set<Modifier> modifiers = member.getModifiers();

          if (modifiers.contains(Modifier.STATIC)) {
            // Static fields are skipped
            continue;
          }

          if (modifiers.contains(Modifier.PRIVATE)) {

            if (ignorePrivateFields) {
              continue;
            }

            ProcessorMessage.error(member,
                "The field %s  in %s is private. At least default package visibility is required "
                    + "or annotate this field as not been parcelable with @%s "
                    + "or configure this class to ignore private fields "
                    + "with @%s( ignorePrivateFields = true )", member.getSimpleName(),
                element.getSimpleName(), NoThanks.class.getSimpleName(),
                ParcelablePlease.class.getSimpleName());
          }

          if (modifiers.contains(Modifier.FINAL)) {
            ProcessorMessage.error(member,
                "The field %s in %s is final. Final can not be Parcelable", element.getSimpleName(),
                member.getSimpleName());
          }

          // If we are here the field is be parcelable
          fields.add(new ParcelableField((VariableElement) member));
        }
      }

      //
      // Generate the code
      //

      try {
        CodeGenerator codeGenerator = new CodeGenerator(elementUtils, filer);
        codeGenerator.generate((TypeElement) element, fields);
      } catch (Exception e) {
        e.printStackTrace();
        ProcessorMessage.error(lastElement, "An error has occurred: %s", e.getMessage());
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
