package com.example.enumerator_monitor.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
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

        // Check if we're in edit mode
        val entryId = intent.getLongExtra(EXTRA_ENTRY_ID, -1L)
        val isEditMode = intent.getBooleanExtra(EXTRA_IS_EDIT_MODE, false)
        
        if (isEditMode && entryId != -1L) {
            viewModel.loadEntryForEdit(entryId)
        }

        // spinners
        val houseTypes = arrayOf("Owned", "On Rent", "Rent Free", "Other")
        val familyTypes = arrayOf("A", "B", "C", "D", "A-X", "B-X", "X", "C-X", "Other")
        val simpleItem = android.R.layout.simple_spinner_dropdown_item
        binding.apply {
            actHouseType.adapter = ArrayAdapter(this@AddEntryActivity, simpleItem, houseTypes)
            actFamilyType.adapter = ArrayAdapter(this@AddEntryActivity, simpleItem, familyTypes)
        }

        onBackPressedDispatcher.addCallback(this) {
            exitDialog()
        }


        binding.ivBack.setOnClickListener {
            exitDialog()
        }

        // Handle UI state changes
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                // Update title based on mode
                if (state.isEditMode) {
                    binding.textHeading.text = "Edit Survey Entry"
                    binding.btnSubmit.text = "Update Entry"
                } else {
                    binding.textHeading.text = "Add New Survey"
                    binding.btnSubmit.text = "Submit"
                }
                
                // Pre-fill form if in edit mode
                if (state.isEditMode && state.editingEntry != null) {
                    val entry = state.editingEntry!!
                    binding.etHouseNo.setText(entry.houseNo.toString())
                    binding.etRespondent.setText(entry.respondentName)
                    binding.etFamilyMembers.setText(entry.familyMembers.toString())
                    binding.etChits.setText(entry.chitsCount.toString())
                    binding.etPhone.setText(entry.phoneNumber)
                    
                    // Set spinners
                    val houseTypeIndex = houseTypes.indexOf(entry.houseType)
                    if (houseTypeIndex >= 0) binding.actHouseType.setSelection(houseTypeIndex)
                    
                    val familyTypeIndex = familyTypes.indexOf(entry.familyType)
                    if (familyTypeIndex >= 0) binding.actFamilyType.setSelection(familyTypeIndex)
                    
                    // Set checkboxes
                    binding.cbBuffalo.isChecked = entry.ownsBuffalo
                    binding.cbCow.isChecked = entry.ownsCow
                    binding.cbGoat.isChecked = entry.ownsGoat
                    binding.cbSheep.isChecked = entry.ownsSheep
                    binding.cbAC.isChecked = entry.ownsAC
                    binding.cbRefrigerator.isChecked = entry.ownsRefrigerator
                    binding.cbMotorcycle.isChecked = entry.ownsMotorcycle
                    binding.cbScooter.isChecked = entry.ownsScooter
                    binding.cbCar.isChecked = entry.ownsCar
                    binding.cbTractor.isChecked = entry.ownsTractor
                    
                    // Set radio buttons
                    if (entry.hasInfantChild) {
                        binding.rbInfantYes.isChecked = true
                    } else {
                        binding.rbInfantNo.isChecked = true
                    }
                }
                else if (!state.isEditMode) {
                    // Auto-fill next house no for new entries
                    val desired = state.nextHouseNo.toString()
                    if (binding.etHouseNo.text?.toString() != desired) binding.etHouseNo.setText(desired)
                }
                
                state.errorMessage?.let { Toast.makeText(this@AddEntryActivity, it, Toast.LENGTH_SHORT).show() }
                if (state.saveSuccess == true) {
                    val message = if (state.isEditMode) "Entry updated successfully" else "Data saved successfully"
                    Toast.makeText(this@AddEntryActivity, message, Toast.LENGTH_SHORT).show()
                    if (!state.isEditMode) {
                        clearInputs()
                    } else {
                        finish()
                    }
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
                phoneNumber = binding.etPhone.text?.toString()?.trim(),
                ownsAC = binding.cbAC.isChecked,
                ownsRefrigerator = binding.cbRefrigerator.isChecked,
                ownsMotorcycle = binding.cbMotorcycle.isChecked,
                ownsScooter = binding.cbScooter.isChecked,
                ownsCar = binding.cbCar.isChecked,
                ownsTractor = binding.cbTractor.isChecked
            )
        }
    }

    private fun exitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Exit")
            .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No", null)
            .show()
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
            cbAC.isChecked = false
            cbRefrigerator.isChecked = false
            cbMotorcycle.isChecked = false
            cbScooter.isChecked = false
            cbCar.isChecked = false
            cbTractor.isChecked = false
            rgInfant.check(rbInfantNo.id)
            actHouseType.setSelection(0)
            actFamilyType.setSelection(0)
            etChits.setText("")
            etPhone.setText("")
            val desired = viewModel.uiState.value.nextHouseNo.toString()
            if (binding.etHouseNo.text?.toString() != desired) binding.etHouseNo.setText(desired)
        }
    }

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
        const val EXTRA_IS_EDIT_MODE = "extra_is_edit_mode"
    }
}