package com.deliveryhero.android.braintreesample.model

import com.google.gson.annotations.SerializedName

data class ClientToken(@SerializedName("client_token") val value: String = "")
