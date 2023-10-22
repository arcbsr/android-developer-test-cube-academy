package com.cube.cubeacademy.lib.repository

import com.cube.cubeacademy.lib.data.DataSource
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RepositoryImpl @Inject constructor(private val remoteDataSource: DataSource) : Repository {
    // TODO: Add additional code if you need it

    override suspend fun getAllNominations(): Flow<ResponseWrapper<List<Nomination>>> {
        return flow {
            emit(remoteDataSource.getNominationList())
        }
    }

    override suspend fun getAllNominees(): Flow<ResponseWrapper<List<Nominee>>> {
        return flow {
            emit(remoteDataSource.getNomineeList())
        }
    }

    override suspend fun createNomination(
        nomineeId: String,
        reason: String,
        process: String
    ): Flow<ResponseWrapper<Nomination>> {
        return flow {
            emit(remoteDataSource.createNomination(nomineeId, reason, process))
        }
    }
}