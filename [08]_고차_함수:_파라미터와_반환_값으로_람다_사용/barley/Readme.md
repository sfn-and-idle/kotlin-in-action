# 8장. 고차함수: 파라미터와 반환 값으로 람다 사용

## 입구
- 함수 타입
- 고차 함수와 코드를 구조화할 때 고차 함수를 사용하는 방법
- 인라인 함수
- 비로컬 return과 레이블
- 무명 함수

람다를 인자로 받거나 반환하는 함수인
<span style="color:orange">고차 함수(high order function)</span>를 만드는 방법을 다루어 보자.

고차 함수로 코드를 더 간결하게 다듬고 코드 중복을 없애고 더 나은 추상화를 구축하는 방법을 살펴본다.

또한 람다를 사용함에 따라 발생할 수 있는 성능상 부가 비용을 없애고 
람다 안에서 더 유연하게 흐름을 제어할 수 있는 코틀린 특성인
<span style="color:orange">인라인(inline) 함수</span>에 대해 배워보자.

<br/>
<br/>


## 8.1. 고차 함수 정의

<span style="color:orange">고차 함수</span>는 다른 함수를 인자로 받거나 함수를 반환하는 함수다. 

코틀린에서는 람다나 함수 참조를 사용해 함수를 값으로 표현할 수 있다. 
따라서, 고차 함수는 <span style="color:orange">람다나 함수 참조를 인자로 넘길 수 있거나 
람다나 함수 참조를 반환하는 함수</span>다.

아래 예시의 표준 라이브러리 함수 filter는 술어 함수를 인자로 받으므로 고차 함수다.

```kotlin
list.filter { x > 0 }
```



<br/>


## 8.1.1. 함수 타입

```kotlin
// 타입 추론
val sum = {x: Int, y: Int -> x + y}
val action = { println(42) }
```

이 경우 컴파일러는 sum과 action이 함수 타입임을 추론한다. 
이제는 각 변수에 구체적인 타입 선언을 추가하면 어떻게 되는지 살펴보자.

```kotlin
// 함수 타입 명시
// Int 파라미터 두개를 받아서 Int 값 반환하는 함수
val sum: (Int, Int) -> Int = {x, y -> x + y}
// 아무 인자도 받지 않고 리턴값이 없는(Unit) 함수
val action: () -> Unit = { println(42) }
```

![코틀린 함수 타입 문법](/Users/barley.son/dev/WhatTheKotlinInAction/src/main/kotlin/ch8/img8-1.png)

- `->` 좌측: 함수의 파라미터을 괄호 안에 명시
- `->` 우측: 함수의 반환 타입을 명시

함수 타입을 정의하려면 함수 파라미터의 타입을 괄호 안에 넣고, 그 뒤에 화살표(->)를 추가한 다음, 
함수의 반환 타입을 지정하면 된다. 
그냥 함수를 정의한다면 함수의 파라미터 목록 뒤에 오는 Unit 반환 타입 지정을 생략해도 되지만, 
**함수 타입을 선언할 때는 반환 타입을 반드시 명시해야 하므로 Unit을 빼먹어서는 안 된다.**

이렇게 변수 타입을 함수 타입으로 지정하면 
함수 타입에 있는 파라미터로부터 람다의 파라미터 타입을 유추할 수 있다. 
따라서 람다 식 안에서 굳이 파라미터 타입을 적을 필요가 없다.

다른 함수와 마찬가지로 함수 타입에서도 반환 타입을 널이 될 수 있는 타입으로 지정할 수 있다.

```kotlin
// 반환값이 널이 될 수 있는 경우
var canReturnNull: (Int, Int) -> Int? = {x, y -> null}
// 함수 자체가 널이 될 수 있는 경우
var funOrNull: ((Int, Int) -> Int)? = null
```

canReturnNull의 타입과 funOrNull의 타입 사이에는 큰 차이가 있다. 
funOrNull의 타입을 지정하면서 괄호를 빼먹으면 널이 될 수 있는 타입이 아니라 
널이 될 수 있는 반환 타입을 갖는 함수 타입을 선언하게 된다.


> ### ✅ 파라미터 이름과 함수 타입
> ```kotlin
> fun performRequest(
>     url: String,
>     callback: (code: Int, conetnt: String) -> Unit // 함수 타입의 각 파라미터에 이름을 붙인다
> ) {
> /* ... */
> }
>
> val url = "http://kotl.in"
> performRequest(url) { code, content -> /* ... */ } // API에서 제공하는 이름을 람다에서 사용
> performRequest(url) { code, page -> /* ... */ } // 원하는 이름으로 붙여서 사용
> ```
> 파라미터 이름은 타입 검사 시 무시된다. 
> 이 함수 타입의 람다를 정의할 때 파라미터 이름이 꼭 함수 타입 선언의 파라미터 이름과 일치하지 않아도 되지만, 
> 함수 타입에 인자 이름을 추가하면 코드 가독성이 좋아지고, IDE는 그 이름을 코드 완성에 사용할 수 있다.



<br/>


## 8.1.2. 인자로 받은 함수 호출

```kotlin
// 함수 타입인 파라미터를 선언한다
fun twoAndThree(operation: (Int, Int)-> Int){
    // 함수타입인 파라미터를 호출한다
    val result = operation(2, 3)
    println("The result is $result")
}

>>> twoAndThree{ a, b -> a + b }
The result is 5
>>> twoAndThree{ a, b -> a * b }
The result is 6
```

인자로 받은 함수를 호출하는 구문은 일반 함수를 호출하는 구문과 같다.
예제를 보기 위해 filter 함수를 구현해보자.
예제를 단순히 하기 위해 String에 대한 filter를 구현한다.
하지만 제네릭을 사용해 모든 타입의 원소를 지원하게 구현해도 많이 달라지지는 안는다.

![술어 함수를 파라미터로 받는 filter 함수 정의](/Users/barley.son/dev/WhatTheKotlinInAction/src/main/kotlin/ch8/img8-2.png)

