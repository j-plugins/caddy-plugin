package com.github.xepozz.caddy.documentation

import com.github.xepozz.caddy.DirectivesDictionary
import com.github.xepozz.caddy.language.psi.CaddyDirective
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.markdown.utils.lang.CodeBlockHtmlSyntaxHighlighter
import com.intellij.psi.PsiElement

class CaddyDocumentationProvider : AbstractDocumentationProvider() {
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element !is CaddyDirective) return null

        val directiveName = element.name!!
        val doc = DirectivesDictionary.getDocumentation(directiveName) ?: return null

        return doc.description
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element !is CaddyDirective) return null

        val directiveName = element.name!!
        val doc = DirectivesDictionary.getDocumentation(directiveName) ?: return null

        return buildString {
            append("<div class='definition'><pre>")
            append(doc.name)
            append("</pre></div>")

            append("<div class='content'>")
            append("<p>").append(doc.description).append("</p>")

            if (doc.syntax != null) {
                append("<h3>Syntax</h3>")
                append("<pre>").append(doc.syntax).append("</pre>")
            }

            if (doc.examples != null) {
                append("<h3>Examples</h3>")
                append("<pre>").append(
                    CodeBlockHtmlSyntaxHighlighter(project = element.project)
                        .color("Caddyfile", doc.examples)
                ).append("</pre>")
            }

            append("<p><a href='https://caddyserver.com/docs/caddyfile/directives/")
            append(doc.name)
            append("'>Official Documentation</a></p>")
            append("</div>")
        }
    }
}