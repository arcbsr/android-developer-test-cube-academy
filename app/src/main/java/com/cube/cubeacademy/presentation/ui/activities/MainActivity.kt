package com.cube.cubeacademy.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityMainBinding
import com.cube.cubeacademy.lib.adapters.NominationsRecyclerViewAdapter
import com.cube.cubeacademy.lib.models.NominationWithNominee
import com.cube.cubeacademy.lib.models.singletone.NewNominationHolder
import com.cube.cubeacademy.presentation.viewmodels.NominationListViewState
import com.cube.cubeacademy.presentation.viewmodels.NominationViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NominationViewModel
    private var isLoadedCompleted: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NominationViewModel::class.java]
        populateUI()
    }

    private fun handleMessage(rawResponse: String) {
        Snackbar.make(findViewById(android.R.id.content), rawResponse, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()

    }

    private fun updateNominationListView(list: List<NominationWithNominee>) {
        if (list.isEmpty()) {
            binding.nominationsList.visibility = View.GONE
            binding.emptyContainer.visibility = View.VISIBLE
        } else {
            binding.nominationsList.visibility = View.VISIBLE
            binding.emptyContainer.visibility = View.GONE

        }
    }

    private fun populateUI() {

        /**
         * TODO: Populate the UI with data in this function
         * 		 You need to fetch the list of user's nominations from the api and put the data in the recycler view
         * 		 And also add action to the "Create new nomination" button to go to the CreateNominationActivity
         */

        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { processResponse(it) }.launchIn(lifecycleScope)
        binding.createButton.setOnClickListener {
            handleMessage("${getString(R.string.no_data_found)}\n${getString(R.string.try_again)}")
        }
    }

    private fun processResponse(state: NominationListViewState) {
        val adapter = NominationsRecyclerViewAdapter()
        binding.nominationsList.adapter = adapter
        binding.nominationsList.layoutManager = LinearLayoutManager(this)
        isLoadedCompleted = true
        when (state) {
            is NominationListViewState.Init -> Unit
            is NominationListViewState.ErrorResponse -> state.message?.let { handleMessage(it) }
            is NominationListViewState.SuccessResponse -> {
                updateNominationListView(state.nominationList)
                adapter.submitList(state.nominationList)
                binding.createButton.setOnClickListener {
                    callNominationCreateActivity()
                }
            }

            is NominationListViewState.IsLoading -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        if (isLoadedCompleted) {
            viewModel.refresh()
        }
    }

    private fun callNominationCreateActivity() {
        val intent = Intent(this, CreateNominationActivity::class.java)
        startActivity(intent)
    }

}
