package ch9

fun main() {

    println("\n\n===== 9.1.1 =====")

//    println(max("kotlin", 42))

    println("\n\n===== 9.1.4 =====")
//    val nullableStringProcessor = Processor<String?>()


    println("\n\n===== 9.3.1 =====")
    val strings = mutableListOf("abc", "bac")
//    addAnswer(strings) // 이 줄이 컴파일 되면
    println(strings.maxByOrNull { it.length }) // 실행시점에 예외가 발생할 것이다

}

// ===== 9.3.1 =====
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}

// ===== 9.1.4 =====
class Processor<T : Any> {
    fun process(value: T) {
        value.hashCode() // T 타입의 value는 null이 될 수 없다
    }
}

fun <T : Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}

// ===== 9.1.1 =====
fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        // predicate 파라미터로 전달받은 함수를 호출
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}