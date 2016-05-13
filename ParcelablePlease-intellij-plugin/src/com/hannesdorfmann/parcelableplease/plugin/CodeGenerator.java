/*
 * Copyright (C) 2014 Hannes Dormfann (www.hannesdorfmann.com)
 *
 *
 * Parts of this code are taken from  Michał Charmas.
 *
 * Copyright (C) 2013 Michał Charmas (http://blog.charmas.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.hannesdorfmann.parcelableplease.plugin;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatementBase;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

/**
 * Generates the real code for ParcelablePlease that will be injected in intellij code editor
 * window
 *
 * @author Hannes Dorfmann
 */
public class CodeGenerator {

  private static final String PARCELABLE_PLEASE_SUFFIX = "ParcelablePlease";

  private static final String ANNOTATION_PACKAGE =
      "com.hannesdorfmann.parcelableplease.annotation";

  private static final String ANNOTATION_NAME = "ParcelablePlease";

  private PsiClass psiClass;

  public CodeGenerator(PsiClass psiClass) {
    this.psiClass = psiClass;
  }

  private String generateCreator() {

    String className = psiClass.getName();
    String parcelablePleaseClass = getParcelablePleaseClassName();

    StringBuilder sb = new StringBuilder("public static final android.os.Parcelable.Creator<");
    sb.append(className)
        .append("> CREATOR = new android.os.Parcelable.Creator<")
        .append(className)
        .append(">(){")
        .append("public ")
        .append(className)
        .append(" createFromParcel(android.os.Parcel source) {")
        .append(className)
        .append(" target =  new ")
        .append(className)
        .append("();")
        .append(parcelablePleaseClass)
        .append(".readFromParcel(target, source); return target;}")
        .append("public ")
        .append(className)
        .append("[] newArray(int size) {")
        .append("return new ")
        .append(className)
        .append("[size];}")
        .append("};");

    return sb.toString();
  }

  private String generateWriteToParcel() {

    String parcelablePleaseClass = getParcelablePleaseClassName();

    StringBuilder sb = new StringBuilder(
        "@Override public void writeToParcel(android.os.Parcel dest, int flags) {");

    sb.append(parcelablePleaseClass).append(".writeToParcel(this, dest, flags);").append("}");

    return sb.toString();
  }

  private String generateDescribeContents() {
    return "@Override public int describeContents() { return 0; }";
  }

  private void addImport(PsiElementFactory elementFactory, String fullyQualifiedName){
    final PsiFile file = psiClass.getContainingFile();
    if (!(file instanceof PsiJavaFile)) {
      return;
    }
    final PsiJavaFile javaFile = (PsiJavaFile)file;

    final PsiImportList importList = javaFile.getImportList();
    if (importList == null) {
      return;
    }

    // Check if already imported
    for (PsiImportStatementBase is : importList.getAllImportStatements()) {
      String impQualifiedName = is.getImportReference().getQualifiedName();
      if (fullyQualifiedName.equals(impQualifiedName)){
        return; // Already imported so nothing neede
      }

    }

    // Not imported yet so add it
    importList.add(elementFactory.createImportStatementOnDemand(fullyQualifiedName));
  }


  public void clearPrevious(){

    // Delete creator
    PsiField creatorField = psiClass.findFieldByName("CREATOR", false);
    if (creatorField != null) {
      creatorField.delete();
    }

    // Delete Methods
    findAndRemoveMethod("describeContents");
    findAndRemoveMethod("writeToParcel", "android.os.Parcel", "int");

  }

  /**
   * Finds and removes a given method
   * @param methodName
   * @param arguments
   */
  private void findAndRemoveMethod(String methodName, String... arguments) {
    // Maybe there's an easier way to do this with mClass.findMethodBySignature(), but I'm not an expert on Psi*
    PsiMethod[] methods = psiClass.findMethodsByName(methodName, false);

    for (PsiMethod method : methods) {
      PsiParameterList parameterList = method.getParameterList();

      if (parameterList.getParametersCount() == arguments.length) {
        boolean shouldDelete = true;

        PsiParameter[] parameters = parameterList.getParameters();

        for (int i = 0; i < arguments.length; i++) {
          if (!parameters[i].getType().getCanonicalText().equals(arguments[i])) {
            shouldDelete = false;
          }
        }

        if (shouldDelete) {
          method.delete();
        }
      }
    }
  }

  /**
   * Generate and insert the Parcel and ParcelablePlease code
   */
  public void generate() {

    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
    JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(psiClass.getProject());

    // Clear any previous
    clearPrevious();

    // Implements parcelable
    makeClassImplementParcelable(elementFactory, styleManager);

    // @ParcelablePlease Annotation
    addAnnotation(elementFactory, styleManager);

    // Creator
    PsiField creatorField = elementFactory.createFieldFromText(generateCreator(), psiClass);

    // Describe contents method
    PsiMethod describeContentsMethod =
        elementFactory.createMethodFromText(generateDescribeContents(), psiClass);

    // Method for writing to the parcel
    PsiMethod writeToParcelMethod =
        elementFactory.createMethodFromText(generateWriteToParcel(), psiClass);

    styleManager.shortenClassReferences(
        psiClass.addBefore(describeContentsMethod, psiClass.getLastChild()));

    styleManager.shortenClassReferences(
        psiClass.addBefore(writeToParcelMethod, psiClass.getLastChild()));

    styleManager.shortenClassReferences(psiClass.addBefore(creatorField, psiClass.getLastChild()));
  }

  /**
   * Add the @Parcelable annotation if not already annotated
   */
  private void addAnnotation(PsiElementFactory elementFactory, JavaCodeStyleManager styleManager) {

    boolean annotated = AnnotationUtil.isAnnotated(psiClass, ANNOTATION_PACKAGE+"."+ANNOTATION_NAME, false);

    if (!annotated) {
      styleManager.shortenClassReferences(psiClass.getModifierList().addAnnotation(
          ANNOTATION_NAME));
    }
  }

  /**
   * Make the class implementing Parcelable
   */
  private void makeClassImplementParcelable(PsiElementFactory elementFactory, JavaCodeStyleManager styleManager) {
    final PsiClassType[] implementsListTypes = psiClass.getImplementsListTypes();
    final String implementsType = "android.os.Parcelable";

    for (PsiClassType implementsListType : implementsListTypes) {
      PsiClass resolved = implementsListType.resolve();

      // Already implements Parcelable, no need to add it
      if (resolved != null && implementsType.equals(resolved.getQualifiedName())) {
        return;
      }
    }

    PsiJavaCodeReferenceElement implementsReference =
        elementFactory.createReferenceFromText(implementsType, psiClass);
    PsiReferenceList implementsList = psiClass.getImplementsList();

    if (implementsList != null) {
      styleManager.shortenClassReferences(implementsList.add(implementsReference));
    }
  }

  private String getParcelablePleaseClassName() {
    String className = psiClass.getName();
    StringBuilder classNameBuilder = new StringBuilder();
    classNameBuilder.append(className);
    classNameBuilder.append(PARCELABLE_PLEASE_SUFFIX);
    PsiClass containingClass = psiClass.getContainingClass();
    while (containingClass != null) {
      classNameBuilder.insert(0, '$');
      classNameBuilder.insert(0, containingClass.getName());
      containingClass = containingClass.getContainingClass();
    }

    return classNameBuilder.toString();
  }
}
