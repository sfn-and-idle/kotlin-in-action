# 7장. 연산자 오버로딩과 기타 관례  

## 입구
- 연산자 오버로딩
- 관례: 여러 연산을 지원하기 위해 특별한 이름이 붙은 메소드
- 위임 프로퍼티

자바에는 표준 라이브러리와 밀접하게 연관된 언어 기능이 몇 가지 있다.
이와 비슷하게 코틀린에서도 어떤 언어 기능이 정해진 사용자 작성 함수와 연결되는 경우가 몇 가지 있는데, 
코틀린에서는 이런 언어 기능이 어떤 타입(클래스)과 연관되기보다는 **특정 함수 이름과 연관**된다. 
예를 들어 어떤 클래스안에 plus라는 이름의 메소드를 정의하면 그 클래스 인스턴스에 대해 `+` 연산자를 사용할 수 있다. 
이런 식으로 <span style="color:orange">어떤 언어 기능과 미리 정해진 이름의 함수를 연결해주는 기법을 
코틀린에서 `관례`(covention)</span>이라 부른다.

언어 기능을 타입에 의존하는 자바와 달리 코틀린은 함수 이름을 통한 관례에 의존. 
이런 관례를 채택한 이유는 **기존 자바 클래스를 코틀린 언어에 적용하기 위함**이다. 
- 기존 자바 클래스가 구현하는 인터페이스는 이미 고정되있어서 
  코틀린 쪽에서 자바 클래스가 새로운 인터페이스를 구현하게 만들 수 없다. 
- 반면 확장 함수를 사용하면 기존 클래스에 새로운 메소드를 추가할 수 있어서, 
  기존 자바 클래스에 대해 확장 함수를 구현하면서 관례에 따라 이름을 붙이면 
  기존 자바 코드를 바꾸지 않아도 새로운 기능을 쉽게 부여할 수 있다.



<br/>
<br/>


## 7.1. 산술 연산자 오버로딩

코틀린에서 관례를 사용하는 가장 단순한 예는 산술 연산자. 
자바에서는 원시 타입에만 산술 연산자를 사용할 수 있고, 
추가로 String에 대해 + 연산자를 사용할 수 있다. 

그러나 다른 클래스에서도 산술 연산자가 유용할 수 있다. 
예를 들어 BigInteger 클래스에 add 메소드를 명시적으로 호출하기 보다 
`+` 연산자를 사용하는 편이 더 낫고, 컬렉션에 원소를 추가하더라도 `+=` 연산자를 사용하면 편리하다. 
코틀린에서는 이러한 일이 가능하다.

<br/>


## 7.1.1. 이항 산술 연산 오버로딩

아래 연산은 두 점의 X좌표와 Y 좌표를 각각 더하는데, `+` 연산자를 구현하여 만든다.

```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

>>> val p1 = Point(10, 20)
>>> val p2 = Point(30, 40)
>>> println(p1 + p2)
Point(x=40, y=60)
```

plus 함수 앞에 `operator` 키워드가 있는데, 연산자를 오버로딩 하는 함수 앞에는 꼭 operator가 있어야 한다. 
operator 키워드를 붙임으로써 **어떤 함수가 관례를 따르는 함수임을 명확히 할 수** 있다. 
(아래 다이어그램) + 연산자는 plus 함수 호출로 컴파일 된다.

```mermaid
graph LR
      A[a + b]-->B["a.plus(b)"]
```

그리고 연산자를 멤버 함수로 만드는 대신 **확장 함수로 정의**할 수도 있다. 
외부 함수의 클래스에 대한 연산자를 정의할 때는 관례를 따르는 이름의 확장 함수로 구현하는게 일반적인 패턴.

다른 언어와 비교해 코틀린에서 오버로딩한 연산자를 정의하고 사용하기 더 쉬운데, 
코틀린에서는 프로그래머가 직접 연산자를 만들어 사용할 수 없고 언어에서 미리 정해둔 연산자만 오버로딩할 수 있으며, 
관례에 따르기 위해 클래스에서 정의해야 하는 이름이 연산자별로 정해져 있다. 
아래는 이항 연산자와 그에 상응하는 연산자 함수 이름을 보여준다.

|식|함수 이름|
|------|---|
|a * b|times|
|a / b|div|
|a % b|mod(1.1부터 rem)|
|a + b|plus|
|a - b|minus|

직접 정의한 함수를 통해 구현하더라도 연산자 우선순위는 언제나 표준 숫자 타입에 대한 연산자 우선순위와 같다.

> ### ✅연산자 함수와 자바
> 코틀린 연산자를 자바에서 호출하기 쉽다. 모든 오버로딩한 연산자는 함수로 정의되며, 
> 긴 이름(FQN)을 사용하면 일반 함수로 호출할 수 있다. 만약 자바 클래스에 원하는 연산자 기능을 제공하는 메소드가 이미 있지만, 
> 이름이 다르다면 관례에 맞는 이름을 가진 확장 함수를 작성하고 연산을 기존 자바 메소드에 위임하면 된다.

연산자를 정의할 때, 두 피연산자는(연산자 함수의 두 파라미터) 같은 타입일 필요는 없다.

```kotlin
operator fun Point.times(scale: Double) 
    = Point((x*scale).toInt(), (y * scale).toInt())

>>> println(p1 * 1.5)
Point(x=15, y=30)
```

연산자 함수의 반환 타입이 두 피연산자 중 하나와 일치하지 않아도 된다.

```kotlin
operator fun Char.times(count: Int) = toString().repeat(count)

>>> println('a' * 3)
aaa
```

일반 함수와 마찬가지로 operator 함수도 오버로딩 가능하다. 
따라서 이름은 같지만 파라미터 타입이 서로 다른 연산자 함수를 여럿 만들 수 있다.
대신 operator 함수는 파라미터의 개수는 1개밖에 정의하지 못한다. 이항 연산이기 때문!

> ### ✅비트 연산자에 대해 특별한 연산자 함수를 사용하지 않는다.
> 코틀린은 표준 숫자 타입에 대해 비트 연산자를 정의하지 않는다. 
> 따라서 커스텀 타입에서 비트 연산자를 정의할 수도 없다.
> 대신, 중위 연산자 표기법을 지원하는 일반 함수를 사용해 비트 연산을 수행한다.
> - shl - 왼쪽 시프트(자바 <<)
> - shr - 오른쪽 시프트(부호 비트 유지, 자바 >>)
> - ushr - 오른쪽 시프트(0으로 부호 비트 설정, 자바 >>>)
> - and - 비트 곱(자바 &)
> - or - 비트 합(자바 |)
> - xor - 비트 배타 합(자바 ^)
> - inv - 비트 반전(자바 ~)


