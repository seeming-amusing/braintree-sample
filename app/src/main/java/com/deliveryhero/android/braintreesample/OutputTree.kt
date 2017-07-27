package com.deliveryhero.android.braintreesample

import android.widget.TextView
import timber.log.Timber

class OutputTree(private val view: TextView) : Timber.Tree() {

  override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
    view.text = "$message\n\n${view.text}"
  }
}