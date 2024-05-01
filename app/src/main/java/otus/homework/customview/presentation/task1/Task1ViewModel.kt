package otus.homework.customview.presentation.task1

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import otus.homework.customview.data.repository.PaymentRepository
import otus.homework.customview.domain.Payment

internal class Task1ViewModel(
    application: Application
) : ViewModel() {

    private val repository = PaymentRepository(application)

    private val _stateFlow = MutableStateFlow<List<Payment>>(emptyList())
    val stateFlow = _stateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val items = repository.getItems()
            _stateFlow.value = items
        }
    }
}