`filter` 함수는 술어를 파라미터로 받는다. 
`predicate` 파라미터는 문자(Char)를 파라미터로 받고 Boolean 결과 값을 반환한다. 
술어는 인자로 받은 문자가 filter 함수가 돌려주는 결과 문자열에 남아있기를 바라면 true, 아니면 false를 반환한다. 
아래는 filter 함수를 구현한 방법이다.

```kotlin
fun String.filter(predicate: (Char) -> Boolean): String{
    val sb = StringBuilder()
    for(index in 0 until length){
        val element = get(index)
        // predicate 파라미터로 전달받은 함수를 호출
        if(predicate(element)) sb.append(element)
    }
    return sb.toString()
}

// 람다를 predicate 파라미터로 전달한다
>>> println("ab1c".filter {it in 'a'..'z'})
abc
```


<br/>


## 8.1.3. 자바에서 코틀린 함수 타입 사용

컴파일된 코드 안에서 함수 타입은 일반 인터페이스로 바뀐다. 
즉 함수 타입의 변수는 `FunctionN` 인터페이스를 구현하는 객체를 저장한다. 

코틀린 표준 라이브러리는 함수 인자의 개수에 따라 Function0<R> (인자가 없는 함수), 
Function1<P1, R> (인자가 1개인 함수) 등의 인터페이스를 제공한다.

각 인터페이스에는 <span style="color:orange">invoke 메소드</span> 정의가 하나 들어있다. 
`invoke`를 호출하면 함수를 실행할 수 있다. 
함수 타입인 변수는 인자 개수에 따라 적당한 FunctionN 인터페이스를 구현하는 클래스의 인스턴스를 저장하며, 
그 클래스의 invoke 메소드 본문에는 람다의 본문이 들어간다.

```kotlin
fun processTheAnswer(f: (Int) -> Int){
    println(f(42))
}
/* 자바 */
>>> processTheAnswer(number -> number+1)
43
```

자바 8 이전의 자바에서는 필요한 FunctionN 인터페이스의 invoke 메소드를 구현하는 무명 클래스를 넘기면 된다.

```
>>> processTheAnswer {
       // 자바 코드에서 코틀린 함수 타입을 사용한다 (자바 8 이전)
...    new Function1<Integer, Integer>() {
...        @override
...        public Integer invoke(Integer number) {
...            System.out.println(number);
...            return number + 1;
...        }
...});
43
```

자바에서 코틀린 표준 라이브러리가 제공하는 람다를 인자로 받는 확장 함수를 쉽게 호출할 수 있다. 
하지만 수신 객체를 확장 함수의 첫 번재 인자로 명시적으로 넘겨야 하므로 
코틀린에서 확장 함수를 호출할 때처럼 코드가 깔끔하지는 않다.

```
>>> List<String< strings = new ArrayList();
>>> strings.add("42")
    // 코틀린 표준 라이브러리에서 가져온 함수를 자바 코드에서 호출할 수 있다
>>> CollectionsKt.forEach(strings, s -> { // strings는 확장 함수의 수신 객체
...    System.out.println(s);
       // Unit 타입의 값을 명시적으로 반환해야만 한다.
...    return Unit.INSTANCE;
...});
```

코틀린 Unit 타입에는 값이 존재하므로 자바에서는 그 값을 명시적으로 반환해줘야 한다. 
`(String) -> Unit`처럼 반환 타입이 Unit인 함수 타입의 파라미터 위치에 
void를 반환하는 자바 람다를 넘길 수는 없다.


<br/>


## 8.1.4. 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 타입 파라미터

파라미터를 함수 타입으로 선언할 때도 디폴트 값을 정할 수 있다.
3장에서 살펴본 joinToString 함수를 함수 타입의 파라미터에 대한 디폴트 값을 지정하여 가공해보자.

```kotlin
fun<T> Collection<T>.joinToString(
    separator: String= ",",
    prefix: String= "",
    postfix: String= ""
): String {
    val result = StringBuilder(prefix)
    for((index, element) in this.withIndex()){
        if(index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    // 기본 toString 메소드를 사용해 객체를 문자열로 변환한다
    return result.toString()
}
```

이 구현은 유연하지만 핵심 요소를 제어할 수 없다는 단점있다. 
그 핵심 요소는 컬렉션의 각 원소를 문자열로 변환하는 방법이다. 
코드는 `StringBuilder.append(o: Any?)`를 사용하는데, 
이 함수는 항상 객체를 toString 메소드를 통해 문자열로 바꾼다. 
toString으로 충분한 경우도 많지만 그렇지 않을 때도 있다. 

이럴 때 함수 타입의 파라미터에 대한 디폴트 값을 지정하면 이런 문제를 해결할 수 있다. 
**디폴트 값으로 람다 식을 넣으면 된다.**

```kotlin
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

// ---- Result ----
val letters = listOf("Alpha", "Beta")

// 디폴트 변환 함수를 사용한다.
println(letters.joinToString())
// 결과: Alpha, Beta

// 람다를 인자로 전달한다
println(letters.joinToString { it.toLowerCase() })
// 결과: alpha, beta

// 이름 붙은 인자 구문을 사용해 람다를 포함하는 여러 인자를 전달한다
println(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.toUpperCase() }))
//결과: ALPHA! BETA!

```

이 함수는 제네릭 함수다. 따라서 컬렉션의 원소 타입을 표현하는 `T`를 타입 파라미터로 받는다. 
transform 람다는 그 T 타입의 값을 인자로 받는다.

다른 디폴트 파라미터 값과 마찬가지로 함수 타입에 대한 디폴트 값 선언도 `=` 뒤에 람다를 넣으면 된다.
- 람다를 아예 생략하거나
  - 람다를 생략하면 디폴트 람다에 있는 대로 toString을 써서 원소를 변환
