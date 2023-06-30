package com.maruchan.notes.base

import com.crocodic.core.base.viewmodel.CoreViewModel

open class BaseViewModel : CoreViewModel() {
    override fun apiLogout() { }

    override fun apiRenewToken() { }
}