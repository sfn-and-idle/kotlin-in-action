# E. 코루틴과 Async/Await

## 입구
코틀린 1.3부터 코루틴이 표준 라이브러리에 정식 포함.
- 권태환님 자료
  - https://speakerdeck.com/taehwandev/kotlin-coroutines

(필자 : 아마도 안드로이드 개발자 사이에서 셀럽?)

## E.1. 코루틴이란?

위키피디아의 코루틴 정의를 번역

> 코루틴은 컴퓨터 프로그램 구성 요소 중 하나로
비선점형 멀티태스킹(non-preemptive multitasking)을 수행하는
일반화한 서브루틴(subroutine)이다.
코루틴은 실행을 일시 중단(suspend)하고 재개(resume)할 수 있는 여러 진입 지점(entry point)을 허용한다.

<span style="color:orange">서브루틴</span>(subroutine)은 
여러 명령어를 모아 이름을 부여해서 반복 호출을 할 수 있게 정의한 
프로그램 구성 요소(a.k.a. 함수).
객체지향 언어에서는 메서드도 서브루틴이라 할 수 있음.

서브루틴에 진입하는 방법은 오직 한 가지 뿐.
- 해당 함수를 호출하면 서브루틴의 맨 처음부터 실행 시작.
- 그때마다 활성 레코드(activation record)가 스택에 할당되면서 
  서브루틴 내부의 로컬 변수 등이 초기화 됨.

반면 서브루틴 안에서 여러 번 return 을 사용할 수 있다.
서브루틴이 실행을 중단하고 제어를 호출한쪽caller에게 돌려주는 지점은 여럿 있을 수 있다.

다만 일단 서브루틴에서 반환되고 나면 활성 레코드가 스택에서 사라지기 때문에 
실행 중이던 모든 상태를 잃어버린다. 
그래서 서브루틴을 여러 번 반복 실행해도 항상 같은 결과를 얻게 된다.
(전역 변수나 다른 부수 효과가 있지 않는 한)

<span style="color:orange">멀티태스킹</span>은 
여러 작업을 동시에 수행하는 것처럼 보이거나, 실제로 동시에 수행하는 것이다.


<span style="color:orange">비선점형</span>(non-preemptive)이란
멀티태스킹의 각 작업을 실행하는 참여자들의 실행을 
운영체제가 강제로 일시 중단시키고 다른 참여자를 실행하게 만들 수 없다는 뜻.
각 참여자들이 서로 자발적으로 협력해야만 비선점형 멀티태스킹이 제대로 작동할 수 있다.

따라서...

코루틴이란 서로 협력해서 실행을 주고받으면서 작동하는 여러 서브루틴을 말한다.
코루틴의 대표격인 제네레이터를 예로 들면
- 어떤 함수 A가 실행되다가 제너레이터인 코루틴 B를 호출하면
- A가 실행되던 스레드 안에서 코루틴 B의 실행이 시작
- 코루틴 B는 실행을 진행하다가 실행을 A에 양보한다.
  - yield라는 명령을 사용하는 경우가 많다
- A는 다시 코루틴을 호출했던 바로 다음 부분부터 실행을 계속 진행하다가 또 코루틴 B를 호출
  - 이때 B가 일반적인 함수라면 로컬 변수를 초기화하면서 처음부터 실행을 다시 시작하겠지만
  - 코루틴이면 이전 yield로 실행을 양보했던 지점부터 실행을 계속하게 된다.
  
아래는 코루틴의 제어 흐름과 일반적인 함수의 제어 흐름을 비교한 것.

////////////////////////


코루틴을 사용하는 경우 장점은...
- 일반적인 프로그램 로직을 기술하듯 코드를 작성하고
- 상대편 코루틴에 데이터를 넘겨야 하는 부분에서만 yield를 사용하면 된다는 점

예시. 제네레이터를 사용해 카운트다운을 구현하고 이터레이터처럼 불러와 사용하는 의사 코드
```kotlin
generator countdown(n) {
    while (n > 0) {
        yield n
        n -= 1
    }
}

for i in countdown(10) {
    println(i)
}
```