- 인자 목록 뒤에 람다를 넣거나
  - 여기서는 람다 밖에 전달할 인자가 없어서 () 없이 람다만 썼다
- 이름 붙인 인자로 람다를 전달할 수 있다

다른 방법으로 널이 될 수 있는 함수 타입을 사용할 수 있다.
**널이 될 수 있는 함수 타입을 사용하는 경우 그 함수를 직접 호출할 수 없다.** 
NPE 발생 가능성이 있으므로 컴파일을 거부하게 된다. null 여부를 명시적으로 검사하는 것도 한 가지 해결 방법.

```kotlin
fun foo(callback: (() -> Unit)?) {
    // ...
    if(callback != null) {
        callback()
    }
}
```

전에 말했듯이 함수 타입은 `invoke()` 메소드를 구현하는 인터페이스다.
이 사실을 기억하면 일반 메소드처럼 `invoke()`도 
안전한 호출 구문(`callback?.invoke()`)으로 호출할 수 있다는 것을 알 수 있다.

```kotlin
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    // 널이 될 수 있는 함수 타입의 파라미터를 선언
    transform: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)
    for((index, element) in this.withIndex()) {
        if(index > 0) result.append(separator)
        // 안전 호출을 사용해 함수를 호출
        val str = transform?.invoke(element)
            ?: element.toString() // 엘비스 연산자를 사용해 람다를 인자로 받지 않은 경우 처리
        result.append(str)
    }

    result.append(postfix)
    return result.toString()
}
```

<br/>

## 8.1.5. 함수를 함수에서 반환

함수가 함수를 반환할 필요가 있는 경우보다는 함수가 함수를 인자로 받아야 할 필요가 있는 경우가 훨씬 더 많다.
하지만 함수를 반환하는 함수도 여전히 유용하다. 프로그램의 상태나 다른 조건에 따라 달라질 수 있는 로직이 있다고 생각해보자.
- 예) 사용자가 선택한배송수단에 따라 배송비를 계산하는 방법이 달라질 수 있다
- 이럴 때 적절한 로직을 선택해서 함수로 반환하는 함수를 정의해 사용할 수 있다.

```kotlin
enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCaculator(
        delivery: Delivery
) : (Order) -> Double { // 함수를 반환하는 함수를 선언

    if (delivery == Delivery.EXPEDITED) {
        // 함수에서 람다를 반환
        return { order -> 6 + 2.1 * order.itemCount }
    }
    // (이것도) 함수에서 람다를 반환
    return { order -> 1.2 * order.itemCount }
}

>>> val calculator = getShippingCostCaculator(Delivery.EXPEDITED)
// 반환받은 함수를 호출
>>> println("Shipping costs ${calculator(Order(3))}")
Shipping costs 12.3
```

다른 함수를 반환하는 함수를 정의하려면 함수의 반환 타입으로 함수 타입을 지정해야 한다.
위의 코드에서 `getShippingCostCalculator` 함수는 order 을 받아서 double 을 반환하는 함수를 반환한다.
함수를 반환하려면 return 식에 람다나 멤버 참조, 함수 타입의 값을 계산하는 식 등을 넣으면 된다.

GUI 연락처 관리 앱을 만드는 데 UI의 상태에 따라 어떤 연락처 정보를 표시할지 결정해야 할 필요가 있다고 가정하자.

```kotlin
class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false
}
```

이름이나 성이 D로 시작하는 연락처를 보기 위해 사용자가 D를 입력하면 prefix 값이 변한다.
연락처 목록 표시 로직과 연락처 필터링 UI를 분리하기 위해 연락처 목록을 필터링하는 술어 함수를 만드는 함수를 정의할 수 있다.

```kotlin
data class Person(
        val firstName: String,
        val lastName: String,
        val phoneNumber: String?
)

class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false

    fun getPredicate(): (Person) -> Boolean { // 함수를 반환하는 함수를 정의한다. 
        val startsWithPrefix = { p: Person ->
            p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
        }
        if (!onlyWithPhoneNumber) {
            return startsWithPrefix // 함수 타입의 변수를 반환한다. 
        }
        return { startsWithPrefix(it)
                    && it.phoneNumber != null } // 람다를 반환한다. 
    }
}


// --- Rusult ---
val contacts = listOf(Person("Dmitry", "Jemerov", "123-4567"),
  Person("Svetlana", "Isakova", null))
val contactListFilters = ContactListFilters()
with (contactListFilters) {
  prefix = "Dm"
  onlyWithPhoneNumber = true
}
println(contacts.filter(
  contactListFilters.getPredicate()))
// 결과: [Person(firstName=Dmitry, lastName=Jemerov, phoneNumber=123-4567)]
```

<br/>

## 8.1.6. 람다를 활용한 중복 제거


함수 타입과 람다 식은 재활용하기 좋은 코드를 만들 때 쓸 수 있는 훌륭한 도구다.
람다를 사용할 수 없는 환경에선 아주 복잡한 구조를 만들어야만 피할 수 있는 코드 중복도 람다를 활용한다면 
간결하고 쉽게 중복을 제거할 수 있다.
웹사이트 방문 기록을 분석하는 예를 보도록 하자.

```kotlin
data class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
)

enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)


```

윈도우 사용자의 평균 방문 시간을 출력하고 싶다 하면 다음과 같다.

```kotlin
val averageWindowsDuration = log
    .filter { it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()

println(averageWindowsDuration)
// 결과: 23.0
```

이제 맥 사용자의 평균 방문 시간을 출력할 것인데, 중복을 피하기 위해 OS를 파라미터로 뽑아낸다.

```kotlin
fun List<SiteVisit>.averageDurationFor(os: OS) =
        filter { it.os == os}.map(SiteVisit::duration).average()
        
>>> println(log.averageDurationFor(OS.WINDOWS))
23.0
>>> println(log.averageDurationFor(OS.MAC))
22.0
```

