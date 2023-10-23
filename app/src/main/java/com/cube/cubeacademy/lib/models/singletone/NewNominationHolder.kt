package com.cube.cubeacademy.lib.models.singletone

import com.cube.cubeacademy.lib.models.Nomination

/**
 * This singleton class is added to hold new nomination create from API
 * It can be replaced by local database
 * new created list is added in viewmodel and cleared instantly,
 * otherwise duplication may be occurred
 * */
class NewNominationHolder private constructor() {

    private val nominations: MutableList<Nomination> = mutableListOf()

    companion object {
        @Volatile
        private var instance: NewNominationHolder? = null

        fun getInstance(): NewNominationHolder {
            return instance ?: synchronized(this) {
                instance ?: NewNominationHolder().also { instance = it }
            }
        }
    }

    fun addNomination(nomination: Nomination) {
        nominations.add(nomination)
    }

    fun getNominations(): List<Nomination> {
        return nominations.toList()
    }

    fun clearNominations() {
        nominations.clear()
    }
}