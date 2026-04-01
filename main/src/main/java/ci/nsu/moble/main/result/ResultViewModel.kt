package ci.nsu.moble.main.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ci.nsu.moble.main.data.database.DepositCalculation
import ci.nsu.moble.main.data.repository.DepositRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val repository: DepositRepository) : ViewModel() {

    private val _calculationData = MutableLiveData<DepositCalculation?>()
    val calculationData: LiveData<DepositCalculation?> = _calculationData

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCalculationData(
        initialAmount: Double,
        periodMonths: Int,
        interestRate: Double,
        monthlyTopUp: Double?,
        finalAmount: Double,
        interestEarned: Double
    ) {
        _calculationData.value = DepositCalculation(
            initialAmount = initialAmount,
            periodMonths = periodMonths,
            interestRate = interestRate,
            monthlyTopUp = monthlyTopUp,
            finalAmount = finalAmount,
            interestEarned = interestEarned
        )
    }

    fun saveCalculation() {
        _calculationData.value?.let { calculation ->
            viewModelScope.launch {
                try {
                    repository.saveCalculation(calculation)
                    _saveSuccess.value = true
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка при сохранении: ${e.message}"
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class ResultViewModelFactory(private val repository: DepositRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResultViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}