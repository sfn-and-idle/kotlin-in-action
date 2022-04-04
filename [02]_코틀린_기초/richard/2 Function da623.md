# 2. Functions and Variables

## 2.1 Functions & Variables

### 2.1.1 Hello World

```jsx
// Array는 일반 클래스 처럼 사용 된다
// 리턴타입이 void일 경우 Unit으로 해도 된다
fun main(args: Array<String>): Unit {
	// Java: System.out.println("Hello, world!");
	println("Hello, world!")
}
```

- function정의: fun을 사용
    - function name, args, return type, body
- function은 클래스 밖에 있어도 됨
- 배열은 그냥 일반 클래스 처럼 사용하면 됨
    - Java: int[], Kotlin: Array<Int>()
- Java: System.out.println(””) ⇒ Kotlin: println(””)

### 2.1.2 Functions

**Function정의**

![Screen Shot 2022-01-15 at 11.44.43 PM.png](2%20Function%20da623/Screen_Shot_2022-01-15_at_11.44.43_PM.png)

```java
삼항 연산자

// Java
int value = 1;
String abc = value == 1 ? "a" : "b";

// Kotlin
val value = 1;
val abc = if (value == 1) "a" else "b";
```

- 문: statement
- 식: expression
    - 값이 있음, 코틀린에서 `if` 는 식이다
    

**식이 본문인 함수**

```kotlin
fun max(a: Int, b: Int): Int = if (a > b) a else b
```

- function을 정의한 다음 `=` 을 사용해서 식을 함수 본문을 바로 지정 할 수 있다
    - 본문
        - 블록: fun someFunction() { return ”abc”) }
        - 식: fun someFunction() = ”abc”
        - 반환타입을 생략해도 된다. String을 리턴한다는 것은 코틀린에서 알아서 추측해준다

### 2.1.3 Variables

**변수 선언(타입 정의)**

- 코틀린에서는 타입 추론이 가능하다
    - Java에서는 변수를 선언할 때 타입부터 적는다
    - Kotlin에서는 keyword(val/var)부터 시작하고 타입 정의는 선택적으로 할 수 있다

```kotlin
val question = "The Ultimate Question of Life, the Universe, and Everything"
val answer = 42

val answer: Int = 42

// double
val yearsToCompute = 7.5
```

**변경 가능/변경 불가능 변수**

- 변경 가능(var)
- 변경 불가능(val)
- 될 수 있으면 val을 사용하는게 좋다

```kotlin
// 명확하게 val이 한번만 지정 될 수 있으면 된다
val message: String
if (canPerformOperation()) {
    message = "Success"
    // ... perform the operation
}
else {
    message = "Failed"
}
```

변경 불가능 변수

```kotlin
// 변경 불가능 한 변수이지만 객체 참조에 대한 변경이 불가능한 것이다. 실제 객체를 변경 할 수 있다.
val languages = arrayListOf("Java")
languages.add("Kotlin")
```

변수의 타입은 고정이다

```kotlin
// 오류
var answer = 42
answer = "no answer"
```

### 2.1.4 String Templates

문자열 템플릿(스트링 안에 변수를 값으로 치환)

```kotlin
// 변수가 존재하는지 컴파일러가 확인 해준다
fun main(args: Array<String>) {
    val name = if (args.size > 0) args[0] else "Kotlin"
    println("Hello, $name!")
}

// 단순히 변수만 사용할 수 있는게 아니다(array index)
fun main(args: Array<String>) {
    if (args.size > 0) {
        println("Hello, ${args[0]}!")
    }
}

// 단순히 변수만 사용할 수 있는게 아니다(if/else)
fun main(args: Array<String>) {
    println("Hello, ${if (args.size > 0) args[0] else "someone"}!")
}
```

- 컴파일 된 코드는 StringBuilder을 사용한다

## 2.2 Classes and Properties

### 2.2 Classes and Properties

- 자바 처럼 코틀린에서도 클래스가 있다(객체를 인스턴스를 만들 때 사용되는 틀)
- 코틀린 특징
    - Value Object: 생성자에서 변수 값을 지정해주는 부분은 코틀린에서 생략 가능
    - 코트린에서 가시성 기본 값은 public

```java
/* Java */
public class Person {
	private final String name;
	
	public Person(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	} 
}
```

```kotlin
class Person(val name: String)
```

### 2.2.1 Properties

**Java**

- 객체를 통한 캡슐화를 사용한다
    - 객체에 담기는 정보는 필드로 참조 하고 있다
    - 보통 필드의 가시성은 private으로 하고 필요에 따라 accessor(getter)을 제공 한다
    - 가시성 + 필드 ⇒ property
        - read only: getter
        - writable: getter + setter
    
    ```jsx
    /* Java */
    public class Person {
    	private final String name;
    	
    	public Person(String name) {
    		this.name = name;
    	}
    	
    	public String getName() {
    		return name;
    	} 
    }
    ```
    

