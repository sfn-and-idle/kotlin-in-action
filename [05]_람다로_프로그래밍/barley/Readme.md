# 5장. 람다로 프로그래밍

## 입구

**람다식 또는 람다는 기본적으로 다른 함수에 넘길 수 있는 작은 코드 조각을 의미**한다.
람다를 주로 사용하는 경우는 컬렉션 처리를 예로 들 수 있겠다.
이 장에서는 컬렉션을 처리하는 패턴을 표준 라이브러리 함수에 람다로 넘기는 방식,
자바 라이브러리와 람다를 함께 사용하는 방법도 살펴본다.
그리고 수신 객체 지정 람다(lambda with receiver)에 대해서도 살펴본다.

<br/>

## 5.1. 람다 식과 멤버 참조

## 5.1.1.  람다 소개: 코드 블록을 함수 인자로 넘기기

"이벤트가 발생하면 이 핸들러를 실행하자"나 "데이터 구조의 모든 원소에 이 연산을 적용하자"와 같은 생각을 
코드로 표현하기 위해 일련의 동작을 변수에 저장하거나 다른 함수에 넘겨야 하는 경우가 자주 있다.
예전에 자바에서는 무명 내부 클래스를 통해 이런 목적을 달성했다.
무명 내부 클래스를 사용하면 코드를 함수에 넘기거나 변수에 저장할 수 있기는 하지만 상당히 번거롭다.

이와 달리 함수형 프로그래밍에서는 함수를 값처럼 다루는 접근 방법을 택함으로써 이 문제를 해결한다.
클래스를 선언하고 그 클래스의 인스턴스를 함수에 넘기는 대신 함수형 언어에서는 함수를 직접 다른 함수에 전달할 수 있다.

람다 식을 사용하면 코드가 더욱 더 간결해진다. 
람다 식을 사용하면 함수를 선언할 필요가 없고 코드 블록을 직접 함수의 인자로 전달할 수 있다.

```java
/* 자바 */
button.setOnClickListener(new onClickListener() {
    @Override
    public void onClick(View view) {
        /* 클릭 시 수행할 동작 */
    }
});
```

무명 내부 클래스를 선언하느라 코드가 번잡스러워졌다.
이와 비슷한 작업을 많이 수행해야 하는 경우 그런 번잡함은 난잡함으로 변해 개발자를 괴롭힌다.
클릭 시 벌어질 동작을 간단히 기술할 수 있는 표기법이 있다면 이런 불필요한 코드를 제거할 수 있을 것이다.

```kotlin
button.setOnClickListener { /* 클릭 시 수행할 동작 */ }
```

이 코틀린 코드는 앞에서 살펴본 자바 무명 내부 클래스와 같은 역할을 하지만 훨씬 더 간결하고 읽기 쉽다.


## 5.1.2. 람다와 컬렉션

코드에서 중복을 제거하는 것은 프로그래밍 스타일을 개선하는 중요한 방법 중 하나다.
컬렉션을 다룰 때 수행하는 대부분의 작업은 몇 가지 일반적인 패턴에 속한다. 
따라서 그런 패턴은 라이브러리 안에 있어야 한다. 
하지만 람다가 없다면 컬렉션을 편리하게 처리할 수 있는 좋은 라이브러리를 제공하기 힘들다.

그에 따라 자바에서 쓰기 편한 컬렉션 라이브러리가 적었으며, 
그에 따라 자바 개발자들은 필요한 컬렉션 기능을 직접 작성하곤 했다.
코틀린에서는 이런 습관을 버려야 한다.

```kotlin
data class Person(val name: String, val age: Int)
```

사람들로 이뤄진 리스트가 있고 그중에 가장 연장자를 찾고 싶다. 
람다를 사용해본 경험이 없는 개발자라면 루프를 써서 직접 검색을 구현할 것이다.

```kotlin
fun findTheOldest(people: List<Person>) {
    var maxAge = 0  // 가장 많은 나이를 저장한다.
    var theOldest: Person? = null   // 가장 연장자인 사람을 저장한다.
    for (person in people) {
        // 현재까지 발견한 최연장자보다 더 나이가 많은 사람을 찾으면 최댓값을 바꾼다.
        if (person.age > maxAge) {
            maxAge = person.age
            theOldest = person
        }
    }
    println(theOldest)
}

>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))
>>> findTheOldest(people)
Person(name=Bob, age=31)
```
코틀린에서는 더 좋은 방법이 있다. 라이브러리 함수를 쓰면 된다.

```kotlin
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))
>>> println(people.maxBy { it.age }) // 나이 프로퍼티를 비교해서 값이 가장 큰 원소 찾기
Person(name=Bob, age=31)
```

- maxBy : 가장 큰 원소를 찾기 위해 비교에 사용할 값을 돌려주는 함수
- { it.age } : 바로 비교에 사용할 값을 돌려주는 함수 (it이 그 인자를 가리킨다.)

이 예제에서는 컬렉션의 원소가 Person 객체였으므로 이 함수가 반환하는 값은 
Person 객체의 age 필드에 저장된 나이 정보다.

```kotlin
people.maxBy(Person::age)
```

<br/>


## 5.1.3. 람다 식의 문법

람다는 값처럼 여기저기 전달할 수 있는 동작의 모음이다.
람다를 선언해서 변수에 저장할 수도 있다.
하지만 함수에 인자로 넘기면서 바로 람다를 정의하는 경우가 대부분이다.

```
  파라미터             본문
{ x: Int, y: Int -> x + y }
항상 중괄호 사이에 위치함
```

코틀린 람다 식은 항상 중괄호로 둘러싸여 있다. 화살표(→) 가 인자 목록과 람다 본문을 구분해준다.
람다 식은 변수에 저장할 수 있다. 람다가 저장된 변수를 다른 일반 함수와 마찬가지로 다룰 수 있다.

```kotlin
>>> val sum = { x: Int, y: Int -> x + y }
>>> println(sum(1, 2))
3
```

원한다면 람다 식을 직접 호출해도 된다.

```kotlin
>>> { println(42) } ()
42
```

하지만 이런 코드는 그다지 쓸모가 없다. 
만약 이렇게 코드의 일부분을 블록으로 둘러싸 실행할 필요가 있다면 run을 사용한다. 
`run`은 인자로 받은 람다를 실행해주는 라이브러리 함수이다.
실행 시점에 코틀린 람다 호출에는 아무 부가 비용이 들지 않으며, 프로그램의 기본 구성요소와 비슷한 성능을 낸다.

```kotlin
>>> run { println(42) } // 람다 본문에 있는 코드를 실행한다.
42
```
```kotlin
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))
>>> println(people.maxBy { it.age })
Person(name=Bob, age=31)
```

이 예제에서 코틀린이 코드를 줄여 쓸 수 있게 제공했던 기능을 제거하고 정식으로 람다를 작성하면 다음과 같다.

```kotlin
people.maxBy({p: Person -> p.age})
```
중괄호 안에 있는 코드는 람다 식이고 그 람다 식을 maxBy 함수에 넘긴다.
람다 식은 Person 타입의 값을 인자로 받아서 인자의 age를 반환한다.

하지만 이 코드는 번잡하다. 우선 구분자가 너무 많이 쓰여서 가독성이 떨어진다. 
그리고 컴파일러가 문맥으로부터 유추할 수 있는 인자 타입을 굳이 적을 필요는 없다. 
마지막으로 인자가 단 하나뿐인 경우 인자에 이름을 붙이지 않아도 된다.

먼저 중괄호부터 시작하면 코틀린에는 함수 호출 시 맨 뒤에 있는 인자가 람다 식이라면 
그 람다를 괄호 밖으로 빼낼 수 있다는 문법 관습이 있다. 이 예제에서는 람다가 유일한 인자이므로 마지막 인자이기도 하다. 
따라서 괄호 뒤에 람다를 둘 수 있다.

```kotlin
people.maxBy() { p: Person -> p.age }
```

