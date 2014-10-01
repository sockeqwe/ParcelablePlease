package com.hannesdorfmann.parcelableplease.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
/**
 * A simple plugin for generating the boilerplateCode of ParcelablePlease
 *
 * @author Hannes Dorfmann
 */
public class ParcelablePleaseAction extends AnAction {
  public void actionPerformed(AnActionEvent e) {

    final PsiClass psiClass = getPsiClassFromContext(e);

    // Generate Code
    new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
      @Override
      protected void run() throws Throwable {
        new CodeGenerator(psiClass).generate();
      }
    }.execute();
  }

  @Override
  public void update(AnActionEvent e) {
    PsiClass psiClass = getPsiClassFromContext(e);
    e.getPresentation().setEnabled(psiClass != null && !psiClass.isEnum() && !psiClass.isInterface());
  }


  /**
   * Get the class where currently the curser is
   */
  private PsiClass getPsiClassFromContext(AnActionEvent e) {
    PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
    Editor editor = e.getData(PlatformDataKeys.EDITOR);
    

    if (psiFile == null || editor == null) {
      return null;
    }

    int offset = editor.getCaretModel().getOffset();
    PsiElement element = psiFile.findElementAt(offset);

    return PsiTreeUtil.getParentOfType(element, PsiClass.class);
  }

}
