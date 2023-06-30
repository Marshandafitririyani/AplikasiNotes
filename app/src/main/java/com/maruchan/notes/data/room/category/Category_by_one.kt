package com.maruchan.notes.data.room.category

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Entity
data class Category_by_one(
    @PrimaryKey
    @Expose
    @SerializedName("id")
    val id : String?,
    @Expose
    @SerializedName("category")
    val category: String?
) {
    override fun toString(): String {
        return category.toString()
    }

}