이 코드처럼 람다가 어떤 함수의 유일한 인자이고 괄호 뒤에 람다를 썼다면 호출 시 빈 괄호를 없애도 된다.

```kotlin
people.maxBy { p: Person -> p.age }
```

람다가 함수의 유일한 인자라면 괄호 없이 람다를 바로 쓰기 원할 것이다.
인자가 여럿 있는 경우에는 람다를 밖으로 빼낼 수도 있고 람다를 괄호 안에 유지해서 함수의 인자임을 분명히 할 수도 있다.
둘 이상의 람다를 인자로 받는 함수라고 해도 인자 목록의 맨 마지막 람다만 밖으로 뺄 수 있다.
따라서 그런 경우에는 괄호를 사용하는 일반적인 함수 호출 구문을 사용하는 편이 낫다.

```kotlin
>>> val people = listOf(Person("이몽룡", 29), Person("성춘향", 31))
>>> val names = people.joinToString(separator = " ", transform = { p: Person -> p.name })
>>> println(names)
이몽룡 성춘향
```

이 함수 호출에서 함수를 괄호 밖으로 뺀 모습은 다음과 같다.

```kotlin
people.joinToString(" ") { p: Person -> p.name }
```

처음 코드는 이름 붙은 인자를 사용해 람다를 넘김으로써 람다를 어떤 용도로 쓰는지 더 명확히 했다.
다음 코드는 더 간결하지만 람다의 용도를 분명히 알아볼 수는 없다.

```kotlin
people.maxBy { p: Person -> p.age } // 파라미터 타입을 명시
people.maxBy { p -> p.age } // 파라미터 타입을 생략(컴파일러가 추론)
```

maxBy 함수의 경우 파라미터의 타입은 항상 컬렉션 원소 타입과 같다.
컴파일러는 여러분이 Person 타입의 객체가 들어있는 컬렉션에 대해 maxBy를 호출한다는 사실을 알고 있으므로 
람다의 파라미터도 Person이라는 사실을 이해할 수 있다.

람다의 파라미터 이름을 디폴트 이름인 it으로 바꾸면 람다 식을 더 간단하게 만들 수 있다.
람다의 파라미터가 하나뿐이고 그 타입을 컴파일러가 추론할 수 있는 경우 it을 바로 쓸 수 있다.

```kotlin
people.maxBy { it.age } // "it"은 자동 생성된 파라미터 이름이다.
```

람다 파라미터 이름을 따로 지정하지 않은 경우에만 it이라는 이름이 자동으로 만들어진다.


> it을 사용하는 관습은 코드를 아주 간단하게 만들어준다.
하지만 이를 남용하면 안된다.
**특히 람다 안에 람다가 중첩되는 경우 각 람다의 파라미터를 명시하는 편이 낫다.**
파라미터를 명시하지 않으면 각각의 it이 가리키는 파라미터가 어떤 람다에 속했는지 파악하기 어려울 수 있다.
문맥에서 람다 파라미터의 의미나 파라미터의 타입을 쉽게 알 수 없는 경우에도 파라미터를 명시적으로 선언하면 도움이 된다.

람다를 변수에 저장할 때는 파라미터의 타입을 추론할 문맥이 존재하지 않는다.
따라서 파라미터 타입(여기서는 Person)을 명시해야 한다.

```kotlin
>>> val getAge = { p: Person -> p.age }
>>> people.maxBy(getAge)
```

본문이 여러 줄로 이뤄진 경우 본문의 맨 마지막에 있는 식이 람다의 결과 값이 된다.

```kotlin
>>> val sum = { x: Int, y: Int ->
        println("Computing the sum of $x and $y...")
        x + y
    }
>>> println(sum(1,2))
```

<br/>


## 5.1.4. 현재 영역에 있는 변수에 접근

자바 메소드 안에서 무명 내부 클래스를 정의할 때 메소드의 로컬 변수를 무명 내부 클래스에서 사용할 수 있다.
람다 안에서도 같은 일을 할 수 있다.
람다를 함수 안에서 정의하면 함수의 파라미터뿐 아니라 
람다 정의의 앞에 선언된 로컬 변수까지 람다에서 모두 사용할 수 있다.

```kotlin
fun printMessageWithPrefix(message: Collection<String>, prefix: String) {
    message.forEach {   // 각 원소에 대해 수행할 작업을 람다로 받는다.
        println("$prefix $it")  // 람다 안에서 함수의 "prefix" 파라미터를 사용한다.
    }
}

>>> val errors = listOf("403 Forbidden", "404 Not Found")
>>> printMessageWithPrefix(errors, "Error:")
Error: 403 Forbidden
Error: 404 Not Found
```

자바와 다른 점 중 중요한 한 가지는 코틀린 람다 안에서는 파이널 변수가 아닌 변수에 접근할 수 있다는 점이다.
또한 람다 안에서 바깥의 변수를 변경해도 된다.
다음 코드는 전달받은 상태 코드 목록에 있는 클라이언트와 서버 오류의 횟수를 센다.

```kotlin
fun printProblemCounts(responses: Collection<String>) {
    var clientErrors = 0
    var serverErrors = 0
    responses.forEach {
        if (it.startsWith("4")) {
            clientErrors++
        } else if (it.startsWith("5")) {
            serverErrors++
        }
    }
    println("$clientErrors client errors, $serverErrors server errors")
}

>>> val responses = listOf("200 OK", "418 I'm a teapot", "500 Internal Server Error")
>>> printProblemCounts(responses)
1 client errors, 1 server errors
```

코틀린에서는 자바와 달리 람다에서 람다 밖 함수에 있는 파이널이 아닌 변수에 접근할 수 있고, 
그 변수를 변경할 수도 있다.
이 예제의 prefix, clientErrors, serverErrors와 같이 람다 안에서 사용하는 외부 변수를 
**람다가 포획(capture)한 변수**라고 부른다.

> `클로저`(closure)
> - 람다를 실행 시점에 표현하는 데이터 구조는...
>   - 람다에서 시작하는 모든 참조가 닫힌 객체 그래프를 람다 코드와 함께 저장해야 한다.
> - 함수를 쓸모 있는 1급 시민으로 만드려면 포획한 변수를 제대로 처리해야 하고, 
>   포획한 변수를 제대로 저리하려면 클로저가 꼭 필요하다.
> - 그래서 람다를 클로저라고 부르기도 한다.
> - 람다, 무명 함수, 함수 리터럴, 클로저를 서로 혼용하는 일이 많다.

기본적으로 함수 안에 정의된 로컬 변수의 생명주기는 함수가 반환되면 끝난다.
하지만 어떤 함수가 자신의 로컬 변수를 포획한 람다를 반환하거나 다른 변수에 저장한다면 
로컬 변수의 생명주기와 함수의 생명주기가 달라질 수 있다.

포획한 변수가 있는 람다를 저장해서 함수가 끝난 뒤에 실행해도 람다의 본문 코드는 
여전히 포획한 변수를 읽거나 쓸 수 있다. 
어떻게 그런 동작이 가능할까?
파이널 변수를 포획한 경우에는 람다 코드를 변수 값과 함께 저장한다.
파이널이 아닌 변수를 포획한 경우에는 변수를 특별한 래퍼로 감싸서 나중에 변경하거나 읽을 수 있게 한 다음, 
래퍼에 대한 참조를 람다 코드와 함께 저장한다.

