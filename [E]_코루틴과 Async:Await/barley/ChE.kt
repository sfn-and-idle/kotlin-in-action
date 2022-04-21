package chE

import kotlinx.coroutines.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

fun main() {
    println("\n\n===== E.2.1 / launch =====")

    log("main() started...")
//    launchInGlobalScope()
//    log("launchInGlobalScope() executed")
//    runBlockingExample()
//    log("runBlockingExample() executed")
//    Thread.sleep(2000L)
    yieldExample()
    log("yieldExample() executed")
    Thread.sleep(2000L)
    log("main() terminated...")

    println("\n\n===== E.2.1 / launch =====")
    sumAll()

    println("\n\n===== E.2.2 =====")
    runBlocking {
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
    }

    println("\n\n===== E.4.1 =====")
//    val gen = idMaker()
//    println(gen.next(Unit)) // 0
//    println(gen.next(Unit)) // 1
//    println(gen.next(Unit)) // 2
//    println(gen.next(Unit)) // null
}

// ===== E.4.1 =====
//fun idMaker() = generate(Int, Unit) {
//    var index = 0
//    while (index < 3)
//        yield(index++)
//}

interface Generator<out R, in T> {
    fun next(param: T): R? // 제네레이터가 끝나면 null을 돌려주므로 ?가 붙음
}

@RestrictsSuspension
interface GeneratorBuilder<in T, R> {
    suspend fun yield(value: T): R
    suspend fun yieldAll(generator: Generator<T, R>, param: R)
}



// ===== E.3 =====

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

// ===== E.2.1 / async =====

fun sumAll() {
    runBlocking {
        val d1 = async { delay(500L); 1 }
        log("after async (d1)")
        val d2 = async { delay(1000L); 2 }
        log("after async (d2)")
        val d3 = async { delay(1500L); 3 }
        log("after async (d3)")

        log("1 + 2 + 3 = ${d1.await() + d2.await() + d3.await()}")
        log("after await all & add")
    }
}


// ===== E.2.1 / launch =====
fun now() = ZonedDateTime.now().toLocalTime().truncatedTo((ChronoUnit.MILLIS))

fun log(msg: String) = println("${now()}:${Thread.currentThread()}: $msg")

fun launchInGlobalScope() {
    GlobalScope.launch {
        log("coroutine started.......")
    }
}

fun runBlockingExample() {
    runBlocking {
        launch {
            log("GlobalScope.launch started.......")
        }
    }
}

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