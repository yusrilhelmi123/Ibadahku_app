package com.apps.motivasiapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.motivasiapp.data.ActivityRepository
import com.apps.motivasiapp.model.DailyActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val todayDate = LocalDate.now().format(dateFormatter)

    private val _dailyActivities = MutableStateFlow<List<DailyActivity>>(emptyList())
    val dailyActivities: StateFlow<List<DailyActivity>> = _dailyActivities.asStateFlow()

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        initializeAndLoad()
    }

    private fun initializeAndLoad() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Initialize activities for today
                repository.initializeDailyActivities(todayDate)

                // Load activities
                repository.getActivitiesByDate(todayDate).collect { activities ->
                    _dailyActivities.value = activities
                    _completedCount.value = activities.count { it.isCompleted }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleActivityCompletion(activity: DailyActivity) {
        viewModelScope.launch {
            val updatedActivity = activity.copy(
                isCompleted = !activity.isCompleted,
                completedTime = if (!activity.isCompleted) java.time.LocalTime.now().toString() else null
            )
            repository.updateActivity(updatedActivity)
        }
    }

    fun refreshActivities() {
        viewModelScope.launch {
            repository.initializeDailyActivities(todayDate)
        }
    }
}