> ### ✅변경 가능한 변수 포획하기: 자세한 구현 방법
> 자바에서는 파이널 변수만 포횔할 수 있다.
하지만 교묘한 속임수를 통해 변경 가능한 변수를 포획할 수 있다.
그 속임수는 변경 가능한 변수를 저장하는 원소가 단 하나뿐인 배열을 선언하거나, 
변경 가능한 변수를 필드로 하는 클래스를 선언하는 것이다
(안에 들어있는 원소는 변경 가능할지라도 배열이나 클래스의 인스턴스에 대한 참조를 final로 만들면 포획이 가능하다).
이런 속임수를 코틀린으로 작성하면 다음과 같다.
> ```kotlin
> class Ref<T>(var value: T) // 변경 가능한 변수를 포획하는 방법을 보여주기 위한 클래스
> 
> >>> val counter = Ref(0)
> 
> // 공식적으로 변경 불가능한 변수를 포획했지만 그 변수가 가리키는 객체의 필드 값을 바꿀 수 있다.
> >>> val inc = { counter.value++ }
> ```
> 실제 코드에서는 이런 래퍼를 만들지 않아도 된다.
대신, 변수를 직접 바꾼다.
> ```kotlin
> var counter = 0
> var inc = { counter++ }
> ```
> 이 코틀린 코드가 어떻게 작동할까?
첫 번째 예제는 두 번째 예제가 작동하는 내부 모습을 보여준다.
람다가 파이널 변수(val)를 포획하면 자바와 마찬가지로 그 변수의 값이 복사된다.
하지만 람다가 변경 가능한 변수(var)를 포획하면 변수를 Ref 클래스 인스턴스에 넣는다.
그 Ref 인스턴스에 대한 참조를 파이널로 만들면 쉽게 람다로 포획할 수 있고, 
람다 안에서는 Ref 인스턴스의 필드를 변경할 수 있다.

한 가지 꼭 알아둬야 할 함정이 있다.
람다를 이벤트 핸들러나 다른 비동기적으로 실행되는 코드로 활용하는 경우 
함수 호출이 끝난 다음에 로컬 변수가 변경될 수도 있다.
예를 들어 다음 코드는 버튼 클릭 횟수를 제대로 셀 수 없다.

```kotlin
fun tryToCountButtonClicks(button: Button): Int {
    var clicks = 0
    button.onClick { clicks++ }
    return clicks
}
```

이 함수는 항상 0을 반환한다.
onClick 핸들러는 호출될 때마다 clicks의 값을 증가시키지만 그 값의 변경을 관찰할 수는 없다.
핸들러는 tryToCountButtonClicks가 clicks를 반환한 다음에 호출되기 때문이다.
이 함수를 제대로 구현하려면 클릭 횟수를 세는 카운터 변수를 함수의 내부가 아니라 
클래스의 프로퍼티나 전역 프로퍼티 등의 위치로 빼내서 나중에 변수 변화를 살펴볼 수 있게 해야 한다.

<br/>


## 5.1.5. 멤버 참조

하지만 넘기려는 코드가 이미 함수로 선언된 경우는 어떻게 해야할까?
물론 그 함수를 호출하는 람다를 만들면 된다.
하지만 이는 중복이다. 함수를 직접 넘길 수는 없을까?

코틀린에서는 자바 8과 마찬가지로 함수를 값으로 바꿀 수 있다. 이때 이중 콜론을 사용한다.

```kotlin
val getAge = Person::age
```

::를 사용하는 식을 **멤버 참조**라고 부른다.
멤버 참조는 프로퍼티나 메소드를 단 하나만 호출하는 함수 값을 만들어준다.
::는 클래스 이름과 여러분이 참조하려는 멤버(프로퍼티나 메소드) 이름 사이에 위치한다.

```kotlin
val getAge = { person: Person -> person.age }
```

참조 대상이 함수인지 프로퍼티인지와는 관계없이 멤버 참조 뒤에는 괄호를 넣으면 안된다.
멤버 참조는 그 멤버를 호출하는 람다와 같은 타입이다. 따라서 다음 예처럼 그 둘을 자유롭게 바꿔 쓸 수 있다.

```kotlin
fun salute() = println("Salute!")
>>> run(::salute)   // 최상위 함수를 참조한다.
Salute!
```

클래스 이름을 생략하고 ::로 참조를 바로 시작한다.
::salute라는 멤버 참조를 run 라이브러리 함수에 넘긴다(run은 인자로 받은 람다를 호출한다).
람다가 인자가 여럿인 다른 함수한테 작업을 위임하는 경우 
람다를 정의하지 않고 직접 위임 함수에 대한 참조를 제공하면 편리하다.

```kotlin
val action = { person: Person, message: String ->   // 이 람다는 sendEmail 함수에게 작업을 위임한다.
    sendEmail(person, message)
}
val nextAction = ::sendEmail // 람다 대신 멤버 참조를 쓸 수 있다.
```

생성자 참조를 사용하면 클래스 생성 작업을 연기하거나 저장해둘 수 있다.
:: 뒤에 클래스 이름을 넣으면 생성자 참조를 만들 수 있다.

```kotlin
data class Person(val name: String, val age: Int)

>>> val createPerson = ::Person // "Person"의 인스턴스를 만드는 동작을 값으로 저장한다.
>>> val p = createPerson("Alice", 29)
>>> println(p)
Person(name=Alice, age=29)
```

확장 함수도 멤버 함수와 똑같은 방식으로 참조할 수 있다는 점을 기억하라.

```kotlin
fun Person.isAdult() = age >= 21
val predicate = Person::isAdult
```

isAdult는 Person클래스의 멤버가 아니고 **확장 함수**다.
그렇지만 isAdult를 호출할 때 person.isAdult()로 인스턴스 멤버 호출 구문을 쓸 수 있는 것처럼 
Person::isAdult로 멤버 참조 구문을 사용해 이 확장 함수에 대한 참조를 얻을 수 있다.

> ### ✅바운드 멤버 참조
> 코틀린 1.0에서는 클래스의 메소드나 프로퍼티에 대한 참조를 얻은 다음에 그 참조를 호출할 때 
> 항상 인스턴스 객체를 제공해야 했다. 
> 코틀린 1.1부터는 **바운드 멤버 참조**(bound member reference)를 지원한다. 
> - 멤버 참조를 생성할 때 클래스 인스턴스를 함께 저장한 다음 나중에 그 인스턴스에 대해 멤버를 호출해준다. 
> - 따라서 호출 시 수신 대상 객체를 별도로 지정해 줄 필요가 없다.
> ```kotlin
> >>> val p = Person("Dmitry", 34)
> >>> val personsAgeFunction = Person::age
> >>> println(personsAgeFunction(p))
> 34
> >>> val dmitrysAgeFunction = p::age    // 코틀린 1.1부터 사용할 수 있는 바운드 멤버 참조
> >>> println(dmitrysAgeFunction())
> 34
> ```
> 여기서 personsAgeFunction은 인자가 하나(인자로 받은 사람의 나이를 반환)이지만, 
> dmitrysAgeFunction은 인자가 없는(참조를 만들 때 p가 가리키던 사람의 나이를 반환) 함수라는 점에 유의하라.
> 
> 코틀린 1.0에서는 p::age 대신에 { p.age } 라고 직접 객체의 프로퍼티를 돌려주는 람다를 만들어야만 한다.


<br/>


## 5.2. 컬렉션 함수 API

함수형 프로그래밍 스타일을 사용하면 컬렉션을 다룰 때 편리하다.
대부분의 작업에 라이브러리 함수를 활용할 수 있고 그로 인해 코드를 아주 간결하게 만들 수 있다.

<br/>


## 5.2.1. 필수적인 함수: filter와 map

filter와 map은 컬렉션을 활용할 때 기반이 되는 함수다.
대부분의 컬렉션 연산을 이 두 함수를 통해 표현할 수 있다.

```kotlin
data class Person(val name: String, val age: Int)
```

`filter` 함수는 컬렉션을 이터레이션하면서 주어진 람다에 각 원소를 넘겨서 람다가 true를 반환하는 원소만 모은다.

```kotlin
>>> val list = listOf(1, 2, 3, 4)
>>> println(list.filter{ it % 2 == 0 })
[2, 4]
```

```kotlin
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))
>>> println(people.filter { it.age > 30 })
[Person(name=Bob, age=31)]
```

filter 함수는 컬렉션에서 원치 않는 원소를 제거한다.
하지만 filter는 원소를 변환할 수 는 없다. 원소를 변환하려면 map 함수를 사용해야 한다.
`map` 함수는 주어진 람다를 컬렉션의 각 원소에 적용한 결과를 모아서 새 컬렉션을 만든다.