<br>

## E.2. 코틀린의 코루틴 지원: 일반적인 코루틴

언어에 따라 제네레이터 등 특정 형태의 코루티만을 지원하는 경우도 있고 
일반적인 코루틴을 만들 수 있는 기능을 언어가 기본 제공하고 
다양한 코루틴은 그런 기본 기능을 활용해 직접 사용자가 만들거나 라이브러리를 통해 사용하도록 하는 형태가 있다.

제네레이터만 제공하는 경우에도 yield 시 퓨처 등 비동기 처리가 가능한 객체를 넘기는 방법을 사용하면
async/await 등을 비교적 쉽게 구현할 수 있다.

코틀린은 코루틴을 구현할 수 있는 기본 도구를 언어가 제공하는 형태. 
(특정 코루틴을 언어가 지원하는 형태가 아님.)

코틀린의 코루틴 지원 기본 기능
- kotlin.coroutine 패키지 밑에 있고
- 코틀린 1.3부터는 별도의 설정 없이도 모든 기능 사용 가능

하지만 코틀린이 지원하는 기본 기능을 활용해 다양한 형태의 코루틴들은 kotlinx.coroutines 패키지 밑에 있으며
코루틴 github에서 소스코드를 볼 수 있다.

이제 프로젝트의 빌드 설정에 관련 의존성을 추가하고 코틀린 컴파일러를 1.3으로 지정해보자.

```
// 메이븐은 생략, 본 예시는 그래이들 (그리고 최신)
// 안드로이드 설정도 생략
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
}

plugins {
    kotlin("jvm") version "1.6.10"
}
```

<br>

### E.2.1. 여러 가지 코루틴

kotlinx.coroutines.core 모듈에 들어있는 코루틴. 
각각은 코루틴을 만들어주는 코루틴 빌더라고 부른다.
코틀린에서 코루틴 빌더에 원하는 동작을 람다로 넘겨서 코루틴을 만들어 실행하는 방식으로 코루틴을 활용한다.

#### 👉 kotlinx.coroutines.CoroutineScope.launch

launch는 코루틴을 잡(job)으로 반환하며,
만들어진 코루틴은 기본적으로 즉시 실행된다.
원하면 launch가 반환한 Job의 cancel을 호출해 코루틴 실행을 중단시킬 수 있다.

launch가 작동하려면 CoroutineScope 객체가 블록의 this로 지정돼야 하는데...
(API 문서나 소스를 보면 launch가 받는 블록의 타입이 
`suspend CoroutineScope.() -> Unit`임을 알 수 있다. 이해 안될 시 DSL 내용 다시 보기)
- 다른 suspend 함수 내부라면 해당 함수가 사용중인 CoroutineScope가 있겠지만
- 그렇지 않은 경우에는 GlobalScope를 사용하면 된다.

```kotlin
fun now() = ZonedDateTime.now().toLocalTime().truncatedTo((ChronoUnit.MILLIS))

fun log(msg:String) = println("${now()}:${Thread.currentThread()}: $msg")

fun launchInGlobalScope() {
    GlobalScope.launch {
        log("coroutine started.......")
    }
}

fun main() {
  log("main() started...")
  launchInGlobalScope()
  log("launchInGlobalScope() executed")
  Thread.sleep(2000L)
  log("main() terminated...")
}
```
```
01:04:53.115:Thread[main,5,main]: main() started...
01:04:53.223:Thread[main,5,main]: launchInGlobalScope() executed
01:04:53.226:Thread[DefaultDispatcher-worker-1,5,main]: coroutine started.......
01:04:55.229:Thread[main,5,main]: main() terminated...
```

유의할 점은 메인 함수와 `GlobalScope.launch`가 만들어낸 코루틴이 서로 다른 스레드에서 실행된다는 점.
`GlobalScope`는 메인 스레드가 실행중인 동안만 코루틴의 동작을 보장.
앞 코드에서 main의 끝에서 두번째 줄에 있는 sleep을 없애면 코루틴이 아예 실행되지 않을 것.

