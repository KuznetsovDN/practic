package ci.nsu.moble.main.stage_2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ci.nsu.moble.main.data.database.DepositCalculation
import ci.nsu.moble.main.data.repository.DepositRepository

class Stage2ViewModel(private val repository: DepositRepository) : ViewModel() {

    private var initialAmount: Double = 0.0
    private var periodMonths: Int = 0

    private val _availableRates = MutableLiveData<List<Double>>()
    val availableRates: LiveData<List<Double>> = _availableRates

    private val _selectedRate = MutableLiveData<Double>()
    val selectedRate: LiveData<Double> = _selectedRate

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _navigateToResult = MutableLiveData<DepositCalculation?>()
    val navigateToResult: LiveData<DepositCalculation?> = _navigateToResult

    fun loadStage1Data(initialAmount: Double, periodMonths: Int) {
        this.initialAmount = initialAmount
        this.periodMonths = periodMonths
        updateAvailableRates()
    }

    private fun updateAvailableRates() {
        val rates = when {
            periodMonths <= 0 -> {
                _errorMessage.value = "Срок вклада не указан. Пожалуйста, вернитесь и укажите срок."
                listOf()
            }
            periodMonths < 6 -> listOf(15.0)
            periodMonths < 12 -> listOf(10.0)
            else -> listOf(5.0)
        }
        _availableRates.value = rates
        if (rates.isNotEmpty()) {
            _selectedRate.value = rates[0]
        }
    }

    fun calculateDeposit(monthlyTopUp: Double?) {
        if (periodMonths <= 0) {
            _errorMessage.value = "Срок вклада не указан. Пожалуйста, вернитесь и укажите срок."
            return
        }

        val rate = _selectedRate.value ?: run {
            _errorMessage.value = "Выберите процентную ставку"
            return
        }

        // Расчет сложных процентов с ежемесячным пополнением
        val monthlyRate = rate / 100 / 12
        var currentAmount = initialAmount

        for (i in 1..periodMonths) {
            currentAmount += monthlyTopUp ?: 0.0
            currentAmount += currentAmount * monthlyRate
        }

        val finalAmount = currentAmount
        val interestEarned = finalAmount - initialAmount - (monthlyTopUp?.times(periodMonths) ?: 0.0)

        val calculation = DepositCalculation(
            initialAmount = initialAmount,
            periodMonths = periodMonths,
            interestRate = rate,
            monthlyTopUp = monthlyTopUp,
            finalAmount = finalAmount,
            interestEarned = interestEarned
        )

        _navigateToResult.value = calculation
    }

    fun onResultNavigated() {
        _navigateToResult.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class Stage2ViewModelFactory(private val repository: DepositRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Stage2ViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return Stage2ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}