<br/>


## 7.1.2. 복합 대입 연산자 오버로딩

코틀린은 + 연산자 뿐 아니라 `+=`, `-=` 등 복합 대입 연산자도 지원한다.

```kotlin
var point = Point(1,2)
point += Point(3,4)
println(point)

// Result
Point(x=4, y=6)
```

`+=` 연산이 객체에 대한 참조를 다른 참조로 바꾸기보다 원래 객체의 내부 상태를 변경하게 만들고 싶을 때가 있다.
변경가능한 컬렉션에 원소를 추가하는 경우가 대표적인 예.

> ### ✅객체에 대한 참조를 다른 참조로 바꾸기
> point = point + Point(3,4)의 실행을 살펴보자. point의 plus는 새로운 객체를 반환한다.
> point + Point(3,4)는 두 점의 좌표 각각 더한 값을 좌표로 갖는 새로운 Point 객체를 반환한다. 
> 그 후 대입이 이뤄지면 point 변수는 새로운 Point 객체를 가리키게 된다.

```kotlin
val numbers = ArrayList<Int>()
numbers += 42
println(numbers[0])

// 결과
42
```

반환 타입이 Unit인 plusAssign 함수를 정의하면 코틀린은 += 연산자에 그 함수를 사용한다. 
다른 복합 연산자 함수도 비슷하게 minusAssign, timesAssign 등의 이름을 사용한다. 
코틀린 표준 라이브러리는 변경 가능한 컬렉션에 대해 plusAssign을 정의하며 앞의 컬렉션에 원소를 추가하는 경우가 그렇다.

```kotlin
operator fun <T> MutableCollection<T>.plusAssign(element: T){
    this.add(element)
}
```

```mermaid
graph LR
      A[a += b]-->B["a = a.plus(b)"]
      A-->C["a.plusAssign(b)"]
```

+=를 plus와 plusAssign 양쪽으로 컴파일 할 수 있다. 
어떤 클래스가 이 두 함수를 모두 정의하고 둘 다 +=에 사용 가능한 경우 컴파일러는 오류를 보여준다.
일반 연산자를 이용해 해결하거나 var를 val로 바꿔서 plusAssign 적용을 불가능하게 할 수도 있다.
하지만, 일반적으로 새로운 클래스를 일관성 있게 설계하는 게 가장 좋다. 
**plus와 plusAssign을 동시에 정의하는 것을 피해야 한다.**

코틀린은 컬렉션에 대해 두 가지 접근 방법을 제공한다.
- +, -는 항상 새로운 컬렉션을 반환한다.
- +=, -= 연산자는 항상 변경 가능한 컬렉션에 작용해 메모리에 있는 객체 상태를 변화시킨다.
- 또한, 읽기 전용 컬렉션에서 +=, -=는 변경을 적용한 복사본을 반환한다.\
  (따라서 var로 선언한 변수가 가리키는 읽기 전용 컬렉션에만 +=와 -=를 적용할 수 있다)

이런 연산자의 피연산자로 개별 원소를 사용하거나 원소 타입이 일치하는 다른 컬렉션을 사용할 수 있다.

```kotlin
val list = arrayListOf(1,2)
list += 3 // 변경 가능한 컬렉션 list에 대해 +=을 통해 객체 상태를 변경.
val newList = list + listOf(4,5) // 두 리스트를 +로 합쳐 새로운 리스트를 반환.
println(list)
println(newList)

// Result
[1, 2, 3]
[1, 2, 3, 4, 5]
```

<br/>


## 7.1.3. 단항 연산자 오버로딩

이항 연산자의 오버로딩과 마찬가지로 미리 정해진 이름의 함수를 멤버나 확장 함수로 선언하면서 operator를 표시하면 된다.

```kotlin
// 단항 minus 함수는 파라미터가 없다
operator fun Point.unaryMinus(): Point {
  // 좌표에서 각 성분의 음수를 취한 새 점을 반환한다
  return Point(-x, -y)
}

val p = Point(10, 20)
println(-p)

// Result
Point(x=-10, y=-20)
```

단항 연산자를 오버로딩하기 위해 사용하는 함수는 인자를 취하지 않는다.

```mermaid
graph LR
      A[+a]-->B["a.unaryPlus()"]
```

단항 + 연산자는 unaryPlus 호출로 반환된다.

|식|함수 이름|
|------|---|
|+a|unaryPlus|
|-a|unaryMinus|
|!a|not|
|++a, a++|inc|
|--a, a--|dec|

inc나 dec 함수를 정의해 증가/감소 연산자를 오버로딩하는 경우 
컴파일러는 일반적인 값에 대한 전위와 후위 증가/감소 연산자와 같은 의미를 제공. 
아래의 예제는 BigDecimal 클래스에서 ++를 오버로딩하는 모습을 보여준다.

```kotlin
operator fun BigDecimal.inc() = this + BigDecimal.ONE

var bd = BigDecimal.ZERO
// 후위 증가 연산은 println이 실행된 다음 값을 증가
println(bd++)
// 전위 증가 연산은 println이 실행되기 전 값을 증가
println(++bd)
```


<br/>


## 7.2. 비교 연산자 오버로딩

equals, compareTo를 호출해야 하는 자바와 달리 
코틀린에서는 == 비교 연산자를 직접 사용함으로써 코드가 간결하며 이해하기 쉬운 장점이 있다.

<br/>


## 7.2.1. 동등성 연산자 : equals

!= 연산자도 equals로 컴파일된다. 이는 비교 결과를 뒤집은 값을 결과값으로 사용한다.
==와 !=는 내부에서 인자가 널인지 검사하므로 다른 연산과 달리 널이 될 수 있는 값에도 적용할 수 있다.

a가 널인지 판단해서 널이 아닌 경우에만 a.equals(b)를 호출한다.
만약 a가 널이라면 b도 널인 경우에만 결과가 true가 된다.

```mermaid
graph LR
      A["a == b"]-->B["a?.equals(b) ?: (b == null)"]
```

동등성 검사 `==`는 equals 호출과 널 검사로 컴파일된다.

