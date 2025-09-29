package com.github.xepozz.caddy.language

import com.github.xepozz.caddy.language.parser.CaddyLexerAdapter
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class CaddySyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer() = CaddyLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType) = when (tokenType) {
        CaddyTypes.NUMBER -> NUMBER_KEYS
//        CaddyTypes.TEXT -> TEXT_KEYS
        CaddyTypes.COMMENT -> COMMENT_KEYS
        CaddyTypes.MATCHER -> MATCHER_KEYS
        CaddyTypes.HEREDOC_START, CaddyTypes.HEREDOC_END -> HEREDOC_MARKER_KEYS
        CaddyTypes.HEREDOC_CONTENT -> HEREDOC_CONTENT_KEYS
        TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
        else -> EMPTY_KEYS
    }

    companion object {
        private val BAD_CHAR_KEYS = arrayOf(
            HighlighterColors.BAD_CHARACTER,
        )

        private val COMMENT_KEYS = arrayOf(
            DefaultLanguageHighlighterColors.DOC_COMMENT
        )
        private val MATCHER_KEYS = arrayOf(
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
        )
        private val TEXT_KEYS = arrayOf(
            DefaultLanguageHighlighterColors.STRING
        )
        private val NUMBER_KEYS = arrayOf(
            DefaultLanguageHighlighterColors.NUMBER
        )
        private val HEREDOC_MARKER_KEYS = arrayOf(
            DefaultLanguageHighlighterColors.KEYWORD
        )
        private val HEREDOC_CONTENT_KEYS = arrayOf(
            DefaultLanguageHighlighterColors.STRING
        )
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}
