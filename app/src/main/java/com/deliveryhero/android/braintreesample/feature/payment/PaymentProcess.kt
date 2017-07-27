package com.deliveryhero.android.braintreesample.feature.payment

import com.deliveryhero.android.braintreesample.feature.process.Process

interface PaymentProcess : Process<PaymentProcess.Status> {

  fun initialize(withToken: String, withDeviceData: String? = null)
  fun makeSinglePayment(forAmount: Double)

  sealed class Status {
    data class ConfigurationReceived(val data: String) : Status()
    data class NonceReceived(val data: String) : Status()

    sealed class Terminal : Status() {
      object Cancelled : Terminal()
      object Success : Terminal()
      object Failure : Terminal() // TODO: May require field(s) to capture reasons
      data class Error(val throwable: Throwable) : Terminal()
    }
  }
}