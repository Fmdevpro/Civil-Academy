package com.fmdev.civilacademy.presentation.navigation

import kotlinx.serialization.Serializable

interface Route {
    val route: String?
}

@Serializable
object LoginNavigation: Route {
    override val route = this::class.qualifiedName
}