launchInGlobalScope가 호출한 launch는 스레드가 생성되고 시작되기 전에 
메인 스레드의 제어를 main에 돌려주기 때문에 따로 sleep을 하지 않으면 main이 바로 끝나고
메인 스레드가 종료되면서 바로 프로그램 전체가 끝나 버린다.
GlobalScope를 사용할 때는 조심해야 한다!!!!

이를 방지하려면 비동기적으로 launch를 실행하거나
launch가 모두 다 실행될 때까지 기다려야 한다.

특히 코루틴의 실행이 끝날 때까지 현재 스레드를 블록시키는 함수로 `runBlocking`이 있다.
runBlocking은 CoroutineScope의 확장 함수가 아닌 일반 함수이기 때문에
별도의 코루틴 스코프 객체 없이 사용 가능하다

launchInGlobalScope를 runBlockingExample이라는 이름으로 함수를 만들어보자.

```kotlin
fun runBlockingExample() {
    runBlocking {
        launch {
            log("GlobalScope.launch started.......")
        }
    }
}
```
```
01:46:11.884:Thread[main,5,main]: main() started...
01:46:11.954:Thread[main,5,main]: GlobalScope.launch started.......
01:46:11.954:Thread[main,5,main]: runBlockingExample() executed
01:46:13.959:Thread[main,5,main]: main() terminated...
```

스레드가 모두 main 스레드이다!!!

코루틴들은 서로 yield를 해주면서 협력할 수 있다. 다음 예시를 보자.

```kotlin
fun yieldExample() {
  runBlocking {
    launch {
      log("1")
      yield()
      log("3")
      yield()
      log("5")
    }
    log("after first launch...")
    launch {
      log("2")
      delay(500L)
      log("4")
      delay(500L)
      log("6")
    }
    log("after second launch...")
  }
}

// main
log("main() started...")
yieldExample()
log("yieldExample() executed")
Thread.sleep(2000L)
log("main() terminated...")
```
```
01:56:28.478:Thread[main,5,main]: main() started...
01:56:28.546:Thread[main,5,main]: after first launch...
01:56:28.550:Thread[main,5,main]: after second launch...
01:56:28.551:Thread[main,5,main]: 1
01:56:28.552:Thread[main,5,main]: 2
01:56:28.557:Thread[main,5,main]: 3
01:56:28.557:Thread[main,5,main]: 5
01:56:29.055:Thread[main,5,main]: 4
01:56:29.559:Thread[main,5,main]: 6
01:56:29.560:Thread[main,5,main]: yieldExample() executed
01:56:31.560:Thread[main,5,main]: main() terminated...
```

다음 특징을 확인할 수 있다.
- launch는 즉시 반환된다.
- runBlocking은 내부 코루틴이 모두 끝난 다음에 반환된다.
- delay를 사용한 코루틴은 그 시간이 지날 때까지 다른 코루틴에게 실행을 양보한다.
  - 앞 코드에서 delay 대신 yield를 쓰면 숫자가 차례로 표시될 것이다.
  - 흥미로운 것 : 첫번째 코루틴이 두번이나 yield를 했지만 두번째 코루틴이 delay 상태에 있었기 때문에 
    다시 제어가 첫번째 코루틴에게 돌아왔다는 것

<br>

#### 👉 kotlinx.coroutines.CoroutineScope.async

async는 사실상 launch와 같은 일을 한다.
- 유일한 차이점
  - launch가 Job을 반환하는 반면
  - async는 Deffered를 반환한다는 점

Deffered는 Job을 상속한 클래스이기 때문에 async를 대신 사용해도 문제없다.

Deffered와 Job의 차이
- Job
  - 아무 타입 파라미터가 없다.
  - Job은 Unit을 돌려주는 Deffered<Unit>이라고 생각할 수도 있다.
- Deffered
  - 타입 파라미터가 있는 제네릭 타입
  - 안에 await 함수가 정의돼 있다.
  - 타입 파라미터는 Deffered 코루틴이 계산을 하고 돌려주는 값의 타입

