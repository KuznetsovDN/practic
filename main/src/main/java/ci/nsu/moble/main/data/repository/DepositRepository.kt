// data/repository/DepositRepository.kt
package ci.nsu.moble.main.data.repository

import com.example.depositcalculator.data.database.DepositCalculation
import com.example.depositcalculator.data.database.DepositDao
import kotlinx.coroutines.flow.Flow

class DepositRepository(private val depositDao: DepositDao) {

    fun getAllCalculations(): Flow<List<DepositCalculation>> {
        return depositDao.getAllCalculations()
    }

    suspend fun saveCalculation(calculation: DepositCalculation) {
        depositDao.insertCalculation(calculation)
    }

    suspend fun getCalculationById(id: Long): DepositCalculation? {
        return depositDao.getCalculationById(id)
    }
}