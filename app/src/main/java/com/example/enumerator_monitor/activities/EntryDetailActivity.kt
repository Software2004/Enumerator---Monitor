package com.example.enumerator_monitor.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.enumerator_monitor.databinding.ActivityEntryDetailBinding
import com.example.enumerator_monitor.viewmodel.EntryDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EntryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntryDetailBinding
    private val viewModel: EntryDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        val id = intent.getLongExtra(EXTRA_ENTRY_ID, -1L)
        if (id != -1L) {
            viewModel.load(id)
        } else {
            finish()
            return
        }

        lifecycleScope.launchWhenStarted {
            viewModel.entry.collectLatest { entry ->
                entry ?: return@collectLatest
                binding.tvTitle.text = entry.respondentName
                binding.tvHouseNo.text = entry.houseNo.toString()
                binding.tvRespondentName.text = entry.respondentName
                binding.tvFamilyMembers.text = entry.familyMembers.toString()
                binding.tvHouseType.text = entry.houseType
                binding.tvBuffalo.text = if (entry.ownsBuffalo) "Yes" else "No"
                binding.tvCow.text = if (entry.ownsCow) "Yes" else "No"
                binding.tvGoat.text = if (entry.ownsGoat) "Yes" else "No"
                binding.tvSheep.text = if (entry.ownsSheep) "Yes" else "No"
                binding.tvInfant.text = if (entry.hasInfantChild) "Yes" else "No"
                binding.tvFamilyType.text = entry.familyType
                binding.tvChits.text = entry.chitsCount.toString()
                binding.tvPhone.text = entry.phoneNumber
                binding.tvAC.text = if (entry.ownsAC) "Yes" else "No"
                binding.tvRefrigerator.text = if (entry.ownsRefrigerator) "Yes" else "No"
                binding.tvMotorcycle.text = if (entry.ownsMotorcycle) "Yes" else "No"
                binding.tvScooter.text = if (entry.ownsScooter) "Yes" else "No"
                binding.tvCar.text = if (entry.ownsCar) "Yes" else "No"
                binding.tvTractor.text = if (entry.ownsTractor) "Yes" else "No"

                binding.btnShare.setOnClickListener {
                    val text = buildString {
                        appendLine("House No.:  ${entry.houseNo}")
                        appendLine("Respondent Name: ${entry.respondentName}")
                        appendLine("Family Members: ${entry.familyMembers}")
                        appendLine("House Type: ${entry.houseType}")
                        appendLine("Buffalo: ${if (entry.ownsBuffalo) "Yes" else "No"}")
                        appendLine("Cow: ${if (entry.ownsCow) "Yes" else "No"}")
                        appendLine("Goat: ${if (entry.ownsGoat) "Yes" else "No"}")
                        appendLine("Sheep: ${if (entry.ownsSheep) "Yes" else "No"}")
                        appendLine("Infant Child: ${if (entry.hasInfantChild) "Yes" else "No"}")
                        appendLine("Family Type: ${entry.familyType}")
                        appendLine("No. of Chits: ${entry.chitsCount}")
                        appendLine("Phone No.: ${entry.phoneNumber}")
                        appendLine("AC: ${if (entry.ownsAC) "Yes" else "No"}")
                        appendLine("Refrigerator: ${if (entry.ownsRefrigerator) "Yes" else "No"}")
                        appendLine("Motorcycle: ${if (entry.ownsMotorcycle) "Yes" else "No"}")
                        appendLine("Scooter: ${if (entry.ownsScooter) "Yes" else "No"}")
                        appendLine("Car: ${if (entry.ownsCar) "Yes" else "No"}")
                        append("Tractor: ${if (entry.ownsTractor) "Yes" else "No"}")
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    startActivity(Intent.createChooser(intent, "Share entry"))
                }

                binding.btnEdit.setOnClickListener {
                    val intent = Intent(this@EntryDetailActivity, AddEntryActivity::class.java).apply {
                        putExtra(AddEntryActivity.EXTRA_ENTRY_ID, entry.id)
                        putExtra(AddEntryActivity.EXTRA_IS_EDIT_MODE, true)
                    }
                    startActivity(intent)
                }

                binding.btnDelete.setOnClickListener {
                    showDeleteConfirmationDialog(entry)
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(entry: com.example.enumerator_monitor.data.SurveyEntry) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry for ${entry.respondentName}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteEntry(entry)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
    }
}