Point는 data class이므로 컴파일러가 자동으로 equals를 생성해준다.

```kotlin
class Point(val x: Int, val y: Int){
  // Any에 정의된 메소드 오버라이딩
  override equals(obj: Any?): Boolean {
    // 최적화 : 파라미터가 this와 같은 객체인지
    if(this === obj) return true
    // 파라미터 타입 검사
    if(obj !is Point) return false
    // Point로 스마트 캐스트해서 x와 y 프로퍼티에 접근
    return x == obj.x && y == obj.y
  }
}

println(Point(1, 2) == Point(1, 2)) // true 
println(Point(1, 2) != Point(4, 5)) // true 
println(null == Point(3, 2)) // false
```

`===`(식별자 비교 연산자)를 사용해 equals의 파라미터가 수신 객체와 같은지 확인한다. ===는 자바의 == 연산자와 같다. 
따라서 ===는 자신의 두 피연산자가 서로 같은 객체를 가리키는지(원시 타입인 경우 두 값이 같은지) 비교한다.

===를 사용해 자기 자신과의 비교를 최적화하는 경우가 많으며, ===는 오버로딩할 수 없다.
Any의 equals에는 operator가 붙어있지만 그 메소드를 오버라이드하는 하위 클래스의 메소드 앞에는 
operator를 붙이지 않아도 자동으로 상위 클래스의 operator 지정이 적용된다. 

또한, Any에서 상속받은 equals가 확장 함수보다 우선순위가 높기 때문에 equals를 확장 함수로 정의할 수 없다.

`!=`는 equals의 반환 값에 반전을 하여 값을 돌려준다. 즉, 개발자 따로 정의할 필요가 없다.



<br/>


## 7.2.2. 순서 연산자 : compareTo

자바에서 정렬이나 최댓값, 최솟값 등 값을 비교하는 알고리즘에 사용할 클래스는 `Comparable` 인터페이스를 구현한다.
코틀린도 똑같은 Comparable 인터페이스를 지원한다. 

게다가 코틀린은 Comparable 인터페이스 안에 있는 `compareTo` 메소드를 호출하는 관례를 제공한다.
따라서 비교 연산자(<, >, <=, >=)는 compareTo 호출로 컴파일 된다.
반환값은 Int이다. 다른 비교 연산자도 동일한 방식으로 동작한다.

```mermaid
graph LR
      A["a >= b"]-->B["a.compareTo(b) >= 0"]
```

```kotlin
class Person(val firstName: String, val lastName: String): Comparable<Person>{
    override fun compareTo(other: Person): Int {
        // 인자로 받은 함수를 차례로 호출하면서 값을 비교한다
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}

val person1 = Person("Alice", "Smith")
val person2 = Person("Bob", "Johnson")
println(person1 < person2)

// Result
false
```

여기서 정의한 Person 객체의 Comparable 인터페이스를 코틀린뿐 아니라 자바 쪽의 컬렉션 정렬 메소드 등에도 사용할 수 있다.
equals와 마찬가지로 Comparable의 compareTo에도 operator 변경자가 붙어있으므로 
하위 클래스의 오버라이딩 함수에 operator를 붙일 필요가 없다.

`compareValuesBy`는 두 개의 객체와 여러 비교 함수를 인자로 받는다. 
첫 번째 비교 함수에 두 객체를 넘겨 값을 비교 후 같지 않다면 그 결과를 반환하고
같다면 두 번째 비교 함수를 이용하여 비교한다.
이렇게 지정한 비교함수를 지속적으로 비교한다. 만약 모든 비교 함수가 0을 반환하면 최종으로 0을 반환한다.

필드를 직접 비교하면 코드는 조금 더 복잡해지긴 하지만 비교 속도는 훨씬 빠르다.
코드를 작성할 때 일반적으로 이해하기 쉽게 코드를 작성하고 
나중에 그 코드가 얼마나 자주 호출됨에 따라 성능에 문제가 발생한다면 그때 성능을 개선한다.

```kotlin
println("abc" > "def")

// 결과
true
```


<br/>


## 7.3. 컬렉션과 범위에 대해 쓸 수 있는 관례

컬렉션을 다룰 때 가장 많이 쓰는 연산은 인덱스를 사용해 원소를 읽거나 쓰는 연산과 
어떤 값이 컬렉션에 포함되어 있는지 확인하는 연산이다.

이 연산들을 연산자 구문으로 사용할 수 있다. 
인덱스를 사용해 원소를 설정하거나 가져오고 싶을 때는 `a[b]`라는 식을 사용한다.
(이를 <span style="color:orange">인덱스 연산자</span>라고 부른다.) 
in 연산자는 원소가 컬렉션이나 범위에 속하는지 검사하거나 컬렉션에 있는 원소를 이터레이션할 때 사용합니다.

<br/>


## 7.3.1. 인덱스로 원소에 접근: get과 set

코틀린에서 맵의 원소에 접근할 때나 자바에서 배열 원소에 접근할 때 모두 각괄호([ ])를 사용한다는 사실을 알고 있다.
같은 연산자를 사용해 변경 가능 맵에 키/값 쌍을 넣거나 이미 맵에 들어있는 키/값 연관 관계를 변경할 수 있다.

```kotlin
val value = map[key]

mutablaMap[key] = newValue
```

코틀린에서는 인덱스 연산자도 관례를 따른다.
인덱스 연산자를 사용해 원소를 읽는 연산은 get으로 변환되고 원소를 쓰는 연산은 set으로 변환된다.
Map과 MutableMap 인터페이스에는 get, set 연산자 메소드가 이미 들어있다.

```kotlin
// get 연산자 함수를 정의한다
operator fun Point.get(index: Int): Int{
    return when(index){
        // 주어진 인덱스에 해당하는 좌표를 찾는다
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

val p = Point(10,20)
println(p[1])

// 결과
20
```

get 매소드를 만들고 operator 변경자를 붙이면 된다.
p[1]이라는 식은 p가 Point 타입인 경우 방금 정의한 get 메소드로 변환된다.

```mermaid
graph LR
      A["x[a, b]"]-->B["x.get(a, b)"]
```
각괄호를 사용한 접근은 get 함수 호출로 변환된다.



`get` 메소드의 파라미터로 Int가 아닌 타입도 사용할 수 있다.
예를 들면 맵 인덱스 연산의 경우 get 파라미터 타입은 맵의 키 타입과 같은 타입이 될 수 있다.

