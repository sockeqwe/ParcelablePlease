package com.hannesdorfmann.parcelableplease.processor;

import com.hannesdorfmann.parcelableplease.annotation.NoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ThisPlease;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.BaggerCodeGen;
import com.hannesdorfmann.parcelableplease.processor.codegenerator.CodeGenerator;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
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
import javax.tools.JavaFileObject;
import repacked.com.squareup.javawriter.JavaWriter;

/**
 * @author Hannes Dorfmann
 */
@SupportedAnnotationTypes("com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease")
public class ParcelablePleaseProcessor extends AbstractProcessor {

  private static boolean BAGGERS_CREATED = false;

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
    CodeGenerator codeGenerator = new CodeGenerator(elementUtils, filer);

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
          fields.add(new ParcelableField((VariableElement) member, elementUtils, typeUtils));
        }
      }

      //
      // Generate the code
      //

      try {
        codeGenerator.generate((TypeElement) element, fields);
      } catch (Exception e) {
        e.printStackTrace();
        ProcessorMessage.error(lastElement, "An error has occurred while processing %s : %s",
            element.getSimpleName(), e.getMessage());
      }
    }

    //
    // Generate baggers code
    //
    // Baggers are used, so generate mapping class
    try {
      generateBaggersMappingClass(lastElement);
    } catch (IOException e) {
      e.printStackTrace();
      ProcessorMessage.error(lastElement,
          "An error has occurred while generating Baggers class: %s", e.getMessage());
    }

    return true;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /**
   * Checks if the element is a class
   */
  private boolean isClass(Element element) {
    if (element.getKind() == ElementKind.CLASS) {

      if (element.getModifiers().contains(Modifier.ABSTRACT)) {
        ProcessorMessage.error(element,
            "Element %s is annotated with @%s but is an abstract class. "
                + "Abstract classes can not be annotated. Annotate the concrete class "
                + "that implements all abstract methods with @%s", element.getSimpleName(),
            ParcelablePlease.class.getSimpleName(), ParcelablePlease.class.getSimpleName());
        return false;
      }

      if (element.getModifiers().contains(Modifier.PRIVATE)) {
        ProcessorMessage.error(element, "The private class %s is annotated with @%s. "
                + "Private classes are not supported because of lacking visibility.",
            element.getSimpleName(), ParcelablePlease.class.getSimpleName());
        return false;
      }

      // Ok, its a valid class
      return true;
    } else {
      ProcessorMessage.error(element,
          "Element %s is annotated with @%s but is not a class. Only Classes are supported",
          element.getSimpleName(), ParcelablePlease.class.getSimpleName());
      return false;
    }
  }

  /**
   * Generates a file
   *
   * @throws IOException
   */
  private void generateBaggersMappingClass(Element lastElement) throws IOException {

    if (BAGGERS_CREATED) {
      return;
    }

    JavaFileObject jfo = filer.createSourceFile(BaggerCodeGen.BAGGERS_QUALIFIED_NAME, lastElement);
    Writer writer = jfo.openWriter();
    JavaWriter jw = new JavaWriter(writer);

    jw.emitPackage(BaggerCodeGen.BAGGERS_PACKAGE);

    jw.emitEmptyLine();
    jw.emitJavadoc("Generated class by @%s . Do not modify this code!",
        ParcelablePlease.class.getSimpleName());

    jw.beginType(BaggerCodeGen.BAGGERS_CLASS_NAME, "class", EnumSet.of(Modifier.PUBLIC));
    jw.emitEmptyLine();

    Set<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    // Fields
    for (String c : BaggerCodeGen.usedBaggers) {

      jw.emitEmptyLine();

      jw.emitField(c, BaggerCodeGen.classToFieldName(c), modifiers, "new " + c + "()");
    }

    jw.emitEmptyLine();
    jw.emitEmptyLine();

    // Empty constructor
    jw.beginConstructor(EnumSet.of(Modifier.PRIVATE));
    jw.endConstructor();

    jw.endType();
    jw.close();

    BAGGERS_CREATED = true;
  }
}