async는 코드 블록을 비동기로 실행할 수 있고
(제공하는 코루틴 컨텍스트에 따라 여러 스레드를 사용하거나
한 스레드안에서 제어만 왔다 갔다 할 수 있다.)
async가 반환하는 Deffered의 await을 사용해서 코루틴이 결과 값을 내놓을 때까지 기다렸다가 
결과값을 얻어낼 수 있다.

```
19:17:19.027:Thread[main,5,main]: after async (d1)
19:17:19.028:Thread[main,5,main]: after async (d2)
19:17:19.029:Thread[main,5,main]: after async (d3)
19:17:20.540:Thread[main,5,main]: 1 + 2 + 3 = 6
19:17:20.540:Thread[main,5,main]: after await all & add
```

d1 ~ d3를 순서대로 실행하면
(병렬 처리에서 직렬화해 실행한다고 말한다)
6초 이상이 걸려야 하지만 결과를 얻을 때까지 3초가 걸렸다.
async로 코드를 실행하는데는 시간이 거의 걸리지 않았다.

그럼에도 불구하고 스레드를 여럿 사용하는 병렬 처리와 달리 
모든 async 함수들이 메인 스레드 안에서 실행됨을 볼 수 있다.
이 부분이 async/await와 스레드를 사용한 병렬 처리의 큰 차이.

이 예제에서는 겨우 3개의 비동기 코드만을 실행했지만,
비동기 코드가 늘어남에 따라 async/await을 사용한 비동기가 빛을 발한다.
실행하려는 작업이 시간이 얼마 걸리지 않거나 I/O에 의한 대기 시간이 크고,
CPU 코어 수가 작아 동시에 실행할 수 있는 스레드 개수가 한정된 경우에는
특히 코루틴고 ㅏ일반 스레드를 사용한 비동기 처리 사이에 차이가 커진다.

<br>
<br>

### E.2.2. 코루틴 컨텍스트와 디스패처

launch, async 등은 CoroutineScope의 확장 함수.
그런데 CoroutineScope에는 CoroutineContext 타입의 필드 하나만 들어있다.

CoroutineScope는 CoroutineContext 필드를 launch 등의 확장 함수 내부에서 사용하기 위한 
매개체 역할만을 담당.
원한다면 launch 등에 CoroutineContext를 넘길 수 있다는 점에서
CoroutineScope보다 CoroutineContext가 코루틴 실행에 더 중요한 의미가 있다.

CoroutineContext는 실제로 코루틴이 실행 중인 여러 작업(Job 타입)과 디스패처를 저장하는 일종의 맵.
코틀린 런타임은 이 CoroutineContext를 사용해서
다음에 실행할 작업을 선정하고, 어떻게 스레드에 배정할지 대한 방법을 결정한다.

```kotlin
launch { // 부모 컨텍스트를 사용 (이 경우 main)  
    println("main runBlocking : I'm working in thread ${Thread.currentThread().name}")
}
launch(Dispatchers.Unconfined) { // 특정 스레드에 종속되지 않음, 에인 스레드 사용   
    println("Unconfined : I'm working in thread ${Thread.currentThread().name}")
}
launch(Dispatchers.Default) { // 기본 디스패처를 사용   
    println("Default  : I'm working in thread ${Thread.currentThread().name}")
}
launch(newSingleThreadContext("MyOwnThread")) { // 새 스레드를 사용
    println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
}
```

같은 launch를 사용하더라도 전달하는 컨텍스트에 따라 서로 다른 스레드상에서 코루틴이 실행됨을 알 수 있다.

<br>

### E.2.3. 코루틴 빌더와 일시 중단 함수

launch, async, runBlocking은 모두 코루틴 빌더. 코루틴을 만들어주는 함수.
`kotlinx-coroutines-core` 모듈이 제공하는 코루틴 빌더는 다음과 같이 더 있다.

- produce
  - 정해진 채널로 데이터를 스트림으로 보내는 코루틴을 만든다. 
  - ReceiveChannel<> 을 반환하며 해당 채널로 부터 메세지를 전달 받아 사용가능하다.
