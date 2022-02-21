package ch6

import java.awt.event.ActionEvent
import java.io.BufferedReader
import java.io.File
import java.io.StringReader
import javax.swing.AbstractAction
import javax.swing.JList

fun main(args: Array<String>) {

    println("\n\n===== 6.1.1 =====")
    strLen("abcde")
//    strLen(null)
//    val x: String? = null
//    var y: String = x
//    strLen(x)

    val x: String? = null
    println(strLenSafe(x))
    println(strLenSafe("abc"))


    println("\n\n===== 6.1.3 =====")
    printAllCaps("abc")
    printAllCaps(null)

    val ceo = Employee("Da Boss", null)
    val developer = Employee("Bob Smith", ceo)
    println(managerName(developer))
    println(managerName(ceo))

    val person = Person("Dmitry", null)
    println(person.countryName())


    println("\n\n===== 6.1.4 =====")
    // todo : // Exception in thread "main" java.lang.IllegalArgumentException: No address
//    printShippingLabel(person)
//    printShippingLabel(Person("Alexey", null))

    println("\n\n===== 6.1.5 =====")
    val p1 = Person615("Dmitry", "Jemerov")
    val p2 = Person615("Dmitry", "Jemerov")
    println(p1 == p2)   // == 연산자는 "equals" 메소드를 호출한다.
    println(p1.equals(42))


    println("\n\n===== 6.1.6 =====")
    // todo : // Exception in thread "main" java.lang.NullPointerException
//    ignoreNulls(null)
//    person.company!!.address!!.country


    println("\n\n===== 6.1.7 =====")
    var email: String? = "yole@example.com"
    email?.let { sendEmailTo(it) }
    email = null
    email?.let { sendEmailTo(it) }


    println("\n\n===== 6.1.9 =====")
    verifyUserInput(" ")
    verifyUserInput(null) // isNullOrBlank에 "null"을 수신객체로 전달해도 아무런 예외가 발생하지 않는다.


    println("\n\n===== 6.1.10 =====")
    printHashCode(null)
    printHashCode(42)


    println("\n\n===== 6.1.11 =====")
//    yellAt(Person(null))
//    yellAtSafe(Person(null))
//    val i: Int = person.name
    val s: String? = person.name    // 자바 프로퍼티를 널이 될 수 있는 타입으로 볼 수 있다.
    val s1: String = person.name    // 자바 프로퍼티를 널이 될 수 없는 타입으로 볼 수 있다.


    println("\n\n===== 6.2.1 =====")
    val i: Int = 1
    val list: List<Int> = listOf(1, 2, 3)
    showProgress(146)

    println("\n\n===== 6.2.2 =====")
//    println(Person622("Sam", 35).isOlderThan(Person("Amy", 42)))
//    println(Person622("Sam", 35).isOlderThan(Person("Jane")))
    val listOfInts = listOf(1, 2, 3)

    println("\n\n===== 6.2.3 =====")
    val i623 = 1
//    val l1: Long = i623 // "Error: type mismatch" 컴파일 오류 발생
    val l2: Long = i623.toLong()

    val x623 = 1   // Int 타입인 변수
    val list623 = listOf(1L, 2L, 3L)   // Long 값으로 이뤄진 리스트
    println(x623.toLong() in listOf(1L, 2L, 3L))

    val b: Byte = 1 // 상수 값은 적절한 타입으로 해석된다.
    val l = b + 1L // + 는 Byte와 Long을 인자로 받을 수 있다.
    foo(42) // 컴파일러는 42를 Long 값으로 해석한다.

    println("42".toInt())


    println("\n\n===== 6.2.4 =====")
    val answer: Any = 42

    println("\n\n===== 6.2.6 =====")
//    fail("Error occurred")

//    val address = company.address ?: fail("No address")
//    println(address.city)


    println("\n\n===== 6.3.1 =====")
    val reader = BufferedReader(StringReader("1\nabc\n42"))
    val numbers = readNumbers(reader)
    addValidNumbers(numbers)

    println("\n\n===== 6.3.2 =====")
    val source: Collection<Int> = arrayListOf(3, 5, 7)
    val target: MutableCollection<Int> = arrayListOf(1)
    copyElement(source, target)
    println(target)


    val target2: Collection<Int> = arrayListOf(1)
//    copyElement(source, target2)    // "target2" 인자에서 컴파일 오류 발생

    println("\n\n===== 6.3.3 =====")
    val list633 = listOf("a", "b", "c")  //읽기 전용 컬렉션
    println(printInUppercase(list633))  //자바 메소드에 파라미터로 코틀린 읽기 전용 컬렉션 전달


    println("\n\n===== 6.3.5 =====")
    // 배열의 인덱스 값의 범위에 대해 이터레이션하기 위해 array.indices 확장 함수를 사용한다.
    for (i in args.indices) {
        // array[index]로 인덱스를 사용해 배열 원소에 접근한다.
        println("Argument $i is: ${args[i]}")
    }

    val letters = Array<String>(26) { i -> ('a' + i).toString() }
    println(letters.joinToString(""))

    val strings = listOf("a", "b", "c")
    println("%s/%s/%s".format(*strings.toTypedArray()))

    // 위의 첫번째와 두번째 방법으로 배열 만들기
    val fiveZeros = IntArray(5)
    val fiveZerosToo = intArrayOf(0, 0, 0, 0, 0)

    // 람다를 인자로 받는 생성자를 사용하는 방법
    val squares = IntArray(5) { i -> (i+1) * (i+1) }
    println(squares.joinToString())

    args.forEachIndexed { index, element ->
        println("Argument $index is: $element")
    }
}

