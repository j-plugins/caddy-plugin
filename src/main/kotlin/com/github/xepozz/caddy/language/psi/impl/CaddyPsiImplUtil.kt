package com.github.xepozz.caddy.language.psi.impl

import com.github.xepozz.caddy.language.psi.CaddyAddress
import com.github.xepozz.caddy.language.psi.CaddyHeredoc
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.tree.TokenSet

class CaddyPsiImplUtil {
    companion object {
        @JvmStatic
        fun getValue(element: CaddyAddress): String? = element.text

        @JvmStatic
        fun getValue(element: CaddyHeredoc): String? =
            element.node
                .getChildren(TokenSet.create(CaddyTypes.HEREDOC_CONTENT))
                .joinToString("") { it.text }
                .trimIndent()

        @JvmStatic
        fun getReferences(element: PsiElement): Array<PsiReference> =
            ReferenceProvidersRegistry.getReferencesFromProviders(element)
    }
}
