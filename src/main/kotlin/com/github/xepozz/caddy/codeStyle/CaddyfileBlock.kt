package com.github.xepozz.caddy.codeStyle

import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.Indent
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.formatting.WrapType
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock

class CaddyfileBlock(
    node: ASTNode,
    wrap: Wrap,
    alignment: Alignment,
    private val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {
    companion object {
        val skipTokens = setOf(TokenType.WHITE_SPACE, CaddyTypes.EOL)
    }

    protected override fun buildChildren() = buildList {
        var child = myNode.firstChildNode
        while (child != null) {
            if (!skipTokens.contains(child.elementType)) {
                val block = CaddyfileBlock(
                    child,
                    Wrap.createWrap(WrapType.NONE, false),
                    Alignment.createAlignment(),
                    spacingBuilder,
                )
                add(block)
            }
            child = child.treeNext
        }
    }

    override fun getIndent() = when (this.node.elementType) {
        CaddyTypes.DIRECTIVE,
        CaddyTypes.MATCHER_DEFINITION -> Indent.getIndent(Indent.Type.NORMAL, true, true)

        else -> Indent.getNoneIndent()
    }

    override fun getSpacing(child1: Block?, child2: Block) = spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf() = myNode.firstChildNode == null
}