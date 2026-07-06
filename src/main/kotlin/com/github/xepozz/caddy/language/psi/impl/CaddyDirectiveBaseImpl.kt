package com.github.xepozz.caddy.language.psi.impl

import com.github.xepozz.caddy.language.psi.CaddyDirective
import com.github.xepozz.caddy.language.psi.CaddyElementFactory
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil

abstract class CaddyDirectiveBaseImpl : CaddyDirective, CaddyElementImpl, PsiNamedElement {
    constructor(node: ASTNode) : super(node)

    fun getNameElement(): PsiElement = this.node.findChildByType(CaddyTypes.IDENTIFIER)?.psi ?: this

    override fun getName(): String? = this.node.findChildByType(CaddyTypes.IDENTIFIER)?.text

    override fun setName(name: @NlsSafe String): PsiElement? {
        val file = CaddyElementFactory.createFile(project, "example.com {\n    $name\n}\n")
        val directive = PsiTreeUtil.findChildOfType(file, CaddyDirective::class.java) ?: return this
        val newNameElement = directive.node.findChildByType(CaddyTypes.IDENTIFIER)?.psi ?: return this

        getNameElement().replace(newNameElement)
        return this
    }

    override fun getPresentation() = PresentationData(name, null, getIcon(0), null)

    override fun getIcon(flags: Int) = AllIcons.Nodes.Property
}
