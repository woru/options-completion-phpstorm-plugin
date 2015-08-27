package com.github.woru.phpoptionscompletion;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.PhpExpressionImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class OptionsCompletionContributor extends CompletionContributor {
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition().getParent();

        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
        if (parameterList == null) {
            return;
        }
        PsiElement[] givenParameters = parameterList.getParameters();
        PsiElement context = parameterList.getContext();
        if (context instanceof FunctionReference) {
            FunctionReference function = (FunctionReference) context;
            addCompletionForFunctionOptions(function, element, givenParameters, result);
        } else if (context instanceof NewExpression) {
            NewExpression newExpression = (NewExpression) context;
            addCompletionForConstructorOptions(newExpression, element, givenParameters, result);
        }
    }

    private void addCompletionForConstructorOptions(NewExpression newExpression, PsiElement element, PsiElement[] givenParameters, CompletionResultSet result) {
        ClassReference classReference = newExpression.getClassReference();
        if (classReference != null) {
            PsiElement resolvedClass = classReference.resolve();
            Method method = (Method) resolvedClass;
            if (method != null) {
                PhpDocComment docComment = method.getDocComment();
                if (docComment != null) {
                    addCompletionForOptions(result, element, givenParameters, docComment.getText());
                }
            }
        }
    }

    private void addCompletionForFunctionOptions(FunctionReference function, PsiElement element, PsiElement[] givenParameters, CompletionResultSet result) {
        PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
        Collection<? extends PhpNamedElement> bySignature = phpIndex.getBySignature(function.getSignature());
        if (!bySignature.isEmpty()) {
            PhpNamedElement first = bySignature.iterator().next();
            PhpDocComment docComment = first.getDocComment();
            if (docComment != null) {
                addCompletionForOptions(result, element, givenParameters, docComment.getText());
            }
        }
    }

    private void addCompletionForOptions(CompletionResultSet result, PsiElement element, PsiElement[] givenParameters, String docCommentText) {
        ArrayCreationExpression arrayCreation = PsiTreeUtil.getParentOfType(element, ArrayCreationExpression.class);
        if (arrayCreation != null && canBecomeKey(element)) {
            Integer parameterIndex = getParameterIndex(givenParameters, arrayCreation);
            if (parameterIndex != null) {
                Map<Integer, OptionsParam> optionsParams = new PhpDocCommentParser().parse(docCommentText);
                OptionsParam optionsParam = optionsParams.get(parameterIndex);
                if (optionsParam != null) {
                    Map<String, String> options = optionsParam.getOptions();
                    for (String name : Sets.difference(options.keySet(), getAllKeys(arrayCreation))) {
                        result.addElement(LookupElementBuilder.create(name).withTypeText(options.get(name)));
                    }
                }
            }
        }
    }

    private Set<String> getAllKeys(ArrayCreationExpression arrayCreation) {
        Set<String> keys = newHashSet();
        ArrayHashElement[] hashElements = PsiTreeUtil.getChildrenOfType(arrayCreation, ArrayHashElement.class);
        if (hashElements == null) {
            return keys;
        }
        for (ArrayHashElement hashElement : hashElements) {
            keys.add(getContent(hashElement.getKey()));
        }
        return keys;
    }

    private boolean canBecomeKey(PsiElement element) {
        return (PsiTreeUtil.getParentOfType(element, ArrayHashElement.class) == null || PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).accepts(element.getParent()));
    }

    private Integer getParameterIndex(PsiElement[] methodParameters, PsiElement parameterElement) {
        for (int i = 0; i < methodParameters.length; i++) {
            if (methodParameters[i].equals(parameterElement)) {
                return i;
            }
        }
        return null;
    }

    public static String getContent(PsiElement value) {
        if (value instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) value).getContents();
        }
        if (value instanceof PhpExpressionImpl && ((PhpExpressionImpl) value).getType().equals(PhpType.INT)) {
            return value.getText();
        }

        if (value instanceof ConstantReference || value instanceof ClassConstantReference) {
            return value.getText(); //we cannot resolve constant value when this method is called from index because indices are not accessible then.
        }
        return null;
    }
}