```kotlin
>>> val list = listOf(1, 2, 3, 4)
>>> println(list.map { it * it })
[1, 4, 9, 16]
```

사람의 리스트가 아니라 이름의 리스트를 출력하고 싶다면 map으로 사람의 리스트를 이름의 리스트로 변환하면 된다.

```kotlin
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))
>>> println(people.map { it.name })
[Alice, Bob]
```
```kotlin
people.map(Person::name) // 멤버 참조를 사용해보자.
```
```kotlin
// 30살 이상인 사람의 이름 출력
>>> people.filter { it.age > 30 }.map(Person::name)
[Bob]
```
```kotlin
// 가장 나이 많은 사람의 이름 출력
people.filter { it.age == people.maxBy(Person::age)!!.age }
```

하지만 위 코드는 목록에서 최댓값을 구하는 작업을 계속 반복한다는 단점이 있다.
아래는 이를 좀 더 개선해 한번만 계산하게 만든 코드다.

```kotlin
val maxAge = people.maxBy(Person::age)!!.age
people.filter { it.age == maxAge }
```

필터와 변환 함수를 맵에 적용할 수도 있다.

```kotlin
>>> val numbers = mapOf(0 to "zero", 1 to "one")
>>> println(numbers.mapValues { it.value.toUpperCase() })
{0=ZERO, 1=ONE}
```

<br/>


## 5.2.2. all, any, count, find: 컬렉션에서 술어 적용

컬렉션에 대해 자주 수행하는 연산으로 컬렉션의 모든 원소가 **어떤 조건을 만족하는지 판단**하는
(또는 그 변종으로 컬렉션 안에 어떤 조건을 만족하는 원소가 있는지 판단하는) 연산이 있다.
코틀린에서는 all과 any가 이런 연산이다.
`count` 함수는 조건을 만족하는 원소의 개수를 반환하며, `find` 함수는 조건을 만족하는 첫 번째 원소를 반환한다.

```kotlin
// 어떤 사람의 나이가 27살 이하인지 판단
val canBeInClub27 = { p: Person -> p.age <= 27 }
```

모든 원소가 이 술어를 만족하는지 궁금하다면 `all` 함수를 쓴다.

```kotlin
>>> val people = listOf(Person("Alice", 27), Person("Bob", 31))
>>> println(people.all(canBeInClub27))
false
```

술어를 만족하는 원소가 하나라도 있는지 궁금하면 `any`를 쓴다.

```kotlin
>>> println(people.any(canBeInClub27))
true
```

어떤 조건에 대해 !all을 수행한 결과와 그 조건의 부정에 대해 any를 수행한 결과는 같다(드모르간의 법칙).
또 어떤 조건에 대해 !any를 수행한 결과와 그 조건의 부정에 대해 all을 수행한 결과도 같다.
가독성을 높이려면 !를 붙이지 않는 편이 낫다.

```kotlin
>>> val list = listOf(1, 2, 3)
>>> println(!list.all { it == 3 })
true
>>> println(list.any { it != 3 } )
true
```

술어를 만족하는 원소의 개수를 구하려면 count를 사용한다.

```kotlin
>>> val people = listOf(Person("Alice", 27), Person("Bob", 31))
>>> println(people.count(canBeInClub27))
1
```

> ### ✅함수를 적재적소에 사용하라: count와 size
> count가 있다는 사실을 잊어버리고, 컬렉션을 필터링한 결과의 크기를 가져오는 경우가 있다.
> ```kotlin
> >>> println(people.filter(canBeInClub27).size)
> 1 
> ```
> 하지만 이렇게 처리하면 조건을 만족하는 모든 원소가 들어가는 중간 컬렉션이 생긴다. 
> 반면 **count는 조건을 만족하는 원소의 개수만을 추적하지 조건을 만족하는 원소를 따로 저장하지 않는다.** 
> 따라서 count가 훨씬 더 효율적이다.

술어를 만족하는 원소를 하나 찾고 싶으면 find 함수를 사용한다.

```kotlin
>>> val people = listOf(Person("Alice", 27), Person("Bob", 31))
>>> println(people.find(canBeInClub27))
Person(name=Alice, age=27)
```

이 식은 조건을 만족하는 원소가 하나라도 있는 경우 가장 먼저 조건을 만족한다고 확인된 원소를 반환하며, 
만족하는 원소가 전혀 없는 경우 null을 반환한다.
find는 `firstOfNull`과 같다.
조건을 만족하는 원소가 없으면 null이 나온다는 사실을 더 명확히 하고 싶다면 `firstOrNull`을 쓸 수 있다.

<br/>


## 5.2.3. groupBy: 리스트를 여러 그룹으로 이뤄진 맵으로 변경
컬렉션의 모든 원소를 어떤 특성에 따라 여러 그룹으로 나누고 싶다고 하자.
특성을 파라미터로 전달하면 컬렉션을 자동으로 구분해주는 함수가 있으면 편리할 것이다.
groupBy 함수가 그런 역할을 한다.

```kotlin
>>> val people = listOf(Person("Alice", 31), Person("Bob", 29), Person("Carol", 31))
>>> println(people.groupBy { it.age })

{29=[Person(name=Bob, age=29)],
    31=[Person(name=Alice, age=31), Person(name=Carol, age=31)]}
```

이 연산의 결과는 컬렉션의 원소를 구분하는 특성(age)이고, 
키 값에 따른 각 그룹(Person 객체의 모임)이 모인 값인 맵이다.
각 그룹은 리스트다. 따라서 groupBy의 결과 타입은 Map<Int, List>이다.

```kotlin
>>> val list = listOf("a", "ab", "b")
>>> println(list.groupBy(String::first))
{a=[a, ab], b=[b]}
```

first는 String의 멤버가 아니라 확장 함수지만 여전히 멤버 참조를 사용해 first에 접근할 수 있다.

<br/>


## 5.2.4. flatMap과 flatten: 중첩된 컬렉션 안의 원소 처리

Book으로 표현한 책에 대한 정보를 저장하는 도서관이 있다고 가정하자.

```kotlin
class Book(val title: String, val authors: List<String>)
```

책마다 저자가 한 명 또는 여러 명 있다.
도서관에 있는 책의 저자를 모두 모은 집합을 다음과 같이 가져올 수 있다.

```kotlin
books.flatMap { it.authors }.toSet()    // books 컬렉션에 있는 책을 쓴 모든 저자의 집합
```

`flatMap` 함수는 먼저 인자로 주어진 람다를 컬렉션의 모든 객체에 적용하고(또는 매핑하기) 
람다를 적용한 결과 얻어지는 여러 리스트를 한 리스트로 한데 모은다(또는 펼치기(flatten)).

```kotlin
>>> val strings = listOf("abc", "def")
>>> println(strings.flatMap { it.toList() })
[a, b, c, d, e, f]
```

`toList` 함수를 문자열에 적용하면 그 문자열에 속한 모든 문자로 이뤄진 리스트가 만들어진다.
map과 toList를 함께 사용하면 그림의 가운데 줄에 표현한 것처럼 문자로 이뤄진 리스트로 이뤄진 리스트가 생긴다.
flatMap 함수는 다음 단계로 리스트의 리스트에 들어있던 모든 원소로 이뤄진 단일 리스트를 반환한다.

```kotlin
>>> val books = listOf(Book("Thursday Next", listOf("Jasper Fforde")),
...                    Book("Mort", listOf("Terry Pratchett")),
...                    Book("Good Omens", listOf("Terry Pratchett",
...                                              "Neil Gaiman")))
>>> println(books.flatMap { it.authors }.toSet())
[Jasper Fforde, Terry Pratchett, Neil Gaiman]
```

toSet은 flatMap의 결과 리스트에서 중복을 없애고 집합으로 만든다.


<br/>


## 5.3. 지연 계산(lazy) 컬렉션 연산

