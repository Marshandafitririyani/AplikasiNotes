package com.maruchan.notes.base

import androidx.databinding.ViewDataBinding
import com.crocodic.core.base.activity.CoreActivity
import com.crocodic.core.base.viewmodel.CoreViewModel
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.clearNotification
import com.crocodic.core.extension.openActivity
import com.maruchan.notes.data.database.AppDatabase
import com.maruchan.notes.ui.splash.SplashActivity
import javax.inject.Inject


open class BaseActivity<VB : ViewDataBinding, VM : CoreViewModel>(layoutRes: Int) :
    CoreActivity<VB, VM>(layoutRes) {

    @Inject
    lateinit var session: CoreSession


    @Inject
    lateinit var appDatabase: AppDatabase

    override fun authLogoutSuccess() {
        super.authLogoutSuccess()
        loadingDialog.dismiss()
        expiredDialog.dismiss()
        clearNotification()
        session.clearAll()
//        appDatabase.clearAllTables()
        /*CoroutineScope(Dispatchers.Default).launch {
            appDatabase.clearAllTables()
        }*/
        openActivity<SplashActivity>()
        finishAffinity()
    }

}