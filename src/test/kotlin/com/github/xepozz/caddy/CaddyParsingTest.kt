package com.github.xepozz.caddy

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CaddyParsingTest : BasePlatformTestCase() {

    private fun parse(text: String): PsiFile = myFixture.configureByText("test.Caddyfile", text)

    private fun assertNoErrors(text: String) {
        val file = parse(text)
        val errors = PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java)
        if (errors.isNotEmpty()) {
            val rendered = errors.joinToString("\n") { "  - ${it.errorDescription} at '${it.text}'" }
            fail("Expected no parse errors, but found:\n$rendered\n\nInput:\n$text")
        }
    }

    // Issue #58: {$ENV_VAR} substitution as the site address.
    fun testEnvVarAsAddress() {
        assertNoErrors(
            """
            {${'$'}TAILSCALE_HOSTNAME} {
                handle_path /hello {
                    reverse_proxy hello:8080
                }
            }
            """.trimIndent()
        )
    }

    // Issue #58: {$ENV_VAR} substitution inside a directive's arguments.
    fun testEnvVarAsArgument() {
        assertNoErrors(
            """
            example.com {
                reverse_proxy {${'$'}UPSTREAMS}
            }
            """.trimIndent()
        )
    }

    // Issue #8: multiple addresses separated by whitespace before the block.
    fun testMultipleAddressesPerBlock() {
        assertNoErrors(
            """
            http://vikunja.home.mydomain.com http://vikunja.mydomain.com {
                reverse_proxy vikunja:3456
            }
            """.trimIndent()
        )
    }

    // Issue #27: regex with parenthesised capture group inside a directive argument.
    fun testRegexWithCaptureGroups() {
        assertNoErrors(
            """
            example.com {
                @api path_regexp api ^/api/(v[0-9]+)/(.*)${'$'}
                handle @api {
                    reverse_proxy backend:8080
                }
            }
            """.trimIndent()
        )
    }

    // Regression: snippet definitions `(name) { ... }` must still parse at the top level.
    fun testSnippetDefinitionStillParses() {
        assertNoErrors(
            """
            (common) {
                encode gzip
                header Server "Caddy"
            }

            example.com {
                import common
                reverse_proxy backend:8080
            }
            """.trimIndent()
        )
    }

    // Regression: an anonymous global options block (just `{ ... }`) must still parse.
    fun testAnonymousGlobalBlockStillParses() {
        assertNoErrors(
            """
            {
                auto_https disable_redirects
                admin off
            }

            example.com {
                respond "hello"
            }
            """.trimIndent()
        )
    }
}
