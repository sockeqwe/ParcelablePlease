package com.hannesdorfmann.parcelableplease.processor.codegenerator;

import com.hannesdorfmann.annotationprocessing.TypeUtils;
import com.hannesdorfmann.parcelableplease.annotation.Bagger;
import com.hannesdorfmann.parcelableplease.processor.ParcelableField;
import com.hannesdorfmann.parcelableplease.processor.ProcessorMessage;
import com.hannesdorfmann.parcelableplease.processor.SupportedTypes;
import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import repacked.com.squareup.javawriter.JavaWriter;

/**
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

  public void generate(Element classElement, List<ParcelableField> fields) throws Exception {

    String packageName = TypeUtils.getPackageName(elementUtils, (TypeElement) classElement);
    String originClass = classElement.getSimpleName().toString();
    String className = originClass + "ParcelablePlease";

    JavaFileObject jfo = filer.createSourceFile(packageName, classElement);
    Writer writer = jfo.openWriter();
    JavaWriter jw = new JavaWriter(writer);

    jw.emitPackage(packageName);
    jw.emitEmptyLine();
    jw.emitImports("android.os.Parcel");
    jw.beginType(className, "class", EnumSet.of(Modifier.PUBLIC));

    generateWriteToParcel(jw, originClass, fields);

    jw.endType();
    jw.close();
  }

  /**
   * Generate the writeToParcel method
   * @param jw
   * @param originClass
   * @param fields
   * @throws IOException
   */
  private void generateWriteToParcel(JavaWriter jw, String originClass,
      List<ParcelableField> fields) throws IOException {

    jw.beginMethod("void", "writeToParcel", EnumSet.of(Modifier.PUBLIC), originClass, PARAM_SOURCE,
        "Parcel", PARAM_PARCEL, "int", PARAM_FLAGS);

    for (ParcelableField field : fields) {
      FieldCodeGen gen = SupportedTypes.getGenerator(field);

      if (gen == null) {
        ProcessorMessage.error(field.getElement(), "The field %s is not Parcelable. Use a @%s",
            field.getFieldName(), Bagger.class.getSimpleName() + " to make it parcelable");

        throw new IOException("Unparcelable Field " + field.getFieldName());
      }

      gen.generateWriteToParcel(field, jw);
    }

    jw.endMethod();
  }
}
