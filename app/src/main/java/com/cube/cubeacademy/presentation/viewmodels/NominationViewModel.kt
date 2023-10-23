package com.cube.cubeacademy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cube.cubeacademy.lib.domain.remote.UseCase
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.NominationWithNominee
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.ResponseWrapper
import com.cube.cubeacademy.lib.models.singletone.NewNominationHolder
import com.cube.cubeacademy.lib.models.singletone.NomineeManager
import com.cube.cubeacademy.lib.models.withNominee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NominationViewModel @Inject constructor(private val useCase: UseCase) : ViewModel() {
    private val _state = MutableStateFlow<NominationListViewState>(NominationListViewState.Init)
    val mState: StateFlow<NominationListViewState> = _state

    private suspend fun fetchNominationsData() {
        useCase.getAllNominations().onStart {
            _state.value = NominationListViewState.IsLoading(true)
            NewNominationHolder.getInstance().clearNominations()
        }.catch {
            _state.value = NominationListViewState.IsLoading(false)
            _state.value = NominationListViewState.ErrorResponse("OnCatch")
        }.collect {
            _state.value = NominationListViewState.IsLoading(false)
            when (it) {
                is ResponseWrapper.GenericError -> {
                    it.error?.let { msg ->
                        _state.value = NominationListViewState.ErrorResponse("${it.code} : $msg")

                    }
                }

                ResponseWrapper.NetworkError -> {
                    NominationListViewState.ErrorResponse("No Internet")
                }

                is ResponseWrapper.Success -> {
                    val nominations = it.value
                    val nominationsWithNominee = mutableListOf<NominationWithNominee>()
                    for (nomination in nominations) {
                        val nominee = findNomineeByID(nomination.nomineeId)
                        nominationsWithNominee.add(
                            nomination.withNominee(nominee)
                        )
                    }
                    _state.value = NominationListViewState.SuccessResponse(nominationsWithNominee)


                }
            }
        }

    }

    init {
        fetchNomineeData()
    }

    private fun fetchNomineeData() {
        viewModelScope.launch {
            useCase.getAllNominees().onStart {
                _state.value = NominationListViewState.IsLoading(true)
            }.catch {
                _state.value = NominationListViewState.IsLoading(false)
            }.collect {
                _state.value = NominationListViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value =
                                NominationListViewState.ErrorResponse("${it.code} : $msg")

                        }
                    }

                    is ResponseWrapper.NetworkError -> {
                        NominationListViewState.ErrorResponse("No Internet")
                    }

                    is ResponseWrapper.Success -> {
                        NomineeManager.getInstance().addNominees(it.value)
                        fetchNominationsData()
                    }
                }
            }
        }

    }

    private fun updateNominationList(newNominationItem: NominationWithNominee) {
        val currentState = _state.value

        if (currentState is NominationListViewState.SuccessResponse) {
            val updatedData = currentState.nominationList.toMutableList()
            updatedData.add(newNominationItem)
            _state.value = NominationListViewState.SuccessResponse(updatedData)
        }
    }

    fun addNewNomination(nomination: Nomination) {
        val nominee = findNomineeByID(nomination.nomineeId)
        val nominationWithNominee =
            NominationWithNominee(nomination = nomination, nominee = nominee)
        updateNominationList(nominationWithNominee)
    }

    private fun findNomineeByID(nomineeId: String): Nominee {
        var nominee = NomineeManager.getInstance().getNomineeList().find {
            it.nomineeId == nomineeId
        }
        if (nominee == null) {
            nominee = Nominee("", "Unknown", "")
        }
        return nominee
    }

    fun refresh() {
        NewNominationHolder.getInstance().getNominations().map {
            addNewNomination(it)
            NewNominationHolder.getInstance().clearNominations()
        }
    }

}

sealed class NominationListViewState {
    data object Init : NominationListViewState()
    data class IsLoading(val isLoading: Boolean) : NominationListViewState()
    data class SuccessResponse(
        val nominationList: List<NominationWithNominee>
    ) : NominationListViewState()

    data class ErrorResponse(val message: String?) : NominationListViewState()
}