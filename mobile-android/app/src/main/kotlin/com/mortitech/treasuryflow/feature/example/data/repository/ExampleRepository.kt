package com.mortitech.treasuryflow.feature.example.data.repository

import com.mortitech.treasuryflow.core.common.AppResult
import com.mortitech.treasuryflow.core.common.appResultOf
import com.mortitech.treasuryflow.core.model.PagedData
import com.mortitech.treasuryflow.core.network.ApiService
import com.mortitech.treasuryflow.feature.example.data.remote.dto.CreateExampleRequest
import com.mortitech.treasuryflow.feature.example.data.remote.dto.UpdateExampleRequest
import com.mortitech.treasuryflow.feature.example.domain.model.Example
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExampleRepository @Inject constructor(
    private val apiService: ApiService,
) {

    suspend fun list(page: Int = 0, size: Int = 20): AppResult<PagedData<Example>> =
        appResultOf {
            val response = apiService.listExamples(page = page, size = size)
            PagedData(
                content = response.content.map { it.toDomain() },
                page = response.page,
                size = response.size,
                totalElements = response.totalElements,
                totalPages = response.totalPages,
            )
        }

    suspend fun getById(id: String): AppResult<Example> =
        appResultOf {
            apiService.getExample(id).toDomain()
        }

    suspend fun create(title: String, description: String?): AppResult<Example> =
        appResultOf {
            apiService.createExample(CreateExampleRequest(title, description)).toDomain()
        }

    suspend fun update(id: String, title: String?, description: String?, status: String?): AppResult<Example> =
        appResultOf {
            apiService.updateExample(id, UpdateExampleRequest(title, description, status)).toDomain()
        }

    suspend fun delete(id: String): AppResult<Unit> =
        appResultOf {
            apiService.deleteExample(id)
        }
}
