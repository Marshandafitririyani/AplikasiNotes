package com.maruchan.notes.ui.detail

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
import com.maruchan.notes.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddNotesViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val userDao: UserDao,
    private val session: CoreSession

) : BaseViewModel() {
    val user = userDao.getUser()

    private val _saveFavourite = MutableSharedFlow<ApiResponse>()
    val saveFavourite = _saveFavourite.asSharedFlow()

    private val _saveNotes = MutableSharedFlow<ApiResponse>()
    val saveNotes = _saveNotes.asSharedFlow()

    private val _saveGetCategoryName = MutableSharedFlow<Category?>()
    val saveGetCategoryName = _saveGetCategoryName.asSharedFlow()

    private val _saveGetCategory = MutableSharedFlow<List<Category?>>()
    val saveGetCategory = _saveGetCategory.asSharedFlow()


    fun getCategoryName(id: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            {
                apiService.getCategoryById(id)
            },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Category>(gson)
                    _saveGetCategoryName.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess("Category"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
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

    fun createNote(title: String?, content: String?, categoryId: String?, photo: File? = null) =
        viewModelScope.launch {
            if (photo != null) {
                val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)
                _saveNotes.emit(ApiResponse().responseLoading())
                ApiObserver(
                    { apiService.createNoteWithPhoto(title, content, categoryId, filePart) },
                    false,
                    object : ApiObserver.ResponseListener {
                        override suspend fun onSuccess(response: JSONObject) {
                            _saveNotes.emit(ApiResponse().responseSuccess("Notes"))

                        }

                        override suspend fun onError(response: ApiResponse) {
                            super.onError(response)
                            _saveNotes.emit(ApiResponse().responseError())
                        }
                    }
                )

            } else {
                _saveNotes.emit(ApiResponse().responseLoading())
                ApiObserver(
                    { apiService.createNote(title, content, categoryId) },
                    false,
                    object : ApiObserver.ResponseListener {
                        override suspend fun onSuccess(response: JSONObject) {
                            _saveNotes.emit(ApiResponse().responseSuccess("Notes"))
                        }

                        override suspend fun onError(response: ApiResponse) {
                            super.onError(response)
                            _saveNotes.emit(ApiResponse().responseError())
                        }
                    })
            }

        }

    fun updateNote(
        id: String?,
        title: String?,
        content: String?,
        categoryId: String?,
        photo: File? = null
    ) =
        viewModelScope.launch {
            if (photo != null) {
                val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)
                _saveNotes.emit(ApiResponse().responseLoading())
                ApiObserver(
                    { apiService.updateNoteWithPhoto(id, title, content, categoryId, filePart) },
                    false,
                    object : ApiObserver.ResponseListener {
                        override suspend fun onSuccess(response: JSONObject) {

                            _saveNotes.emit(ApiResponse().responseSuccess("Notes"))

                        }

                        override suspend fun onError(response: ApiResponse) {
                            super.onError(response)
                            _saveNotes.emit(ApiResponse().responseError())
                        }
                    }
                )

            } else {
                _saveNotes.emit(ApiResponse().responseLoading())
                ApiObserver(
                    { apiService.updateNote(id, title, content, categoryId) },
                    false,
                    object : ApiObserver.ResponseListener {
                        override suspend fun onSuccess(response: JSONObject) {
                            _saveNotes.emit(ApiResponse().responseSuccess("Notes"))
                        }

                        override suspend fun onError(response: ApiResponse) {
                            super.onError(response)
                            _saveNotes.emit(ApiResponse().responseError())
                        }
                    })
            }

        }

    fun deleteNote(id: String) = viewModelScope.launch {
        _saveNotes.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.deleteNote(id) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _saveNotes.emit(ApiResponse().responseSuccess("Notes"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _saveNotes.emit(ApiResponse().responseError())
                }
            })
    }

    fun favourite(id: String?) = viewModelScope.launch {
        ApiObserver(
            { apiService.favourite(id) },
            true,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _saveFavourite.emit(ApiResponse().responseSuccess("favorite"))
                }
            }
        )

    }

    fun unFavourite(id: String?) = viewModelScope.launch {
        ApiObserver(
            { apiService.unFavourite(id) },
            true,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _saveFavourite.emit(ApiResponse().responseSuccess("unFavorite"))
                }
            }
        )

    }


}