**Kotlin**

- Property선언
    - val
        - field, getter
    - var
        - field, getter, setter

![Screen Shot 2022-01-16 at 12.04.53 PM.png](2%20Function%20da623/Screen_Shot_2022-01-16_at_12.04.53_PM.png)

**Java에서 Kotlin클래스 (Person)코드 사용하기**

```jsx
/* Java */
>>> Person person = new Person("Bob", true);
>>> System.out.println(person.getName());
Bob
>>> System.out.println(person.isMarried());
true
```

**Kotlin에서 Kotlin클래스 (Person)코드 사용하기**

- 간결해지는 부분
    - Property를 사용할 때 getter/setter없이 사용 가능
    - 객체를 생성할 때 `new` 를 사용하지 않아도 된다
    
    ```jsx
    val person = Person("Bob", true)
    println(person.name)
    println(person.isMarried)
    ```
    
- 팁

<aside>
💡 Java에서 getter / setter이 정의 되어 있을 경우 코틀린에서 property방식으로 접근 가능하다

Kotlin custom getter / setter: 단순한 필드 값이 아니고 계산해주는 로직이 추가해야할 때 사용하면 된다

</aside>

### 2.2.2 Custom Accessors

필드에 따로 값을 지정하지 않고 getter을 정의하고 동적으로 값을 계산 할 수 있다

```kotlin
// Definition
class Rectangle(val height: Int, val width: Int) {
	val isSquare: Boolean get() { return height == width }
}

// Usage
val rectangle = Rectangle(41, 43)
println(rectangle.isSquare)
false
```

### 2.2.3 **Kotlin source code layout: directories and packages**

- 코틀린 파일에서 최 상단에 package를 정의할 수 있다
- 파일 안에 정의 된 class, function, property는 다 파일 상단에 정의한 package로 지정 된다
- 다른 파일에 같은 package로 지정 된 코드는 바로 사용 할 수 있다
    - 다른 파일 + 다른 package일 경우 자바 처럼 import가 필요함
    
    ![Untitled](2%20Function%20da623/Untitled.png)
    
    - 코틀린에서 class/function을 import할 수 있다
        - top level function포함
        
        ```kotlin
        package geometry.example
        import geometry.shapes.createRandomRectangle
        
        fun main(args: Array<String>) {
        	println(createRandomRectangle().isSquare)
        }
        ```
        
    - 하위/일괄 import할 때 다음 문법 사용 가능 `.*`
    - Package
        - Java
        
        ![Screen Shot 2022-01-16 at 1.03.38 PM.png](2%20Function%20da623/Screen_Shot_2022-01-16_at_1.03.38_PM.png)
        
        - Kotlin
            - package를 자바에 비해 더 자유롭게 사용 할 수 있다.  shapes라는 하위 폴더를 사용하지 않아도 되고 geometry.shapes.*관련 모든 코드를 shapes.kt에 넣어도 된다
            
            ![Screen Shot 2022-01-16 at 1.04.13 PM.png](2%20Function%20da623/Screen_Shot_2022-01-16_at_1.04.13_PM.png)
            
            - 가능하면 Java규칙을 사용하는것 도 괜찮다

## 2.3 선택 표현과 처리: enum과 when

### 2.3 선택 표현과 처리: enum과 when

Java의 switch랑 비슷하다고 생각하면 된다

### 2.3.1 enum 클래스 정의

- 이넘 정의하는 방법
    
    ```kotlin
    enum class Color {
        RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
    }
    ```
    
- `enum` 은 soft keyword이라서 `class` 앞에 사용하면 특별한 의미가 있다. 그 외에 상황에서는 그냥 사용 가능하다
- `enum` 에 코드 추가할 수 있음. 단순 값의 묶음뿐이 아님.
    
    ![Untitled](2%20Function%20da623/Untitled%201.png)
    

### 2.3.2 when으로 enum클래스 다루기

- Switch처럼 분기할 수 있다
- 분기 탈 조건을 찾으면 해당 코드만 실행 된다. 자바 switch처러 break없으면 다음 조건으로 흘러가지 않는다
    
    ```kotlin
    fun getMnemonic(color: Color) =
        when (color) {
            Color.RED -> "Richard"
            Color.ORANGE -> "Of"
            Color.YELLOW -> "York"
            Color.GREEN -> "Gave"
            Color.BLUE -> "Battle"
            Color.INDIGO -> "In"
            Color.VIOLET -> "Vain"
    }
    ```
    

### 2.3.3 when과 임의의 객체를 함께 사용