앞 절에서는 map이나 filter 같은 몇 가지 컬렉션 함수를 살펴봤다.
그런 함수는 결과 컬렉션을 즉시 생성한다.
이는 컬렉션 함수를 연쇄하면 매 단계마다 계산 중간 결과를 새로운 컬렉션에 임시로 담는다는 말이다.
<span style="color:orange">시퀀스</span>를 사용하면 
중간 임시 컬렉션을 사용하지 않고도 컬렉션 연산을 연쇄할 수 있다.

```kotlin
people.map(Person::name).filter {it.startsWith("A") }
```

코틀린 표준 라이브러리 참조 문서에는 filter와 map이 리스트를 반환한다고 써있다.
이는 이 연쇄 호출이 리스트를 2개 만든다는 뜻이다.
한 리스트는 filter의 결과를 담고, 다른 하나는 map의 결과를 담는다.
원본 리스트에 원소가 2개밖에 없다면 리스트가 2개 더 생겨도 큰 문제가 되지 않겠지만, 
**원소가 수백만 개가 되면 훨씬 더 효율이 떨어진다.**
이를 더 효율적으로 만들기 위해서는 각 연산이 컬렉션을 직접 사용하는 대신 시퀀스를 사용하게 만들어야 한다.

```kotlin
people.asSequence() // 원본 컬렉션을 시퀀스로 변환한다.
.map(Person::name) // 시퀀스도 컬렉션과 똑같은 API를 제공한다.
.filter{ it.startsWith("A") }
.toList() // 결과 시퀀스를 다시 리스트로 변환한다.
```

코틀린 지연 계산 시퀀스는 **Sequence 인터페이스**에서 시작한다.
이 인터페이스는 단지 한 번에 하나씩 열거될 수 있는 원소의 시퀀스를 표현할 뿐이다.
Sequence 안에는 `iterator`라는 단 하나의 메소드가 있다.
그 메소드를 통해 시퀀스로부터 원소 값을 얻을 수 있다.

Sequence 인터페이스의 강점은 그 인터페이스 위에 구현된 연산이 계산을 수행하는 방법 때문에 생긴다.
**시퀀스의 원소는 필요할 때 비로소 계산된다.**
따라서 중간 처리 결과를 저장하지 않고도 연산을 연쇄적으로 적용해서 효율적으로 계산을 수행할 수 있다.

`asSequence` 확장 함수를 호출하면 어떤 컬렉션이든 시퀀스로 바꿀 수 있다.
시퀀스를 리스트로 만들 때는 toList를 사용한다.

왜 시퀀스를 다시 컬렉션으로 되돌려야 할까?
시퀀스의 원소를 차례로 이터레이션해야 한다면 시퀀스를 직접 써도 된다.
하지만 시퀀스의 원소를 인덱스를 사용해 접근하는 등의 다른 API 메소드가 필요하다면 시퀀스를 리스트로 변환해야 한다.

> **큰 컬렉션에 대해서 연산을 연쇄시킬 때는 시퀀스를 적용하는 것을 규칙으로 삼아라.** 
> 8.2절에서는 중간 컬렉션을 생성함에도 불구하고 
> 코틀린에서 즉시 계산 컬렉션에 대한 연산이 더 효율적인 이유를 나중에 설명한다. 
> 하지만 컬렉션에 들어있는 원소가 많으면 중간 원소를 재배열하는 비용이 커지기 때문에 지연 계산이 더 낫다.

시퀀스에 대한 연산을 지연 계산하기 때문에 정말 계산을 실행하게 만들려면 
최종 시퀀스의 원소를 하나씩 이터레이션하거나 최종 시퀀스를 리스트로 변환해야 한다.

<br/>


## 5.3.1. 시퀀스 연산 실행: 중간 연산과 최종 연산

시퀀스에 대한 연산은 **중간 연산**과 **최종 연산**으로 나뉜다.
- 중간 연산은 다른 시퀀스를 반환, 그 시퀀스는 최초 시퀀스의 원소를 변환하는 방법을 안다.
- 최종 연산은 결과를 반환한다.

결과는 최초 컬렉션에 대해 변환을 적용한 시퀀스로부터 일련의 계산을 수행해 얻을 수 있는 컬렉션이나 원소, 숫자 또는 객체다.

중간 연산은 항상 지연 계산된다.

```kotlin
>>> listOf(1, 2, 3, 4).asSequence()
...             .map { print("map($it) "); it * it }
...             .filter { print("filter($it) "); it % 2 == 0 }
```

이 코드를 실행하면 아무 내용도 출력되지 않는다.
이는 map과 filter 변환이 늦춰져서 결과를 얻을 필요가 있을 때(즉 최종 연산이 호출될 때) 적용된다는 뜻이다.

```kotlin
>>> listOf(1, 2, 3, 4).asSequence()
...             .map { print("map($it) "); it * it }
...             .filter { print("filter($it) "); it % 2 == 0 }
...             .toList()
max(1) filter(1) map(2) filter(4) map(3) filter(9) map(4) filter(16)
```

최종 연산을 수행하면 연기됐던 모든 계산이 수행된다.

시퀀스의 경우 모든 연산은 각 원소에 대해 순차적으로 적용된다.
즉 첫 번째 원소가 (반환된 다음에 걸러지면서) 처리되고, 다시 두 번째 원소가 처리되며, 
이런 처리가 모든 원소에 대해 적용한다.

따라서 원소에 연산을 차례대로 적용하다가 결과가 얻어지면 그 이후의 원소에 대해서는 변환이 이뤄지지 않을 수도 있다.
map으로 리스트의 각 숫자를 제곱하고 제곱한 숫자 중에서 find로 3보다 큰 첫 번째 원소를 찾아보자.

```kotlin
>>> println(listOf(1, 2, 3, 4).asSequence()
.map { it * it }.find { it > 3 })
4
```

같은 연산을 시퀀스가 아니라 컬렉션에 수행하면 map의 결과가 먼저 평가돼 최초 컬렉션의 모든 원소가 변환된다.
두 번째 단계에서는 map을 적용해서 얻은 중간 컬렉션으로부터 술어를 만족하는 원소를 찾는다.
시퀀스를 사용하면 지연 계산으로 인해 원소 중 일부의 계산은 이뤄지지 않는다.

- 즉시 계산(컬렉션)은 전체 컬렉션에 연산을 적용하지만, 
- 지연 계산(시퀀스)은 원소를 한번에 하나씩 처리한다.

컬렉션을 사용하면 리스트가 다른 리스트로 변환된다.
시퀀스를 사용하면 find 호출이 원소를 하나씩 처리하기 시작한다.
최초 시퀀스로부터 수를 하나 가져와서 map에 지정된 변환을 수행한 다음에 find에 지정된 술어를 만족하는지 검사한다.
최초 시퀀스에서 2를 가져오면 제곱 값(4)이 3보다 커지기 때문에 그 제곱 값을 결과로 반환한다.

컬렉션에 대해 수행하는 연산의 순서도 성능에 영향을 끼친다.
사람의 컬렉션이 있는데 이름이 어떤 길이보다 짧은 사람의 명단을 얻고 싶다고 하자.
이를 처리하기 위해서는 각 사람의 이름으로 map한 다음에 이름 중에서 길이가 긴 사람을 제외시켜야 한다.

이 경우 map과 filter를 어떤 순서로 수행해도 된다.
그러나 map 다음에 filter를 하는 경우와 filter 다음에 map을 하는 경우 결과는 같아도 
수행해야 하는 변환의 전체 횟수는 다르다.

```kotlin
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31), Person("Charles", 31), Person("Dan", 21))
>>> println(people.asSequence().map(Person::name).filter { it.length < 4 }.toList())  // map 다음에 filter 수행
[Bob, Dan]
>>> println(people.asSequence().filter { it.name.length < 4 }.map(Person::name).toList())   // filter 다음에 map 수행
[Bob, Dan]
```

