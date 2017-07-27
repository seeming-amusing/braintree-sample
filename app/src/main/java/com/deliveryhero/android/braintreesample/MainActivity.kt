package com.deliveryhero.android.braintreesample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.deliveryhero.android.braintreesample.BraintreeSampleApplication.Companion.initializeTrees
import com.deliveryhero.android.braintreesample.feature.payment.PaymentProcess
import com.deliveryhero.android.braintreesample.feature.payment.PaymentProcessImpl
import com.deliveryhero.android.braintreesample.feature.payment.RetrieveToken
import com.deliveryhero.android.braintreesample.feature.payment.RetrieveTokenImpl
import com.deliveryhero.android.braintreesample.service.Threader
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

  companion object {
    const val AUTH_TOKEN = "braintree.demo.authToken"
    const val DEVICE_DATA = "braintree.demo.deviceData"
  }

  private val disposables by lazy { CompositeDisposable() }
  private val retrieveToken: RetrieveToken by lazy { RetrieveTokenImpl() }
  private val paymentProcess: PaymentProcess by lazy { PaymentProcessImpl(this) }

  private lateinit var authToken: String
  private var deviceData: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setUpPaymentStatusHandler()
    initializeButtons()
    initializeTrees(outputTo = response_output)
    if (savedInstanceState == null) retrievePaymentToken()
  }

  private fun setUpPaymentStatusHandler() {
    Timber.d("Listening for payment process status")
    paymentProcess.statusUpdates()
        .compose(Threader.observableTransformer())
        .subscribe({ handleStatus(it) },
                   { Timber.w(it, "Payment process failed unexpectedly") })
        .let { disposables.add(it) }
  }

  private fun handleStatus(status: PaymentProcess.Status) {
    when (status) {
      is PaymentProcess.Status.ConfigurationReceived -> onReceiveDeviceData(status.data)
      is PaymentProcess.Status.NonceReceived -> onReceiveNonce(status.data)
      PaymentProcess.Status.Terminal.Success -> Timber.d("Payment successful!")
      PaymentProcess.Status.Terminal.Failure -> Timber.w("Payment unsuccessful. :(")
      PaymentProcess.Status.Terminal.Cancelled -> cancelPayment()
      is PaymentProcess.Status.Terminal.Error ->
        Timber.w("Payment error: ${status.throwable.message} (${status.throwable.javaClass.simpleName})")
    }
  }

  private fun onReceiveDeviceData(data: String) {
    Timber.i("Received device data: $data")
    deviceData = data
  }

  private fun onReceiveNonce(data: String) {
    Timber.i("Received nonce: $data")
    Timber.i("No further steps are available; remaining status is pending")
    paymentProcess.resume()
  }

  private fun cancelPayment() {
    Timber.d("Payment cancelled")
    clear()
  }

  private fun clear() {
    purchase_button.isEnabled = false
    authToken = ""
    deviceData = null
    paymentProcess.stop()
  }

  private fun initializeButtons() {
    restart.setOnClickListener {
      response_output.text = ""
      clear()
      retrievePaymentToken()
    }
    purchase_button.setOnClickListener { makePurchase() }
  }

  private fun retrievePaymentToken() {
    Timber.d("Loading token...")
    retrieveToken()
        .compose(Threader.singleTransformer())
        .subscribe({ onTokenRetrieved(it.value) },
                   { Timber.w(it, "Unexpected error: ${it.message}") })
        .let { disposables.add(it) }
  }

  private fun onTokenRetrieved(token: String) {
    Timber.d("Received token: $token")
    authToken = token
    paymentProcess.initialize(token, deviceData)
    if (deviceData == null) purchase_button.isEnabled = true
  }

  private fun makePurchase() {
    purchase_button.isEnabled = false
    paymentProcess.makeSinglePayment(forAmount = 0.05)
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.clear()
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    outState?.apply {
      putString(AUTH_TOKEN, authToken)
      putString(DEVICE_DATA, deviceData)
    }
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    savedInstanceState?.let {
      onReceiveDeviceData(it.getString(DEVICE_DATA))
      onTokenRetrieved(it.getString(AUTH_TOKEN))
    }
  }
}