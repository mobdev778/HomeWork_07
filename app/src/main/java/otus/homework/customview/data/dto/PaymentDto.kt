package otus.homework.customview.data.dto

internal data class PaymentDto(
    val id: Int,
    val name: String,
    val amount: Int,
    val category: String,
    val time: Long
)
