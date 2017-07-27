package com.deliveryhero.android.braintreesample.feature.process

import io.reactivex.Observable

interface Process<S> {

  fun resume()

  fun stop()

  fun statusUpdates(): Observable<S>
}