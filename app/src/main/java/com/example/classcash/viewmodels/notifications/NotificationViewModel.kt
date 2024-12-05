package com.example.classcash.viewmodels.notifications

import Notification
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.os.Parcel


class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    fun fetchNotifications() {
        viewModelScope.launch {
            val missedContributions = notificationsRepository.getMissedContributions()
            val progressPercentage = notificationsRepository.getProgressPercentage()
            val withdrawalLogs = notificationsRepository.getWithdrawalLogs()
            val studentStatuses = notificationsRepository.getStudentStatus()

            val notificationsList = mutableListOf<Notification>()

            // Notification for missed contributions
            if (missedContributions.isEmpty()) {
                notificationsList.add(Notification("All students have completed their contributions!"))
            } else {
                notificationsList.add(Notification("${missedContributions.size} students missed their contributions. Check it out!"))
            }

            // Notification for overall progress
            if (progressPercentage > 0) {
                notificationsList.add(Notification("${"%.2f".format(progressPercentage)}% of the contribution is collected"))
            }

            // Notification for withdrawals
            withdrawalLogs.forEach { log ->
                notificationsList.add(Notification("You withdrew P${"%.2f".format(log.amount)} from the fund on ${log.date}"))
            }

            // Notification for students who completed their target
            studentStatuses.filter { it.isTargetCompleted }.forEach { status ->
                notificationsList.add(Notification("${status.studentName} completed contributions for a year. Congratulations!"))
            }

            // Notification for students who completed monthly contributions
            studentStatuses.filter { it.isMonthlyCompleted }.forEach { status ->
                notificationsList.add(Notification("${status.studentName} completed her ${status.monthName} contributions."))
            }

            // Write notifications to Parcel
            val parcel = Parcel.obtain()
            val bundle = Bundle()
            bundle.putParcelableArrayList("notifications", ArrayList(notificationsList))
            parcel.writeBundle(bundle)

            // Post notifications LiveData (still using original approach)
            _notifications.postValue(notificationsList)

            parcel.recycle()
        }
    }
}
