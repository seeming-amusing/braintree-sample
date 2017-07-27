package com.deliveryhero.android.braintreesample.service

import io.reactivex.CompletableTransformer
import io.reactivex.MaybeTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object Threader {

  private val subscribeOn = Schedulers.io()
  private val observeOn = AndroidSchedulers.mainThread()

  fun <T> observableTransformer() = ObservableTransformer<T, T> { it.subscribeOn(subscribeOn).observeOn(observeOn) }
  fun <T> maybeTransformer() = MaybeTransformer<T, T> { it.subscribeOn(subscribeOn).observeOn(observeOn) }
  fun <T> singleTransformer() = SingleTransformer<T, T> { it.subscribeOn(subscribeOn).observeOn(observeOn) }
  fun completableTransformer() = CompletableTransformer { it.subscribeOn(subscribeOn).observeOn(observeOn) }
}