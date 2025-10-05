package com.example.enumerator_monitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enumerator_monitor.data.SurveyEntry
import com.example.enumerator_monitor.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DayGroup(val dateLabel: String, val entries: List<SurveyEntry>)

@OptIn(FlowPreview::class)
@HiltViewModel
class EntriesViewModel @Inject constructor(
    private val repository: SurveyRepository
) : ViewModel() {
    private val query = MutableStateFlow("")

    private val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val grouped = combine(
        query.debounce(300),
        repository.getAllEntries()
    ) { q, list ->
        val filtered = if (q.isBlank()) list else list.filter {
            it.respondentName.contains(q, true) || it.houseNo.toString().contains(q)
        }
        filtered.groupBy { formatter.format(Date(it.createdAt)) }
            .map { (date, items) ->
                val sorted = items.sortedByDescending { it.createdAt }
                DayGroup(date, sorted)
            }
            .sortedByDescending { it.entries.firstOrNull()?.createdAt }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(text: String) { query.update { text } }
}


