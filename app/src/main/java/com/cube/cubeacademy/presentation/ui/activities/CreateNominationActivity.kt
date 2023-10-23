package com.cube.cubeacademy.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityCreateNominationBinding
import com.cube.cubeacademy.lib.adapters.NomineeArrayAdapter
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.singletone.NomineeManager
import com.cube.cubeacademy.presentation.controllers.FormEnabledCallback
import com.cube.cubeacademy.presentation.controllers.NominationsFormController
import com.cube.cubeacademy.presentation.viewmodels.CreateViewModel
import com.cube.cubeacademy.presentation.viewmodels.CreateViewState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CreateNominationActivity : AppCompatActivity(), FormEnabledCallback {
    private lateinit var binding: ActivityCreateNominationBinding
    private lateinit var createViewModel: CreateViewModel

    private lateinit var formController: NominationsFormController
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showBottomSheet()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNominationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createViewModel = ViewModelProvider(this)[CreateViewModel::class.java]
        populateUI()
    }

    private fun handleError(rawResponse: String) {
        Snackbar.make(findViewById(android.R.id.content), rawResponse, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()

    }

    private fun populateUI() {
        /**
         * TODO: Populate the form after having added the views to the xml file (Look for TODO comments in the xml file)
         * 		 Add the logic for the views and at the end, add the logic to create the new nomination using the api
         * 		 The nominees drop down list items should come from the api (By fetching the nominee list)
         */

        var nomineeList = NomineeManager.getInstance().getNomineeList()
        val defaultOption = Nominee("", getString(R.string.select_option), "")
        nomineeList = listOf(defaultOption) + nomineeList
        val cubesList = binding.nomineeSpinner
        val adapter = NomineeArrayAdapter(this, nomineeList)
        cubesList.adapter = adapter
        //setup method for form controller and observer
        initSetUp()

    }

    private fun initSetUp() {
        formController = NominationsFormController(
            binding.reasoning, binding.radioGroup, binding.nomineeSpinner, this
        )
        createViewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { processResponse(it) }.launchIn(lifecycleScope)
        binding.submitButton.setOnClickListener {
            submittedNomination(
                formController.getNomineeID(),
                formController.getReasoningTxt(),
                formController.getProcessTxt(binding.root)
            )
        }
        binding.backButton.setOnClickListener {
            showBottomSheet()
        }
        onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            backPressedCallback
        )
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.nominee_screen_bottom_sheet, null)
        val btnClose = view.findViewById<Button>(R.id.bottom_btn_cancel)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        val btnLeavePage = view.findViewById<Button>(R.id.bottom_btn_leave)
        btnLeavePage.setOnClickListener {
            dialog.dismiss()
            this.finish()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun submittedNomination(nomineeId: String, reason: String, process: String) {
        createViewModel.createNominationData(nomineeId, reason, process)
    }

    private fun processResponse(state: CreateViewState) {
        when (state) {
            is CreateViewState.Init -> Unit
            is CreateViewState.ErrorResponse -> state.message?.let {
                handleError(it)
            }

            is CreateViewState.SuccessResponse -> {
                gotoSubmittedActivity()
            }

            is CreateViewState.IsLoading -> {
                binding.submitButton.isEnabled = !state.isLoading
            }
        }

    }

    private fun gotoSubmittedActivity() {
        val intent = Intent(this, NominationSubmittedActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onFormDataChange(isEnable: Boolean) {
        binding.submitButton.isEnabled = isEnable
    }

}