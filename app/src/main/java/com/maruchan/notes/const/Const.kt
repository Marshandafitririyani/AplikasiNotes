package com.maruchan.notes.const

object Const {
    object TOKEN {
        const val API_TOKEN = "access_token"
        const val FCM_TOKEN = "fcm_token"
    }
    object LIST{
        const val RELOAD = 876 // TODO: result code untuk memberi tahu harus reload data lagi
    }
    object NOTE{
        const val NOTE = "note"
    }

    object BIOMETRIC{
        const val EMAIL ="email"
        const val PASSWORD ="password"
    }

    object TIMEOUT {
        const val NINETY_LONG = 90L
    }


}