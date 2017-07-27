package com.deliveryhero.android.braintreesample

import android.app.Application
import android.widget.TextView
import com.deliveryhero.android.braintreesample.service.ApiClientRequestInterceptor
import com.deliveryhero.android.braintreesample.service.PaymentService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class BraintreeSampleApplication : Application() {

  companion object {

    private val okhttp by lazy {
      OkHttpClient.Builder()
          .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
          .addInterceptor(ApiClientRequestInterceptor)
          .build()
    }
    private val retrofit by lazy {
      Retrofit.Builder().baseUrl("https://braintree-sample-merchant.herokuapp.com")
          .client(okhttp)
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build()
    }
    val paymentService by lazy { retrofit.create(PaymentService::class.java) }

    fun initializeTrees(outputTo: TextView) {
      Timber.plant(Timber.DebugTree())
      Timber.plant(OutputTree(outputTo))
    }
  }
}