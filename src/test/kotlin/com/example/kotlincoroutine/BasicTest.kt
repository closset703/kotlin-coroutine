package com.example.kotlincoroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit

@SpringBootTest
class BasicTest {
    @Timeout(value = 3000L, unit = TimeUnit.SECONDS)
    @Test
    fun `your first coroutine`() {
        GlobalScope.launch { // 백그라운드로 새로운 코루틴을 실행한고 계속한다. launch a new coroutine in background and continue
            delay(1000L) // 비동기를 1초간 지연시킨다. / non-blocking delay for 1 second (default time unit is ms)
            println("World!") // 지연 이후에 출력한다. / print after delay
        }
        println("Hello") // 메인 쓰레드는 코루틴이 지연되는동안 계속된다. / main thread continues while coroutine is delayed
        Thread.sleep(2000L) // JVM이 2초간 살아있도록 메인쓰레드를 블락한다. / block main thread for 2 seconds to keep JVM alive
    }

    @Timeout(value = 3000L, unit = TimeUnit.SECONDS)
    @Test
    fun `Bridging blocking and non-blocking worlds - first example`() {
        GlobalScope.launch { // 백그라운드로 새로운 코루틴을 실행한고 계속한다. / launch a new coroutine in background and continue
            delay(1000L)
            println("World!")
        }
        println("Hello,") // 메인 쓰레드는 여기까지 바로 진행한다. / main thread continues here immediately
        runBlocking {     // 메인 쓰레드를 블라킹한다. / but this expression blocks the main thread
            delay(2000L)  //JVM을 유지시키기 위해 2초의 딜레이 시간을 준다 / .. while we delay for 2 seconds to keep JVM alive
        }
    }


    @Timeout(value = 3000L, unit = TimeUnit.SECONDS)
    @Test
    fun `Bridging blocking and non-blocking worlds - with run blocking`() {
        runBlocking { // 메인 코루틴을 시작한다. /  start main coroutine
            GlobalScope.launch { // 백그라운드에 새로운 코루틴을 시작하고 계속한다. / launch a new coroutine in background and continue
                delay(1000L)
                println("World!")
            }
            println("Hello,") // 메인 쓰레드는 여기까지 진행한다 / main coroutine continues here immediately
            delay(2000L)// JVM을 지속시키기위해 2초간 지연시킨다. delaying for 2 seconds to keep JVM alive
        }
    }


    @Timeout(value = 1100L, unit = TimeUnit.SECONDS)
    @Test
    fun `Waiting for a job`() {
        runBlocking { // **샘플과 다르게 추가함, 없을경우 suspended를 추가하여 task가 되기 때문에 테스트가 진행되지 않아서 추가하였다.
            val job = GlobalScope.launch { // 코루틴을 시작하고 그 잡에 대해 참조를 얻는다. / launch a new coroutine and keep a reference to its Job
                delay(1000L)
                println("World!")
            }
            println("Hello,")
            job.join() // 코루틴이 종료될 때까지 기다린다. / wait until child coroutine completes
        }
    }

    @Timeout(value = 1100L, unit = TimeUnit.SECONDS)
    @Test
    fun `Structured concurrency`() {
        runBlocking { // this : 코루틴 스코프 / this: CoroutineScope
            launch { // 블로킹 스코프에서 새로운 코루틴을 시작한다. / launch a new coroutine in the scope of runBlocking
                delay(1000L)
                println("World!")
            }
            println("Hello,")
        }
    }

    @Timeout(value = 1100L, unit = TimeUnit.SECONDS)
    @Test
    fun `Scope builder`() {
        runBlocking { // 코루틴 스코프 / this: CoroutineScope
            launch {
                delay(200L)
                println("Task from runBlocking")
            }

            coroutineScope { // 코루틴 스코프를 생성한다. / Creates a coroutine scope
                launch {
                    delay(500L)
                    println("Task from nested launch")
                }

                delay(100L)
                println("Task from coroutine scope") // 이 줄은 중첩 스코프가 실행되기전에 출력된다. /  This line will be printed before the nested launch
            }

            println("Coroutine scope is over") // 중첩된 실행이 완료되기 전에 이 줄은 출력되지 않는다. / This line is not printed until the nested launch completes
        }
    }


    @Timeout(value = 1100L, unit = TimeUnit.SECONDS)
    @Test
    fun `Extract function refactoring`() {
        suspend fun doWorld() {
            delay(1000L)
            println("World!")
        }

        runBlocking {
            launch { doWorld() }
            println("Hello,")
        }
    }
}
