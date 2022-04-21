package ch8

import java.util.concurrent.locks.Lock

fun main() {

    println("\n\n===== 8.1.1 =====")
    val sum = { x: Int, y: Int -> x + y }
    val action = { println(42) }
    action()

    // 함수 타입 명시
    // Int 파라미터 두개를 받아서 Int 값 반환하는 함수
    val sum2: (Int, Int) -> Int = { x, y -> x + y }
    // 아무 인자도 받지 않고 리턴값이 없는(Unit) 함수
    val action2: () -> Unit = { println(42) }
    action2()

    // 반환값이 널이 될 수 있는 경우
    var canReturnNull: (Int, Int) -> Int? = { x, y -> null }
    // 함수 자체가 널이 될 수 있는 경우
    var funOrNull: ((Int, Int) -> Int)? = null

    println("\n\n===== 8.1.2 =====")
    twoAndThree { a, b -> a + b }
    twoAndThree { a, b -> a * b }

    println("ab1c".filter { it in 'a'..'z' })

    println("\n\n===== 8.1.4 =====")
    val letters = listOf("Alpha", "Beta")

    // 디폴트 변환 함수를 사용한다.
    println(letters.joinToString())
    // 결과: Alpha, Beta

    // 람다를 인자로 전달한다
    println(letters.joinToString { it.toLowerCase() })
    // 결과: alpha, beta

    // 이름 붙은 인자 구문을 사용해 람다를 포함하는 여러 인자를 전달한다
    println(
        letters.joinToString(
            separator = "! ",
            postfix = "! ",
            transform = { it.toUpperCase() })
    )
    // 결과: ALPHA! BETA!

    println("\n\n===== 8.1.5 =====")
    val calculator = getShippingCostCaculator(Delivery.EXPEDITED)
    // 반환받은 함수를 호출
    println("Shipping costs ${calculator(Order(3))}")

    val contacts = listOf(
        Person("Dmitry", "Jemerov", "123-4567"),
        Person("Svetlana", "Isakova", null)
    )
    val contactListFilters = ContactListFilters2()
    with(contactListFilters) {
        prefix = "Dm"
        onlyWithPhoneNumber = true
    }
    println(contacts.filter(contactListFilters.getPredicate()))
    // 결과: [Person(firstName=Dmitry, lastName=Jemerov, phoneNumber=123-4567)]


    println("\n\n===== 8.1.6 =====")
    val log = listOf(
        SiteVisit("/", 34.0, OS.WINDOWS),
        SiteVisit("/", 22.0, OS.MAC),
        SiteVisit("/login", 12.0, OS.WINDOWS),
        SiteVisit("/signup", 8.0, OS.IOS),
        SiteVisit("/", 16.3, OS.ANDROID)
    )

    val averageWindowsDuration = log
        .filter { it.os == OS.WINDOWS }
        .map(SiteVisit::duration)
        .average()

    println(averageWindowsDuration)
    // 결과: 23.0

    println(log.averageDurationFor(OS.WINDOWS))
    println(log.averageDurationFor(OS.MAC))

    val averageMobileDuration = log
        .filter { it.os in setOf(OS.IOS, OS.ANDROID) }
        .map(SiteVisit::duration)
        .average()
    println(averageMobileDuration)

    println(log.averageDurationFor2 {
        it.os in setOf(OS.ANDROID, OS.IOS)
    })
    println(log.averageDurationFor2 { it.os == OS.IOS && it.path == "/signup" })


}

inline fun doSomethingElse(lambda: () -> Unit) {
    println("Doing something else")
    lambda()
}

// ===== 8.2.2 =====


inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    // ...
}


// ===== 8.2.1 =====
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

fun foo(l: Lock) {
    println("Before sync")
    synchronized(l) {
        println("Action")
    }  // 마지막 인자가 람다이므로 {} 안에 작성할 수 있다.
    println("After sync")
}

class LockOwner(val lock: Lock) {
    fun runUnderLock(body: () -> Unit) {
        synchronized(lock, body) // 람다 대신에 함수 타입인 변수를 인자로 넘긴다
    }
}

class LockOwner2(val lock: Lock) {
    // 이 함수는 runUnderLock을 실제로 컴파일한 바이트코드와 비슷하다
    fun __runUnderLock__(body: () -> Unit) {
        lock.lock()
        try {
            // synchronized를 호출하는 부분에서 람다를 알 수 없으므로 본문(body())은 인라이닝되지 않는다
            body()
        } finally {
            lock.unlock()
        }
    }
}

// ===== 8.1.6 =====
data class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
)

enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

fun List<SiteVisit>.averageDurationFor(os: OS) =
    filter { it.os == os }.map(SiteVisit::duration).average()

fun List<SiteVisit>.averageDurationFor2(predicate: (SiteVisit) -> Boolean) =
    filter(predicate).map(SiteVisit::duration).average()

// ===== 8.1.5 =====
enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCaculator(
    delivery: Delivery
): (Order) -> Double { // 함수를 반환하는 함수를 선언

    if (delivery == Delivery.EXPEDITED) {
        // 함수에서 람다를 반환
        return { order -> 6 + 2.1 * order.itemCount }
    }
    // (이것도) 함수에서 람다를 반환
    return { order -> 1.2 * order.itemCount }
}

class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false
}

data class Person(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?
)

class ContactListFilters2 {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false

    fun getPredicate(): (Person) -> Boolean { // 함수를 반환하는 함수를 정의한다.
        val startsWithPrefix = { p: Person ->
            p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
        }
        if (!onlyWithPhoneNumber) {
            return startsWithPrefix // 함수 타입의 변수를 반환한다.
        }
        return {
            startsWithPrefix(it)
                    && it.phoneNumber != null
        } // 람다를 반환한다.
    }
}


// ===== 8.1.4 =====
fun <T> Collection<T>.joinToString(
    separator: String = ",",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    // 기본 toString 메소드를 사용해 객체를 문자열로 변환한다
    return result.toString()
}

fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    // 함수 타입 파라미터를 선언하면서 람다를 디폴트 값으로 지정한다.
    transform: (T) -> String = { it.toString() }
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element)) // "transform" 파라미터로 받은 함수를 호출한다.
    }

    result.append(postfix)
    return result.toString()
}

fun foo(callback: (() -> Unit)?) {
    // ...
    if (callback != null) {
        callback()
    }
}

fun <T> Collection<T>.joinToString2(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    // 널이 될 수 있는 함수 타입의 파라미터를 선언
    transform: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        // 안전 호출을 사용해 함수를 호출
        val str = transform?.invoke(element)
            ?: element.toString() // 엘비스 연산자를 사용해 람다를 인자로 받지 않은 경우 처리
        result.append(str)
    }

    result.append(postfix)
    return result.toString()
}


// ===== 8.1.3 =====
fun processTheAnswer(f: (Int) -> Int) {
    println(f(42))
}

// ===== 8.1.2 =====
// 함수 타입인 파라미터를 선언한다
fun twoAndThree(operation: (Int, Int) -> Int) {
    // 함수타입인 파라미터를 호출한다
    val result = operation(2, 3)
    println("The result is $result")
}

fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        // predicate 파라미터로 전달받은 함수를 호출
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}
