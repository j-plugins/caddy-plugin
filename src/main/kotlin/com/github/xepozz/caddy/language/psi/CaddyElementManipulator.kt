package com.github.xepozz.caddy.language.psi

import com.github.xepozz.caddy.language.psi.impl.CaddyElementImpl
import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException

class CaddyElementManipulator : AbstractElementManipulator<CaddyElementImpl>() {

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(entry: CaddyElementImpl, range: TextRange, newContent: String): CaddyElementImpl {
        val replacementText = range.replace(entry.text, newContent)
        val file = when (entry) {
            is CaddyHeredoc -> CaddyElementFactory
                .createFile(entry.project, "example.com {\n    respond $replacementText\n}\n")

            else -> CaddyElementFactory.createFile(entry.project, replacementText)
        }

        val newEntry = when (entry) {
            is CaddyHeredoc -> PsiTreeUtil.findChildOfType(file, CaddyHeredoc::class.java)
            else -> PsiTreeUtil.findChildOfType(file, CaddyElementImpl::class.java)
        }

        return when (newEntry) {
            null -> entry
            else -> entry.replace(newEntry) as CaddyElementImpl
        }
    }

    override fun getRangeInElement(element: CaddyElementImpl): TextRange {
        if (element is CaddyHeredoc) {
            val contentNodes = element.node.getChildren(TokenSet.create(CaddyTypes.HEREDOC_CONTENT))
            if (contentNodes.isNotEmpty()) {
                val first = contentNodes.first()
                val last = contentNodes.last()
                return TextRange(first.startOffsetInParent, last.startOffsetInParent + last.textLength)
            }
        }

        return TextRange(0, element.textLength)
    }
}
