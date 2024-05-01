package otus.homework.customview.presentation.task1

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import otus.homework.customview.R
import otus.homework.customview.databinding.FragmentTaskOneBinding
import otus.homework.customview.domain.Payment
import otus.homework.customview.presentation.task1.piechart.PieCharView
import otus.homework.customview.presentation.colors.ChartColors

class Task1Fragment : Fragment() {

    private val viewModel: Task1ViewModel by viewModels<Task1ViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return Task1ViewModel(
                    this@Task1Fragment.requireContext().applicationContext as Application
                ) as T
            }
        }
    }

    private var toast: Toast? = null
    private lateinit var binding: FragmentTaskOneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskOneBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceBundle: Bundle?) {
        super.onViewCreated(view, savedInstanceBundle)
        bindViews()
        bindFlow()
    }

    private fun bindViews() {
        updatePieChartViewListeners()
        binding.tabLayout1.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragmentTransaction = childFragmentManager.beginTransaction()
                when {
                    tab?.position == 0 -> fragmentTransaction.replace(R.id.content1, Task1ExactFragment())
                    else -> fragmentTransaction.replace(R.id.content1, Task1MatchFragment())
                }
                fragmentTransaction.commitNow()
                relinkViews()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun bindFlow() {
        viewModel.stateFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { updatePieChartView(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun relinkViews() {
        updatePieChartViewListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            updatePieChartView(viewModel.stateFlow.first())
        }
    }

    private fun updatePieChartView(items: List<Payment>) {
        val data = items.mapIndexed { index, item ->
            PieCharView.Item(
                item.name,
                item.amount,
                ChartColors[index % ChartColors.size],
                item
            )
        }
        val pieChartView = binding.content1.findViewById<PieCharView?>(R.id.pie_chart_view)
        pieChartView?.setData(data)
    }

    private fun updatePieChartViewListeners() {
        val pieChartView = binding.content1.findViewById<PieCharView?>(R.id.pie_chart_view)
        pieChartView?.setOnItemClickListener { item ->
            toast?.cancel()
            context?.let {
                toast = Toast.makeText(
                    it,
                    item.label + ", ${(item.data as Payment).category}",
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }
    }
}
