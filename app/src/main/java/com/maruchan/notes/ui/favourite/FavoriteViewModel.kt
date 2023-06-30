package com.maruchan.notes.ui.favourite

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.maruchan.notes.api.ApiService
import com.maruchan.notes.base.BaseViewModel
import com.maruchan.notes.data.room.note.Note
import com.maruchan.notes.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val userDao: UserDao,
    private val session: CoreSession

) : BaseViewModel() {
    val user = userDao.getUser()


    private val _saveFavourite = MutableSharedFlow<List<Note>>()
    val saveFavourite = _saveFavourite.asSharedFlow()

    fun getNote() = viewModelScope.launch {
        ApiObserver({ apiService.getNotes() }, false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                Log.d("cek ss", "succes")
                val data = response.getJSONArray(ApiCode.DATA).toList<Note>(gson)
                _saveFavourite.emit(data)
            }

            override suspend fun onError(response: ApiResponse) {
                Log.d("cek error", "err")
            }


        })


    }

}