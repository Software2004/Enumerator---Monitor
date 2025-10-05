package com.example.enumerator_monitor.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.enumerator_monitor.adapters.GroupedEntriesAdapter
import com.example.enumerator_monitor.data.SurveyEntry
import com.example.enumerator_monitor.databinding.ActivityAllEntriesBinding
import com.example.enumerator_monitor.viewmodel.EntriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllEntriesActivity : AppCompatActivity() {
    private val viewModel: EntriesViewModel by viewModels()
    private lateinit var binding: ActivityAllEntriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = GroupedEntriesAdapter()
        adapter.onShareClick = { entry -> shareEntry(entry) }
        adapter.onItemClick = { entry -> openDetail(entry) }
        binding.rvEntries.apply {
            layoutManager = LinearLayoutManager(this@AllEntriesActivity)
            this.adapter = adapter
        }

        binding.ivBack.setOnClickListener { finish() }

        binding.etSearch.addTextChangedListener { viewModel.updateQuery(it?.toString().orEmpty()) }

        lifecycleScope.launchWhenStarted {
            viewModel.grouped.collectLatest { groups ->
                adapter.submit(groups)
            }
        }
    }

    private fun openDetail(entry: SurveyEntry) {
        val intent = Intent(this, EntryDetailActivity::class.java)
        intent.putExtra(EntryDetailActivity.EXTRA_ENTRY_ID, entry.id)
        startActivity(intent)
    }

    private fun shareEntry(entry: SurveyEntry) {
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