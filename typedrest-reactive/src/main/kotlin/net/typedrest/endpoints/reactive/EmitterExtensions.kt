package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.ObservableEmitter
import java.time.Duration
import kotlin.math.min

/**
 * Sleep for the specified duration unless the emitter is disposed in the meantime.
 */
fun ObservableEmitter<*>.sleep(duration: Duration) {
    val waitMillis = duration.toMillis()
    var slept = 0L
    while (slept < waitMillis && !isDisposed) {
        val step = min(500L, waitMillis - slept)
        Thread.sleep(step)
        slept += step
    }
}
