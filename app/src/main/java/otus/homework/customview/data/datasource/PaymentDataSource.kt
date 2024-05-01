package otus.homework.customview.data.datasource

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import otus.homework.customview.R
import otus.homework.customview.data.dto.PaymentDto
import java.nio.charset.StandardCharsets

internal class PaymentDataSource(
    private val application: Application
) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi.adapter(Array<PaymentDto>::class.java)

    fun getItems(): List<PaymentDto> {
        val json = readJson()
        val items = jsonAdapter.fromJson(json) ?: emptyArray()
        return items.map { it }
    }

    private fun readJson(): String {
        val stream = application.resources.openRawResource(R.raw.payload)
        val bufferedReader = stream.bufferedReader(StandardCharsets.UTF_8)
        val builder = StringBuilder()
        while (true) {
            val line = bufferedReader.readLine() ?: break
            builder.append(line).append("\n")
        }
        return builder.toString()
    }
}