함수를 확장으로 정의함으로써 가독성이 좋아진 것을 볼 수 있다.
이 함수가 어떤 함수 내부에서만 쓰인다면 로컬 확장 함수로 정의할 수 있다.
모바일 디바이스 사용자(IOS, ANDROID)의 평균 방문 시간을 구하고 싶다면 다음과 같이 해야 한다.

```kotlin
val averageMobileDuration = log
    .filter { it.os in setOf(OS.IOS, OS.ANDROID) }
    .map(SiteVisit::duration)
    .average()
    
>>> println(averageMobileDuration)
12.15
```

플랫폼을 표현하는 간단한 파라미터로는 이런 상황을 처리할 수 없어 하드 코딩한 필터를 사용해야 한다.
"IOS 사용자의 /signup 페이지 평균 방문 시간?"과 같은 더 복잡한 질의를 사용해야 한다면 람다를 사용하면 된다.
함수 타입을 사용하면 필요한 조건을 파라미터로 뽑아낼 수 있다.

```kotlin
fun List<SiteVisit>.averageDurationFor2(predicate: (SiteVisit) -> Boolean) =
  filter(predicate).map(SiteVisit::duration).average()
        
>>> println(log.averageDurationFor {
...    it.os in setOf(OS.ANDROID, OS.IOS) })
// 12.15

>>> println(log.averageDurationFor {
...    it.os == OS.IOS && it.path == "/signup"})
// 6.0
```

코드의 일부분을 복사해 붙여 넣고 싶은 경우가 있다면 그 코드를 람다로 만들면 중복을 제거할 수 있다.
변수, 프로퍼티, 파라미터 등을 사용해 데이터의 중복을 없앨 수 있는 것과 같이 람다를 사용해서 코드의 중복을 제거할 수 있다.

> 일부 잘 알려진 (객체 지향) 디자인 패턴틀 함수 타입과 람다식을 사용해 단순화할 수 있다.
> <span style="color:orange">전략 패턴</span>을 생각해보자.
> 람다 식이 없다면 인터페이스를 선언하고 그 인터페이스의 구현 클래스를 통해 전략을 정의해야 한다. 
> 함수 타입을 언어가 지원하면 일반 함수 타입을 사용해 전략을 표현할 수 있고
> 경우에 따라 다른 람다 식을 넘김으로써 여러 전략을 전달할 수 있다.

고차 함수를 여기저기 활용하면 전통적인 루프와 조건문을 사용할 때보다 더 느려지지 않을까?
다음 절에서는 람다를 활용한다고 코드가 항상 더 느려지지는 않는다는 사실을 설명하고
inline 키워드를 통해 어떻게 람다의 성능을 개선하는지 보여준다.

<br/>
<br/>
<br/>


## 8.2. 인라인 함수: 람다의 부가 비용 없애기

코틀린이 함수 타입을 제대로 지원하기 때문에 람다의 활용도가 높은데, 
람다를 사용하면 코드의 성능은 어떨까? 람다를 사용하면 내부적으로 코드가 더 느려지는게 아닐까?

람다가 변수를 포획하면 람다가 생성되는 시점마다 새로운 무명 클래스 객체가 생긴다. 
이런 경우 실행 시점에 무명 클래스 생성에 따른 부가 비용이 든다. 
반복되는 코드를 별도의 라이브러리 함수로 빼내되 컴파일러가 자바의 일반 명령문만큼 효율적인 코드를 생성하게 만들 수는 없을까? 

사실 코틀린 컴파일러에서는 가능하다. 
`inline` 변경자를 어떤 함수에 붙이면 컴파일러는 <span style="color:orange">그 함수를 호출하는 모든 문장을 
함수 본문에 해당하는 바이트코드로 바꿔치기</span> 해준다.

<br/>


## 8.2.1. 인라이닝이 작동하는 방식

어떤 함수를 `inline`으로 선언하면 그 함수의 본문이 인라인된다. 
함수를 호출하는 코드를 함수를 호출하는 바이트 코드 대신에 함수 본문을 번역한 바이트 코드로 컴파일한다는 뜻이다.

아래 함수는 다중 스레드 환경에서 어떤 공유 자원에 대한 동시 접근을 막기 위한 것. 
이 함수는 Lock 객체를 잠그고 주어진 코드 블록을 실행한 다음에 Lock 객체에 대한 잠금을 해제한다. 

```kotlin
// 인라인 함수 정의하기
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

val l = Lock()
synchronized(l) {
    // ...
}
```

이 함수를 호출하는 코드는 자바의 `synchronized` 문과 똑같아 보인다. 
차이는 자바에서는 임의의 객체에 대해 synchronized 를 사용할 수 있지만 
이 함수는 Lock 클래스의 인스턴스를 요구한다는 점뿐이다.
코틀린 표준 라이브러리는 아무 타입의 객체나 인자로 받을 수 있는 synchronized 함수를 제공한다. 

동기화에 명시적인 lock을 사용하면 더 신뢰할 수 있고 관리하기 쉬운 코드를 만들 수 있다.
뒤에서 코틀린 표준 라이브러리가 제공하는 `withLock` 함수를 소개한다.
코틀린에서 락을 건 상태에서 코드를 실행해야 한다면 먼저 withLock을 써도 될지 고려해봐야 한다.

synchronized 함수를 inline으로 선언했으므로 
synchronized를 호출하는 코드는 모두 자바의 synchronized문과 같아진다.

```kotlin
fun foo(l: Lock) {
    println("Before sync")
    synchronized(l) {
        println("Action")
    }  // 마지막 인자가 람다이므로 {} 안에 작성할 수 있다.
    println("After sync")
}
```

아래는 위 코틀린 코드와 동등한 코드를 보여준다.
이 코드는 앞의 코드와 같은 바이트코드를 만들어낸다.

