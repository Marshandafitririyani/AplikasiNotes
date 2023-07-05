package com.maruchan.notes.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("email_or_phone") emailOrPhone: String?,
        @Field("password") password: String?
    ): String

    @FormUrlEncoded
    @POST("api/auth/register")
    suspend fun register(
        @Field("name") name: String?,
        @Field("phone") phone: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("password_confirmation") passwordConfirmation: String?
    ): String

    @FormUrlEncoded
    @POST("api/user/edit-profile")
    suspend fun editProfile(
        @Field("name") name: String?
    ): String

    @Multipart
    @POST("api/user/edit-profile")
    suspend fun editProfileWithPhoto(
        @Part("name") name: String,
        @Part photo: MultipartBody.Part?
    ): String

    @FormUrlEncoded
    @POST("api/user/edit-password")
    suspend fun editPassword(
        @Field("new_password") newPassword: String?,
        @Field("password_confirmation") passwordConfirmation: String?
    ): String

    @GET("api/auth/me")
    suspend fun getProfile(): String

    @GET("api/note/index")
    suspend fun getNotes(): String

    @GET("api/category/index")
    suspend fun getCategory(): String

    @GET("api/category/{categories_id}")
    suspend fun getCategoryById(
        @Path("categories_id") categoriesId: String?
    ): String

    @POST("api/auth/logout")
    suspend fun logout(): String

    @FormUrlEncoded
    @POST("api/note/create")
    suspend fun createNote(
        @Field("title") title: String?,
        @Field("content") content: String?,
        @Field("categories_id") categoriesId: String?,
    ): String

    @FormUrlEncoded
    @POST("api/note/edit/{id}")
    suspend fun updateNote(
        @Path("id") id: String?,
        @Field("title") title: String?,
        @Field("content") content: String?,
        @Field("categories_id") categoriesId: String?,
    ): String

    @Multipart
    @POST("api/note/create")
    suspend fun createNoteWithPhoto(
        @Part("title") title: String?,
        @Part("content") content: String?,
        @Part("categories_id") categoriesId: String?,
        @Part photo: MultipartBody.Part?
    ): String

    @Multipart
    @POST("api/note/edit/{id}")
    suspend fun updateNoteWithPhoto(
        @Path("id") id: String?,
        @Part("title") title: String?,
        @Part("content") content: String?,
        @Part("categories_id") categoriesId: String?,
        @Part photo: MultipartBody.Part?
    ): String

    @FormUrlEncoded
    @POST("api/category/create")
    suspend fun creatCategory(
        @Field("category") category: String?,
    ): String

    @FormUrlEncoded
    @POST("api/category/edit/{id}")
    suspend fun editCategory(
        @Path("id") id: String?,
        @Field("category") category: String?,
    ): String

    @DELETE("api/note/delete/{id}")
    suspend fun deleteNote(
        @Path("id") id: String?
    ): String

    @DELETE("api/category/delete/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String?
    ): String

    @POST("api/note/favorite/{id}")
    suspend fun favourite(
        @Path("id") id: String?
    ): String

    @POST("api/note/unfavorite/{id}")
    suspend fun unFavourite(
        @Path("id") id: String?
    ): String

}