- 여러 값을 통해 같은 분기를 태울 수 있다
    
    ```kotlin
    
    fun getWarmth(color: Color) = when(color) {
        Color.RED, Color.ORANGE, Color.YELLOW -> "warm"
        Color.GREEN -> "neutral"
        Color.BLUE, Color.INDIGO, Color.VIOLET -> "cold"
    }
    ```
    
- enum값들을 직접 import해서 값을 사용하는 코드를 더 간결하게 작성할 수 있다
    
    ```kotlin
    import ch02.colors.Color
    import ch02.colors.Color.*
    
    fun getWarmth(color: Color) = when(color) {
        RED, ORANGE, YELLOW -> "warm"
        GREEN -> "neutral"
        BLUE, INDIGO, VIOLET -> "cold"
    }
    ```
    
- Kotlin `when` 을 사용할 때 분기 조건은 상수 외에도 가능하다
    
    ![Untitled](2%20Function%20da623/Untitled%202.png)
    

### 2.3.4 인자 없는 when사용

- 인자 없는 when: set을 사용하면 garbage가 많이 생기니깐 다음 처럼 바꿀 수 있음(boolean값이 true인 분기문을 타게 된다)
    
    ```kotlin
    fun mixOptimized(c1: Color, c2: Color) =
        when {
            (c1 == RED && c2 == YELLOW) ||
            (c1 == YELLOW && c2 == RED) -> ORANGE
    
            (c1 == YELLOW && c2 == BLUE) ||
            (c1 == BLUE && c2 == YELLOW) -> GREEN
    
            (c1 == BLUE && c2 == VIOLET) ||
            (c1 == VIOLET && c2 == BLUE) -> INDIGO
    
            else -> throw Exception("Dirty color")
        }
    
    >>> println(mixOptimized(BLUE, YELLOW))
    GREEN
    ```
    

### 2.3.5 스마트 캐스트: 타입 검사와 타입 캐스트를 조합

- 간단한 연산: (1 + 2) + 4
- Tree구조를 사용하기
    
    ![Untitled](2%20Function%20da623/Untitled%203.png)
    
- 코드(kotlin에서 interface를 implement하기)

```kotlin
// 모델
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

// 계산
fun eval(e: Expr): Int {
    if (e is Num) { // instanceof랑 비교할 수 있음
				val n = e as Num // explicit cast
        return n.value
    }
    if (e is Sum) {
        return eval(e.right) + eval(e.left) // smart cast
		}
    throw IllegalArgumentException("Unknown expression")
}

// 결과
>>> println (eval(Sum(Sum(Num(1), Num(2)), Num (4))))
```

- 스마트 케스트: 타입을 한번 확인 했으면 해당 타입으로 다시 케스트 처리 안해도 된다
    - 컴파일러가 알아서 캐스팅 해준다
    - 자바 instanceof랑 비슷하다

### 2.3.6 리팩토링: if를 when으로 변경

2.3.5의 eval function리펙토링하기

- kotlin에서 if/else는 expression이라서 다음 처럼 작성할 수 있음
    
    ```kotlin
    fun eval(e: Expr): Int =
      if (e is Num) {
          e.value
      } else if (e is Sum) {
    		eval(e.right) + eval(e.left)
      } else {
        throw IllegalArgumentException("Unknown expression")
    }
    
    >>> println(eval(Sum(Num(1), Num(2))))
    3
    ```
    
- if/else대신 when으로 리펙토링
    
    ```kotlin
    fun eval(e: Expr) : Int = 
    	when (e) {
    		is Num -> e.value
    		is Sum -> eval(e.right) + eval(e.left)
    		else -> throw IllegalArgumentException("Unknown expression")
    	}
    ```
    

### 2.3.7 if와 when의 분기에서 블록 사용

- 블록 안에 마지막 expression이 리턴 된다
- 위 사항은 function을 제외한 나머지 블록에 해당 된다
    
    ```kotlin
    fun evalWithLogging(e: Expr): Int =
        when (e) {
            is Num -> {
                println("num: ${e.value}")
                e.value
    				}
    				is Sum -> {
                val left = evalWithLogging(e.left)
    						val right = evalWithLogging(e.right)
    				    println("sum: $left + $right")
    				    left + right
    				}
            else -> throw IllegalArgumentException("Unknown expression")
    	 }
    ```
    

## 2.4 대상을 이터레이션: while과 for 루프

### 2.4 대상을 이터레이션: while과 for 루프

for loop: for <item> in <elements>

### 2.4.1 while루프

while/do-while

```kotlin
// The body is executed while the condition is true.
while (condition) {
    /*...*/
}

// The body is executed for the first time unconditionally. After that, it’s executed while the condition is true.
do {
    /*...*/
} while (condition)
```

### 2.4.2 수에 대한 이터레이션: 범위와 수열

