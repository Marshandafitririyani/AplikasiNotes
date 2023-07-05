package com.maruchan.notes.data.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.maruchan.notes.R

class ViewHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrlHelper"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrlHelper: String?) {

            view.setImageDrawable(null)

            imageUrlHelper?.let {
                Glide
                    .with(view.context)
                    .load(imageUrlHelper)
                    .placeholder(R.drawable.img_loading)
                    .error(R.drawable.img_placeholder)
                    .into(view)

            }

        }

    }
}