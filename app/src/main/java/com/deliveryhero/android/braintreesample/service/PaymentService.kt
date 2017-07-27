package com.deliveryhero.android.braintreesample.service

import com.deliveryhero.android.braintreesample.model.ClientToken
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PaymentService {

  @GET("/client_token")
  fun getClientToken(@Query("customer_id") customerId: String?,
                     @Query("merchant_account_id") merchantAccountId: String?)
      : Single<ClientToken>
}

object ApiClientRequestInterceptor : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
        .newBuilder()
        .addHeader("User-Agent", "braintree/android-demo-app/2.5.5-SNAPSHOT")
        .addHeader("Accept", "application/json")
        .build()
    return chain.proceed(request)
  }
}
