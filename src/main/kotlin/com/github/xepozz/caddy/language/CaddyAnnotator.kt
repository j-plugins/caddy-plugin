package com.github.xepozz.caddy.language

import com.github.xepozz.caddy.language.psi.CaddyArgument
import com.github.xepozz.caddy.language.psi.CaddyBlock
import com.github.xepozz.caddy.language.psi.impl.CaddyDirectiveImpl
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement

class CaddyAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is CaddyDirectiveImpl -> {
                val nameElement = element.getNameElement()
                when {
                    nameElement.text.startsWith("@") -> {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(nameElement)
                            .textAttributes(REFERENCE_DECLARATION_HIGHLIGHT)
                            .create()
                    }

                    else -> {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(nameElement)
                            .textAttributes(IDENTIFIER_HIGHLIGHT)
                            .create()
                    }
                }
            }

            is CaddyArgument -> {
                val text = element.simpleValue?.text ?: ""
                if (text.startsWith("@")) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(element.simpleValue!!)
                        .textAttributes(REFERENCE_USAGE_HIGHLIGHT)
                        .create()
                }
            }

            is CaddyBlock -> {
                element.blockName?.apply {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(this)
                        .textAttributes(BLOCK_NAME_HIGHLIGHT)
                        .create()
                    }
            }
        }
    }

    companion object {
        val PATTERN_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
            "CADDY_PATTERN",
            DefaultLanguageHighlighterColors.STRING,
        )
        val REFERENCE_DECLARATION_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
            "CADDY_DIRECTIVE",
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION,
        )
        val REFERENCE_USAGE_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
            "CADDY_ARGUMENT",
            DefaultLanguageHighlighterColors.FUNCTION_CALL,
        )
        val BLOCK_NAME_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
            "CADDY_BLOCK_NAME",
            DefaultLanguageHighlighterColors.CLASS_NAME,
        )
        private val IDENTIFIER_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
            "CADDY_IDENTIFIER",
            DefaultLanguageHighlighterColors.KEYWORD,
        )
    }
}