또한 여러 파라미터를 사용하는 `get`을 정의할 수도 있다.
예를 들면 2차원 행렬이나 배열을 표현하는 클래스에
operator fun get(rowIndex: Int, colIndex: Int)를 정의하면
matrix[row, col] 으로 get 메소드를 호출할 수 있다.
컬렉션 클래스가 다양한 키 타입을 지원해야 한다면 다양한 파라미터 타입에 대해 오버로딩한 get 메소드를 여럿 정의할 수도 있다.

인덱스에 해당하는(a[index]) 컬렉션 원소를 쓰고 싶을 때는 `set`이라는 함수를 정의하면된다.
불변 클래스는 set을 쓸 수 없다. 변경 가능한 클래스에서 가능하다.

```kotlin
data class MutablePoint(var x: Int, var y: Int)

// set이라는 연산자 함수를 정의한다
operator fun MutablePoint.set(index: Int, value: Int) {
    when(index) {
        // 주어진 인덱스에 해당하는 좌표를 변경한다
        0 -> x = value
        1 -> y = value
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

>>> var p = MutablePoint(10, 20)
>>> p[1] = 42
>>> println(p)
MutablePoint(x=10, y=42)
```

대입에 인덱스 연산자를 사용하려면 `set`이라는 이름의 함수를 정의해야 한다.
set이 받는 마지막 파라미터 값은 대입문의 우항에 들어가고, 나머지 파라미터 값은 인덱스 연산자([])에 들어간다.

```mermaid
graph LR
      A["x[a, b] = c"]-->B["x.set(a, b, c)"]
```
각괄호를 사용한 대입문은 set 함수 호출로 컴파일된다.


<br/>


## 7.3.2. in관례

in은 객체가 컬렉션에 들어있는지 검사(멤버십 검사)한다. (true/false)
이 경우에 in 연산자는 contain 함수를 호출한다.

```kotlin
data class Rectangle(val upperLeft: Point, val lowerRight: Point)
operator fun Rectangle.comtains(p: Point): Boolean {
    // 범위를 만들고 x좌표가 그 범위 안에 있는지 검사한다
    return p.x in upperLeft.x until lowerRight.x &&
            // until 함수를 사용해 열린 범위를 만든다
            p.y in upperLeft.y until lowerRight.y
}

>>> val rect = Rectangle(Point(10, 20), Point(50, 50))
>>> println(Point(20, 30) in rect)
true
>>> println(Point(5, 5) in rect)
false
```

in의 우항에 있는 객체는 contains 메소드의 `수신 객체`(예제에서는 Rectangle)가 되고, 
in의 좌항에 있는 객체는 contains 메소드에 `인자`(예제에서는 Point)로 전달된다.

```mermaid
graph LR
      A["a in c"]-->B["c.contains(a)"]
```
in 연산자는 contains 함수 호출로 변환된다.

in은 열린범위이다. 열린범위는 끝 값을 포함하지 않는 범위.

..은 닫힌범위이다. (10..20). 닫힌범위는 끝 값을 포함하는 범위.


<br/>


## 7.3.3. rangeTo 관례

1..10 : 1부터 10까지 모든 수가 들어있는 범위를 가리킨다.
`..`연산자는 rangeTo 함수를 간략하게 표현하는 방법이다.

```mermaid
graph LR
      A["start .. end"]-->B["start.rangeTo(end)"]
```
`..`연산자는 rangeTo로 컴파일된다.


<span style="color:orange">rangeTo 함수는 범위를 반환</span>한다.
이 연산자(rangeTo 함수)는 아무 클래스에나 정의할 수 있지만,
클래스가 Comparable 인터페이스를 구현하면 이 연산자(rangeTo 함수)를 정의하지 않아도 된다.
왜냐면 코틀린 표준 라이브러리에는 모든 Comparable 객체에 대해 적용 가능한 rangeTo 함수가 들어있다.

```kotlin
operator fun <T: Comparable<T>> T.rangeTo(that: T): ClosedRange<T>
```

이 함수는 범위를 반환하며, 어떤 원소가 그 범위 안에 들어있는지 in을 통해 검사할 수 있다.

```kotlin
val now = LocalDate.now()
// 오늘부터 시작해 10일짜리 범위를 만든다
val vacation = now..now.plusDays(10)
// 특정 날짜가 날짜 범위 안에 들어가는지 검사
println(now.plusWeeks(1) in vacation)

// 결과
true
```

now.rangeTo(now.plusDays(10)) 으로 컴파일러에 의해 변환된다.
rangeTo 함수는 LocalDate의 멤버는 아니며, 앞에서 설명한대로 Comparable의 확장 함수.

rangeTo 연산자는 다른 산술 연산자보다 우선순위가 낮다. 하지만 혼동을 피하기 위해 괄호로 감싸주는 것이 더 좋다.
또한, 범위 연산자는 우선 순위가 낮아서 범위의 메소드를 호출하려면 범위를 괄호로 둘러싸야 한다.

```kotlin
val n = 9
println(0..(n+1))  // 산술연산자보다 우선순위가 낮다

(0..n).forEach { println(it) } // 우선순위가 낮아서 메서드 호출시 괄호를 써주는게 좋다.
```

<br/>


## 7.3.4. for 루프를 위한 iterator 관례

2장에서 살펴봤듯이 코틀린의 for 루프는 범위 검사와 똑같이 in 연산자를 사용한다.
하지만 의미는 다르다.
`for (x in list){ ... }`와 같은 문장은 list.iterator()를 호출해서 이터레이터를 얻은 다음, 
자바와 마찬가지로 그 이터레이터에 대해 hasNext, next 호출을 반복하는 식으로 변환된다.

하지만 코틀린에서는 이 또한 관례이므로 iterator 메소드를 확장 함수로 정의할 수 있다. 
이런 성질로 인해 자바 문자열에 대한 for 루프가 가능하다.
코틀린은 String의 상위 클래스인 CharSequence에 대한 iterator 확장 함수를 제공한다. 
따라서 아래와 같은 구문이 가능하다.

```kotlin
// 이 라이브러리 함수는 문자열을 이터레이션 할 수 있게 해준다.
operator fun CharSequence.iterator(): CharIterator

for(c in "abc") { }
```

클래스 안에 직접 iterator를 구현한 예이다.