// ===== 6.1.1 =====
fun strLen(s: String) = s.length
//fun strLenSafe(s: String?) = s.length

// null 검사를 추가하면 코드가 컴파일된다.
fun strLenSafe(s: String?): Int = if (s != null) s.length else 0


// ===== 6.1.3 =====
fun printAllCaps(s: String?) {
    val allCaps: String? = s?.toUpperCase() // allCaps는 널일 수도 있다.
    println(allCaps)
}

class Employee(val name: String, val manager: Employee?)

fun managerName(employee: Employee): String? = employee.manager?.name

class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun Person.countryName(): String {
    // 여러 안전한 호출 연산자를 연쇄해 사용한다.
    val country = this.company?.address?.country
    return if (country != null) country else "Unknown"
}


// ===== 6.1.4 =====
fun foo(s: String?) {
    // "s"가 null이면 결과는 빈 문자열("")이다.
    val t: String = s ?: ""
}

fun Person.countryName614() = company?.address?.country ?: "Unknown"

fun printShippingLabel(person: Person) {
    val address = person.company?.address
        ?: throw IllegalArgumentException("No address") // 주소가 없으면 예외를 발생시킨다.
    with(address) {
        println(streetAddress)
        println("$zipCode $city, $country")
    }
}

// ===== 6.1.5 =====
class Person615(val firstName: String, val lastName: String) {
    override fun equals(o: Any?): Boolean {
        // 타입이 서로 일치하지 않으면 false를 반환
        val otherPerson = o as? Person615 ?: return false
        // 안전한 캐스트를 하고나면 otherPerson이 Person 타입으로 스마트 캐스트된다.
        return otherPerson.firstName == firstName && otherPerson.lastName == lastName
    }

    override fun hashCode(): Int = firstName.hashCode() * 37 + lastName.hashCode()
}

// ===== 6.1.6 =====
fun ignoreNulls(s: String?) {
    val sNotNull: String = s!!  // 예외는 이 지점을 가리킨다.
    println(sNotNull.length)
}

class CopyRowAction(val list: JList<String>) : AbstractAction() {
    override fun isEnabled(): Boolean = list.selectedValue != null

    // actionPerformed는 isEnabled가 "true"인 경우에만 호출된다.
    override fun actionPerformed(e: ActionEvent) {
        val value = list.selectedValue!!
        // value를 클립보드로 복사
    }
}


// ===== 6.1.7 =====
//fun sendEmailTo(email: String) { /*...*/ }
fun sendEmailTo(email: String) {
    println("Sending email to $email")
}

fun getTheBestPersonInTheWorld(): Person? = null


