package com.deliveryhero.android.braintreesample.feature.payment

import com.deliveryhero.android.braintreesample.model.ClientToken
import io.reactivex.Single

interface RetrieveToken {

  operator fun invoke(): Single<ClientToken>
}