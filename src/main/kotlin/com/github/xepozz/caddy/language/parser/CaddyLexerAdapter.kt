package com.github.xepozz.caddy.language.parser

import com.intellij.lexer.FlexAdapter

class CaddyLexerAdapter private constructor(
    private val caddyLexer: CaddyLexer
) : FlexAdapter(caddyLexer) {

    constructor() : this(CaddyLexer(null))

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        caddyLexer.resetCustomState(initialState)
        super.start(buffer, startOffset, endOffset, initialState)
    }
}
