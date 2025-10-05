package com.example.enumerator_monitor.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.enumerator_monitor.databinding.ActivityAddEntryBinding
import com.example.enumerator_monitor.viewmodel.AddEntryViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEntryActivity : AppCompatActivity() {

    private val viewModel: AddEntryViewModel by viewModels()
    private lateinit var binding: ActivityAddEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // spinners
        val houseTypes = arrayOf("Owned", "On Rent", "Rent Free", "Other")
        val familyTypes = arrayOf("A", "B", "C", "D", "A-X", "B-X", "X", "C-X", "Other")
        val simpleItem = android.R.layout.simple_spinner_dropdown_item
        binding.apply {
            actHouseType.adapter = ArrayAdapter(this@AddEntryActivity, simpleItem, houseTypes)
            actFamilyType.adapter = ArrayAdapter(this@AddEntryActivity, simpleItem, familyTypes)
        }

        binding.ivBack.setOnClickListener { finish() }

        // autofill next house no
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                val desired = state.nextHouseNo.toString()
                if (binding.etHouseNo.text?.toString() != desired) binding.etHouseNo.setText(desired)
                state.errorMessage?.let { Toast.makeText(this@AddEntryActivity, it, Toast.LENGTH_SHORT).show() }
                if (state.saveSuccess == true) {
                    Toast.makeText(this@AddEntryActivity, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    clearInputs()
                }
            }
        }

        fun TextInputLayout.clearErrorOnChange(editText: TextInputEditText) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { error = null }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        binding.apply {
            tilHouseNo.clearErrorOnChange(etHouseNo)
            tilRespondent.clearErrorOnChange(etRespondent)
            tilFamilyMembers.clearErrorOnChange(etFamilyMembers)
            tilChits.clearErrorOnChange(etChits)
            tilPhone.clearErrorOnChange(etPhone)
        }

        binding.btnClear.setOnClickListener { clearInputs() }
        binding.btnSubmit.setOnClickListener {
            // set errors if empty (for TextInputLayouts only)
            if (binding.etHouseNo.text.isNullOrBlank()) binding.tilHouseNo.error = "Required"
            if (binding.etRespondent.text.isNullOrBlank()) binding.tilRespondent.error = "Required"
            if (binding.etFamilyMembers.text.isNullOrBlank()) binding.tilFamilyMembers.error = "Required"
            if (binding.etChits.text.isNullOrBlank()) binding.tilChits.error = "Required"
            if (binding.etPhone.text.isNullOrBlank()) binding.tilPhone.error = "Required"

            val selectedHouseType = binding.actHouseType.selectedItem?.toString()?.trim()
            val selectedFamilyType = binding.actFamilyType.selectedItem?.toString()?.trim()

            viewModel.save(
                houseNo = binding.etHouseNo.text?.toString()?.toIntOrNull(),
                respondentName = binding.etRespondent.text?.toString()?.trim(),
                familyMembers = binding.etFamilyMembers.text?.toString()?.toIntOrNull(),
                houseType = selectedHouseType,
                ownsBuffalo = binding.cbBuffalo.isChecked,
                ownsCow = binding.cbCow.isChecked,
                ownsGoat = binding.cbGoat.isChecked,
                ownsSheep = binding.cbSheep.isChecked,
                hasInfantChild = binding.rbInfantYes.isChecked,
                familyType = selectedFamilyType,
                chitsCount = binding.etChits.text?.toString()?.toIntOrNull(),
                phoneNumber = binding.etPhone.text?.toString()?.trim()
            )
        }
    }

    private fun clearInputs() {
        binding.apply {
            etHouseNo.setText("")
            etRespondent.setText("")
            etFamilyMembers.setText("")
            cbBuffalo.isChecked = false
            cbCow.isChecked = false
            cbGoat.isChecked = false
            cbSheep.isChecked = false
            rgInfant.check(rbInfantNo.id)
            actHouseType.setSelection(0)
            actFamilyType.setSelection(0)
            etChits.setText("")
            etPhone.setText("")
        }
    }
}