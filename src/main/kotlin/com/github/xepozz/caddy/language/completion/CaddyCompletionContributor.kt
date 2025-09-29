package com.github.xepozz.caddy.language.completion

import com.github.xepozz.caddy.DirectivesDictionary
import com.github.xepozz.caddy.language.CaddyFile
import com.github.xepozz.caddy.language.psi.CaddyBlock
import com.github.xepozz.caddy.language.psi.CaddyDirective
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class CaddyCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(
                    PlatformPatterns.psiElement(CaddyDirective::class.java)
                        .withParent(
                            PlatformPatterns.psiElement(CaddyBlock::class.java)
                                .withAncestor(2, PlatformPatterns.psiElement(CaddyFile::class.java))
                        )
                ),
            DirectiveCompletionProvider()
        )
    }

    private class DirectiveCompletionProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet
        ) {
            val position = parameters.position.originalElement

            result.addAllElements(
                DirectivesDictionary.directives.values
                    .map { directive ->
                        LookupElementBuilder.create(directive.name)
                            .withPresentableText(directive.name)
                            .withIcon(AllIcons.Nodes.Function)
                            .withTypeText(directive.description, true)
                            .withInsertHandler { context, _ ->
                                val editor = context.editor
                                editor.document.insertString(context.tailOffset, " ")
                                editor.caretModel.moveToOffset(context.tailOffset)
                            }
                    }
            )
        }
    }
}