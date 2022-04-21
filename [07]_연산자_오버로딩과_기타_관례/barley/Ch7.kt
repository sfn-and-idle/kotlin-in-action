package ch7

import java.time.LocalDate

fun main() {

    println("\n\n===== 7.1.1 =====")
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    println(p1 + p2)


    println(p1 * 1.5)

}

// ===== 7.1.1 =====
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

operator fun Point.times(scale: Double) =
    Point((x * scale).toInt(), (y * scale).toInt())


// ===== 7.3.4 =====
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    // 이 객체는 LocalDate 원소에 대한 iterator를 구현한다.
    object : Iterator<LocalDate> {
        var current = start
        override fun hasNext() =
            // compareTo 관례를 사용해 날짜를 비교한다.
            current <= endInclusive

        // 현재 날짜를 저장한 다음에 날짜를 변경한다. 그 후 저장해둔 날짜를 반환한다.
        override fun next() = current.apply {
            // 현재 날짜를 1일 뒤로 변경한다
            current = plusDays(1)
        }
    }