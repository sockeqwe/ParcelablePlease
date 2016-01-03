package com.hannesdorfmann.parcelableplease.processor.util;

import java.io.IOException;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * @author Hannes Dorfmann
 */
public class TypeUtils {

  /**
   * Checks if a element is of the type
   */
  public static boolean isTypeOf(Element element, Class<?> clazz) {
    return isTypeOf(element.asType(), clazz);
  }

  /**
   * Checks if a TypeMirror equals a class
   */
  public static boolean isTypeOf(TypeMirror type, Class<?> clazz) {
    return type.toString().equals(clazz.getCanonicalName());
  }

  /**
   * Returns the class this element is part of
   */
  public static Class<?> isTypeOf(TypeMirror type, List<Class<?>> classList) {

    for (Class<?> c : classList) {
      if (isTypeOf(type, c)) {
        return c;
      }
    }

    return null;
  }

  /**
   * Get the Package name
   *
   * @throws IOException
   */
  public static String getPackageName(Elements elementUtils, TypeElement type) throws IOException {
    PackageElement pkg = elementUtils.getPackageOf(type);
    if (!pkg.isUnnamed()) {
      return pkg.getQualifiedName().toString();
    } else {
      return ""; // Default package
    }
  }

  /**
   * Get the qualified name of a class. Also respects innner classes
   *
   * @param elementUtils Elements
   * @param type TypeElement
   * @return The qualified name of the class represented by the passed type element
   * @throws IOException
   */
  public static String getBinaryName(Elements elementUtils, TypeElement type) throws IOException {
    String packageName = getPackageName(elementUtils, type);
    String qualifiedName = type.getQualifiedName().toString();
    if (packageName.length() > 0) {
      return packageName + '.' + qualifiedName.substring(packageName.length() + 1).replace('.', '$');
    } else {
      return qualifiedName.replace('.', '$');
    }
  }
}
