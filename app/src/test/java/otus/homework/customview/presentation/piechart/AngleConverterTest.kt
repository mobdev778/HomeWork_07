package otus.homework.customview.presentation.piechart

import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import otus.homework.customview.presentation.task1.piechart.AngleConverter

class AngleConverterTest {

    @ParameterizedTest()
    @MethodSource("convertTestCases")
    fun testConvert(diffX: Double, diffY: Double, expectedAngle: Double) {
        val angle = AngleConverter.convert(diffX, diffY)
        MatcherAssert.assertThat(angle, `is`(expectedAngle))
    }

    companion object {

        // проходим весь тригонометрический круг с шагом в 30 градусов, чтобы убедиться,
        // что вычисления выполняются правильно
        @JvmStatic
        fun convertTestCases() = listOf(
            Arguments.of(10.0, 0.0, 0.0),
            Arguments.of(8.660254038, -5.0, 29.9999999995542),
            Arguments.of(5.0, -8.660254038, 60.000000000445795),
            Arguments.of(0.0, -10, 90.0),
            Arguments.of(-5.0, -8.660254038, 119.9999999995542),
            Arguments.of(-8.660254038, -5.0, 150.0000000004458),
            Arguments.of(-10.0, 0.0, 180.0),
            Arguments.of(-8.660254038, 5.0, 209.9999999995542),
            Arguments.of(-5.0, 8.660254038, 240.0000000004458),
            Arguments.of(0, 10, 270.0),
            Arguments.of(5.0, 8.660254038, 299.99999999955423),
            Arguments.of(8.660254038, 5.0, 330.00000000044577)
        )
    }

}
