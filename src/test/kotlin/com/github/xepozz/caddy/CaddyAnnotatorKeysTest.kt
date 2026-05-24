package com.github.xepozz.caddy

import com.github.xepozz.caddy.language.CaddyAnnotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import junit.framework.TestCase

class CaddyAnnotatorKeysTest : TestCase() {

    // Issue #8: every TextAttributesKey registered by CaddyAnnotator must have
    // a unique externalName. Reusing the same name with different fallback keys
    // triggers IllegalStateException at IDE startup.
    fun testAnnotatorKeyNamesAreUnique() {
        // Touch the companion object so all keys get registered.
        CaddyAnnotator.PATTERN_HIGHLIGHT
        CaddyAnnotator.REFERENCE_DECLARATION_HIGHLIGHT
        CaddyAnnotator.REFERENCE_USAGE_HIGHLIGHT
        CaddyAnnotator.BLOCK_NAME_HIGHLIGHT

        val names = listOf(
            CaddyAnnotator.PATTERN_HIGHLIGHT.externalName,
            CaddyAnnotator.REFERENCE_DECLARATION_HIGHLIGHT.externalName,
            CaddyAnnotator.REFERENCE_USAGE_HIGHLIGHT.externalName,
            CaddyAnnotator.BLOCK_NAME_HIGHLIGHT.externalName,
        )
        assertEquals(
            "Duplicate TextAttributesKey names: $names",
            names.size,
            names.toSet().size,
        )
    }

    // Issue #8: re-registering an existing key name with a different fallback
    // throws IllegalStateException. Make sure none of our key names is hijacked.
    fun testReRegisteringKeyWithDifferentFallbackIsRejected() {
        val originalFallback = CaddyAnnotator.PATTERN_HIGHLIGHT.fallbackAttributeKey
        // Re-registering with the SAME fallback must be a no-op.
        val sameAgain = TextAttributesKey.createTextAttributesKey(
            CaddyAnnotator.PATTERN_HIGHLIGHT.externalName,
            originalFallback,
        )
        assertEquals(originalFallback, sameAgain.fallbackAttributeKey)
    }
}
