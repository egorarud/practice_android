package com.example.practice.domain.model

data class Profile(
    val fullName: String = "",
    val avatarUri: String = "", // content:// or file:// uri as string
    val resumeUrl: String = "",
    val position: String = "" // optional extra field
)



