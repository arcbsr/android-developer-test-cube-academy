package com.cube.cubeacademy.lib.domain.remote

import com.cube.cubeacademy.lib.repository.Repository
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UseCase @Inject constructor(private val repository: Repository) {

    suspend fun getAllNominations(): Flow<ResponseWrapper<List<Nomination>>> {
        return repository.getAllNominations().map {
            when (it) {
                is ResponseWrapper.GenericError -> {
                    ResponseWrapper.GenericError(it.code, it.error)
                }

                ResponseWrapper.NetworkError -> {
                    ResponseWrapper.NetworkError
                }

                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value)
                }
            }
        }
    }

    suspend fun getAllNominees(): Flow<ResponseWrapper<List<Nominee>>> {
        return repository.getAllNominees().map {
            when (it) {
                is ResponseWrapper.GenericError -> {
                    ResponseWrapper.GenericError(it.code, it.error)
                }

                ResponseWrapper.NetworkError -> {
                    ResponseWrapper.NetworkError
                }

                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value)
                }
            }
        }

    }

    suspend fun createNomination(
        nomineeId: String,
        reason: String,
        process: String
    ): Flow<ResponseWrapper<Nomination>> {
        return repository.createNomination(nomineeId, reason, process).map {
            when (it) {
                is ResponseWrapper.GenericError -> {
                    ResponseWrapper.GenericError(it.code, it.error)
                }

                ResponseWrapper.NetworkError -> {
                    ResponseWrapper.NetworkError
                }

                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value)
                }
            }
        }

    }
}