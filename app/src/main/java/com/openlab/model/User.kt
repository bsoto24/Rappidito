package com.openlab.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var uid: String = "", var firstName: String = "", var lastName: String = "", var phone: String = "", var email: String = ""): Parcelable