package com.mortitech.treasuryflow.feature.example.domain.model

data class Example(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
)
