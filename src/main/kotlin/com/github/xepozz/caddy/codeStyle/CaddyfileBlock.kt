package com.github.xepozz.caddy.codeStyle

import com.github.xepozz.caddy.language.parser.CaddyParserDefinition
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.formatting.ASTBlock
import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.ChildAttributes
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
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
        private val NO_SPACE: Spacing = Spacing.createSpacing(0, 0, 0, false, 0)
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

    override fun getIndent() = when {
        myNode.elementType == CaddyTypes.DIRECTIVE ||
            myNode.elementType == CaddyTypes.MATCHER_DEFINITION -> Indent.getNormalIndent()
        // A bare COMMENT lives at FILE level (top-of-file note) OR inside a
        // BLOCK (commenting a directive). Only the latter needs indenting.
        myNode.elementType == CaddyTypes.COMMENT &&
            myNode.treeParent?.elementType == CaddyTypes.BLOCK -> Indent.getNormalIndent()
        else -> Indent.getNoneIndent()
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        // Caddy treats tokens with no whitespace between them as a single
        // argument (e.g. `{path}/`, `{$VAR}:8080`). Even though our grammar
        // splits them into env_value + simple_value, the formatter must NOT
        // insert space between them — that would corrupt working configs.
        val a = child1 as? ASTBlock
        val b = child2 as? ASTBlock
        if (a != null && b != null) {
            val end = a.node?.textRange?.endOffset
            val start = b.node?.textRange?.startOffset
            if (end != null && start != null && end == start) {
                return NO_SPACE
            }
        }
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    // When the user presses Enter, IntelliJ asks the enclosing block where
    // to put the caret. Inside a BLOCK (a site block, a nested matcher
    // definition, or a directive's own block via flattening), the new line
    // must land at the directives' indent level — column 0 was the bug.
    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val containsBlock = when (myNode.elementType) {
            CaddyTypes.BLOCK, CaddyTypes.DIRECTIVE, CaddyTypes.MATCHER_DEFINITION -> true
            else -> false
        }
        return if (containsBlock) ChildAttributes(Indent.getNormalIndent(), null)
        else ChildAttributes(Indent.getNoneIndent(), null)
    }

    override fun isLeaf() = myNode.firstChildNode == null
}