map을 먼저 하면 모든 원소를 변환한다.
하지만 filter를 먼저 하면 부적절한 원소를 먼저 제외하기 때문에 그런 원소는 변환되지 않는다.

> ### ✅자바 스트림과 코틀린 시퀀스 비교
> **자바 8 스트림**을 아는 독자라면 **시퀀스**라는 개념이 스트림과 같다는 사실을 알았을 것이다. 
> 코틀린에서 같은 개념을 따로 구현해 제공하는 이유는 
> 안드로이드 등에서 예전 버전 자바를 사용하는 경우 자바 8에 있는 스트림이 없기 때문이다. 
> 자바 8을 채택하면 현재 코틀린 컬렉션과 시퀀스에서 제공하지 않는 중요한 기능을 사용할 수 있다. 
> **바로 스트림 연산(map과 filter 등)을 여러 CPU에서 병렬적으로 실행하는 기능**이 그것이다.

<br/>

## 5.3.2. 시퀀스 만들기

시퀀스를 만드는 다른 방법으로 `generateSequence` 함수를 사용할 수 있다.

```kotlin
>>> val naturalNumbers = generateSequence(0) { it + 1 }
>>> val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
>>> println(numbersTo100.sum()) // 모든 지연 연산은 "sum"의 결과를 계산할 때 수행된다.
5050
```

이 예제에서 naturalNumbers와 numbersTo100은 모두 시퀀스며, 연산을 지연 계산한다.
최종 연산을 수행하기 전까지는 시퀀스의 각 숫자는 계산되지 않는다(여기서는 sum이 최종 연산).

시퀀스를 사용하는 일반적인 용례 중 하나는 **객체의 조상으로 이뤄진 시퀀스를 만들어내는 것**이다.
어떤 객체의 조상이 자신과 같은 타입이고(사람이나 자바 파일이 그렇다) 
모든 조상의 시퀀스에서 어떤 특징을 알고 싶을 때가 있다.

```kotlin
fun File.isInsideHiddenDirectory() =
        generateSequence(this) { it.parentFile }.any { it.isHidden }

>>> val file = File("/Users/svtk/.HiddenDir/a.txt")
>>> println(file.isInsideHiddenDirectory())
true
```

여기서도 첫 번째 원소를 지정(this)하고, 시퀀스의 한 원소로부터 다음 원소를 계산하는 방법을 제공함으로써 시퀀스를 만든다.
any를 find로 바꾸면 원하는 디렉터리를 찾을 수도 있다.
이렇게 시퀀스를 사용하면 조건을 만족하는 디렉터리를 찾은 뒤에는 더 이상 상위 디렉터리를 뒤지지 않는다.

<br/>


## 5.4. 수신 객체 지정 람다: with와 apply

어떻게 코틀린 람다를 자바 API에 활용할 수 있는지 살펴본다.

```kotlin
button.setOnClickListener { /* 클릭 시 수행할 동작 */ } // 람다를 인자로 넘김
```

Button 클래스는 setOnClickListener 메소드를 사용해 버튼의 리스너를 설정한다.

```java
/* 자바 */
public class Button {
    public void setOnClickListener(OnClickListener l) { ... }
}
```

OnClickListener 인터페이스는 onClick이라는 메소드만 선언된 인터페이스다.

```java
/* 자바 */
public interface OnClickListener {
    void onClick(View v);
}
```

자바 8 이전의 자바에서는 setOnClickListener 메소드에게 인자로 넘기기 위해 무명 클래스의 인스턴스를 만들어야만 했다.
(여기서는 OnClickListener)

```kotlin
button.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
        ...
    }
})
```

코틀린에서는 무명 클래스 인스턴스 대신 람다를 넘길 수 있다.

```kotlin
button.setOnClickListener { view -> ... }
```

OnClickListener를 구현하기 위해 사용한 람다에는 view라는 파라미터가 있다.
view의 타입은 View다. 이는 onClick 메소드의 인자 타입과 같다.
람다의 파라미터는 메소드의 파라미터와 대응한다.

```
public interface OnClickListener {
    void onClick(View v); --> { view -> ... }
}
```

이런 코드가 작동하는 이유는 OnClickListener에 추상 메소드가 단 하나만 있기 때문이다.
그런 인터페이스를 함수형 인터페이스 또는 SAM 인터페이스 라고 한다.
<span style="color:orange">SAM(Single abstract method)</span>은 단일 추상 메소드라는 뜻이다.

자바 API에는 Runnable이나 Callable과 같은 함수형 인터페이스와 그런 함수형 인터페이스를 활용하는 메소드가 많다.
코틀린은 함수형 인터페이스를 인자로 취하는 자바 메소드를 호출할 때 람다를 넘길 수 있게 해준다.
따라서 코틀린 코드는 무명 클래스 인스턴스를 정의하고 활용할 필요가 없어서 여전히 깔끔하며 코틀린다운 코드로 남아있을 수 있다.

> 자바와 달리 **코틀린에는 제대로 된 함수 타입이 존재**한다. 
> 따라서 코틀린에서 함수를 인자로 받을 필요가 있는 함수는 
> 함수형 인터페이스가 아니라 함수 타입을 인자 타입으로 사용해야 한다. 
> 코틀린 함수를 사용할 때는 코틀린 컴파일러가 코틀린 람다를 함수형 인터페이스로 변환해주지 않는다. 
> 함수 선언에는 함수 타입을 사용하는 방법은 8.1절에서...

<br/>


## 5.4.1. 자바 메소드에 람다를 인자로 전달

함수형 인터페이스를 인자로 원하는 자바 메소드에 코틀린 람다를 전달할 수 있다.
예를 들어 다음 메소드는 Runnable 타입의 파라미터를 받는다.

```java
/* 자바 */
void postponeComputation(int delay, Runnable computation);
```

코틀린에서 람다를 이 함수에 넘길 수 있다. 
컴파일러는 자동으로 람다를 Runnable 인스턴스로 변환해준다.

```kotlin
postponeComputation(1000) { println(42) }
```

여기서 **Runnable 인스턴스**라는 말은 실제로는 'Runnable을 구현한 무명 클래스의 인스턴스'라는 뜻이다.
컴파일러는 자동으로 그런 무명 클래스와 인스턴스를 만들어준다.
이때 그 무명 클래스에 있는 유일한 추상 메소드를 구현할 때 람다 본문을 메소드 본문으로 사용한다.
여기서는 Runnable의 run이 그런 추상 메소드다.
Runnable을 구현하는 무명 객체를 명시적으로 만들어서 사용할 수도 있다.

```kotlin
postponeComputation(1000, object : Runnable {   // 객체 식을 함수형 인터페이스 구현으로 넘긴다.
    override fun run() {
        println(42)
    }
})
```

하지만 람다와 무명 객체 사이에는 차이가 있다.
- 무명 객체 : 객체를 명시적으로 선언하는 경우 메소드를 호출할 때마다 새로운 객체가 생성한다.
- 람다 : 정의가 들어있는 함수의 변수에 접근하지 않는 람다에 대응하는 무명 객체를 메소드를 호출할 때마다 반복 사용한다.

```kotlin
// 프로그램 전체에서 Runnable의 인스턴스는 단 하나만 만들어진다.
postponeComputation(1000) { println(42) }
```

따라서 명시적인 object 선언을 사용하면서 람다와 동일한 코드는 다음과 같다.
이 경우 Runnable 인스턴스를 변수에 저장하고 메소드를 호출할 때마다 그 인스턴스를 사용한다.

```kotlin
val runnable = Runnable { println(42) } // Runnable은 SAM 생성자

// 전역 변수로 컴파일되므로 프로그램 안에 단 하나의 인스턴스만 존재한다.
fun handleComputation() {
    // 모든 handleComputation 호출에 같은 객체를 사용한다.
    postponeComputation(1000, runnable)
}
```

