package com.example.classcash.viewmodels.payment

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

class SharedRepository {
    // LiveData for active days
    val activeDays = MutableLiveData<List<String>>()

    // LiveData for daily fund
    val dailyFund = MutableLiveData<Double>()

    // MediatorLiveData for target amount
    val targetAmt = MediatorLiveData<Double>().apply {
        addSource(activeDays) { days -> updateTargetAmt(days, dailyFund.value) }
        addSource(dailyFund) { fund -> updateTargetAmt(activeDays.value, fund) }
    }

    private fun updateTargetAmt(activeDays: List<String>?, dailyFund: Double?) {
        if (activeDays != null && dailyFund != null && dailyFund > 0) {
            targetAmt.value = activeDays.size * dailyFund
        }
    }
}
