package com.micheal.sample

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object TestMain {
    @JvmStatic
    fun main(args: Array<String>){
        firstCoroutineDemo0()
        coroutineDemo1()
    }


    private fun firstCoroutineDemo0() = runBlocking {
        launch{
            delay(3000L)
            println("Hello,")
        }
        println("World!")
        delay(3000L)

        launch {
            delay(1000)
            println("Hello,")
        }
    }


    @JvmStatic
    private fun coroutineDemo1() = runBlocking {
        val job = async {
            println("Hello,")
            delay(1000)
        }
        job.await().also {
            print("World")
            delay(500)
        }

    }
}