람다가 주변 영역의 변수를 포획한다면 매 호출마다 같은 인스턴스를 사용할 수 없다.
그런 경우 컴파일러는 매번 주변 영역의 변수를 포획한 새로운 인스턴스를 생성해준다.
예를 들어 다음 함수에서는 id를 필드로 저장하는 새로운 Runnable 인스턴스를 매번 새로 만들어 사용한다.

```kotlin
fun handleComputation(id: String) { // 람다 안에서 "id" 변수를 포획한다.
    // handleComputation을 호출할 때마다 새로 Runnable 인스턴스를 만든다.
    postponeComputation(1000) { println(id) }
}
```

<br/>

> ### ✅람다의 자세한 구현
> 코틀린 1.0에서는 인라인(inline) 되지 않은 모든 람다 식은 무명 클래스로 컴파일된다. 
> 코틀린 1.1부터는 자바 8 바이트코드를 생성할 수 있지만, 여전히 코틀린 1.0처럼 람다마다 별도의 클래스를 만들어낸다. 
> 하지만 향후 별도의 클래스를 만들지 않고 자바 8부터 도입된 람다 기능을 활용한 바이트코드를 만들어낼 계획이다. 
> 
> 람다가 변수를 포획하면 무명 클래스 안에 포획된 변수를 저장하는 필드가 생기며, 
> 매 호출마다 그 무명 클래스의 인스턴스를 새로 만든다. 
> 하지만 포획하는 변수가 없는 람다에 대해서는 인스턴스가 단 하나만 생긴다. 
> HandleComputation$1처럼 람다가 선언된 함수 이름을 접두사로 하는 이름이 람다를 컴파일한 클래스에 붙는다.
> 
> 다음은 앞에 살펴본 (포획이 있는) 람다 식의 바이트코드를 디컴파일하면 볼 수 있는 코드다.
> 
> ```kotlin
> class HandleComputation$1(val id: String) : Runnable {
>   override fun run() {
>       println(id)
>   }
> }
> 
> fun handleComputation(id: String) {
>   // 내부적으로는 람다 대신 특별한 클래스의 인스턴스가 만들어진다.
>   postponeComputation(1000, HandleComputation$1(id))
> }
> ```
> 
> 컴파일러는 포획한 변수마다 그 값을 저장하기 위한 필드를 만든다. (이 경우에는 id)

람다에 대해 무명 클래스를 만들고 그 클래스의 인스턴스를 만들어서 메소드에 넘긴다는 설명은 
함수형 인터페이스를 받는 자바 메소드를 코틀린에서 호출할 때 쓰는 방식을 설명해주지만, 
컬렉션을 확장한 메소드에 람다를 넘기는 경우 코틀린은 그런 방식을 사용하지 않는다.
코틀린이 `inline`으로 표시된 코틀린 함수에게 람다를 넘기면 아무런 무명 클래스도 만들어지지 않는다.
대부분의 코틀린 확장 함수들은 inline 표시가 붙어있다. (8.2절에서...)

지금까지 살펴본 대로 대부분의 경우 람다와 자바 함수형 인터페이스 사이의 변환은 자동으로 이뤄진다.
하지만 어쩔 수 없이 수동으로 변환해야 하는 경우가 있다.


<br/>


## 5.4.2. SAM 생성자: 람다를 함수형 인터페이스로 명시적으로 변경

**SAM 생성자**는 람다를 함수형 인터페이스의 인스턴스로 변환할 수 있게 컴파일러가 자동으로 생성한 함수다.
컴파일러가 자동으로 람다를 함수형 인터페이스 무명 클래스로 바꾸지 못하는 경우 SAM 생성자를 사용할 수 있다.
예를 들어 함수형 인터페이스의 인스턴스를 반환하는 메소드가 있다면 람다를 직접 반환할 수 없고, 
반환하고픈 람다를 SAM 생성자로 감싸야 한다.

```kotlin
fun createAllDoneRunnable() : Runnable {
    return Runnable { println("All done!") }
}

>>> createAllDoneRunnable().run()
All done!
```

SAM 생성자의 이름은 사용하려는 함수형 인터페이스의 이름과 같다.
SAM 생성자는 그 함수형 인터페이스의 유일한 추상 메소드(여기서는 run 메소드)의 본문에 사용할 람다만을 인자로 받아서 
함수형 인터페이스를 구현하는 클래스의 인스턴스를 반환한다.

람다로 생성한 함수형 인터페이스 인스턴스를 변수에 저장해야 하는 경우에도 SAM 생성자를 사용할 수 있다.
여러 버튼에 같은 리스너를 적용하고 싶다면 
다음 코드처럼 SAM 생성자를 통해 람다를 함수형 인터페이스 인스턴스로 만들어서 변수에 저장해 활용할 수 있다
(안드로이드라면 Activity.onCreate 메소드 안에 이런 코드가 들어갈 수 있다).

```kotlin
// 람다를 함수형 인터페이스 인스턴스로 만들어서 변수에 저장
val listener = onClickListener { view ->
    val text = when (view.id) {
        R.id.button1 -> "First button"
        R.id.button2 -> "Second button"
        else -> "Unknown button"
    }
    toast(text)
}
button1.setOnClickListener(listener)
button2.setOnClickListener(listener)
```

OnClickListener를 구현하는 객체 선언을 통해 리스너를 만들 수도 있지만 SAM 생성자를 쓰는 쪽이 더 간결하다.

> ### ✅람다와 리스너 등록/해제하기
> 람다에는 무명 객체와 달리 인스턴스 자신을 가리키는 `this`가 없다는 사실에 유의하라. 
> 따라서 람다를 변환한 무명 클래스의 인스턴스를 참조할 방법이 없다. 
> 컴파일러 입장에서 보면 람다는 코드 블록일 뿐이고, 객체가 아니므로 **객체처럼 람다를 참조할 수는 없다.** 
> **람다 안에서는 this는 그 람다를 둘러싼 클래스의 인스턴스를 가리킨다.** 
> 
> 이벤트 리스너가 이벤트를 처리하다가 자기 자신의 리스너 등록을 해제해야 한다면 람다를 사용할 수 없다. 
> 그런 경우 람다 대신 무명 객체를 사용해 리스너를 구현하라. 
> 무명 객체 안에서는 this가 그 무명 객체 인스턴스 자신을 가리킨다. 
> 따라서 리스너를 해제하는 API 함수에게 this를 넘길 수 있다.

또한 함수형 인터페이스를 요구하는 메소드를 호출할 때 대부분의 SAM 변환을 컴파일러가 자동으로 수행할 수 있지만, 
가끔 오버로드한 메소드 중에서 어떤 타입의 메소드를 선택해 람다를 변환해 넘겨줘야 할지 모호한 때가 있다.
그런 경우 명시적으로 SAM 생성자를 적용하면 컴파일 오류를 피할 수 있다.

<br/>

<br/>



## 5.5. 수신 객체 지정 람다: with와 apply

자바의 람다에는 없는 코틀린 람다의 독특한 기능은 바로 
**수신 객체를 명시하지 않고 람다의 본문 안에서 다른 객체의 메소드를 호출할 수 있게 하는 것**이다.
그런 람다를 **수신 객체 지정 람다**(lambda with receiver)라고 부른다.


## 5.5.1. with 함수

어떤 객체의 이름을 반복하지 않고도 그 객체에 대해 다양한 연산을 수행할 수 있다.

```kotlin
fun alphabet(): String {
    val result = StringBuilder()
    for (letter in 'A'..'Z') {
        result.append(letter)
    }
    result.append("\nNow I know the alphabet!")
    return result.toString()
}

>>> println(alphabet())
ABCDEFGHIJKLMNOPQRSTUVWXYZ
Now I know the alphabet!
```

`with`로 다시 작성한 결과를 살펴보자.

```kotlin
fun alphabet(): String {
    val stringBuilder = StringBuilder()
    return with(stringBuilder) {    // 메소드를 호출하려는 수신 객체를 지정한다.
        for (letter in 'A'..'Z') {
            this.append(letter) // "this"를 명시해서 앞에서 지정한 수신 객체의 메소드를 호출한다.
        }
        append("\nNow I know the alphabet!")    // "this"를 생략하고 메소드를 호출한다.
        this.toString() // 람다에서 값을 반환한다.
    }
}
```

