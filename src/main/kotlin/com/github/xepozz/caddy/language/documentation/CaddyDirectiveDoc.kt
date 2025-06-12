package com.github.xepozz.caddy.language.documentation

data class CaddyDirectiveDoc(
    val name: String,
    val description: String,
    val syntax: String? = null,
    val examples: String? = null
)