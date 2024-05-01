package otus.homework.customview.presentation.task1.piechart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Suppress("TooManyFunctions")
internal class PieCharView(
    context: Context,
    attributeSet: AttributeSet
) : View(
    context,
    attributeSet
) {

    private var sectors = emptyList<Sector>()

    private val largePath = Path()
    private val smallPath = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 1f
        strokeCap = Paint.Cap.ROUND
    }

    @Suppress("MagicNumber")
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 1f
        strokeCap = Paint.Cap.ROUND
        textSize = 3f
        textAlign = Paint.Align.CENTER
    }

    private var listener: ((Item) -> Unit)? = null

    init {
        when {
            isInEditMode -> setData(editModeValues)
            else -> setData(defaultValues)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    @Suppress("MagicNumber")
    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2
        val centerY = height / 2
        val largeRadius = centerX.toFloat().coerceAtMost(centerY.toFloat()) * MAX_RADIUS
        val smallRadius = centerX.toFloat().coerceAtMost(centerY.toFloat()) * MIN_RADIUS

        when {
            sectors.isEmpty() -> {
                drawEmpty(canvas, centerX, centerY, smallRadius, largeRadius)
            }
            else -> {
                drawSectors(canvas, centerX, centerY, smallRadius, largeRadius)
                textPaint.textSize = largeRadius / 15f
                val textRadius = (smallRadius + largeRadius) / 2
                drawLabels(canvas, centerX, centerY, textRadius)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (listener != null) {
                handleClickEvent(event.x, event.y)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun setData(data: List<Item>) {
        sectors = when {
            data.isEmpty() -> emptyList()
            else -> buildNewSectors(data)
        }
        invalidate()
    }

    fun setOnItemClickListener(listener: ((Item) -> Unit)? = null) {
        this.listener = listener
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        val result = when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> min(desiredSize.toDouble(), specSize.toDouble()).toInt()
            else -> desiredSize
        }
        if (result < desiredSize) {
            Log.e("PieCharView", "Warning! result < desiredSize!")
        }
        return result
    }

    private fun drawEmpty(
        canvas: Canvas,
        centerX: Int,
        centerY: Int,
        smallRadius: Float,
        largeRadius: Float
    ) {
        smallPath.reset()
        smallPath.addCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            smallRadius,
            Path.Direction.CW
        )
        smallPath.close()
        canvas.clipOutPath(smallPath)

        paint.color = Color.DKGRAY
        largePath.reset()
        largePath.moveTo(centerX.toFloat(), centerY.toFloat())
        largePath.addCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            largeRadius,
            Path.Direction.CW
        )
        largePath.close()
        canvas.drawPath(largePath, paint)
    }

    private fun drawSectors(
        canvas: Canvas,
        centerX: Int,
        centerY: Int,
        smallRadius: Float,
        largeRadius: Float
    ) {
        for (i in 0 until sectors.size) {
            smallPath.reset()
            smallPath.addCircle(
                centerX.toFloat(),
                centerY.toFloat(),
                smallRadius,
                Path.Direction.CW
            )
            smallPath.close()
            canvas.clipOutPath(smallPath)

            val sector = sectors[i]
            paint.color = sector.item.color
            largePath.reset()
            largePath.moveTo(centerX.toFloat(), centerY.toFloat())
            largePath.arcTo(
                centerX - largeRadius,
                centerY - largeRadius,
                centerX + largeRadius,
                centerY  + largeRadius,
                sector.startAngle,
                sector.endAngle - sector.startAngle,
                false
            )
            largePath.close()
            canvas.drawPath(largePath, paint)
        }
    }

    private fun drawLabels(canvas: Canvas, centerX: Int, centerY: Int, textRadius: Float) {
        for (sector in sectors) {
            val textAngle = (sector.startAngle + sector.endAngle) / 2
            val textX = centerX + cos(textAngle.toRadians()) * textRadius
            var textY = centerY + sin(textAngle.toRadians()) * textRadius - textPaint.textSize / 2
            canvas.drawText(sector.item.label, textX.toFloat(), textY.toFloat(), textPaint)
            textY += textPaint.textSize
            canvas.drawText("${sector.percent} %", textX.toFloat(), textY.toFloat(), textPaint)
        }
    }

    private fun buildNewSectors(items: List<Item>): List<Sector> {
        val sum = items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.value)}
        val result = ArrayList<Sector>()
        var startAngle = 0f
        for (item in items) {
            val angle = ROUND_ANGLE
                .multiply(item.value)
                .divide(sum, 2, RoundingMode.HALF_UP)
                .toFloat()

            val percent = ONE_HUNDRED_PERCENT
                .multiply(item.value)
                .divide(sum, 2, RoundingMode.HALF_UP)
                .toFloat()
            result.add(
                Sector(
                    item,
                    startAngle,
                    startAngle + angle,
                    percent
                )
            )
            startAngle += angle
        }
        return result
    }

    private fun handleClickEvent(x: Float, y: Float) {
        val centerX = width / 2
        val centerY = height / 2
        val largeRadius = centerX.toFloat().coerceAtMost(centerY.toFloat()) * MAX_RADIUS
        val smallRadius = centerX.toFloat().coerceAtMost(centerY.toFloat()) * MIN_RADIUS

        val diffX = (x - centerX)
        val diffY = -(y - centerY)
        val radius = diffX * diffX + diffY * diffY
        // если клик был между радиусами min и max - значит, мы кликнули по одному из секторов
        // 8 192 * 8 192 = 67 108 864, что далеко за пределами верхнего диапазона Int,
        // поэтому мы здесь можем оценивать расстояние через квадраты без применения
        // тяжелой операции Math.sqrt
        if (radius > smallRadius * smallRadius && radius < largeRadius * largeRadius) {
            // вычисляем угол через asin или acos
            val angle = AngleConverter.convert(diffX.toDouble(), diffY.toDouble()).toFloat()
            // находим методом деления пополам нужный отрезок
            val sector = findSector(angle)
            listener?.invoke(sector.item)
        }
    }

    // в данном случае leftmost binary search будет наиболее уместным
    private fun findSector(angle: Float): Sector {
        var l = 0
        var r = sectors.size
        while (l < r) {
            val m = (l + r) shr 1
            if (sectors[m].endAngle < angle) {
                l = m + 1
            } else {
                r = m
            }
        }
        return sectors[l]
    }

    @Suppress("MagicNumber")
    private fun Float.toRadians(): Double {
        return this * Math.PI * 2 / 360f
    }

    @Suppress("MagicNumber")
    class Item(
        val label: String,
        val value: BigDecimal,
        val color: Int,
        val data: Any? = null
    ) {
        constructor(
            label: String, value: Int, color: Int, data: Any? = null
        ): this(label, BigDecimal(value), color, data)

        constructor(
            label: String, value: Long, color: Int, data: Any? = null
        ): this(label, BigDecimal(value), color, data)

        constructor(
            label: String, value: Float, color: Int, data: Any? = null
        ): this(label, BigDecimal(value.toDouble()), color, data)

        constructor(
            label: String, value: Double, color: Int, data: Any? = null
        ): this(label, BigDecimal(value), color, data)

        override fun toString() = "Item($label,$value,${color.toString(16)}"
    }

    @Suppress("MagicNumber")
    private class Sector(
        val item: Item,
        val startAngle: Float,
        val endAngle: Float,
        val percent: Float,
    ) {
        override fun toString() = "Sector(" +
                "${item.label}," +
                "${item.value}," +
                "${item.color.toString(16)}," +
                "$startAngle," +
                "$endAngle," +
                "$percent)"
    }

    companion object {
        private const val MAX_RADIUS = 0.8f
        private const val MIN_RADIUS = 0.4f
        private val ROUND_ANGLE = BigDecimal(360L)
        private val ONE_HUNDRED_PERCENT = BigDecimal(100L)

        private val defaultValues = listOf<Item>()

        private val editModeValues = listOf(
            Item("Азбука Вкуса", 1580, 0xFFF01D27.toInt()),
            Item("Ригла", 499, 0xFFf0423f.toInt()),
            Item("Пятерочка", 1009, 0xFFf58625.toInt()),
            Item("Truffo", 4541, 0xFFf9a71b.toInt()),
            Item("Simple Wine", 1600, 0xFFfef102.toInt()),
            Item("Азбука Вкуса Экспресс", 1841, 0xFF74c147.toInt()),
            Item("Метро", 300, 0xFF04a763.toInt()),
            Item("Стоматология", 5000, 0xFF02afb0.toInt()),
            Item("Бассейн", 1000, 0xFF0468b3.toInt()),
            Item("Uber", 500, 0xFF22439e.toInt())
        )
    }
}
