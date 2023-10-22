package com.cube.cubeacademy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cube.cubeacademy.lib.domain.remote.UseCase
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(private val useCase: UseCase) : ViewModel() {
    private val _state = MutableStateFlow<CreateViewState>(CreateViewState.Init)
    val mState: StateFlow<CreateViewState> = _state
    fun createNominationData(
        nomineeId: String,
        reason: String,
        process: String
    ) {
        viewModelScope.launch {
            _state.value = CreateViewState.Init
            useCase.createNomination(nomineeId, reason, process).onStart {
                _state.value = CreateViewState.IsLoading(true)
            }.catch {
                _state.value = CreateViewState.IsLoading(false)
                _state.value = CreateViewState.ErrorResponse("Unknown")
            }.collect {
                _state.value = CreateViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value =
                                CreateViewState.ErrorResponse("${it.code} : ${msg}")

                        }
                    }

                    ResponseWrapper.NetworkError -> {
                        _state.value = CreateViewState.ErrorResponse("Network Error")
                    }

                    is ResponseWrapper.Success -> {
                        _state.value = CreateViewState.SuccessResponse(it.value)
                    }
                }
            }

        }
    }
}

sealed class CreateViewState {
    data object Init : CreateViewState()
    data class IsLoading(val isLoading: Boolean) : CreateViewState()
    data class SuccessResponse(
        val nomination: Nomination
    ) : CreateViewState()

    data class ErrorResponse(val message: String?) : CreateViewState()
}