```kotlin
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

val newYear = LocalDate.ofYearDay(2017, 1)
val daysOff = newYear.minusDays(1)..newYear
for (dayOff in daysOff) { println(dayOff) }

// 결과
2016-12-31
2017-01-01
```

앞에서 rangeTo 함수가 ClosedRange 인스턴스를 반환한다. 
코드에서 ClosedRange<LocaDate>에 대한 확장 함수 Iterator를 정의했기 때문에 
LocalDate의 범위 객체를 for 루프에서 사용할 수 있다.

- CloseRange<LocalData>
  - LocalData 타입의 원소를 가지고 있는 컬렉션인 CloseRange.
- .iterator() : Iterator<LocalDate>
  - 확장 함수 iterator() 정의. 반환 타입은 LocalDate 원소를 가지고 있는 Iterator 컬렉션.
- object : Iterator<LocalDate>
  - LocalDate 타입의 원소를 가지고 있는 Iterator 컬렉션이 타입인 익명객체를 만듬.
- var current = start
  - start : The minimum value in the range.
  - 변수 current에 할당해줌.
- hasNext()
  - current에 원소가 들어있는지 확인해서 있으면 true를 반환한다.
  - true면 for문의 본문이 실행된다.
  - false면 for문 종료
  - current <= endInclusive
  - current.compareTo(endInclusive) <= 0
  - current.compareTo(endInclusive) 의 값이 -거나 0이 나오면 true.
  - +값이 나오거나 바로 false가 반환되면 for문 종료
- next()
  - current 값을 다음 원소 값으로 변경해준다.
  - hasNext()가 true를 반환할경우 next()에서 적용된 원소값으로 본문을 실행한다.

<br/>
<br/>



## 7.4. 구조 분해 선언과 component 함수


구조 분해를 사용하면 복합적인 값을 분해해서 여러 다른 변수를 한꺼번에 초기화할 수 있다.

```kotlin
>>> val p - Point(10, 20)
>>> val (x, y) = p
>>> println(x)
>>> println(y)

10
20
```

구조 분해 선언은 일반 변수 선언과 비슷해 보이지만, =의 좌변에 여러 변수를 괄호로 묶는 것이 다르다.

내부에서 구조 분해 선언은 다시 관례를 사용한다.
구조 분해 선언의 각 변수를 초기화하기 위해 componentN 함수를 호출하는데, 
N은 구조 분해 선언에 있는 변수 위치에 따라붙는 번호이다.

```mermaid
graph LR
      A["val (a, b) = p"]-->B["val a = p.component1() <br/>
                               val b = p.component2()"]
```
구조 분해 선언은 componentN 함수 호출로 변환된다.

data 클래스의 주 생성자에 들어있는 프로퍼티에 대해 컴파일러가 자동으로 componentN 함수를 생성한다.

```kotlin
class Point(val x: Int, val y: Int){
  operator fun component1() = x
  operator fun component2() = y
}
```

구조 분해 선언은 함수에서 여러 값을 반환할 때 유용하다.
여러 값을 반환해야 하는 함수가 있다면 반환해야 하는 모든 값이 들어갈 holder 역할의 데이터 클래스를 정의하고 
함수의 반환 타입을 그 데이터 클래스로 바꾼다. 
구조 분해 선언 구문을 사용해 이 함수가 반환하는 값을 쉽게 풀어 여러 변수에 넣을 수 있다.

```kotlin
// 값을 저장하기 위한 데이터 클래스를 선언한다
data class NameComponents(val name: String,
                          val extension: String)

fun splitFilename(fullName: String): NameComponents {
    val result = fullName.split('.', limit = 2)
    // 함수에서 데이터 클래스의 인스턴스를 반환한다
    return NameComponents(result[0], result[1])
}

// 구조 분해 선언 구문을 사용해 데이터 클래스를 푼다.
val (name, ext) = splitFilename("example.kt")
println(name)
println(ext)

// Result
example
kt
```

<span style="color:orange">배열이나 컬렉션에도 componentN 함수가 있음</span>을 안다면 위 예제를 더 개선할 수 있다. 
크기가 정해진 컬렉션을 다루는 경우 구조 분해가 특히 더 유용하다. 
예를 들어 여기서 split은 2개의 원소로 이뤄진 리스트를 반환한다.

```kotlin
data class NameComponents(val name: String, val extension: String)

fun splitFileName(fullName: String) : NameComponents {
    val (name, ext) = fullName.split(".", limit = 2)
    return NameComponents(name, ext)
}
```

물론 무한히 componentN을 선언할 수 없으므로 이런 구문을 무한정 사용할 수는 없다. 
그럼에도 불구하고 여전히 컬렉션에 대한 구조 분해는 유용하다. 
코틀린 표준 라이브러리에서는 맨 앞의 다섯 원소에 대한 componentN을 제공한다.

> 코틀린은 맨 앞의 다섯 원소에 대한 componentN 함수를 제공한다. 
> 따라서 컬렉션의 크기가 5보다 작아도 1~5까지접근이 가능하다. 
> 하지만, IndexOutOfBoundsException이 발생한다. 
> 여섯 개 이상의 변수를 사용하는 구조 분해를 컬렉션에 대해 적용하면 컴파일 오류가 발생한다.



<br/>


## 7.4.1. 구조 분해 선언과 루프

변수 선언이 들어갈 수 있는 장소라면 어디든 구조 분해 선언을 사용할 수 있다.
맵의 원소에 대해 이터레이션할 때, 구조 분해 선언이 유용하다.

```kotlin
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) { // 루프 변수에 구조 분해 선언 사용
        println("$key -> $value")
    }
}

>>> val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
>>> printEntries(map)
Oracle -> Java
JetBrains -> Kotlin
```

이 간단한 예제는 두 가지 코틀린 관례를 활용한다. 
- 하나는 객체를 iteration하는 관례고, 
- 다른 하나는 구조 분해 선언이다.

코틀린 표준 라이브러리에는 맵에 대한 확장 함수로 iterator가 들어있다. 
그 iterator는 맵 원소에 대한 이터레이터를 반환한다. 
따라서 자바와 달리 코틀린에서는 맵을 직접 이터레이션할 수 있다. 

또한 코틀린 라이브러리는 Map.Entry에 대한 확장 함수로 component1과 component2를 제공한다. 
위의 루프는 이런 확장 함수를 사용하는 아래의 코드와 같다.