with문은 파라미터가 2개 있는 함수다.
첫 번째 파라미터는 stringBuilder이고, 두 번째 파라미터는 람다다.
람다를 괄호 밖으로 빼내는 관례를 사용함에 따라 전체 함수 호출이 언어가 제공하는 특별 구문처럼 보인다.
물론 이 방식 대신 with(stringBuilder, { ... })라고 쓸 수도 있지만 더 읽기 나빠진다.

with 함수는 첫 번째 인자로 받은 객체를 두 번째 인자로 받은 람다의 수신 객체로 만든다.
인자로 받은 람다 본문에서는 this를 사용해 그 수신 객체에 접근할 수 있다.

위 코드에서 this는 with의 첫 번째 인자로 전달된 stringBuilder다.
stringBuilder의 메소드를 this.append(letter)처럼 this 참조를 통해 접근하거나 
append("\nNow...") 처럼 바로 호출할 수 있다.

> ### ✅수신 객체 지정 람다와 확장 함수 비교
> this가 함수의 수신 객체를 가리키는 비슷한 개념을 떠올린 독자가 있을지도 모르겠다. 
> 확장 함수 안에서 this는 그 함수가 확장하는 타입의 인스턴스를 가리킨다. 
> 그리고 그 수신 객체의 this의 멤버를 호출할 때는 this.를 생략할 수 있다.
> 
> 어떤 의미에서는 확장 함수를 수신 객체 지정 함수라고 할 수도 있다.
> 
> - 일반 함수 -> 일반 람다 
> - 확장 함수 -> 수신 객체 지정 람다
> 
> 람다는 일반 함수와 비슷한 동작을 정의하는 한 방법이다. 
> 수신 객체 지정 람다는 확장 함수와 비슷한 동작을 정의하는 한 방법이다.

앞의 alphabet 함수를 더 리팩토링해서 불필요한 stringBuilder 변수를 없앨 수도 있다.

```kotlin
fun alphabet() = with(StringBuilder()) {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I know the alphabet!")
    toString()
}
```

불필요한 stringBuilder 변수를 없애면 alphabet 함수가 식의 결과를 바로 반환하게 된다.
따라서 식을 본문으로 하는 함수로 표현할 수 있다.
StringBuilder의 인스턴스를 만들고 즉시 with에게 인자로 넘기고, 
람다 안에서 this를 사용해서 그 인스턴스를 참조한다.

<br/>

> ### ✅메소드 이름 충돌
> with에게 인자로 넘긴 객체의 클래스와 with를 사용하는 코드가 들어있는 클래스 안에 이름이 같은 메소드가 있으면 
> 무슨 일이 생길까? 
> 그런 경우 this 참조 앞에 레이블을 붙이면 호출하고 싶은 메소드를 명확하게 정할 수 있다. 
> alphabet 함수가 OuterClass의 메소드라고 하자. 
> StringBuilder가 아닌 바깥쪽 클래스(OuterClass)에 정의된 toString을 호출하고 싶다면 다음과 같은 구문을 사용해야 한다.
> ```kotlin
> this@OuterClass.toString()
> ```

with가 반환하는 값은 람다 코드를 실행한 결과며, 그 결과는 람다 식의 본문에 있는 마지막 식의 값이다.
하지만 때로는 람다의 결과 대신 **수신 객체**가 필요한 경우도 있다.

<br/>


## 5.4. apply 함수

apply 함수는 거의 with와 같다.
유일한 차이란 apply는 항상 자신에게 전달된 객체(즉 수신 객체)를 반환한다는 점뿐이다.

```kotlin
fun alphabet() = StringBuilder().apply {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I know the alphabet!")
}.toString()
```

apply는 확장 함수로 정의돼 있다.
apply의 수신 객체가 전달받은 람다의 수신 객체가 된다.
이 함수에서 apply를 실행한 결과는 StringBuilder 객체다.
따라서 그 객체의 toString을 호출해서 String 객체를 얻을 수 있다.

이런 apply 함수는 **객체의 인스턴스를 만들면서 즉시 프로퍼티 중 일부를 초기화해야 하는 경우 유용**하다.
자바에서는 보통 별도의 Builder 객체가 이런 역할을 담당한다.
코틀린에서는 어떤 클래스가 정의돼 있는 라이브러리의 특별한 지원 없이도 
그 클래스 인스턴스에 대해 apply를 활용할 수 있다.

```kotlin
fun createViewWithCustomAttributes(context: Context) =
    TextView(context).apply {
        text = "Sample Text"
        textSize = 20.0
        setPadding(10, 0, 0, 0)
    }
```

apply 함수를 사용하면 함수의 본문에 간결한 식을 사용할 수 있다.
새로운 TextView 인스턴스를 만들고 즉시 그 인스턴스를 apply에 넘긴다.
apply에 전달된 람다 안에서는 TextView가 수신 객체가 된다.
따라서 원하는 대로 TextView의 메소드를 호출하거나 프로퍼티를 설정할 수 있다.
**람다를 실행하고 나면 apply는 람다에 의해 초기화된 TextView 인스턴스를 반환**한다.
그 인스턴스는 createViewWithCustomAttributes 함수의 결과가 된다.

with와 apply는 수신 객체 지정 람다를 사용하는 일반적인 예제 중 하나다.
더 구체적인 함수를 비슷한 패턴으로 활용할 수 있다.

예를 들어 표준 라이브러리의 builderString 함수를 사용하면 alphabet 함수를 더 단순화할 수 있다.
buildString은 앞에서 살펴본 alphabet 코드에서 
StringBuilder 객체를 만드는 일과 toString을 호출해주는 일을 알아서 해준다.
buildString의 인자는 수신 객체 지정 람다며, 수신 객체는 항상 StringBuilder가 된다.

```kotlin
fun alphabet() = buildString {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I know the alphabet!")
}
```

buildString 함수는 StringBuilder를 활용해 String을 만드는 경우 사용할 수 있는 우아한 해법이다.


<br/>
<br/>


## 5.6. 요약

- 람다를 사용하면 코드 조각을 다른 함수에게 인자로 넘길 수 있다.
- 코틀린에서는 람다가 함수 인자인 경우 괄호 밖으로 람다를 빼낼 수 있고, 
  람다의 인자가 단 하나뿐인 경우 인자 이름을 지정하지 않고 it이라는 디폴트 이름으로 부를 수 있다.
- 람다 안에 있는 코드는 그 람다가 들어있는 바깥 함수의 변수를 읽거나 쓸 수 있다.
- 메소드, 생성자, 프로퍼티의 이름 앞에 ::을 붙이면 각각에 대한 참조를 만들 수 있다. 
  그런 참조를 람다 대신 다른 함수에게 넘길 수 있다.
- filter, map, all, any 등의 함수를 활용하면 컬렉션에 대한 대부분의 연산을 직접 원소를 이터레이션하지 않고 
  수행할 수 있다.
- 시퀀스를 사용하면 중간 결과를 담는 컬렉션을 생성하지 않고도 컬렉션에 대한 여러 연산을 조합할 수 있다.
- 함수형 인터페이스(추상 메소드가 단 하나뿐인 SAM 인터페이스)를 인자로 받는 자바 함수를 호출할 경우 
  람다를 함수형 인터페이스 인자 대신 넘길 수 있다.
- 수신 객체 지정 람다를 사용하면 람다 안에서 미리 정해둔 수신 객체의 메소드를 직접 호출할 수 있다.
- 표준 라이브러리 with 함수를 사용하면 어떤 객체에 대한 참조를 반복해서 언급하지 않으면서 
  그 객체의 메소드를 호출할 수 있다. apply를 사용하면 어떤 객체라도 빌더 스타일의 API를 사용해 생성하고 초기화할 수 있다.


<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>