```kotlin
fun __foo__(l: Lock) {
    println("Before sync")    // [1] synchronized 함수를 호출하는 foo 함수의 코드
    l.lock()                  // [2] synchronized 함수가 인라이닝된 코드
    try {                     // [2] synchronized 함수가 인라이닝된 코드
        println("Action")     // [3] 람다 코드의 본문이 인라이닝된 코드
    } finally {               // [2] synchronized 함수가 인라이닝된 코드
        l.unlock()            // [2] synchronized 함수가 인라이닝된 코드
    }                         // [2] synchronized 함수가 인라이닝된 코드
    println("After sync")     // [1] synchronized 함수를 호출하는 foo 함수의 코드
}
```

`synchronized` 함수의 본문뿐 아니라 
<span style="color:orange">synchronized에 전달된 람다의 본문도 함게 인라이닝된다는 점</span>에 유의하자. 
람다의 본문에 의해 만들어지는 바이트코드는 그 람다를 호출하는 코드(synchronized) 정의의 일부분으로 간주되기 때문에 
코틀린 컴파일러는 그 람다를 함수 인터페이스를 구현하는 무명 클래스로 감싸지 않는다.

인라인 함수를 호출하면서 람다를 넘기는 대신에 **함수 타입의 변수**를 넘길 수도 있다.

```kotlin
class LockOwner(val lock: Lock) {
    fun runUnderLock(body: () -> Unit) {
        synchronized(lock, body) // 람다 대신에 함수 타입인 변수를 인자로 넘긴다
    }
}
```

이런 경우에는 인라인 함수(synchronized)를 호출하는 위치에서는 변수에 저장된 람다의 코드를 알 수 없다.
따라서 람다 본문은 인라이닝 되지 않고 synchronized 함수의 본문만 인라이닝된다. 
따라서 람다는 다른 일반적인 경우와 마찬가지로 호출된다.

```kotlin
class LockOwner(val lock: Lock) {
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
```

한 인라인 함수를 두 곳에서 각각 다른 람다를 사용해 호출한다면 그 두 호출은 각각 따로 인라이닝된다. 
인라인 함수의 본문 코드가 호출 지점에 복사되고 
각 람다의 본문이 인라인 함수의 본문 코드에서 람다를 사용하는 위치에 복사된다. (필자: 뭔말임?)


<br/>

## 8.2.2. 인라인 함수의 한계

함수가 인라이닝될 때 **그 함수에 인자로 전달된 람다 식의 본문**은 **결과 코드에 직접 들어갈 수** 있다. 
하지만 이렇게 람다가 본문에 직접 펼쳐지기 때문에 
함수가 파라미터로 전달받은 람다를 본문에 사용하는 방식이 한정될 수밖에 없다.

함수 본문에서 파라미터로 받은 람다를 호출한다면 그 호출을 쉽게 람다 본문으로 바꿀 수 있다. 
하지만 파라미터로 받은 람다를 다른 변수에 저장하고 
나중에 그 변수를 사용한다면 람다를 표현하는 객체가 어딘가는 존재해야 하기 때문에 람다를 인라이닝할 수 없다.

일반적으로...

- 인라인 함수의 본문에서 람다 식을 바로 호출하거나 
- 람다 식을 인자로 전달받아 바로 호출하는 경우 

그 람다를 인라이닝 할 수 있다.
그렇지 않다면 컴파일러는 `Illegal usage of inline-parameter`라는 메시지와 함께 인라이닝을 금지시킨다.

시퀀스에 대해 동작하는 메소드 중에서는 
람다를 받아서 모든 시퀀스 원소에 그 람다를 적용한 새 시퀀스를 반환하는 함수가 많다. 
그런 함수는 인자로 받은 람다를 시퀀스 객체 생성자의 인자로 넘기곤 한다.
(필자: 아래 코드에서는 `transform`)

```kotlin
fun <T, R> Sequence<T>.map(transform: (T) -> R) : Sequence<R> {
    return TransformingSequence(this, transform) 
}
```

이 map 함수는 `transform` 파라미터로 받은 함수를 직접 호출하지 않고 `TransformingSequence`라는 클래스에 전달한다. 
그리고 `TransformingSequence` 생성자는 전달 받은 람다를 프로퍼티로 저장한다.

이런 기능을 지원하려면 map에 전달되는 transform 인자를 일반적인(인라이닝하지 않는) 함수로 만들 수 밖에 없다. 
즉, transform 함수 인터페이스를 구현하는 무명 클래스 인스턴스로 만들어야 한다.

둘 이상의 람다를 인자로 받는 함수에서 일부 람다만 인라이닝하고 싶을 때도 있다. 
인라이닝하면 안 되는 람다를 파라미터로 받는다면
`noinline` 변경자를 파라미터 이름 앞에 붙여서 인라이닝을 금지할 수 있다.

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    // ...
}
```

(필자: 개인적으로 이 절의 핵심이라고 생각되는 부분)
코틀린에서는 어떤 모듈이나 서드파티 라이브러리 안에 인라인 함수를 정의하고 
그 모듈이나 라이브러리 밖에서 해당 인라인 함수를 사용할 수 있다.
또 자바에서도 코틀린에서 정의한 인라인 함수를 호출할 수 있다
이런 경우 컴파일러는 인라인 함수를 인라이닝하지 않고 일반 함수 호출로 컴파일한다.

<br/>


## 8.2.3. 컬렉션 연산 인라이닝

코틀린 표준 라이브러리의 컬렉션 함수는 대부분 람다를 인자로 받는다. 
표준 라이브러리 함수를 사용하지 않고 이런 연산을 구현하면 더 효율적일까?

```kotlin
// 람다를 사용해 컬렉션 거르기
data class Person(val name: String, val age: Int)
val people = listOf(Person("Alice", 29), Person("Bob", 31))

>>> println(people.filter{ it.age < 30 })
[Person(name=Alice, age=29)]
```

이 예제를 람다 식을 사용하지 않게 다시 쓰면 다음과 같다.

```kotlin
// 컬렉션을 직접 거르기
>>> val result = mutableListOf<Person>()
>>> for(person in people) {
        if(person.age < 30) result.add(person)
    }

