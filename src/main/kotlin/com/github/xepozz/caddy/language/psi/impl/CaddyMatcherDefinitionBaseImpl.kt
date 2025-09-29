package com.github.xepozz.caddy.language.psi.impl//package com.github.xepozz.gitcodeowners.language.psi.impl

import com.github.xepozz.caddy.language.psi.CaddyDirective
import com.github.xepozz.caddy.language.psi.CaddyMatcherDefinition
import com.github.xepozz.caddy.language.psi.CaddyTypes
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement

abstract class CaddyMatcherDefinitionBaseImpl : CaddyMatcherDefinition, CaddyElementImpl, PsiNamedElement {
    constructor(node: ASTNode) : super(node)

    fun getNameElement(): PsiElement = this.node.findChildByType(CaddyTypes.IDENTIFIER)!!.psi

    override fun getName(): String? = this.node.findChildByType(CaddyTypes.MATCHER)?.text

    override fun setName(name: @NlsSafe String): PsiElement? {
        TODO("Not yet implemented")
    }

    override fun getPresentation() = PresentationData(text, null, getIcon(0), null)

    override fun getIcon(flags: Int) = AllIcons.Nodes.Property
//    override fun getReference(): PsiReference? {
//        println("getReference $text")
//        return super.getReference()
//    }
//    override fun canNavigate() = true
//    override fun getNavigationElement(): PsiElement {
//        println("getNavigationElement $text")
//
//        return super.getNavigationElement()
//    }
//    override fun navigationRequest(): NavigationRequest? {
//        println("navigationRequest $text")
//        val index = FilenameIndex.getVirtualFilesByName(text, GlobalSearchScope.projectScope(project))
//        val file = index.toList().firstOrNull() ?: return null
//        return NavigationRequest.sourceNavigationRequest(project, file, 0)
//    }

//    override fun getNavigationElement(): PsiElement {
//        val index = FilenameIndex.getVirtualFilesByName(text, GlobalSearchScope.projectScope(project))
//        return index.toList().firstOrNull()
//    }
}