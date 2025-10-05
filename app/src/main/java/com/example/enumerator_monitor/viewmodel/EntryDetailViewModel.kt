package com.example.enumerator_monitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enumerator_monitor.data.SurveyEntry
import com.example.enumerator_monitor.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    private val repository: SurveyRepository
) : ViewModel() {

    private val _entry = MutableStateFlow<SurveyEntry?>(null)
    val entry: StateFlow<SurveyEntry?> = _entry

    fun load(id: Long) {
        viewModelScope.launch {
            _entry.value = repository.getById(id)
        }
    }
}


