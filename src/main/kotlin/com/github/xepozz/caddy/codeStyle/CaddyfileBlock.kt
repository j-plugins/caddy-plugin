package com.github.xepozz.caddy.codeStyle

import com.github.xepozz.caddy.language.parser.CaddyParserDefinition
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

    // Issue #61: a BLOCK PSI node opens with `{` mid-line, so IntelliJ would
    // anchor children's indent to that column. We flatten BLOCK children up
    // into the surrounding formatter block so every nested directive gets a
    // single NORMAL step instead of accumulating from the column of `{`.
    protected override fun buildChildren() = buildList { appendChildren(myNode, this) }

    private fun appendChildren(parent: ASTNode, target: MutableList<Block>) {
        var child = parent.firstChildNode
        while (child != null) {
            when {
                skipTokens.contains(child.elementType) -> {}
                child.elementType == CaddyTypes.BLOCK && parent.elementType != CaddyParserDefinition.FILE -> {
                    appendChildren(child, target)
                }
                else -> target.add(
                    CaddyfileBlock(
                        child,
                        Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(),
                        spacingBuilder,
                    )
                )
            }
            child = child.treeNext
        }
    }

    override fun getIndent() = when (this.node.elementType) {
        CaddyTypes.DIRECTIVE,
        CaddyTypes.MATCHER_DEFINITION -> Indent.getNormalIndent()

        else -> Indent.getNoneIndent()
    }

    override fun getSpacing(child1: Block?, child2: Block) = spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf() = myNode.firstChildNode == null
}