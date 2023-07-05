package com.maruchan.notes.data.helper

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.helper.StringHelper
import com.maruchan.notes.R

class ViewModelHelperAvatar {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrlAvatar", "userHelper"], requireAll = true)
        fun loadImageRecipe(view: ImageView, imageUrlAvatar: String?, userHelper: String?) {

            view.setImageDrawable(null)

            val avatar = StringHelper.generateTextDrawable(
                StringHelper.getInitial(userHelper?.trim()),
                ContextCompat.getColor(view.context, R.color.yellow50), 320
            )

            if (imageUrlAvatar.isNullOrEmpty()) {
                view.setImageDrawable(avatar)

            } else {
                imageUrlAvatar.let {
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.img_loading)
                        .circleCrop()
                    Glide
                        .with(view.context)
                        .load(StringHelper.validateEmpty(imageUrlAvatar))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(requestOptions)
                        .error(avatar)
                        .into(view)

                }

            }

        }

    }
}