package otus.homework.customview.presentation.task2

import android.app.Application
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import otus.homework.customview.R
import otus.homework.customview.databinding.FragmentTaskTwoBinding
import otus.homework.customview.domain.Payment
import otus.homework.customview.presentation.colors.ChartColors
import otus.homework.customview.presentation.task2.categorychart.CategoryChartView
import kotlin.math.abs


class Task2Fragment : Fragment() {

    private val viewModel: Task2ViewModel by viewModels<Task2ViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return Task2ViewModel(
                    this@Task2Fragment.requireContext().applicationContext as Application
                ) as T
            }
        }
    }

    private lateinit var binding: FragmentTaskTwoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskTwoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceBundle: Bundle?) {
        super.onViewCreated(view, savedInstanceBundle)
        bindViews()
        bindFlow()
    }

    private fun bindViews() {
        binding.tabLayout2.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragmentTransaction = childFragmentManager.beginTransaction()
                when {
                    tab?.position == 0 -> fragmentTransaction.replace(R.id.content2, Task2ExactFragment())
                    else -> fragmentTransaction.replace(R.id.content2, Task2MatchFragment())
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
        viewModel.itemsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { updateChartView(it) }
            .launchIn(lifecycleScope)

        viewModel.categoriesFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { updateCategories(it) }
            .launchIn(lifecycleScope)
    }

    private fun relinkViews() {
        viewLifecycleOwner.lifecycleScope.launch {
            updateChartView(viewModel.itemsFlow.first())
            updateCategories(viewModel.categoriesFlow.first())
        }
    }

    private fun updateChartView(items: List<PaymentUiModel>) {
        val data = items.map { item ->
            CategoryChartView.Item(
                item.day,
                item.amount,
                ChartColors[abs(item.category.hashCode()) % ChartColors.size],
                item
            )
        }
        val categoryChartView = binding.content2.findViewById<CategoryChartView?>(R.id.category_chart_view)
        categoryChartView?.setData(data)
    }

    private fun updateCategories(categories: List<CategoryUiModel>) {
        binding.categoriesView.removeAllViews()
        val context = requireContext()
        for (category in categories) {
            val chip = Chip(context)
            chip.text = category.name
            val bgColor = ChartColors[abs(category.name.hashCode()) % ChartColors.size]
            chip.chipBackgroundColor = ColorStateList.valueOf(bgColor)
            chip.isCheckable = true
            chip.isChecked = category.isSelected
            chip.setTextColor(context.getColor(R.color.black))
            chip.setOnClickListener {
                viewModel.selectCategory(category.name)
            }
            binding.categoriesView.addView(chip)
        }
    }
}
