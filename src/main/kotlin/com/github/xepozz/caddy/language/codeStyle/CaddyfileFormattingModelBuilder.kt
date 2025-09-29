package com.github.xepozz.caddy.language.codeStyle

import com.github.xepozz.caddy.language.CaddyFile
import com.github.xepozz.caddy.language.CaddyFileType
import com.github.xepozz.caddy.language.CaddyLanguage
import com.github.xepozz.caddy.language.parser.CaddyParserDefinition
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.formatting.Alignment
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.formatting.WrapType
import com.intellij.psi.codeStyle.CodeStyleSettings

class CaddyfileFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext) = FormattingModelProvider
        .createFormattingModelForPsiFile(
            formattingContext.containingFile,
            CaddyfileBlock(
                formattingContext.node,
                Wrap.createWrap(WrapType.NONE, false),
                Alignment.createAlignment(true),
                createSpaceBuilder(formattingContext.codeStyleSettings)
            ),
            formattingContext.codeStyleSettings,
        )

    companion object {
        private fun createSpaceBuilder(settings: CodeStyleSettings) =
            SpacingBuilder(settings, CaddyLanguage.INSTANCE)
                .after(CaddyTypes.BLOCK_NAME)
                .spaces(1)
                .before(CaddyTypes.ARGUMENT)
                .spaces(1)
                .aroundInside(CaddyTypes.BLOCK, CaddyParserDefinition.FILE)
                .blankLines(1)
    }
}