package com.github.xepozz.caddy

import com.github.xepozz.caddy.language.psi.CaddyDirective
import com.github.xepozz.caddy.language.psi.CaddyHeredoc
import com.github.xepozz.caddy.language.psi.CaddyMatcherDefinition
import com.github.xepozz.caddy.language.psi.impl.CaddyMatcherDefinitionBaseImpl
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CaddyPsiBehaviorTest : BasePlatformTestCase() {

    private fun parse(text: String): PsiFile = myFixture.configureByText("test.Caddyfile", text)

    fun testHeredocValueIncludesAllContentLines() {
        val file = parse(
            """
            example.com {
                respond <<TEXT
                first
                second
                TEXT
            }
            """.trimIndent()
        )

        val heredoc = PsiTreeUtil.findChildOfType(file, CaddyHeredoc::class.java)!!

        assertEquals("first\nsecond", heredoc.value)
    }

    fun testHeredocContentChangePreservesMarkers() {
        val file = parse(
            """
            example.com {
                respond <<TEXT
                first
                TEXT
            }
            """.trimIndent()
        )
        val heredoc = PsiTreeUtil.findChildOfType(file, CaddyHeredoc::class.java)!!
        var updated: CaddyHeredoc? = null

        WriteCommandAction.runWriteCommandAction(project) {
            updated = ElementManipulators.handleContentChange(
                heredoc,
                "    changed\n    again\n"
            ) as CaddyHeredoc
        }

        assertEquals("changed\nagain", updated!!.value)
        assertTrue(file.text.contains("respond <<TEXT"))
        assertTrue(file.text.contains("    changed\n    again\n"))
        assertTrue(file.text.contains("\n    TEXT"))
    }

    fun testMatcherDefinitionNameElementAndRename() {
        val file = parse(
            """
            example.com {
                @api path /api/*
                reverse_proxy @api backend:8080
            }
            """.trimIndent()
        )
        val matcher = PsiTreeUtil.findChildOfType(file, CaddyMatcherDefinition::class.java)!!

        assertEquals("@api", matcher.name)
        assertEquals("@api", (matcher as CaddyMatcherDefinitionBaseImpl).getNameElement().text)

        WriteCommandAction.runWriteCommandAction(project) {
            matcher.setName("static")
        }

        assertEquals("@static", matcher.name)
        assertTrue(file.text.contains("@static path /api/*"))
    }

    fun testDirectiveRenameReplacesOnlyNameToken() {
        val file = parse(
            """
            example.com {
                root * /var/www/html
            }
            """.trimIndent()
        )
        val directive = PsiTreeUtil.findChildOfType(file, CaddyDirective::class.java)!!

        WriteCommandAction.runWriteCommandAction(project) {
            directive.setName("respond")
        }

        assertEquals("respond", directive.name)
        assertTrue(file.text.contains("respond * /var/www/html"))
    }

    fun testDirectiveTextRemainsFullPsiText() {
        val file = parse(
            """
            example.com {
                root * /var/www/html
            }
            """.trimIndent()
        )
        val directive = PsiTreeUtil.findChildOfType(file, CaddyDirective::class.java)!!

        assertEquals("root", directive.name)
        assertEquals("root * /var/www/html", directive.text)
    }
}
