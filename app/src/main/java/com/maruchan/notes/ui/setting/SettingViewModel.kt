package com.maruchan.notes.ui.setting

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.google.gson.Gson
import com.maruchan.notes.api.ApiService
import com.maruchan.notes.base.BaseViewModel
import com.maruchan.notes.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val userDao: UserDao,
    private val session: CoreSession

) : BaseViewModel() {
    private val _editPassword = MutableSharedFlow<ApiResponse>()
    val editPassword = _editPassword.asSharedFlow()

    fun logout() = viewModelScope.launch {
        ApiObserver({ apiService.logout() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("Logout Success"))
                    session.clearAll()
                    userDao.logout()
                    logoutSuccess()
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            }
        )
    }
    fun editPassword(newPassword: String?, passwordConfirmation: String?) = viewModelScope.launch {
        ApiObserver({ apiService.editPassword(newPassword, passwordConfirmation) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _editPassword.emit(ApiResponse().responseSuccess("Profile Updated"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _editPassword.emit(ApiResponse().responseError())
                }
            }
        )
    }
}