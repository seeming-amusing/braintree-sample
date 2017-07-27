package com.deliveryhero.android.braintreesample

import android.app.Application
import android.widget.TextView
import timber.log.Timber

class BraintreeSampleApplication : Application() {

  companion object {

    fun initializeTrees(outputTo: TextView) {
      Timber.plant(Timber.DebugTree())
      Timber.plant(OutputTree(outputTo))
    }
  }
}