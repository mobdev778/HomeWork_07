package otus.homework.customview.data.repository

import android.app.Application
import otus.homework.customview.data.datasource.PaymentDataSource
import otus.homework.customview.data.dto.PaymentDto
import otus.homework.customview.domain.Payment

internal class PaymentRepository(application: Application) {

    private val dataSource = PaymentDataSource(application)

    private var cached: List<Payment>? = null

    fun getItems(): List<Payment> {
        cached?.let { return it }

        return dataSource.getItems().map {
            it.toDomain()
        }.also { cached = it }
    }

    fun getCategories(): List<String> {
        return getItems().map { it.category }.distinct()
    }

    fun getItems(category: String?): List<Payment> {
        if (category == null) return getItems()
        return getItems().filter { it.category == category }
    }

    private fun PaymentDto.toDomain(): Payment {
        return Payment(
            this.id,
            this.name,
            this.amount,
            this.category,
            this.time
        )
    }
}
