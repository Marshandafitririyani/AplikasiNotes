package com.maruchan.notes.ui.home.activity

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.maruchan.notes.api.ApiService
import com.maruchan.notes.base.BaseViewModel
import com.maruchan.notes.data.room.category.Category
import com.maruchan.notes.data.room.note.Note
import com.maruchan.notes.data.room.user.User
import com.maruchan.notes.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val userDao: UserDao,
    private val session: CoreSession

) : BaseViewModel() {
    val user = userDao.getUser()

    private val _savegetNote = MutableSharedFlow<List<Note>>()
    val savegetNote = _savegetNote.asSharedFlow()

    private val _saveGetCategoryName = MutableSharedFlow<Category?>()
    val saveGetCategoryName = _saveGetCategoryName.asSharedFlow()

    private val _saveGetCategory = MutableSharedFlow<List<Category?>>()
    val saveGetCategory = _saveGetCategory.asSharedFlow()

    fun getProfile() = viewModelScope.launch {
        ApiObserver(
            { apiService.getProfile() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    userDao.insert(data.copy(idRoom = 1))

                }
            }
        )
    }
    fun getNote() = viewModelScope.launch {
        ApiObserver({ apiService.getNotes() }, false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                val data = response.getJSONArray(ApiCode.DATA).toList<Note>(gson)
                _savegetNote.emit(data)
            }

            override suspend fun onError(response: ApiResponse) {
            }


        })


    }
    fun getCategory() = viewModelScope.launch {
        ApiObserver({ apiService.getCategory() }, false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                val data = response.getJSONArray(ApiCode.DATA).toList<Category>(gson)
                _saveGetCategory.emit(data)
                _apiResponse.emit(ApiResponse().responseSuccess("Category"))
            }

            override suspend fun onError(response: ApiResponse) {
                super.onError(response)
                _apiResponse.emit(ApiResponse().responseError())
            }


        })


    }



}