- actor
  - 정해진 채널로 메세지를 받아 처리하는 액터를 코루틴으로 만든다. 
  - SendChannel<> 을 반환하며 해당 채널의 send() 매서드를 통해 액터에게 메세지를 보낼 수 있다.

delay() 및 yield() - 일시 중단(suspending) 함수.
아래는 `kotlinx-coroutines-core` 모듈의 최상위에 정의된 일시 중단 함수들.

- withContext : 다른 컨텐스트로 코루틴을 전환한다.
- withTimeout : 코루틴이 정해진 시간 안에 실행되지 않으면 예외를 발생시키게 한다.
- withTimeoutOrNull : 정해진 시간 안에 실행되지 않는다면, null을 결과로 돌려준다.
- awaitAll : 
  모든 작업의 성공을 기다린다. 작업 중 하나라도 예외로 실패하면 awaitAll() 또한 해당 예외로 실패한다.
- joinAll : 모든 작업이 끝날 때 까지 현재 작업을 일시 중단시킨다.


<br>

## E.3.  suspend  키워드와 코틀린의 일시 주단 함수 컴파일 방법

코루틴이 아닌 일반 함수 속에서 delay나 yield를 쓰면??

main 함수 안에 yield를 넣으면 오류가 표시된다. 
컴파일러로 컴파일을 해보면 동일한 오류 메시지가 표시된다.
일시 중단 함수를 코루틴이나 일시 중단 함수가 아닌 함수에서 호출하는 것은 컴파일러 수준에서 금지된다.

일시 중단 함수는 어떻게 만들 수 있을까?
코틀린은 코루틴 지원을 위해 suspend라는 키워드를 제공. 
함수 정의의 fun 앞에 suspend를 넣으면 일시 중단 함수를 만들 수 있다.
launch 시 호출할 코드가 복잡하다면 별도의 suspend 함수를 정의해 호출하는 것도 가능하다.

```kotlin
suspend fun yieldThreeTimes() {
    log("1")
    delay(1000)
    yield()
    log("2")
    delay(1000)
    yield()
    log("3")
    delay(1000)
    yield()
    log("4")
}

fun suspendExample() {
    GlobalScope.launch { yieldThreeTimes() }
}
```

suspend 함수의 작동. 예를 들어 일시 중단 함수안에서 yield를 해야하는 경우.
- 코루틴에 진입할 때와 코루틴에서 나갈 때 
  코루틴이 실행중이던 상태를 저장하고 복구하는 등의 작업을 할 수 있어야 한다.
- 현재 실행 중이던 위치를 저장하고
  다시 코루틴이 재개될 때 해당 위치로부터 실행을 재개할 수 있어야 한다.
- 다음에 어떤 코루틴을 실행할지 결정한다

마지막 동작은 코루틴 컨텍스트에 있느느 디스패처에 의해 수행된다.
일시 중단 함수를 컴파일하는 컴파일러는 앞의 두가지 작업을 할 수 있는 코드를 생성해내야 한다.
이때 코틀린은 컨티뉴에이션 패싱 스타일(CPS) 변환과 상태 기계를 활용해 코드를 생성해낸다.

CPS 변환은 프로그램의 실행 중 특정 시점 이후에 진행해야 하는 내용을 별도의 함수로 뽑고
(이런 함수를 컨티뉴에이션이라 부른다)
그 함수에게 현재 시점까지 시행한 결과를 넘겨서 처리하게 만드는 소스코드 변환 기술이다.

CPS를 사용하는 경우 프로그램이 다음에 해야 할 일이 항상 컨티뉴에이션이라는 함수 형태로 전달되므로,
나중에 할 일이 명확히 알 수 있고 그 컨티뉴에이션에 넘겨야 할 값이 무엇인지도 명확하게 알 수 있기 때문에
프로그램이 실행 중이던 특정 시점의 맥락을 잘 저장했다가 필요할 때 다시 재개할 수 있다.
어떤 면에서 CPS는 콜백 스타일 프로그래밍과도 유사.

