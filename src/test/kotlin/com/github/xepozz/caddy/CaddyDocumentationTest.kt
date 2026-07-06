package com.github.xepozz.caddy

import com.github.xepozz.caddy.documentation.CaddyDocumentationProvider
import com.github.xepozz.caddy.language.psi.CaddyDirective
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CaddyDocumentationTest : BasePlatformTestCase() {

    fun testDirectiveSyntaxIsEscapedInDocumentationHtml() {
        val file = myFixture.configureByText(
            "test.Caddyfile",
            """
            example.com {
                root * /var/www/html
            }
            """.trimIndent()
        )
        val directive = PsiTreeUtil.findChildOfType(file, CaddyDirective::class.java)!!
        val html = CaddyDocumentationProvider().generateDoc(directive, directive)!!

        assertTrue(html.contains("&lt;matcher&gt;"))
        assertTrue(html.contains("&lt;path&gt;"))
        assertFalse(html.contains("[<matcher>]"))
    }
}
