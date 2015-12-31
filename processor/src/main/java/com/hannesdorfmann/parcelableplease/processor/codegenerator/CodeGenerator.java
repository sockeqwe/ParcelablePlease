package com.hannesdorfmann.parcelableplease.processor.codegenerator;

import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import com.hannesdorfmann.parcelableplease.processor.ProcessorMessage;
import com.hannesdorfmann.parcelableplease.processor.util.TypeUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import repacked.com.squareup.javawriter.JavaWriter;

/**
 * Generates the javacode for each field
 *
 * @author Hannes Dorfmann
 */
public class CodeGenerator {

  public static final String PARAM_TARGET = "target";

  public static final String PARAM_SOURCE = "source";

  public static final String PARAM_FLAGS = "flags";

  /**
   * The parameter name for the
   */
  public static final String PARAM_PARCEL = "parcel";

  private Filer filer;
  private Elements elementUtils;

  public CodeGenerator(Elements elementUtils, Filer filer) {
    this.filer = filer;
    this.elementUtils = elementUtils;
  }

  public void generate(TypeElement classElement, List<ParcelableField> fields) throws Exception {

    String classSuffix = "ParcelablePlease";
    String packageName = TypeUtils.getPackageName(elementUtils, classElement);
    String binaryName = TypeUtils.getBinaryName(elementUtils, classElement);
    String originFullQualifiedName = classElement.getQualifiedName().toString();
    String className;
    if (packageName.length() > 0) {
      className = binaryName.substring(packageName.length() + 1) + classSuffix;
    } else {
      className = binaryName + classSuffix;
    }
    String qualifiedName = binaryName + classSuffix;

    //
    // Write code
    //

    JavaFileObject jfo = filer.createSourceFile(qualifiedName, classElement);
    Writer writer = jfo.openWriter();
    JavaWriter jw = new JavaWriter(writer);

    jw.emitPackage(packageName);
    jw.emitImports("android.os.Parcel");
    jw.emitEmptyLine();
    jw.emitJavadoc("Generated class by @%s . Do not modify this code!",
        ParcelablePlease.class.getSimpleName());
    jw.beginType(className, "class", EnumSet.of(Modifier.PUBLIC));
    jw.emitEmptyLine();

    generateWriteToParcel(jw, originFullQualifiedName, fields);
    jw.emitEmptyLine();
    generateReadFromParcel(jw, originFullQualifiedName, fields);

    jw.endType();
    jw.close();
  }

  /**
   * Generate the writeToParcel method
   *
   * @throws IOException
   */
  private void generateWriteToParcel(JavaWriter jw, String originClass,
      List<ParcelableField> fields) throws IOException {

    jw.beginMethod("void", "writeToParcel", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC),
        originClass, PARAM_SOURCE, "Parcel", PARAM_PARCEL, "int", PARAM_FLAGS);

    for (ParcelableField field : fields) {
      FieldCodeGen gen = field.getCodeGenerator();

      if (gen == null) { // Already checked before, but let's check it again
        ProcessorMessage.error(field.getElement(),
            "The field %s is not Parcelable or of unsupported type. Use a @%s",
            field.getFieldName(),
            Bagger.class.getSimpleName() + " to provide your own serialisation mechanism");

        throw new IOException("Unparcelable Field " + field.getFieldName());
      }

      jw.emitEmptyLine();
      gen.generateWriteToParcel(field, jw);
    }

    jw.endMethod();
  }

  private void generateReadFromParcel(JavaWriter jw, String originClass,
      List<ParcelableField> fields) throws IOException {

    jw.beginMethod("void", "readFromParcel", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC),
        originClass, PARAM_TARGET, "Parcel", PARAM_PARCEL);

    for (ParcelableField field : fields) {
      FieldCodeGen gen = field.getCodeGenerator();

      if (gen == null) { // Already checked before, but let's check it again
        ProcessorMessage.error(field.getElement(),
            "The field %s is not Parcelable or of unsupported type. Use a @%s",
            field.getFieldName(),
            Bagger.class.getSimpleName() + " to provide your own serialisation mechanism");

        throw new IOException("Unparcelable Field " + field.getFieldName());
      }

      jw.emitEmptyLine();
      gen.generateReadFromParcel(field, jw);
    }

    jw.endMethod();
  }
}
