package ci.nsu.moble.main.result


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
import com.example.depositcalculator.databinding.FragmentResultBinding
import com.example.depositcalculator.data.repository.DepositRepository
import com.example.depositcalculator.data.database.AppDatabase
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ResultViewModel
    private val decimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("ru")))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        val repository = DepositRepository(
            AppDatabase.getDatabase(requireContext()).depositDao()
        )
        viewModel = ViewModelProvider(this, ResultViewModelFactory(repository))[ResultViewModel::class.java]

        val args = ResultFragmentArgs.fromBundle(requireArguments())
        viewModel.loadCalculationData(
            args.initialAmount,
            args.periodMonths,
            args.interestRate,
            if (args.monthlyTopUp > 0) args.monthlyTopUp else null,
            args.finalAmount,
            args.interestEarned
        )

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            btnSave.setOnClickListener {
                viewModel.saveCalculation()
            }

            btnBackToMain.setOnClickListener {
                findNavController().popBackStack(R.id.mainFragment, false)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.calculationData.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.tvInitialAmount.text = formatCurrency(it.initialAmount)
                binding.tvPeriodMonths.text = "${it.periodMonths} месяцев"
                binding.tvInterestRate.text = "${it.interestRate}%"
                binding.tvMonthlyTopUp.text = if (it.monthlyTopUp != null) {
                    formatCurrency(it.monthlyTopUp)
                } else {
                    "Не указано"
                }
                binding.tvFinalAmount.text = formatCurrency(it.finalAmount)
                binding.tvInterestEarned.text = formatCurrency(it.interestEarned)
            }
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Расчёт сохранён", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun formatCurrency(amount: Double): String {
        return "${decimalFormat.format(amount)} ₽"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}