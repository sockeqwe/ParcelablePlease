package com.hannesdorfmann.parcelableplease.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import javax.tools.JavaFileObject;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class ParcelablePleaseProcessorTest {

  @Test
  public void abstractClass() {

    String annotation = ParcelablePlease.class.getCanonicalName();
    JavaFileObject componentFile = JavaFileObjects.forSourceLines("test.AbstractClass",
        "package test;",
        "",
        "@" + annotation,
        "abstract class AbstractClass {}");

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(componentFile).processedWith(new ParcelablePleaseProcessor())
        .failsToCompile()
        .withErrorContaining("Element AbstractClass is annotated with @ParcelablePlease but is an abstract class. Abstract classes can not be annotated. Annotate the concrete class that implements all abstract methods with @ParcelablePlease");

  }

  @Test
  public void simpleClass() {

    String annotation = ParcelablePlease.class.getCanonicalName();
    JavaFileObject componentFile = JavaFileObjects.forSourceLines("test.SimpleClass",
        "package test;",
        "",
        "@" + annotation,
        "class SimpleClass {" +
            "int id;" +
            "String name;" +
            "}");

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(componentFile).processedWith(new ParcelablePleaseProcessor())
        .compilesWithoutError();
  }


  @Test
  public void innerClass() {

    String annotation = ParcelablePlease.class.getCanonicalName();
    JavaFileObject componentFile = JavaFileObjects.forSourceLines("test.OuterClass",
        "package test;",
        "",
        "class OuterClass {",
        "@" + annotation,
        "class InnerClass {",
            "int id;",
            "String name;" ,
            "}",
        "}");

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(componentFile).processedWith(new ParcelablePleaseProcessor())
        .compilesWithoutError();
  }

  @Test
  public void stringList() {

    String annotation = ParcelablePlease.class.getCanonicalName();
    JavaFileObject componentFile = JavaFileObjects.forSourceLines("test.StringListTest",
        "package test;",
        "",
        "@" + annotation,
        "class StringListTest {",
        "   java.util.List<String> strings;",
        "}");

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(componentFile).processedWith(new ParcelablePleaseProcessor())
        .compilesWithoutError();
  }


}
