package com.mortitech.treasuryflow.feature.example.data.local

import com.mortitech.treasuryflow.feature.example.data.remote.dto.ExampleResponse
import com.mortitech.treasuryflow.feature.example.domain.model.Example

fun ExampleEntity.toDomain(): Example = Example(
    id = id,
    title = title,
    description = description,
    status = status,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Example.toEntity(): ExampleEntity = ExampleEntity(
    id = id,
    title = title,
    description = description,
    status = status,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ExampleResponse.toEntity(): ExampleEntity = ExampleEntity(
    id = id,
    title = title,
    description = description,
    status = status,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