```kotlin
for(entry in map.entries) {
    val key = entry.component1()
    val value = entry.component2()
    // ...
}
```

<br/>
<br/>


## 7.5. 프로퍼티 접근자 로직 재활용 : 위임 프로퍼티

코틀린이 제공하는 관례에 의존하는 특성 중에 독특하면서도 가장 강력한 기능인
<span style="color:orange">위임 프로퍼티(delegated property)</span>다.

백킹 필드에 단순히 값을 저장하는 것보다 더 복잡한 방식으로 동작하는 프로퍼티를 쉽게 구현 가능하다.
그 과정에서 접근자 로직을 매번 재 구현할 필요도 없다.
자신의 값을 필드가 아닌 데이터베이스 브라우저 세션 맵 등에 저장할 수 있다.

<span style="color:orange">위임(delegated property)</span>이란
객체가 직접 작업을 수행하지 않고 다른 도우미 객체가 그 작업을 처리하게 하는 디자인 패턴이다.
이런 도우미 객체를 **위임객체**라 부른다.


<br/>


## 7.5.1. 위임 프로퍼티 소개

```kotlin
class Foo {
    var p: Type by Delegate()
}
```
위임 프로퍼티의 일반적인 문법이다.

p 프로퍼티는 접근자 로직을 다른 객체에 위임하는데, 여기서 `Delegate` 클래스의 인스턴스를 위임 객체로 사용한다.
`by` 뒤에 있는 식을 계산하여 위임에 쓰일 객체를 얻는다.
프로퍼티 위임 객체가 따라야 하는 관례를 따르는 모든 객체를 위임에 사용할 수 있다.

```kotlin
class Foo {
    // 컴파일러가 생성한 도우미 프로퍼티
    private val delegate = Delegates()
  
    // p 프로퍼티를 위해 컴파일러가 생성한 접근자는 
    // delegate의 getValue와 setValue 메소드를 호출
    val p: Type
    set(value: Type) = delegate.setValue(value)
    get() = delegate.getValue(...)
}
```

프로퍼티 위임 관례를 따르는 Delegate 클래스는 getValue()와 setValue()를 제공해야 된다.
멤버 메서드나 확장 함수 두 형태 모두 가능하다.
Delegate 클래스를 단순화하면 다음과 같다.

```kotlin
class Delegate {
    // getValue는 게터를 구현하는 로직을 담는다
    operator fun getValue(...) { ... }
    // setValue는 세터를 구현하는 로직을 담는다
    operator fun setValue(..., value: Type) { ... }
}

class Foo {
    // by 키워드는 프로퍼티와 위임 객체를 연결한다
    var p: Type by Delegate()
}

>>> val foo = Foo()
// foo.p라는 프로퍼티 호출은 내부에서 delegate.getValue를 호출한다.
>>> val oldValue = foo.p
// 프로퍼티 값을 변경하는 문장은 내부에서 delegate.setValue를 호출한다
>>> foo.p = new Value
```

foo.p는 일반 프로퍼티처럼 쓸 수 있고, 일반 프로퍼티 같아 보인다.
하지만 실제로 p의 게터나 세터는 Delegate 타입의 위임프로퍼티 객체에 있는 메소드를 호출한다.

<br/>


## 7.5.2. 위임 프로퍼티 사용 : by lazy()를 사용한 프로퍼티 초기화 지연

<span style="color:orange">지연 초기화</span>는 객체의 일부분을 초기화하지 않고 남겨뒀다가 
실제로 그 부분의 값이 필요할 경우, 초기화할 때 흔히 쓰이는 패턴이다.
초기화 과정에 자원을 많이 사용하거나 객체를 사용할 때마다 꼭 초기화하지 않아도 되는 프로퍼티에 대해 
지연 초기화 패턴을 사용할 수 있다.

Person 클래스가 자신이 작성한 이메일의 목록을 제공한다고 가정. 
이메일은 데이터베이스에 들어있어서 불러오려면 시간이 오래 걸려서, 
이메일 프로퍼티의 값을 최초로 사용할 때 단 한 번만 가져오고 싶다.

```kotlin
class Email{ /*...*/ }

fun loadEmails(person: Person): List<Email> {
    println("${person.name}의 이메일을 가져옴")
    return listOf(/*...*/)
}
```

다음은 이메일을 불러오기 전에는 null을 저장하고, 불러온 다음에는 이메일 리스트를 저장하는 _emails 프로퍼티를 추가해서 
지연 초기화를 구현한 클래스를 보여준다.

```kotlin
class Person(val name: String) {
    // 데이터를 저장하고 emails의 위임 객체 역할을 하는 _emails 프로퍼티
    private var _emails: List<Email>? = null
    val emails: List<Email>
       get() {
           if (_emails == null) {
               // 최초 접근시 이메일을 가져온다
               _emails = loadEmails(this) 
           }
            // 저장해 둔 데이터가 있으면 그 데이터를 반환 
           return _emails!!
       }
}

val p = Person("Alice")
// 최초로 emails를 읽을 때 단 한 번만 이메일을 가져온다
p.emails

// 결과
Load emails for Alice
```

<span style="color:orange">뒷받침하는 프로퍼티</span>라는 기법을 사용한다.
_emails 프로퍼티는 값을 저장하고, emails 프로퍼티는 _emails 프로퍼티에 대한 읽기 연산을 제공한다. 
_emails는 Nullable 하고, emails는 널이 될 수 없는 타입이므로 프로퍼티 2개를 사용해야 한다. 
이런 기법은 자주 사용된다.

이와 같은 방법은 성가시며, 스레드 안전하지 않아서 언제나 제대로 동작한다고 말할 수 없다.
대신 위임 프로퍼티를 사용해보자.

```kotlin
class Person(val name: String){
  val emails by lazy { loadEmails(this) }
}
```

`lazy` 함수는 코틀린 관례에 맞는 시그니처의 getValue() 메소드가 들어있는 객체를 반환한다. 
따라서 lazy와 by 키워드와 함께 사용해 위임 프로퍼티를 만들 수 있다.

lazy 함수의 인자는 값을 초기화할 때 호출할 람다다. 그리고 lazy 함수는 기본적으로 스레드 안전하다. 
추가적으로 필요에 따라 동기화에 사용할 락을 lazy 함수에 전달할 수도 있고, 
다중 스레드 환경에서 사용하지 않을 프로퍼티를 위해 lazy 함수가 동기화를 하지 못하게 막을 수도 있다.


