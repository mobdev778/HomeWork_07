package otus.homework.customview.presentation.task2

import java.io.Serializable

data class PaymentUiModel(
    val amount: Int,
    val category: String,
    val day: String
) : Serializable
