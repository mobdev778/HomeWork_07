package otus.homework.customview.domain

/**
 * Отдельный платеж
 *
 * @param id идентификатор
 * @param name название платежа (обычно место, где платили)
 * @param amount количество (руб)
 * @param category категория
 * @param time время
 */
internal data class Payment(
    val id: Int,
    val name: String,
    val amount: Int,
    val category: String,
    val time: Long
)
