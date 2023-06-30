package com.maruchan.notes.ui.splashscreen

import androidx.lifecycle.viewModelScope
import com.maruchan.notes.base.BaseViewModel
import com.maruchan.notes.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel  @Inject constructor(private val userDao: UserDao) : BaseViewModel() {

   fun splash(done: (Boolean) -> Unit) = viewModelScope.launch {
       val login = userDao.isLogin()
       delay(1000)
       done(login)
   }
}
