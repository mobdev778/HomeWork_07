package otus.homework.customview.presentation.colors

internal object ChartColors : ArrayList<Int>() {

    // 12 цветов стандартного цветового круга
    private val colors = listOf(
        0xFFF01D27,
        0xFFf0423f,
        0xFFf58625,
        0xFFf9a71b,
        0xFFfef102,
        0xFF74c147,
        0xFF04a763,
        0xFF02afb0,
        0xFF0468b3,
        0xFF22439e,
        0xFF5b3198,
        0xFFa72394
    ).map { it.toInt() }

    init {
        addAll(colors)
        addAll(colors.map { darker(it) })
        addAll(colors.map { lighter(it) })
    }

    private fun lighter(color: Int): Int {
        var red = (color shr 16) and 0xFF
        var green = (color shr 8) and 0xFF
        var blue = color and 0xFF
        red = 255 - ((255 - red) / 2)
        green = 255 - ((255 - green) / 2)
        blue = 255 - ((255 - blue) / 2)
        return (0xFF shl 24) or red shl 16 or green shl 8 or blue
    }

    private fun darker(color: Int): Int {
        var red = (color shr 16) and 0xFF
        var green = (color shr 8) and 0xFF
        var blue = color and 0xFF
        red = red shr 1
        green = green shr 1
        blue = blue shr 1
        return (0xFF shl 24) or red shl 16 or green shl 8 or blue
    }
}
