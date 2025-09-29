package com.github.xepozz.caddy.language.parser

import com.github.xepozz.caddy.language.psi.CaddyTokenSets
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.TokenSet

class CaddyLexerAdapter : MergingLexerAdapter(
    FlexAdapter(CaddyLexer(null)),
    TokenSet.create(CaddyTypes.EOL, CaddyTypes.COMMENT)
)