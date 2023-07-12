package com.maruchan.notes.ui.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.maruchan.notes.api.ApiService
import com.maruchan.notes.base.BaseViewModel
import com.maruchan.notes.const.Const
import com.maruchan.notes.data.room.user.User
import com.maruchan.notes.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val userDao: UserDao,
    private val session: CoreSession

) : BaseViewModel()  {

    fun login(emailOrPhone: String, password: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.login(emailOrPhone, password) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    val status = response.getInt(ApiCode.STATUS)
                    val token = response.getJSONObject(ApiCode.DATA).getJSONObject("token").getString("access_token")
                    session.setValue(Const.TOKEN.API_TOKEN, token)
                    session.setValue(Const.BIOMETRIC.EMAILORPHONE,emailOrPhone)
                    session.setValue(Const.BIOMETRIC.PASSWORD,password)
                    userDao.insert(data.copy(idRoom = 1))
                    if (status == ApiCode.SUCCESS) {
                        val message = response.getString(ApiCode.MESSAGE)
                        _apiResponse.emit(ApiResponse().responseSuccess(message))

                    }
                }
            }
        )
    }
}