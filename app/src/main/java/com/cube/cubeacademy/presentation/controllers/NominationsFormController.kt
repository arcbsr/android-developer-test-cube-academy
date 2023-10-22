package com.cube.cubeacademy.presentation.controllers

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import com.cube.cubeacademy.lib.models.Nominee

class NominationsFormController(
    private val editTextReasoning: EditText,
    private val radioGroup: RadioGroup,
    private val spinnerNominee: Spinner,
    private val callback: FormEnabledCallback
) {

    init {
        setupListeners()
    }

    private fun setupListeners() {
        val requiredFields = listOf(editTextReasoning, radioGroup, spinnerNominee)

        // TextWatcher for EditText
        editTextReasoning.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkFields(requiredFields)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        // OnChangeListener for RadioGroup
        radioGroup.setOnCheckedChangeListener { _, _ ->
            checkFields(requiredFields)
        }

        // OnItemSelectedListener for Spinner
        spinnerNominee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                checkFields(requiredFields)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                checkFields(requiredFields)
            }
        }
    }

    private fun checkFields(requiredFields: List<View>) {
        val allFieldsFilled = requiredFields.all {
            when (it) {
                is EditText -> it.text.isNotEmpty()
                is RadioGroup -> it.checkedRadioButtonId != -1
                is Spinner -> it.selectedItemPosition != 0 // Assuming 0th position is for default selection
                else -> false
            }
        }
        callback.onFormDataChange(allFieldsFilled)
    }

    fun getReasoningTxt(): String {
        val reasoning = editTextReasoning.text.toString().trim()
        if (reasoning.isEmpty()) {
            throw NullPointerException("")
        }
        return reasoning
    }

    fun getProcessTxt(view: View): String {
        val selectedRadioButtonId: Int = radioGroup.checkedRadioButtonId
        val selectedRadioButton: RadioButton = view.findViewById(selectedRadioButtonId)
        return selectedRadioButton.tag.toString().lowercase().trim()
    }

    fun getNomineeID(): String {
        try {
            val selectedItem: Nominee = spinnerNominee.selectedItem as Nominee
            return selectedItem.nomineeId
        } catch (exp: Exception) {
            throw NullPointerException("")
        }

    }
}

interface FormEnabledCallback {
    fun onFormDataChange(isEnable: Boolean)
}