<br/>


## 7.5.3. 위임 프로퍼티 구현

어떤 객체를 UI에 표시하는 경우 객체가 바뀌면 자동으로 UI도 바뀌어야 한다.

`PropertyChangeSupport` 클래스는 리스너의 목록을 관리하고 
`PropertyChangeEvent` 이벤트가 들어오면 목록의 모든 리스너에게 이벤트를 통지한다.

필드를 모든 클래스에 추가하고 싶지는 않으므로 PropertyChangeSupport 인스턴스를 `changeSupport`라는 필드에 저장하고 
프로퍼티 변경 리스너를 추적해주는 작은 도우미 클래스를 만들자.

```kotlin
open class PropertyChangeAware {
    protected val changeSupport = PropertyChangeSupport(this)
    
    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }
    
    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }
}
```

이제 읽기 전용 프로퍼티와 변경 가능한 프로퍼티를 정의할 Person 클래스를 작성하자.

```kotlin
class Person(
    val name: String, age: Int, salary: Int
) : PropertyChangeAware() {
    var age: Int = age
    set(newValue) {
        // 뒷받침하는 필드에 접근할 때 field 식별자를 사용
        val oldValue = field
        field = newValue
        // 프로퍼티 변경을 리스너에게 통지한다
        changeSupport.firePropertyChange("age", oldValue, newValue)
    }

    var salary: Int = salary
    set(newValue) {
        val oldValue = field
        field = newValue
        changeSupport.firePropertyChange("salary", oldValue, newValue)
    }
}

>>> val p = Person("Dmitry", 34, 2000)
    // 프로퍼티 변경 리스너를 추가한다. 
>>> p.addPropertyChangeListener(
        PropertyChangeListener { event ->
            println("Property ${event.propertyName} changed from ${event.oldValue} to ${event.newValue}")
        }
    )
>>> p.age = 35
>>> p.salary = 2100

// 결과
Property age changed from 34 to 35
Property salary changed from 2000 to 2100
```

이 코드는 field 키워드를 사용해 age와 salary 프로퍼티를 뒷받침하는 필드에 접근하는 방법을 보여준다.

세터 코드의 중복이 많이 보이므로 이를 제거하고 프로퍼티의 값을 저장하고, 필요에 따라 통지를 보내주는 클래스를 추출하자.

```kotlin
class ObservableProperty(
    val propertyName: String, var propertyValue: Int,
    val changeSupport: PropertyChangeSupport
) {
    fun getValue(): Int = propertyValue
    fun setValue(newValue: Int) {
        val oldValue = propertyValue
        propertyValue = newValue
        changeSupport.firePropertyChange(propertyName, oldValue, newValue)
    }
}

class Person(
    val name: String, age: Int, salary: Int
) : PropertyChangeAware() {
    val _age = ObservableProperty("age", age, changeSupport)

    var age: Int
        get() = _age.getValue()
        set(value) { _age.setValue(value) }

    val _salary = ObservableProperty("salary", salary, changeSupport)
    var salary: Int
        get() = _salary.getValue()
        set(value) { _salary.setValue(value) }
}
```

프로퍼티 값을 저장하고 그 값이 바뀌면 자동으로 변경 통지를 전달해주는 클래스를 만들었고, 로직의 중복을 상당 부분 제거했다.

하지만 아직도 각각의 프로퍼티마다 `ObservableProperty`에 작업을 위임하는 준비 코드가 상당 부분 필요하다.
코틀린의 <span style="color:orange">위임 프로퍼티 기능</span>을 활용하여 이런 준비 코드를 없앨 수 있다.
그전에 `ObservableProperty`에 있는 두 메소드의 시그니처를 코틀린의 관례에 맞게 수정해야 한다.

```kotlin
class ObservableProperty(
    var propertyValue: Int,
    val changeSupport: PropertyChangeSupport
) {
    operator fun getValue(person: Person, property: KProperty<*>): Int = propertyValue
    operator fun setValue(person: Person, property: KProperty<*>, newValue: Int) {
        val oldValue = propertyValue
        propertyValue = newValue
        changeSupport.firePropertyChange(property.name, oldValue, newValue)
    }
}
```
이전 코드와의 차이점.

- 코틀린 관례에 사용하는 다른 함수와 마찬가지로 getValue와 setValue 함수에도 `operator` 변경자가 붙는다.
- `getValue`와 `setValue`는 프로퍼티가 포함된 객체와 프로퍼티를 표현하는 객체를 파라미터로 받는다.
- `KProperty` 인자를 통해 프로퍼티 이름을 전달받으므로 주 생성자에서는 `name` 프로퍼티를 없앤다.

이제 위임 프로퍼티를 사용해 코드를 간결히 할 수 있다.

```kotlin
class Person(
    val name: String, age: Int, salary: Int
) : PropertyChangeAware() {
    var age: Int by ObservableProperty(age, changeSupport)
    var salary: Int by ObservableProperty(salary, changeSupport)
}
```

by 키워드를 사용해 위임 객체를 지정하면 이전 예제에서 직접 코드를 짜야 했던 여러 작업을 
코틀린 컴파일러가 자동으로 처리해준다. 

즉, 컴파일러가 만들어주는 코드는 이전에 직접 작성했던 Person 코드와 비슷하다. 
by 오른쪽에 오는 객체(예제에서는 ObservableProperty)를 
<span style="color:orange">위임 객체(delegate)</span>라고 부른다. 
코틀린은 위임 객체를 감춰진 프로퍼티에 저장하고, 주 객체의 프로퍼티를 읽거나 쓸 때마다 
위임 객체의 getValue와 setVlaue를 호출해준다.

관찰 가능한 프로퍼티 로직을 직접 작성하는 대신 코틀린 표준 라이브러리를 사용해도 된다. 
표준 라이브러리에는 이미 ObservableProperty와 비슷한 클래스가 있다. 

다만 이 표준 라이브러리의 클래스는 PropertyChangeSupport와 연결되어 있지 않아서 
프로퍼티 값을 변경을 통지할 때 PropertyChangeSupport를 사용하는 방법을 알려주는 람다를 
그 표준 라이브러리 클래스에 넘겨야 한다.

