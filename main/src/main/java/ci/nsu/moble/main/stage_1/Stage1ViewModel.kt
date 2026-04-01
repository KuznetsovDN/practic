package ci.nsu.moble.main.stage_1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ci.nsu.moble.main.data.repository.DepositRepository


class Stage1ViewModel(private val repository: DepositRepository) : ViewModel() {

    private val _initialAmount = MutableLiveData<Double>()
    val initialAmount: LiveData<Double> = _initialAmount

    private val _periodMonths = MutableLiveData<Int>()
    val periodMonths: LiveData<Int> = _periodMonths

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun saveStage1Data(initialAmount: Double, periodMonths: Int) {
        _initialAmount.value = initialAmount
        _periodMonths.value = periodMonths
    }

    fun validateInputs(initialAmountStr: String, periodMonthsStr: String): Boolean {
        if (initialAmountStr.isEmpty()) {
            _errorMessage.value = "Введите стартовый взнос"
            return false
        }

        if (periodMonthsStr.isEmpty()) {
            _errorMessage.value = "Введите срок вклада"
            return false
        }

        val initialAmount = initialAmountStr.toDoubleOrNull()
        if (initialAmount == null || initialAmount <= 0) {
            _errorMessage.value = "Стартовый взнос должен быть положительным числом"
            return false
        }

        val periodMonths = periodMonthsStr.toIntOrNull()
        if (periodMonths == null || periodMonths <= 0) {
            _errorMessage.value = "Срок вклада должен быть положительным числом"
            return false
        }

        return true
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class Stage1ViewModelFactory(private val repository: DepositRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Stage1ViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return Stage1ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}