package com.github.xepozz.caddy.language.psi.impl

import com.github.xepozz.caddy.language.psi.CaddyElementFactory
import com.github.xepozz.caddy.language.psi.CaddyMatcherDefinition
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil

abstract class CaddyMatcherDefinitionBaseImpl : CaddyMatcherDefinition, CaddyElementImpl, PsiNamedElement {
    constructor(node: ASTNode) : super(node)

    fun getNameElement(): PsiElement = this.node.findChildByType(CaddyTypes.MATCHER)?.psi ?: this

    override fun getName(): String? = this.node.findChildByType(CaddyTypes.MATCHER)?.text

    override fun setName(name: @NlsSafe String): PsiElement? {
        val normalizedName = if (name.startsWith("@")) name else "@$name"
        val file = CaddyElementFactory.createFile(project, "example.com {\n    $normalizedName path *\n}\n")
        val matcherDefinition = PsiTreeUtil.findChildOfType(file, CaddyMatcherDefinition::class.java) ?: return this
        val newNameElement = matcherDefinition.node.findChildByType(CaddyTypes.MATCHER)?.psi ?: return this

        getNameElement().replace(newNameElement)
        return this
    }

    override fun getPresentation() = PresentationData(name, null, getIcon(0), null)

    override fun getIcon(flags: Int) = AllIcons.Nodes.Property
}