suspend가 붙은 함수를 코틀린 컴파일러는 컴파일하면서 뒤에 Continuation을 인자로 만들어 붙여준다.
이 함수를 호출할 때는 함수 호출이 끝난 후 수행해야 할 작업을 var1에 Continuation으로 전달하고,
함수 내부에서는 필요한 모든 일을 수행한 다음에 결과를 var1에 넘기는 코드를 추가한다.

CPS를 사용하면 코루틴을 만들기 위해 필수적인 일시 중단 함수를 만드는 문제가 쉽게 해결될 수 있다.
다만 모든 코드를 전부 CPS로만 변환하면 지나치게 많은 중간 함수들이 생길 수 있으므로
상태 기계를 적당히 사용해서 코루틴이 제어를 다른 함수에 넘겨야 하는 시점에서만 
컨티뉴에이션이 생기도록 만들 수 있다.

<br>

## E.4. 코루틴 빌더 만들기

일반적으로 직접 코루틴 빌더를 만들 필요는 없다. 기존 async, launch 등만으로도 대부분 처리 가능.
간단히 코틀린 코루틴 예제에 들어있는 제네레이터 빌더를 살펴본다.

<br>

### E.4.1. 제네레이터 빌더 사용법

제네레이터 빌더가 있을 때 어떻게 사용하는지.

```kotlin
fun idMaker() = generate(Int, Unit) {
    var index = 0
    while (index < 3)
        yield(index++)
}

val gen = idMaker()
println(gen.next(Unit)) // 0
println(gen.next(Unit)) // 1
println(gen.next(Unit)) // 2
println(gen.next(Unit)) // null
```

<br>

### E.4.2. 제네레이터 빌더 구현

generate 함수 만들기.

generate 함수가 만들어 반환해야 하는 Generator<R, T> 타입.
이 제네레이터는 next(T)를 호출해 R 타입의 값을 돌려받아 처리할 수 있게 해주는 객체.
타입은 다음과 같이 next라는 메서드만 있는 인터페이스다.

```kotlin
interface Generator<out R, in T> {
    fun next(param: T): R? // 제네레이터가 끝나면 null을 돌려주므로 ?가 붙음
}
```

generator가 받는 블록 안에서는 yield() 메서드를 쓸 수 있어야 한다.
블록 안에서 어떤 메서드를 쓸 수 있게 만드는 것은 
코틀린 수신 객체 지정 람다를 사용한 DSL의 가장 기본적인 활용 예 중 하나다.

- 타입은 R을 받아서 Unit을 돌려주는 함수여야 한다
- yield가 들어있는 어떤 클래스를 this로 갖고 있어야 블록 안에서 yield를 호출할 수 있다

그런 클래스 이름을 GeneratorBuilder라고 붙이자.

```
블록의 타입
block: suspend GeneratorBuilder<T, R>.(R) -> Unit 
```

GeneratorBuilder는 yield를 제공하는 클래스.

```kotlin
@RestrictsSuspension
interface GeneratorBuilder<in T, R> {
  suspend fun yield(value: T): R
  suspend fun yieldAll(generator: Generator<T, R>, param: R)
}
```

RestrictsSuspension 애노테이션은 suspend가 붙은 함수에만 이 클래스를 수신 객체로 지정할 수 있게한다.

```kotlin
fun <T, R> generate(block: suspend GeneratorBuilder<T, R>.(R) -> Unit): Generator<T, R> {
    val coroutine = GeneratorCoroutine<T, R>()
    val initial: suspend (R) -> Unit = { result -> block(coroutine, result) }
    coroutine.nextStep = { param -> initial.startCoroutine(param, coroutine) }
    return coroutine
}
```

generate 함수 안에서는 코루틴을 만들고 
그 코루틴이 맨 처음 호출 됐을 때 실행할 코드와 다음 단계를 진행할 때 실행할 코드를 지정하고,
그렇게 만든 코루틴을 반환해야 한다.

(요약자 : 부분별 설명 생략)

GeneratorBuilder와 Generator의 구현을 제공해야 한다.