// ===== 6.1.9 =====
fun verifyUserInput(input: String?) {
    // 안전한 호출을 하지 않아도 된다.
    if (input.isNullOrBlank()) {
        println("Please fill in the required fields")
    }
}


// ===== 6.1.10 =====
fun <T> printHashCode(t: T) {
    println(t?.hashCode())  // "t"가 널이 될 수 있으므로 안전한 호출을 써야만 한다.
}

//fun <T: Any> printHashCode(t: T) {  // 이제 "T"는 널이 될 수 없는 타입이다.
//    println(t.hashCode())
//}


// ===== 6.1.11 =====
fun yellAt(person: Person) {
    // toUpperCase()의 수신 객체 person.name이 널이어서 예외 발생
    println(person.name.toUpperCase() + "!!!")
}

fun yellAtSafe(person: Person) {
    println((person.name ?: "Anyone").toUpperCase() + "!!!")
}


// ===== 6.2.1 =====
fun showProgress(progress: Int) {
    val percent = progress.coerceIn(0, 100)
    println("We're ${percent}% done!")
}

// ===== 6.2.2 =====
data class Person622(val name: String, val age: Int? = null) {
    fun isOlderThan(other: Person622): Boolean? {
        if (age == null || other.age == null) return null
        return age > other.age
    }
}

// ===== 6.2.3 =====
fun foo(l: Long) = println(l)


// ===== 6.2.5 =====
interface Processor<T> {
    fun process(): T
}

class NoResultProcessor : Processor<Unit> {
    override fun process() { // Unit을 반환하지만 타입을 지정할 필요는 없다.
        // 업무 처리 코드
    } // 여기서 return을 명시할 필요가 없다.
}


// ===== 6.2.6 =====
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}


// ===== 6.3.1 =====
fun readNumbers(reader: BufferedReader): List<Int?> {
    val result = ArrayList<Int?>()  // 널이 될 수 있는 Int 값으로 이뤄진 리스트를 만든다.
    for (line in reader.lineSequence()) {
        try {
            val number = line.toInt()
            result.add(number)  // 정수(널이 아닌 값)를 리스트에 추가한다.
        } catch (e: NumberFormatException) {
            result.add(null)    // 현재 줄을 파싱할 수 없으므로 리스트에 널을 추가한다.
        }
    }
    return result
}

fun addValidNumbers(numbers: List<Int?>) {
    var sumOfValidNumbers = 0
    var invalidNumbers = 0
    // 리스트에서 널이 될 수 있는 값을 읽는다.
    for (number in numbers) {
        // 널에 대한 값을 확인한다.
        if (number != null) {
            sumOfValidNumbers += number
        } else {
            invalidNumbers++
        }
    }
    println("Sum of valid numbers: $sumOfValidNumbers")
    println("Invalid numbers: $invalidNumbers")
}

fun addValidNumbers2(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()
    println("Sum of valid numbers: ${validNumbers.sum()}")
    println("Invalid numbers: ${numbers.size - validNumbers.size}")
}

// ===== 6.3.2 =====
fun <T> copyElement(
    source: Collection<T>,
    target: MutableCollection<T>
) {
    for (item in source) {  // source 컬렉션의 모든 원소에 대해 루프를 돈다.
        target.add(item)    // 변경 가능한 target 컬렉션에 원소를 추가한다.
    }
}


// ===== 6.3.3 =====
// 읽기 전용 파라미터를 선언한다
fun printInUppercase(list: List<String>) {
    // 컬렉션을 변경하는 자바 함수를 호출한다
    println(CollectionUtils.uppercaseAll(list))
    // 컬렉션이 변경됐는지 살펴본다
    println(list.first())
}

// ===== 6.3.4 =====
class FileIndexer : FileContentProcessor {
    override fun processContents(
        path: File?,
        binaryContents: ByteArray?,
        textContents: List<String>?
    ) {
        // ...
    }
}

class PersonParser : DataParser<Person> {
    override fun parseData(
        input: String?,
        output: MutableList<Person>,
        errors: MutableList<String?>
    ) {
        // ...
    }
}

