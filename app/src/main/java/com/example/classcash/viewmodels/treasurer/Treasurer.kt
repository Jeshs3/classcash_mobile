package com.example.classcash.viewmodels.treasurer

data class Treasurer(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var className: String = "",
    var profileImageUrl: String = "",
    var errorMessage: String = "",
    var successMessage: String = "",
    var isAuthenticated: Boolean = false
)
