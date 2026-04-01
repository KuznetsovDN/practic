package ci.nsu.moble.main.stage_2


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ci.nsu.moble.main.data.database.AppDatabase
import ci.nsu.moble.main.data.repository.DepositRepository

class Stage2Fragment : Fragment() {

    private var _binding: FragmentStage2Binding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: Stage2ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStage2Binding.inflate(inflater, container, false)

        val repository = DepositRepository(
            AppDatabase.getDatabase(requireContext()).depositDao()
        )
        viewModel = ViewModelProvider(this, Stage2ViewModelFactory(repository))[Stage2ViewModel::class.java]

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        val args = Stage2FragmentArgs.fromBundle(requireArguments())
        viewModel.loadStage1Data(args.initialAmount, args.periodMonths)

        binding.apply {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnCalculate.setOnClickListener {
                val monthlyTopUp = if (etMonthlyTopUp.text.toString().isEmpty()) {
                    null
                } else {
                    etMonthlyTopUp.text.toString().toDoubleOrNull()
                }

                viewModel.calculateDeposit(monthlyTopUp)
            }
        }

        setupSpinner()
    }

    private fun setupSpinner() {
        viewModel.availableRates.observe(viewLifecycleOwner) { rates ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                rates.map { "${it}%" }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerInterestRate.adapter = adapter
        }
    }

    private fun observeViewModel() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.navigateToResult.observe(viewLifecycleOwner) { calculation ->
            calculation?.let {
                val action = Stage2FragmentDirections.actionStage2ToResult(
                    initialAmount = it.initialAmount,
                    periodMonths = it.periodMonths,
                    interestRate = it.interestRate,
                    monthlyTopUp = it.monthlyTopUp ?: 0.0,
                    finalAmount = it.finalAmount,
                    interestEarned = it.interestEarned
                )
                findNavController().navigate(action)
                viewModel.onResultNavigated()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}