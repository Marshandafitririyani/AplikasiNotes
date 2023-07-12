package com.maruchan.notes.ui.category.fragment

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
import com.maruchan.notes.const.Const
import com.maruchan.notes.data.room.category.Category
import com.maruchan.notes.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val userDao: UserDao,
    private val session: CoreSession

) : BaseViewModel() {
    val user = userDao.getUser()

    private val _saveEdiitcategory = MutableSharedFlow<Category>()
    val saveEdiitcategory = _saveEdiitcategory.asSharedFlow()

    private val _saveGetCategory = MutableSharedFlow<List<Category>>()
    val saveGetCategory = _saveGetCategory.asSharedFlow()

    private val _saveCreatCategory = MutableSharedFlow<ApiResponse>()
    val saveCreatCategory = _saveCreatCategory.asSharedFlow()

    fun creatCategory(category: String) = viewModelScope.launch {
        _saveCreatCategory.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.creatCategory(category) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _saveCreatCategory.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _saveCreatCategory.emit(ApiResponse().responseError())
                }
            }
        )
    }

    fun getCategory() = viewModelScope.launch {
        ApiObserver({ apiService.getCategory() }, false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                val data = response.getJSONArray(ApiCode.DATA).toList<Category>(gson)
                _saveGetCategory.emit(data)
            }
            override suspend fun onError(response: ApiResponse) {
            }
        })
    }

    fun deleteCategory(id: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.deleteCategory(id) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            }
        )
    }

    fun editCategory(id: String?, category: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.editCategory(id, category) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Category>(gson)
                    _saveEdiitcategory.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess("Update Category Success"))
                }
                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            }
        )
    }
}