package com.deliveryhero.android.braintreesample.feature.process

import com.deliveryhero.android.braintreesample.service.Threader
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseProcess<S> : Process<S> {

  private val disposables = CompositeDisposable()
  private val statusPublisher = PublishSubject.create<S>()
  private val transformer = Threader

  private var resumeAction: (() -> Unit)? = null

  protected fun <T> Observable<T>.thenCall(onNext: (T) -> Unit) {
    compose(transformer.observableTransformer())
        .subscribe(onNext, { stop(withStatus = errorState(it)) })
        .let { disposables.add(it) }
  }

  protected fun <T> Single<T>.thenCall(onNext: (T) -> Unit) {
    compose(transformer.singleTransformer())
        .subscribe(onNext, { stop(withStatus = errorState(it)) })
        .let { disposables.add(it) }
  }

  protected fun <T> Maybe<T>.thenCall(onNext: (T) -> Unit) {
    compose(transformer.maybeTransformer())
        .subscribe(onNext, { stop(withStatus = errorState(it)) })
        .let { disposables.add(it) }
  }

  protected fun Completable.thenCall(onNext: () -> Unit) {
    compose(transformer.completableTransformer())
        .subscribe(onNext, { stop(withStatus = errorState(it)) })
        .let { disposables.add(it) }
  }

  /**
   * This should be called only if a non-halting status should be emitted.
   */
  protected fun emit(status: S) {
    statusPublisher.onNext(status)
  }

  /**
   * This should be called only if a halting status should be emitted.
   */
  protected fun halt(status: S, onResume: () -> Unit) {
    statusPublisher.onNext(status)
    resumeAction = onResume
  }

  override fun resume() {
    resumeAction?.invoke() ?: stop(withStatus = errorState(IllegalStateException("Missing resume action")))
  }

  protected abstract fun errorState(throwable: Throwable): S

  protected fun stop(withStatus: S) {
    statusPublisher.onNext(withStatus)
    stop()
  }

  override fun stop() {
    disposables.clear()
    resumeAction = null
    onStop()
  }

  protected abstract fun onStop()

  override fun statusUpdates(): Observable<S> = statusPublisher
}