>>> println(result)
[Person(name=Alice, age=29)]
```

코틀린 `filter` 함수는 인라인 함수다. 
따라서 filter 함수의 바이트코드는 그 함수에 전달된 람다 본문의 바이트 코드와 함게 filter를 호출한 위치에 들어간다. 
그 결과 filter를 써서 생긴 바이트코드와 뒤 예제에서 생긴 바이트코드는 거의 같다. 
코틀린다운 연산을 컬렉션에 안전하게 사용할 수 있고, 코틀린이 제공하는 함수 인라이닝을 믿고 신경쓰지 않아도 된다.

filter와 map을 연쇄해서 사용하면 어떻게 될까?

```kotlin
>>> println(people.filter{ it.age > 30 }).map(Person::name)
[Bob]
```

이 예제는 람다 식와 멤버 참조를 사용한다. 
filter와 map이 둘 다 인라인 함수. 두 함수의 본문은 인라이닝되며, 추가 객체나 클래스 생성은 없다. 

하지만 이 코드는 리스트를 걸러낸 결과를 저장하는 중간 리스트를 만든다.
`filter` 함수에서 만들어진 코드는 원소를 그 중간 리스트에 추가하고, 
`map` 함수에서 만들어진 코드는 그 중간 리스트를 읽어서 사용한다.

처리할 원소가 많아지면 이 중간 리스트를 사용하는 부가 비용도 걱정할 만큼 커진다.
`asSequence를` 통해 리스트 대신 시퀀스를 사용하면 이런 부가 비용이 줄어든다.

이때 각 중간 시퀀스는 람다를 필드에 저장하는 객체로 표현되며, 
최종 연산은 중간 시퀀스에 있는 여러 람다를 연쇄 호출한다. 
따라서 위에서 설명한 대로 시퀀스는 (람다를 저장해야 하므로) 람다를 인라인 하지 않는다. 
따라서 지연 계산을 통해 성능을 향상하려는 이유로 모든 컬렉션 연산에 asSequence 를 붙여서는 안 된다.

시퀀스 연산에서는 람다가 인라이닝되지 않기 때문에 크기가 작은 컬렉션은 오히려 일반 컬렉션 연산이 더 성능이 나을 수 있다. 
따라서, <span style="color:orange">컬렉션 크기가 큰 경우에만 시퀀스를 통해 성능을 향상할 수</span> 있다.

<br/>


## 8.2.4. 함수를 인라인으로 선언해야 하는 경우

(필자: 8장의 핵심인가)

`inline` 키워드를 사용해도 람다를 인자로 받는 함수만 성능이 좋아질 가능성이 있다. 
다른 경우에는 주의 깊게 성능을 측정하고 조사해봐야 한다.

일반 함수 호출의 경우 JVM은 이미 강력하게 인라이닝을 지원한다. 
JVM은 코드 실행을 분석해서 가장 이익이 되는 방향으로 호출을 인라이닝한다. 
이런 과정은 바이트코드를 실제 기계어 코드로 번역하는 과정(JIT)에서 일어난다. 

- 이런 JVM의 최적화를 활용한다면 바이트코드에서는 각 함수 구현이 정확히 한 번만 있으면 되고 
- 그 함수를 호출하는 부분에서 따로 함수 코드를 중복할 필요가 없다. 

반면 코틀린 인라인 함수는 
- 바이트 코드에서 각 함수 호출 지점을 함수 본문으로 대치하기 때문에 코드 중복이 생긴다. 
- 게다가 함수를 직접 호출하면 스택 트레이스가 더 깔끔해진다.

반면 람다를 인자로 받는 함수를 인라이닝하면 이익이 더 많다. 
- 첫째로 인라이닝을 통해 없앨 수 있는 부가 비용이 상당하다. 
  - 함수 호출 비용을 줄일 수 있을 뿐 아니라 
  - 람다를 표현하는 클래스와 람다 인스턴스에 해당하는 객체를 만들 필요도 없어진다. 
- 둘째로 현재의 JVM은 함수 호출과 람다를 인라이닝해 줄 정도로 똑똑하지는 못하다. 
- 마지막으로 인라이닝을 사용하면 일반 람다에서는 사용할 수 없는 몇가지 기능을 사용할 수 있다.
  - 그런 기능 중에는 non-local 변환이 있다. (뒤에서 설명 예정)


inline 변경자를 함수에 붙일 때는 코드 크기에 주의를 기울여야 한다. 
인라이닝하는 함수가 큰 경우 함수의 본문에 해당하는 바이트코드를 모든 호출 지점에 복사해 넣고 나면 
바이트코드가 전체적으로 아주 커질 수 있다.

<br/>


## 8.2.5. 자원 관리를 위해 인라인된 람다 사용

람다로 중복을 없앨 수 있는 일반적인 패턴 중 한 가지는 어떤 작업을 하기 전에 자원을 획득하고
작업을 마친 후 자원을 해제하는 자원관리다.

- <span style="color:orange">자원(resource)</span> : 
  파일, 락, 데이터베이스 트랜잭션 등 여러 다른 대상을 가리킬 수 있다. 

자원 관리 패턴을 만들 때 보통 사용하는 방법은 <span style="color:orange">try/finally문</span>을 사용하되 
try 블록을 시작하기 직전에 자원을 획득하고 finally 블록에서 자원을 해제하는 것이다.

이전 예제 
- try/finally문의 로직을 함수로 캡슐화하고 자원을 사용하는 코드를 람다식으로 그 함수에 전달.
- 자바의 synchronized문과 똑같은 구문을 제공하는 synchronized 함수가 있었다.

synchronized 함수는 락 객체를 인자로 취한다.
코틀린 라이브러리에는 좀 더 코틀린다운 API를 통해 같은 기능을 제공하는 
<span style="color:orange">withLock</span>이라는 함수도 있다.
withLock은 Lock 인터페이스의 확장 함수.

```kotlin
val l: Lock = ...
l.withLock { // <-- 락을 잠근 다음에 주어진 동작을 수행
    // lock에 의해 보호되는 자원을 사용
}
```

다음은 코틀린 라이브러리에 있는 withLock 함수 정의.

```kotlin
fun <T> Lock.withLock(action: () -> T): T { // <-- 락을 획득한 후 작업하는 과정을 별도의 함수로 분리
    lock()
    try {
        return action()
    } finally {
        unlock()
    }
}
```

이런 패턴을 사용할 수 있는 다른 유형의 자원 --> 파일

자바 7부터는 자원을 관리하기 위한 특별한 구문인 `try-with-resource`문이 생겼다.

```kotlin
static String readFirstLineFromFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}
```

코틀린에서는 함수 타입의 값을 파라미터로 받은 함수(따라서 이 함수는 람다를 인자로 받는다)를 통해
아주 매끄럽게 이를 처리할 수 있으므로, 코틀린 언어는 이와 같은 기능을 언어 구성 요소로 제공하지는 않는다. 
대신 자바 `try-with-resource`와 같은 기능을 제공하는 
<span style="color:orange">use</span>라는 함수가 코틀린 표준 라이브러리 안에 들어있다.

```kotlin
fun readFirstLineFromFile(path: String): String {
  // BufferedReader 객체를 만들고 use 함수를 호출하면서 파일에 대한 연산을 실행할 람다를 넘긴다.
    BufferedReader(FileReader(path)).use { br ->
        // 자원(파일)에서 맨 처음 가져온 한줄을 람다가 아닌 
        // readFirstLineFromFile에서 반환한다. 
        return br.readLine()
    }
}
```

use 함수는 닫을 수 있는(closeable) 자원에 대한 확장 함수며, 람다를 인자로 받는다. 
use는 람다를 호출한 다음에 자원을 닫아준다.
use 함수도 인라인 함수. 따라서 사용해도 성능에는 영향이 없다.

<br/>
<br/>
<br/>



## 8.3. 고차 함수 안에서 흐름 제어

루프와 같은 코드를 람다로 바꾸다보면 곧 return 문제에 부딪칠 것이다.
루프의 중간에있느느 return문의 의미를 이해하기는 쉽다.
하지만 그 루프를 filter와 같이 람다를 호출하는 함수로 바꾸고
인자로 전달하는 람다안에서 return을 사용하면???

<br/>

## 8.3.1. 람다 안의 return문: 람다를 둘러싼 함수로부터 반환

이름이 Alice인 경우에 lookForAlice 함수로부터 반환된다.

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(Person("Alice", 29), Person("Bob", 31))

fun lookForAlice(people: List<Person>) {
  for(person in people) {
    if(person.name = "Alice") {
      println("Found!")
      return
    }
  }
  println("Alice is not found") // people 안에 엘리스가 없으면 이 줄이 출력
}

>>> lookForAlie(people)
Found!
```

