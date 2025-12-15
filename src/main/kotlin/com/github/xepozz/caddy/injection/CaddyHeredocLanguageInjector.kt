package com.github.xepozz.caddy.injection

import com.github.xepozz.caddy.language.psi.CaddyHeredoc
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet

class CaddyHeredocLanguageInjector : MultiHostInjector {
    override fun elementsToInjectIn() = mutableListOf(CaddyHeredoc::class.java)

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
//        println("getLanguagesToInject $context")
        if (context !is CaddyHeredoc) {
//            println("PsiElement is not a CaddyHeredoc")
            return
        }
//        println("children = $childrenNodes")

        val heredocStart = context.node.firstChildNode

        val marker = extractMarkerFromHeredocStart(heredocStart)
        val language = findLanguageByMarker(marker)

//        println("Marker: $marker, language: $language")
        if (language == null || Language.ANY == language) {
//            println("Language not found: $heredocStart, $context")
            return
        }

        val contentElements = collectHeredocContent(context)

        registrar.startInjecting(language)

        for (content in contentElements) {
            registrar.addPlace(
                null,
                null,
                context,
                content.textRangeInParent
            )
        }

        registrar.doneInjecting()
    }

    private fun findLanguageByMarker(marker: String) =
        Language.findLanguageByID(marker)
            ?: Language.getRegisteredLanguages()
                .find {
                    it.displayName == marker || it.id.lowercase() == marker.lowercase()
                }

    private fun extractMarkerFromHeredocStart(heredocStart: ASTNode) = heredocStart.text.substring(2).trimEnd()

    private fun collectHeredocContent(heredocStart: CaddyHeredoc): Collection<PsiElement> =
        heredocStart.node
            .getChildren(TokenSet.create(CaddyTypes.HEREDOC_CONTENT))
            .mapNotNull { it.psi }
            .toList()
}
