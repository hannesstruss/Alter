package alter.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun runTest(body: suspend CoroutineScope.() -> Unit) = runBlocking { body() }