루프를 forEach로 바꾸어도 동일하다.

```kotlin
fun lookForAlice(people: List<Person>){
    people.forEach {
        if(it.name == "Alice") {
            println("Found!")
            return // 위 코드와 동일하게 lookForAlice 함수에서 반환
        }
    }
    println("Alice is not founded")
}
```

람다 안에서 return을 사용하면 람다로부터만 반환되는 것이 아니라 
그 람다를 호출하는 함수가 실행을 끝내고 반환된다. 
자신을 둘러싸고 있는 블록보다 **더 바깥에 있는 다른 블록을 반환하게 만드는 return문**을
<span style="color:orange">넌로컬(non-local) return</span>이라 부른다.

synchronized 블록 안이나 for 루프 안에서 `return`은 
synchronized 블록이나 for 루프를 끝내지 않고 메소드를 반환시킨다. 
코틀린에서는 언어가 제공하는 기본 구성 요소가 아니라 
**람다를 받는 함수로 for나 synchronized와 같은 기능을 구현**한다. 
코틀린은 그런 함수 안에서 쓰이는 return이 자바의 return과 같은 의미를 갖게 허용한다.

이렇게 return이 바깥쪽 함수를 반환시킬 수 있는 때는 **람다를 인자로 받는 함수가 인라인 함수인 경우**뿐이다. 

- 인라이닝되지 않는 함수는 람다를 변수에 저장할 수 있고, 
- 바깥쪽 함수로부터 반환된 뒤에 저장해 둔 람다가 호출될 수도 있다. 

그런 경우 람다 안의 return이 실행되는 시점이 바깥쪽 함수를 반환시키기엔 너무 늦은 시점일 수도 있다.

<br/>


## 8.3.2. 람다로부터 반환: 레이블을 사용한 return

람다식에서도 `로컬 return`을 사용할 수 있다. 
람다 안에서 로컬 return은 for 루프의 `break` 와 비슷한 역할을 한다. 
로컬 return은 람다의 실행을 끝내고 람다를 호출했던 코드의 실행을 계속 이어간다. 
로컬 return과 넌로컬 return을 구분하기 위해 label을 사용해야 한다.

```kotlin
fun lookForAlice2(people: List<Person>){
    people.forEach label@{ // 람다 식 앞에 레이블을 붙인다
        if(it.name=="Alice") return@label // 앞에서 정의한 레이블을 참조한다.
    }
    println("Alice might be somewhere") // 항상 이 줄이 출력된다
}
```

람다 식에 레이블을 붙이려면 레이블 이름 뒤에 `@` 문자를 추가한 것을 람다를 여는 `{` 앞에 넣으면 된다.
람다로부터 반환하려면 return 키워드 뒤에 `@` 문자와 레이블을 차례로 추가하면 된다.

람다에 레이블을 붙여서 사용하는 대신 람다를 인자로 받는 인라인 함수의 이름을 return 뒤에 레이블로 사용해도 된다.
람다 식의 레이블을 명시할 경우 함수 이름을 레이블로 사용할 수 없다.

