package com.example.enumerator_monitor.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.enumerator_monitor.activities.AllEntriesActivity
import com.example.enumerator_monitor.activities.EntryDetailActivity
import com.example.enumerator_monitor.adapters.GroupedEntriesAdapter
import com.example.enumerator_monitor.data.SurveyEntry
import com.example.enumerator_monitor.databinding.FragmentDashboardBinding
import com.example.enumerator_monitor.viewmodel.DayGroup
import com.example.enumerator_monitor.viewmodel.EntriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EntriesViewModel by viewModels()
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Recycler setup for today's entries
        val adapter = GroupedEntriesAdapter()
        adapter.onItemClick = { entry -> openDetail(entry) }
        adapter.onShareClick = { entry -> shareEntry(entry) }
        binding.rvRecentEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        // Observe grouped data to compute stats and today's list
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.grouped.collectLatest { groups ->
                val total = groups.sumOf { it.entries.size }
                val days = groups.size
                val avg = if (days > 0) {
                    (total.toDouble() / days.toDouble()).roundToInt()
                } else 0

                val todayLabel = dateFormatter.format(Date())
                val todayEntries =
                    groups.firstOrNull { it.dateLabel == todayLabel }?.entries.orEmpty()

                // Update stats
                binding.tvTotalEntries.text = NumberFormat.getIntegerInstance().format(total)
                binding.tvTodayEntries.text = todayEntries.size.toString()
                binding.tvAvgEntries.text = NumberFormat.getIntegerInstance().format(avg)

                // Show only today's entries in the list
                adapter.submit(listOf(DayGroup("Today", todayEntries)))

                if (todayEntries.isEmpty()) {
                    binding.layResult.visibility = View.VISIBLE
                    binding.rvRecentEntries.visibility = View.GONE
                } else {
                    binding.layResult.visibility = View.GONE
                    binding.rvRecentEntries.visibility = View.VISIBLE
                }
            }
        }
        binding.btnViewAll.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(), AllEntriesActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openDetail(entry: SurveyEntry) {
        val intent = Intent(requireContext(), EntryDetailActivity::class.java)
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