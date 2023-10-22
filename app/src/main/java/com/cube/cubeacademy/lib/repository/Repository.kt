package com.cube.cubeacademy.lib.repository

import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.ResponseWrapper
import kotlinx.coroutines.flow.Flow


interface Repository {
    // TODO: Add additional code if you need it

    suspend fun getAllNominations(): Flow<ResponseWrapper<List<Nomination>>>

    suspend fun getAllNominees(): Flow<ResponseWrapper<List<Nominee>>>
    suspend fun createNomination(
        nomineeId: String,
        reason: String,
        process: String
    ): Flow<ResponseWrapper<Nomination>>
}