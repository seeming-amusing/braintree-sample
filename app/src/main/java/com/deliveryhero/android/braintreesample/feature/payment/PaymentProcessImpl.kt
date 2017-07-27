package com.deliveryhero.android.braintreesample.feature.payment

import android.app.Activity
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.DataCollector
import com.braintreepayments.api.PayPal
import com.braintreepayments.api.interfaces.*
import com.braintreepayments.api.models.PayPalRequest
import com.deliveryhero.android.braintreesample.feature.process.BaseProcess

class PaymentProcessImpl(private val bindingActivity: Activity)
  : PaymentProcess, BaseProcess<PaymentProcess.Status>() {

  private val configurationListener = ConfigurationListener { braintree?.collectData() }
  private val nonceCreatedListener = PaymentMethodNonceCreatedListener {
    halt(PaymentProcess.Status.NonceReceived(it.nonce)) {
      // TODO: Requires further refinement of payment service definition + test environment
      stop(PaymentProcess.Status.Terminal.Success)
    }
  }
  private val cancelListener = BraintreeCancelListener { stop(PaymentProcess.Status.Terminal.Cancelled) }
  private val errorListener = BraintreeErrorListener { stop(PaymentProcess.Status.Terminal.Error(it)) }

  private var braintree: BraintreeFragment? = null
  private var deviceData: String? = null

  private fun BraintreeFragment?.collectData() {
    if (deviceData == null) {
      DataCollector.collectDeviceData(this, {
        deviceData = it
        emit(PaymentProcess.Status.ConfigurationReceived(it))
      })
    }
  }

  override fun initialize(withToken: String, withDeviceData: String?) {
    deviceData = withDeviceData
    braintree = BraintreeFragment.newInstance(bindingActivity, withToken)
    braintree.addListeners(configurationListener, nonceCreatedListener, cancelListener, errorListener)
  }

  private fun BraintreeFragment?.addListeners(vararg listeners: BraintreeListener) =
      this?.let { listeners.forEach { addListener(it) } }

  override fun makeSinglePayment(forAmount: Double) {
    braintree?.let {
      PayPal.requestOneTimePayment(it, paypalRequest(forAmount.toString()))
    } ?: stop(withStatus = errorState(IllegalStateException("Must call initialize(withToken) first")))
  }

  private fun paypalRequest(forAmount: String? = null) =
      PayPalRequest(forAmount).intent(PayPalRequest.INTENT_AUTHORIZE)

  override fun onStop() {
    braintree.removeListeners(configurationListener, nonceCreatedListener, cancelListener, errorListener)
    braintree.detach()
    braintree = null
  }

  private fun BraintreeFragment?.removeListeners(vararg listeners: BraintreeListener) =
      this?.let { listeners.forEach { removeListener(it) } }

  private fun BraintreeFragment?.detach() = this?.let {
    bindingActivity.fragmentManager.beginTransaction().remove(this).commit()
  }

  override fun errorState(throwable: Throwable) = PaymentProcess.Status.Terminal.Error(throwable)
}