- Java처럼 일반적인 for loop은 없고 range라는 개념을 사용한다
    
    ```kotlin
    // Java
    for(int i = 0; i < 10; i++) {
    	System.out.println("abc");
    }
    
    // Kotlin
    fun fizzBuzz(i: Int) = when {
        i % 15 == 0 -> "FizzBuzz "
        i % 3 == 0 -> "Fizz "
        i % 5 == 0 -> "Buzz "
    		else -> "$i "
    
    // 100부터 1까지, 단위는 2
    for (i in 100 downTo 1 step 2) {
    	print(fizzBuzz(i))
    }
    
    // half open range: until
    (x in 0 until size) // 동일: (x in 0..size-1)
    ```
    

### 2.4.3 맵에 대한 이터레이션

- Map iteration
    
    ```kotlin
    // map[x]으로 get/put할 수 있음
    val binaryReps = TreeMap<Char, String>()
    // in을 사용해서  이터레이션
    for (c in 'A'..'F') {
        val binary = Integer.toBinaryString(c.toInt())
    		binaryReps[c] = binary // get/put map entries
    }
    
    // in을 사용해서  이터레이션
    // key,value
    for ((letter, binary) in binaryReps) {
        println("$letter = $binary")
    }
    ```
    
- List iteration
    
    ```kotlin
    // index를 받을 수도 있음
    val list = arrayListOf("10", "11", "1001")
    
    // in을 사용해서 이터레이션
    for ((index, element) in list.withIndex()) {
        println("$index: $element")
    }
    ```
    

### 2.4.4 in으로 컬렉션이나 범위의 원소 검사

- in을 사용해서 컬렉션 포함 여부 체크
    
    ```kotlin
    fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
    fun isNotDigit(c: Char) = c !in '0'..'9'
    ```
    
- when하고 in같이 사용
    
    ```kotlin
    fun recognize(c: Char) = when (c) {
        in '0'..'9' -> "It's a digit!"
        in 'a'..'z', in 'A'..'Z' -> "It's a letter!"
        else -> "I don't know..."
    }
    
    >>> println(recognize('8'))
    It's a digit!
    ```
    
- range는 숫자랑 문자로 제한 되어있지 않다
    - comparable interface를 implement하는 객체이면 range하고 사용할 수 있다
        
        ```kotlin
        // "Java".."Scala"사이에 있는 모든 문자열을 나열할 수 없지만 range에 포함 되는지는 판단 할 수 있다
        >>> println("Kotlin" in "Java".."Scala")
        true
        
        // Range랑 Set의 차이
        >>> println("Kotlin" in setOf("Java", "Scala"))
        false
        ```
        

## 2.5 코틀린의 예외 처리

### 2.5 코틀린의 예외 처리

- throw는 try-catch-finally로 잡아주지 않으면 호출하는 코드쪽으로 계속 이어서 전달된다
- throw키워드랑 코틀린 방식 생성자를 통해 에러를 던질 수 있다(new없이)
    
    ```kotlin
    if (percentage !in 0..100) {
        throw IllegalArgumentException(
            "A percentage value must be between 0 and 100: $percentage")
    }
    ```
    

### 2.5.1 try, catch, finally

- 문법
    
    ```kotlin
    fun readNumber(reader: BufferedReader): Int? {
        try {
            val line = reader.readLine()
            return Integer.parseInt(line)
        }
        catch (e: NumberFormatException) {
            return null
    }
    finally {
            reader.close()
        }
    }
    
    >>> val reader = BufferedReader(StringReader("239"))
    >>> println(readNumber(reader))
    239
    ```
    
- Java랑 비교: 함수 정의하는 부분에 `throws` 를 추가 하지 않는다
    - Java에서는 함수가 던질 수 있는 모든 에러를 명시해야한다(Checked Exception)
        - 코틀린 언어를 설계하면서 일부로 이렇게 선택을 했다. 자바에서 의미 있게 제대로 try-catch-finally를 사용하는 경우가 생각 보다 많이 없다. 코틀린에서 개발자가 의도적으로 try-catch를 사용하기를 바란다.
            - java: rethrow, quiet catch, propagate
        - TODO: check this with Korean book
    
    ```kotlin
    // Java
    public void someFunction() throws ExceptionA, ExceptionB {
    	...
    }
    ```
    

### 2.5.2 try를 식으로 이용

- try-catch가 값을 리턴할 수도 있다

```kotlin
fun readNumber(reader: BufferedReader) {
    val number = try {
        Integer.parseInt(reader.readLine())
    } catch (e: NumberFormatException) {
			return // 또는 null을 식에서 리턴해도 된다 
		}
    println(number)
}

>>> val reader = BufferedReader(StringReader("not a number"))
>>> readNumber(reader)
```

## 2.6 요약

- fun
- val/var
- “${}”
- Value Object: Properties
- if/else, return values
- when
- smart casting, is
- for, while
- ranges: 1..5
- in
- exceptions