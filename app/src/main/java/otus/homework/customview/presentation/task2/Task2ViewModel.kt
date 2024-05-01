package otus.homework.customview.presentation.task2

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import otus.homework.customview.data.repository.PaymentRepository
import otus.homework.customview.domain.Payment
import java.text.SimpleDateFormat
import java.util.Date

internal class Task2ViewModel(
    application: Application
) : ViewModel() {

    private val repository = PaymentRepository(application)
    private val _selectedCategories = MutableStateFlow<String?>(null)

    val categoriesFlow: Flow<List<CategoryUiModel>> = _selectedCategories.map { selected ->
        repository.getCategories().mapIndexed { index, item ->
            CategoryUiModel(index, item, item == selected)
        }
    }

    val itemsFlow: Flow<List<PaymentUiModel>> = _selectedCategories.map { category ->
        repository.getItems(category)
    }.map {
        it.toPaymentUiModels()
    }

    fun selectCategory(category: String?) {
        viewModelScope.launch {
            _selectedCategories.emit(category)
        }
    }

    private fun List<Payment>.toPaymentUiModels(): List<PaymentUiModel> {
        val sdf = SimpleDateFormat.getDateInstance()
        return map {
            sdf.format(Date(it.time * 1000L)).toString() to it
        }
            .groupBy { it.first }
            .map {
                val day = it.key
                val amount = it.value.sumOf { it.second.amount }
                val category = it.value.first().second.category
                PaymentUiModel(
                    day = day,
                    amount = amount,
                    category = category
                )
            }
    }
}

