package com.deliveryhero.android.braintreesample.feature.payment

import com.deliveryhero.android.braintreesample.BraintreeSampleApplication.Companion.paymentService

class RetrieveTokenImpl : RetrieveToken {

  override operator fun invoke() = paymentService.getClientToken(null, null)
}