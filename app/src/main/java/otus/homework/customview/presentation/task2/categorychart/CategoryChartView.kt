package otus.homework.customview.presentation.task2.categorychart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min


internal class CategoryChartView(
    context: Context,
    attributeSet: AttributeSet
) : View(
    context,
    attributeSet
) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 1f
        strokeCap = Paint.Cap.ROUND
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 1f
        strokeCap = Paint.Cap.ROUND
        textSize = 1f
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
    }

    private var columns = emptyList<Column>()

    init {
        if (isInEditMode) {
            setData(editModeValues)
        } else {
            setData(defaultValues)
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

    fun setData(data: List<Item>) {
        columns = when {
            data.isEmpty() -> emptyList()
            else -> buildNewColumns(data)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2
        val centerY = height / 2
        val startX = centerX - width * 0.4f
        val startY = centerY - height * 0.4f
        val endX = centerX + width * 0.4f
        val endY = centerY + height * 0.4f

        when {
            columns.isEmpty() -> {
                drawCells(canvas, startX, startY, endX, endY)
            }
            else -> {
                drawCells(canvas, startX, startY, endX, endY)
                drawColumns(canvas, startX, startY, endX, endY)
                drawLabels(canvas, startX, startY, endX, endY)
            }
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state
        if (state is Bundle) {
            val bundle = state
            val columnCount = bundle.getInt("columnCount")
            val restored = ArrayList<Column>()
            for (i in 0 until columnCount) {
                val column = bundle.getParcelable<Column>("column$i")
                column?.let { restored.add(it) }
            }
            columns = restored
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("columnCount", columns.size)
        for (i in 0 until columns.size) {
            bundle.putParcelable("column$i", columns[i])
        }
        return bundle
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

    private fun buildNewColumns(items: List<Item>): List<Column> {
        val max = items.map { it.value }.max()
        var decMax = BigDecimal.TEN
        val five = BigDecimal(5L)
        val two = BigDecimal(2)
        while (decMax < max) {
            decMax = decMax.multiply(five)
            if (decMax < max) decMax = decMax.multiply(two)
        }
        val result = ArrayList<Column>()
        val width = 1f / items.size.toFloat()
        for (i in 0 until items.size) {
            val item = items[i]
            val height = item.value
                .divide(decMax, 2, RoundingMode.HALF_UP)
                .toFloat()
            val startX = width * i
            result.add(
                Column(
                    item,
                    startX,
                    startX + width,
                    height,
                    decMax
                )
            )
        }
        return result
    }

    private fun drawCells(
        canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float
    ) {
        paint.color = Color.GRAY
        val height = endY - startY
        for (yStep in 0..CELL_STEPS) {
            val y = (yStep * height / CELL_STEPS) + startY
            canvas.drawLine(startX, y, endX, y, paint)
        }
    }

    private fun drawColumns(
        canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float
    ) {
        val width = endX - startX
        val height = endY - startY
        val rxy = width / columns.size / 10f
        for (i in 0 until columns.size) {
            val column = columns[i]
            val x1 = column.startX * width + startX
            val x2 = column.endX * width + startX
            val y1 = endY - column.height * height
            paint.color = column.item.color
            canvas.drawRoundRect(x1, y1, x2, endY, rxy, rxy, paint)
            borderPaint.color = Color.DKGRAY
            canvas.drawRoundRect(x1, y1, x2, endY, rxy, rxy, borderPaint)
        }
    }

    @Suppress("MagicNumber")
    private fun drawLabels(
        canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float
    ) {
        val width = endX - startX
        val height = endY - startY
        textPaint.textSize = height / 40f
        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.CENTER
        for (i in 0 until columns.size) {
            val column = columns[i]
            val x1 = column.startX * width + startX
            val x2 = column.endX * width + startX
            val cx = (x1 + x2) / 2f
            val y1 = endY - column.height * height
            val cy = (y1 + endY) / 2f
            canvas.save()
            canvas.translate(cx, cy)
            canvas.rotate(90f)
            canvas.drawText(column.item.label, 0f, 0f - textPaint.textSize * 0.3f, textPaint)
            canvas.drawText(column.item.value.toString(), 0f, 0f + textPaint.textSize * 0.7f, textPaint)
            canvas.restore()
        }

        val decMax = columns[0].decMax
        var step = decMax.divide(BigDecimal.TEN)
        var value = decMax
        textPaint.textSize = height / 60f
        for (yStep in 0..CELL_STEPS) {
            val y = (yStep * height / CELL_STEPS) + startY
            canvas.drawText(value.toString(), startX - width * 0.05f, y, textPaint)
            value = value.subtract(step)
        }
    }

    class Item(
        val label: String,
        val value: BigDecimal,
        val color: Int,
        val data: Serializable? = null,
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readString().orEmpty(),
            BigDecimal(parcel.readDouble()),
            parcel.readInt(),
            parcel.readSerializable()
        )

        constructor(
            label: String, value: Int, color: Int, data: Serializable? = null
        ): this(label, BigDecimal(value), color, data)

        constructor(
            label: String, value: Long, color: Int, data: Serializable? = null
        ): this(label, BigDecimal(value), color, data)

        constructor(
            label: String, value: Float, color: Int, data: Serializable? = null
        ): this(label, BigDecimal(value.toDouble()), color, data)

        constructor(
            label: String, value: Double, color: Int, data: Serializable? = null
        ): this(label, BigDecimal(value), color, data)

        override fun toString() = "Item($label,$value)"

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(label)
            parcel.writeDouble(value.toDouble())
            parcel.writeInt(color)
            parcel.writeSerializable(data)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Item> {
            override fun createFromParcel(parcel: Parcel): Item {
                return Item(parcel)
            }

            override fun newArray(size: Int): Array<Item?> {
                return arrayOfNulls(size)
            }
        }
    }

    internal class Column(
        val item: Item,
        val startX: Float,
        val endX: Float,
        val height: Float,
        val decMax: BigDecimal
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            Item(parcel),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            BigDecimal(parcel.readDouble())
        )

        override fun toString() = "Column(${item.label},${item.value},$startX,$endX,$height)"

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            item.writeToParcel(parcel, flags)
            parcel.writeFloat(startX)
            parcel.writeFloat(endX)
            parcel.writeFloat(height)
            parcel.writeDouble(decMax.toDouble())
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Column> {
            override fun createFromParcel(parcel: Parcel): Column {
                return Column(parcel)
            }

            override fun newArray(size: Int): Array<Column?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        private val defaultValues = listOf<Item>()
        private const val CELL_STEPS = 10

        private val editModeValues = listOf(
            Item("Азбука Вкуса", 1580, Color.GREEN),
            Item("Ригла", 499, Color.YELLOW),
            Item("Пятерочка", 1009, Color.RED),
            Item("Truffo", 4541, Color.MAGENTA),
            Item("Simple Wine", 1600, Color.GRAY),
            Item("Азбука Вкуса Экспресс", 1841, Color.GREEN),
            Item("Метро", 300, Color.BLUE),
            Item("Стоматология", 5000, Color.WHITE),
            Item("Бассейн", 1000, Color.CYAN),
            Item("Uber", 500, Color.DKGRAY)
        )
    }

}