```kotlin
class Person(
    val name: String, age: Int, salary: Int
): PropertyChangeAware() {
    // 람다 작성
    private val observer = {
        prop: KProperty<*>, oldValue: Int, newValue: Int ->
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
    
    // Delegates.observable을 사용
    var age: Int by Delegates.observable(age, observer)
    var salary: Int by Delegates.observable(salary, observer)
}
```

by 오른쪽에 있는 식이 꼭 새 인스턴스를 만들 필요는 없다. 
함수 호출, 다른 프로퍼티, 다른 식 등이 by의 우항에 올 수 있다. 

다만 우항에 있는 식을 계산한 결과인 객체는 컴파일러가 호출할 수 있는 
올바른 타입의 getValue와 setValue를 반드시 제공해야 한다. 
다른 관례와 마찬가지로 getValue와 setValue 모두 객체 안에 정의된 메소드이거나 확장 함수일 수 있다.

예제에서는 Int 타입의 프로퍼티 위임만 사용했지만, 프로퍼티 위임 메커니즘을 모든 타입에 사용할 수 있다.

<br/>


## 7.5.4. 위임 프로퍼티 컴파일 규칙

아래와 같은 위임 프로퍼티가 있는 클래스가 있다고 가정하자.

```kotlin
class C {
    var prop: Type by MyDelegate()
}

val c = C()
```

컴파일러는 MyDelegate 클래스의 인스턴스를 감춰진 프로퍼티에 저장하며 
그 감춰진 프로퍼티는 <span style="color:orange">\<delegate></span>라는 이름으로 부른다. 

또한, 컴파일러는 프로퍼티를 표현하기 위해 KProperty 타입의 객체를 사용한다. 
이 객체를 <span style="color:orange">\<property></span>라고 부른다.

컴파일러는 다음의 코드를 생성한다.

```kotlin
class C {
    private val <delegate> = MyDelegate()
    var prop: Type
    get() = <delegate>.getValue(this, <property>)
    set(value: Type) = <delegate>.setValue(this, <property>, value)
}
```

컴파일러는 모든 프로퍼티 접근자 안에 getValue와 setValue 호출 코드를 생성해준다.

```mermaid
graph LR
      A["val x = c.prop"]-->B["val x = <delegate>.getValue(c, <property>)"]
      C["c.prop = x"]-->D["<delegate>.setValue(c, <property>, x)"]
```
프로퍼티를 사용하면 \<delegate>에 있는 getValue나 setValue 함수가 호출된다.

이 매커니즘은 상당히 단순하지만, 상당히 흥미로운 활용법이 많다.
- 프로퍼티 값이 저장될 장소를 바꿀 수도 있고(맵, 데이터베이스 테이블, 사용자 세션의 쿠키 등) 
- 프로퍼티를 읽거나 쓸 때 벌어질 일을 변경할 수도 있다.(값 검증, 변경 통지 등)


<br/>


## 7.5.5. 프로퍼티 값을 맵에 저장

자신의 프로퍼티를 동적으로 정의할 수 있는 객체를 만들 때 위임 프로퍼티를 활용하는 경우가 자주 있다.
그런 객체를 <span style="color:orange">확장 가능한 객체(expando object)</span>라고 한다.

연락처 관리 시스템에서 연락처별로 임의의 정보를 저장할 수 있게 허용하는 경우. 
시스템에 저장된 연락처에는 특별히 처리해야 하는 일부 필수 정보(이름 등)가 있고, 
사람마다 달라질 수 있는 추가정보가 있다.(자식 생일 등)

```kotlin
class Person {
    private val _attributes = hashMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }
    val name: String
        // 수동으로 맵에서 정보를 꺼낸다
        get() = _attributes["name"]!!
}

>>> val p = Person()
>>> val data = mapOf("name" to "Dmitry", "company" to "JetBrains")
>>> for((attrName, value) in data)
        p.setAttribute(attrName, value)  
>>> println(p.name)
}

// 결과
Dmitry
```

이 코드는 추가 데이터를 저장하기 위해 일반적인 API를 사용하고(실제 프로젝트에서는 JSON 역직렬화 등의 기술을 활용할 수 있음), 
특정 프로퍼티(name)을 처리하기 위해 구체적인 API를 제공한다. 
이를 쉽게 위임 프로퍼티를 활용하게 변경할 수 있다. 
by 키워드 뒤에 맵을 직접 넣으면 된다.

```kotlin
class Person {
    private val _attributes = hashMapOf<String, String>()
    
    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }
    // 위임 프로퍼티를 맵에 사용
    val name: String by _attributes
}
```

이런 코드가 작동하는 이유는 표준 라이브러리가 Map과 MutableMap 인터페이스에 대해 
getValue와 setValue 확장 함수를 제공하기 때문이다. 
getValue에서 맵에 프로퍼티를 값을 저장할 때는 자동으로 프로퍼티 이름을 키로 활용한다. 
여기서 `p.name`은 `_attributes.getValue(p, prop)`라는 호출을 대신하고, 
`_attributes.getValue(p, prop)`는 다시 `_attributes[prop.name]`을 통해 구현된다.


<br/>

## 7.5.6. 프레임워크에서 위임 프로퍼티 활용

객체 프로퍼티를 저장하거나 변경하는 방법을 바꿀 수 있으면 프레임 워크를 개발할 때 유용하다.
데이터베이스에 User라는 테이블이 있고 name, age를 컬럼으로 갖는다.

```kotlin
object Users: IdTable() { // 객체는 데이터 베이스 테이블에 해당한다.
    // 프로퍼티는 테이블 칼럼에 해당한다.
    val name = varchar("name", length = 50).index()
    val age = integer("age")

}

class User(id: EntityID) : Entity(id){ // 각 User 인스턴스는 테이블에 들어있는 구체적인 엔티티에 해당
    // 사용자 이름은 데이터 베이스 name 칼럼에 들어있다
    var name: String by Users.name
    var age: Int by Users.age
}
```

Users 객체는 데이터베이스 테이블이라 1개만 존재 해야되서 싱글턴으로 생성.
User의 상위 클래스인 Entity 클래스는 데이터베이스 칼럼을 엔티티의 속성 값으로 연결해주는 매핑이 있다.

User 프로퍼티에 접근할때 자동으로 Entity 클래스에 정의된 데이터베이스 맵핑으로 필요한 값을 가져온다.
Users.name(varchar), User.age(integer)는 각각 위임 객체 관례에 따른 시그니처를 요구사항을 구현한다.



<br/>
<br/>
<br/>
<br/>
<br/>