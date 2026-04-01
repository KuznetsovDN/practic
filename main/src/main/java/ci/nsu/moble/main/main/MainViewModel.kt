package ci.nsu.moble.main.main





import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ci.nsu.moble.main.data.repository.DepositRepository
import com.example.depositcalculator.data.repository.DepositRepository

class MainViewModel(private val repository: DepositRepository) : ViewModel() {
    // ViewModel logic if needed
}

class MainViewModelFactory(private val repository: DepositRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}