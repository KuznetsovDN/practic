package ci.nsu.moble.main.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ci.nsu.moble.main.data.database.AppDatabase
import ci.nsu.moble.main.data.repository.DepositRepository
import com.example.depositcalculator.R
import com.example.depositcalculator.databinding.ActivityMainBinding
import com.example.depositcalculator.data.database.AppDatabase
import com.example.depositcalculator.data.repository.DepositRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = DepositRepository(AppDatabase.getDatabase(this).depositDao())
        viewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            btnCalculate.setOnClickListener {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_main_to_stage1)
            }

            btnHistory.setOnClickListener {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_main_to_history)
            }

            btnClose.setOnClickListener {
                finishAffinity()
            }
        }
    }
}