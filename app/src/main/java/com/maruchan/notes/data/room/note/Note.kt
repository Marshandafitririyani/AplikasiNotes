package com.maruchan.notes.data.room.note

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Note(
    @PrimaryKey
    @Expose
    @SerializedName("id")
    val id : String,
    @Expose
    @SerializedName("title")
    val title : String?,
    @Expose
    @SerializedName("content")
    val content : String?,
    @Expose
    @SerializedName("created_by")
    val created_by: String?,
    @Expose
    @SerializedName("categories_id")
    val categories_id: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?,
    @Expose
    @SerializedName("favorite")
    var favorite: Boolean,
    @Expose
    @SerializedName("created_at")
    val created_at: String?,
    @Expose
    @SerializedName("updated_at")
    val updated_at: String?,
    @Expose
    @SerializedName("created_at_formatted")
    val created_at_formatted: String?,
    @Expose
    @SerializedName("updated_at_formatted")
    val updated_at_formatted: String?

) : Parcelable