package com.cube.cubeacademy.lib.models.singletone

import com.cube.cubeacademy.lib.models.Nominee

class NomineeManager private constructor() {
    private val nomineeList: MutableList<Nominee> = mutableListOf()

    companion object {
        @Volatile
        private var INSTANCE: NomineeManager? = null

        fun getInstance(): NomineeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NomineeManager().also { INSTANCE = it }
            }
        }
    }

    fun addNominees(nominees: List<Nominee>) {
        nomineeList.clear()
        nomineeList.addAll(nominees)
    }

    fun clearData() {
        nomineeList.clear()
    }

    fun getNomineeList(): List<Nominee> {
        return nomineeList.toList()
    }
}