(요약자 : 처리해야 하는 작업 설명 생략)

Generator는 next를 제공하는데 
next는 yield가 저장해둔 다음 단계(nextStep) 블록을 현재 스레드상에서 실행하고,
lastValue 값을 반환하면 된다.

GeneratorBuilder와 Generator 구현은 서로 밀접히 물려 있고 내부 정보를 공유해야 하므로
이 두 인터페이스를 한 클래스가 구현하게 만들 것이다.

(요약자 : 예제 생략)

컴파일 에러 -> startCoroutine의 두번째 인자 타입이 Continuation이 아니라는 것.
GeneratorBuilder의 yield에서 Continuation을 사용해 모든 처리가 끝나면
제네레이터의 최종 결과를 받아 처리하는 Continuation을 호출해줘야 
프로그램의 나머지 부분이 제대로 계속 실행되기 때문.
그러므로 generate 함수가 만드는 GeneratorCoroutine는 Unit을 받는 Continuation이기도 해야 한다.

```kotlin
internal class GeneratorCoroutine<T, R>: Generator<T, R>, GeneratorBuilder<T, R>, Continuation<Unit> {
    lateinit var nextStep: (R) -> Unit
    private var lastValue: T? = null
    private var lastException: Throwable? = null

    // Generator<T, R> 구현

    override fun next(param: R): T? {
        nextStep(param)
        lastException?.let { throw it }
        return lastValue
    }

    // GeneratorBuilder<T, R> 구현

    override suspend fun yield(value: T): R = suspendCoroutineUninterceptedOrReturn { cont ->
        lastValue = value
        nextStep = { param -> cont.resume(param) }
        COROUTINE_SUSPENDED
    }

    override suspend fun yieldAll(generator: Generator<T, R>, param: R): Unit = suspendCoroutineUninterceptedOrReturn sc@ { cont ->
        lastValue = generator.next(param)
        if (lastValue == null) return@sc Unit // delegated coroutine does not generate anything -- resume
        nextStep = { param ->
            lastValue = generator.next(param)
            if (lastValue == null) cont.resume(Unit) // resume when delegate is over
        }
        COROUTINE_SUSPENDED
    }

    // Continuation<Unit> implementation

    override val context: CoroutineContext get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        result
            .onSuccess { lastValue = null }
            .onFailure { lastException = it }
    }
}
```

전체 코드가 실행되는 과정
1. 메인 함수가 실행
2. 메인에서 idMaker를 실행
3. idMaker는 generate()를 실행
   1. generate()는 GeneratorCoroutine를 만든다.
      이 GeneratorCoroutine는 코루틴이자 제네레이터 빌더 역할을 함께 수행한다.
   2. GeneratorCoroutine 타입 객체인 coroutine의 nextStep으로는 
      generate()에 넘어간 블록을 갖고 코루틴을 싲가하는 코드를 넣는다.
   3. coroutine을 반환한다.
4. 이 시점에서는 실제로는 아무 코루틴도 만들어지거나 실행되지 않았다.
5. 메인 함수에서 제네레이터의 next를 호출
6. 제네레이터의 next는 nextStep을 호출.
   그 안에는 코루틴을 시작하는 코드가 들어있다.
   코루틴이 시작되면 디스패처는 적절한 스레드를 선택하고 
   initial에 저장된 suspend 람다에 들어가 있는 블록을 시작한다.

(요약자 : 메인함수와 제네레이터, 각 루틴의 설명은 생략, p.611)
(요약자 : launch 구현 생략, p.612)


<br>

### E.5. 결론

코루틴 빌더 구현쪽을 이해하지 못하더라도
launch, async, await 정도의 기본 제공 코루틴 빌더만으로도 충분히 개발 가능.
코틀린 홈페이지 예제나 자바스크립트의 async/await 코드를 살펴보면서
비동기 코딩에서 async/await를 사용하는 경우와
콜백이나 퓿를 사용하는 경우의 어려움을 비교해보고
async/await을 사용하는 간결한 개발을 해보기 바란다.


<br/>
<br/>
<br/>
<br/>