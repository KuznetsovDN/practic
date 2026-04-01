package ci.nsu.moble.main.stage_1


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ci.nsu.moble.main.data.database.AppDatabase
import ci.nsu.moble.main.data.repository.DepositRepository
import com.example.depositcalculator.R
import com.example.depositcalculator.databinding.FragmentStage1Binding
import com.example.depositcalculator.data.repository.DepositRepository
import com.example.depositcalculator.data.database.AppDatabase

class Stage1Fragment : Fragment() {

    private var _binding: FragmentStage1Binding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: Stage1ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStage1Binding.inflate(inflater, container, false)

        val repository = DepositRepository(
            AppDatabase.getDatabase(requireContext()).depositDao()
        )
        viewModel = ViewModelProvider(this, Stage1ViewModelFactory(repository))[Stage1ViewModel::class.java]

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            btnBackToMain.setOnClickListener {
                findNavController().navigateUp()
            }

            btnNext.setOnClickListener {
                val initialAmount = etInitialAmount.text.toString()
                val periodMonths = etPeriodMonths.text.toString()

                if (viewModel.validateInputs(initialAmount, periodMonths)) {
                    viewModel.saveStage1Data(
                        initialAmount.toDouble(),
                        periodMonths.toInt()
                    )
                    findNavController().navigate(R.id.action_stage1_to_stage2)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}