```kotlin
fun lookForAlice(people: List<Person>) {
  people.forEach {
    if (it.name == "Alice") return@forEach // return@forEach는 람다식으로부터 반환시킨다 
  }
  println("Alice might be somewhere")
}
```

> ### ✅ 레이블이 붙은 this 식
> `this`식의 레이블에도 동일한 규칙이 적용된다.
> 수신 객체 지정 람다의 본문에서 this 참조를 사용해 
> 묵시적인 컨텍스트 객체(람다를 만들 때 지정한 수신 객체)를 가리킬 수 있다.
> 수신 객체 지정 람다 앞에 레이블을 붙인 경우 this 뒤에 그 레이블을 붙여서 묵시적인 컨텍스트 객체를 지정할 수 있다.
> ```kotlin
> println(StringBuilder().apply sb@{ // this@sb를 통해 이 람다의 묵시적 수신 객체에 접근할 수 있다
>   listOf(1, 2, 3).apply { // this는 이 위치를 둘러싼 가장 안쪽 영역의 묵시적 수신 객체를 가리킨다
> 
>     // 모든 묵시적 수신 객체를 사용할 수 있다.
>     // 다만 바깥쪽 묵시적 수신 객체에 접근할 때는 레이블을 명시해야 한다
>     this@sb.append(this.toString())
>   }
> })
> 
> [1, 2, 3] 
> ```
> 
> 레이블 붙은 return와 마찬가지로 이 경우에도 람다 앞에 명시한 레이블을 사용하거나 
> 람다를 인자로 받는 함수 이름을 사용할 수 있다.


넌로컬 반환문은 장황하고, 람다 안의 여러 위치에 return 식이 들어가야 하는 경우 사용하기 불편하다.
넌로컬 반환문을 여럿 사용해야 하는 코드 블록을 쉽게 작성하기 위해 `무명 함수`를 알아야 한다.


<br/>

## 8.3.3. 무명 함수: 기본적으로 로컬 return

<span style="color:orange">무명 함수</span>는 코드 블록을 함수에 넘길 때 사용할 수 있는 방법이다.

```kotlin
fun lookForAlice4(people: List<Person>){
    people.forEach(fun (person) { // 람다 식 대신 무명 함수를 사용
        // // return 은 가장 가까운 함수를 가리키는데 이 위치에서 사장 가까운 함수는 무명함수다.
        if(person.name == "Alice") return 
        println("${person.name} is not Alice")
    })
}
```

무명함수는 일반함수와 비슷해 보이지만 함수이름, 파라미터 타입을 생략할 수 있다.

```kotlin
people.filter(fun (person): Boolean {
  return person.age < 30
})
```

무명 함수도 일반 함수와 같은 반환 타입 지정 규칙을 따른다.
블록이 본문인 무명 함수는 반환 타입을 명시해야 하지만, 식을 본문으로 하는 무명 함수의 반환 타입은 생략할 수 있다.

```kotlin
people.filter(fun(person) = person.age < 30)
```

무명함수 안에서의 레이블이 없다면 무명함수 자체를 반환 시킬뿐 무명함수를 둘러싼 어느 함수라도 반환시키지 않는다.
사실 return에 적용되는 규칙은 **단순히 return은 fun 키워드를 사용해 정의된 가장 안쪽 함수를 반환**시킨다는 점이다.

람다 식은 fun을 사용해 정의되지 않으므로 람다 본문의 return은 람다 밖의 함수를 반환시킨다.
무명 함수는 fun을 사용해 정의되므로 그 함수 자신이 바로 가장 안쪽에 있는 fun으로 정의된 함수이다.
따라서 무명 함수 본문의 return은 그 무명 함수를 반환시키고, 밖의 다른 함수를 반환시키지 못한다.

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach(fun(person) {
        if(person.name == "Alice") return  // 가장 안쪽의 fun(person) 익명함수를 리턴
    })
}

fun lookForAlice(people: List<Person>) {
    people.forEach({
        if(it.name == "Alice") return      // fun lookForAlice를 리턴
    })
}
```

무명 함수는 일반 함수와 비슷해 보이지만 실제로는 람다 식에 대한 문법적 편의일 뿐이다.
람다 식의 구현 방법이나 람다 식을 인라인 함수에 넘길 때 
어떻게 본문이 인라이닝 되는지 등의 규칙을 무명 함수에도 적용할 수 있다.

<br/>
<br/>
<br/>


## 8.4. 요약

- **함수 타입**을 사용해 함수에 대한 참조를 담는 변수나 파라미터나 반환 값을 만들 수 있다.
- **고차 함수**는 다른 함수를 인자로 받거나 함수를 반환한다. 
  - 함수의 파라미터 타입이나 반환 타입으로 함수 타입을 사용하면 고차 함수를 선언할 수 있다.
- 인라인 함수를 컴파일할 때 컴파일러는 
  그 함수의 본문과 그 함수에게 전달된 람다의 본문을 컴파일한 바이트코드를 모든 함수 호출 지점에 삽입해준다. 
  - 이렇게 만들어지는 바이트코드는 람다를 활용한 인라인 함수 코드를 풀어서 직접 쓴 경우와 비교할 때 부가 비용이 들지 않는다.
- 고차 함수를 사용하면 컴포넌트를 이루는 각 부분의 코드를 더 잘 재사용할 수 있다. 
  또 고차 함수를 활용해 강력한 재네릭 라이브러리를 만들 수 있다.
- 인라인 함수에서는 람다 안에 있는 return 문이 바깥쪽 함수를 반환시키는 **non-local return**을 사용할 수 있다.
- 무명 함수는 람다 식을 대신할 수 있으며 return 식을 처리하는 규칙이 일반 람다 식과는 다르다. 
  - 본문 여러 곳에서 return 해야 하는 코드 블록을 만들어야 한다면 람다 대신 무명 함수를 쓸 수 있다.
  

<br/>
<br/>
<br/>
<br/>