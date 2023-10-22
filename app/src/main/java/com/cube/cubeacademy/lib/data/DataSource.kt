package com.cube.cubeacademy.lib.data

import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.di.IoDispatcher
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.ResponseWrapper
import com.cube.cubeacademy.lib.api.ErrorHandler.NoConnectivityException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DataSource @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getNominationList(): ResponseWrapper<List<Nomination>> {
        return safeApiCall(apiCall = {
            apiService.getAllNominations().data
        })
    }

    suspend fun getNomineeList(): ResponseWrapper<List<Nominee>> {
        return safeApiCall(apiCall = {
            apiService.getAllNominees().data
        })
    }

    suspend fun createNomination(
        nomineeId: String,
        reason: String,
        process: String
    ): ResponseWrapper<Nomination> {
        return safeApiCall(apiCall = {
            apiService.createNomination(nomineeId, reason, process).data
        })
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResponseWrapper<T> {
        return withContext(ioDispatcher) {
            try {
                ResponseWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is NoConnectivityException -> ResponseWrapper.NetworkError
                    is IOException -> ResponseWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val msg = throwable.message()
                        val errorMsg = if (msg.isNullOrEmpty()) {
                            throwable.response()?.errorBody()?.string()
                        } else {
                            msg
                        }
                        ResponseWrapper.GenericError(code, errorMsg)
                    }

                    else -> {
                        ResponseWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }
}