package eu.kanade.tachiyomi.util

import rx.Observable
import Komari.util.lang.awaitSingle

actual suspend fun <T> Observable<T>.awaitSingle(): T = awaitSingle()
