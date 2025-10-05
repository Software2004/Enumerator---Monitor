package com.example.enumerator_monitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enumerator_monitor.data.SurveyEntry
import com.example.enumerator_monitor.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEntryUiState(
    val nextHouseNo: Int = 1,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    private val repository: SurveyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEntryUiState())
    val uiState: StateFlow<AddEntryUiState> = _uiState

    init {
        viewModelScope.launch { _uiState.value = _uiState.value.copy(nextHouseNo = repository.getNextHouseNo()) }
    }

    fun save(
        houseNo: Int?,
        respondentName: String?,
        familyMembers: Int?,
        houseType: String?,
        ownsBuffalo: Boolean,
        ownsCow: Boolean,
        ownsGoat: Boolean,
        ownsSheep: Boolean,
        hasInfantChild: Boolean,
        familyType: String?,
        chitsCount: Int?,
        phoneNumber: String?
    ) {
        viewModelScope.launch {
            // basic validation
            if (houseNo == null || houseNo <= 0) return@launch emitError("House no is required")
            if (respondentName.isNullOrBlank()) return@launch emitError("Respondent name is required")
            if (familyMembers == null || familyMembers < 0) return@launch emitError("Family members is required")
            if (houseType.isNullOrBlank()) return@launch emitError("House type is required")
            if (familyType.isNullOrBlank()) return@launch emitError("Family type is required")
            if (chitsCount == null || chitsCount < 0) return@launch emitError("No. of chits is required")
            if (phoneNumber.isNullOrBlank()) return@launch emitError("Phone no is required")

            _uiState.value = _uiState.value.copy(isSaving = true, saveSuccess = null, errorMessage = null)
            repository.addEntry(
                SurveyEntry(
                    houseNo = houseNo,
                    respondentName = respondentName,
                    familyMembers = familyMembers,
                    houseType = houseType,
                    ownsBuffalo = ownsBuffalo,
                    ownsCow = ownsCow,
                    ownsGoat = ownsGoat,
                    ownsSheep = ownsSheep,
                    hasInfantChild = hasInfantChild,
                    familyType = familyType,
                    chitsCount = chitsCount,
                    phoneNumber = phoneNumber
                )
            )
            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true, nextHouseNo = repository.getNextHouseNo())
        }
    }

    private fun emitError(msg: String) {
        _uiState.value = _uiState.value.copy(errorMessage = msg, saveSuccess = false)
    }
}


