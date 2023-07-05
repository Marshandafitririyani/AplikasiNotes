package com.maruchan.notes.data.room.category

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Category(
    @PrimaryKey
    @Expose
    @SerializedName("id")
    val id : String,
    @Expose
    @SerializedName("category")
    val category: String,
    @Expose
    @SerializedName("created_by")
    val created_by: String?,
    @Expose
    @SerializedName("created_at")
    val created_at: String?,
    @Expose
    @SerializedName("updated_at")
    val updated_at: String?

) : Parcelable {
    override fun toString(): String {
        return category.toString()
    }
}