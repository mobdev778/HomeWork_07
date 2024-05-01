package otus.homework.customview.presentation.task1.piechart

import kotlin.math.sqrt

internal object AngleConverter {

    @Suppress("MagicNumber")
    fun convert(diffX: Double, diffY: Double): Double {
        val r = sqrt(diffX * diffX + diffY * diffY)
        val result = when {
            diffX >= 0 && diffY > 0 -> {
                val sin = diffY / r
                val radians = Math.PI / 2 - Math.asin(sin)
                radians * 180.0 / Math.PI + 270f
            }
            diffX >= 0 && diffY <= 0 -> {
                val sin = -diffY / r
                val radians = Math.asin(sin)
                radians * 180 / Math.PI
            }
            diffX < 0 && diffY < 0 -> {
                val sin = -diffY / r
                val radians = Math.PI / 2 - Math.asin(sin)
                radians * 180 / Math.PI + 90f
            }
            else -> {
                val sin = diffY / r
                val radians = Math.asin(sin)
                radians * 180 / Math.PI + 180f
            }
        }
        return Math.abs(result) // для результата вида -0.0
    }
}
