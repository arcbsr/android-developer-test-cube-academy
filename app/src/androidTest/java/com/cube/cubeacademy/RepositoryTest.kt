package com.cube.cubeacademy

import com.cube.cubeacademy.lib.domain.remote.UseCase
import com.cube.cubeacademy.lib.models.ResponseWrapper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RepositoryTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var useCase: UseCase

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun getNominationsTest() {
        // TODO: Write a test for getting all the nominations from the mock api
        runBlocking {
            useCase.getAllNominations().onStart {

            }.catch {

            }.collect {
                when (it) {
                    is ResponseWrapper.Success -> {
                        Assert.assertEquals(4, it.value.size)
                    }

                    else -> {
                        Assert.assertFalse("Error data", true)
                    }
                }
            }
        }
    }

    @Test
    fun getNomineesTest() {
        // TODO: Write a test for getting all the nominees from the mock api
        runBlocking {
            useCase.getAllNominees().onStart {

            }.catch {
                Assert.assertFalse("Error Exception", true)
            }.collect {
                when (it) {
                    is ResponseWrapper.Success -> {
                        Assert.assertEquals(3, it.value.size)
                    }

                    else -> {
                        Assert.assertFalse("Error data", true)
                    }
                }
            }
        }
    }

    @Test
    fun createNominationTest() {
        // TODO: Write a test for creating a new nomination using the mock api
        val nomineeId = "12"
        val reason = "This is for test"
        val process = "fair"
        runBlocking {
            useCase.createNomination(nomineeId, reason, process).onStart {

            }.catch {
                Assert.assertFalse("Error Exception", true)
            }.collect {
                when (it) {
                    is ResponseWrapper.Success -> {
                        Assert.assertEquals(nomineeId, it.value.nomineeId)
                        Assert.assertEquals(reason, it.value.reason)
                        Assert.assertEquals(process, it.value.process)
                        Assert.assertEquals("3", it.value.nominationId)
                    }

                    else -> {
                        Assert.assertFalse("Error data", true)
                    }
                }
            }
        }
    }
}