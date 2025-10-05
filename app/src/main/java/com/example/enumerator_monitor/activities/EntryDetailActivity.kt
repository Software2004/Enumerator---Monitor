package com.example.enumerator_monitor.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
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
                        append("Phone No.: ${entry.phoneNumber}")
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    startActivity(Intent.createChooser(intent, "Share entry"))
                }
            }
        }
    }

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
    }
}


