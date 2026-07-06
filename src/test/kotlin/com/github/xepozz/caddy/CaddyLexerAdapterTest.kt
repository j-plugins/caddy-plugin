package com.github.xepozz.caddy

import com.github.xepozz.caddy.language.parser.CaddyLexerAdapter
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CaddyLexerAdapterTest : BasePlatformTestCase() {

    fun testResetClearsCustomStateStack() {
        val lexer = CaddyLexerAdapter()
        lexer.start("{\n{", 0, 3, 0)
        while (lexer.tokenType != null) {
            lexer.advance()
        }

        lexer.start("}", 0, 1, 0)

        assertEquals(CaddyTypes.RBRACE, lexer.tokenType)
        assertEquals(0, lexer.state)
    }
}
