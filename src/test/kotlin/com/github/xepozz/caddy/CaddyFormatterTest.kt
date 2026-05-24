package com.github.xepozz.caddy

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CaddyFormatterTest : BasePlatformTestCase() {

    private fun reformat(source: String): String {
        val file = myFixture.configureByText("test.Caddyfile", source)
        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(file)
        }
        return file.text
    }

    // Issue #61: a region wrapped in `# @formatter:off` / `# @formatter:on`
    // must be left byte-identical by the formatter.
    fun testFormatterOffIsRespected() {
        val protected = """
            # @formatter:off
            {
            log access-file {
            output file /var/log/caddy/access.log {
            roll_size 100MiB
            }
            }
            }
            # @formatter:on
        """.trimIndent()

        assertEquals(
            "Content between @formatter:off and @formatter:on must be untouched.",
            protected,
            reformat(protected),
        )

        // Sanity check: without the tags, the same content WOULD be reformatted —
        // otherwise the previous assertion is vacuous.
        val unprotected = """
            {
            log access-file {
            output file /var/log/caddy/access.log {
            roll_size 100MiB
            }
            }
            }
        """.trimIndent()
        assertTrue(
            "Without @formatter:off, the formatter must change the input — " +
                "otherwise the @formatter:off test proves nothing.",
            unprotected != reformat(unprotected),
        )
    }

    // Issue #61: `# @formatter:off` placed INSIDE a site block must also be honored.
    fun testFormatterOffInsideBlock() {
        // Deliberately-wrong indentation inside @formatter:off — single space, not 4.
        // If the formatter respects @formatter:off, it must NOT correct it.
        val source = """
            example.com {
                # @formatter:off
             log access-file {
             output file /var/log/caddy/access.log
             }
                # @formatter:on
                reverse_proxy backend:8080
            }
        """.trimIndent()

        val formatted = reformat(source)
        assertTrue(
            "The single-space indent inside @formatter:off must be preserved.\n" +
                "Formatted:\n$formatted",
            formatted.contains("\n log access-file {\n") &&
                formatted.contains("\n output file ") &&
                formatted.contains("\n }\n"),
        )
        // And the lines outside @formatter:off must still be reformatted properly.
        assertTrue(
            "reverse_proxy outside @formatter:off must be at 4 spaces indent.\n" +
                "Formatted:\n$formatted",
            formatted.contains("\n    reverse_proxy backend:8080\n"),
        )
    }

    // Issue #61 follow-up: a comment INSIDE a site block must get the same
    // indent as the surrounding directives — not slide back to column 0.
    fun testCommentInsideBlockGetsIndented() {
        val source = """
            example.com:443 {
                # Application Root
                root * /var/www/pterodactyl/
            }
        """.trimIndent()

        val formatted = reformat(source)
        val lines = formatted.lines()
        val commentLine = lines.first { it.contains("# Application Root") }
        val rootLine = lines.first { it.contains("root *") }
        assertEquals(
            "Comment must keep the same indent as the sibling directive.\nFormatted:\n$formatted",
            rootLine.takeWhile { it == ' ' }.length,
            commentLine.takeWhile { it == ' ' }.length,
        )
    }

    // Issue #61: nested directives should not accumulate indentation per nesting level.
    fun testNestedBlockIndentationDoesNotAccumulate() {
        val source = """
            {
            log access-file {
            output file /var/log/caddy/access.log {
            roll_size 100MiB
            roll_keep 2
            }
            }
            }
        """.trimIndent()

        val formatted = reformat(source)

        val lines = formatted.lines()
        val rollSizeLine = lines.first { it.contains("roll_size") }
        val outputLine = lines.first { it.contains("output file") }
        val logLine = lines.first { it.contains("log access-file") }

        val logIndent = logLine.takeWhile { it == ' ' }.length
        val outputIndent = outputLine.takeWhile { it == ' ' }.length
        val rollSizeIndent = rollSizeLine.takeWhile { it == ' ' }.length

        // Expect a constant step (one level deeper per nested block), not accumulation
        // that depends on the column where the opening brace appeared.
        val firstStep = outputIndent - logIndent
        val secondStep = rollSizeIndent - outputIndent

        assertTrue(
            "Indentation step should be > 0 (got $firstStep / $secondStep)\nFormatted:\n$formatted",
            firstStep > 0 && secondStep > 0,
        )
        assertEquals(
            "Each nesting level should add the same indent. Got $firstStep then $secondStep.\nFormatted:\n$formatted",
            firstStep,
            secondStep,
        )
        // Sanity bound: a 3-level Caddyfile should never reach the 20+ columns seen in issue #61.
        assertTrue(
            "rollSize indent should stay reasonable, got $rollSizeIndent.\nFormatted:\n$formatted",
            rollSizeIndent <= 16,
        )
    }

    // Issue #61: a real 2-level Caddyfile must get the expected 4-space step
    // per nesting level — `reverse_proxy` at 4 spaces, `output file` at 8.
    fun testTwoLevelIndentation() {
        val source = """
            example.com {
            reverse_proxy /api/* backend:8080
            log {
            output file /var/log/caddy.log
            }
            }
        """.trimIndent()

        val formatted = reformat(source)
        val lines = formatted.lines()
        val reverseProxyLine = lines.first { it.contains("reverse_proxy") }
        val outputLine = lines.first { it.contains("output file") }

        assertEquals("reverse_proxy should be indented 4 spaces", 4, reverseProxyLine.takeWhile { it == ' ' }.length)
        assertEquals("output file should be indented 8 spaces", 8, outputLine.takeWhile { it == ' ' }.length)
    }
}
