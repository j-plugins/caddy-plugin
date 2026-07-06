package com.github.xepozz.caddy

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CaddyCompletionTest : BasePlatformTestCase() {

    fun testDirectiveCompletionInsideSiteBlock() {
        myFixture.configureByText(
            "test.Caddyfile",
            """
            example.com {
                ro<caret>
            }
            """.trimIndent()
        )

        val variants = myFixture
            .complete(CompletionType.BASIC)
            .map { it.lookupString }

        assertTrue("Expected directive completion to include 'root'. Got: $variants", variants.contains("root"))
    }
}
