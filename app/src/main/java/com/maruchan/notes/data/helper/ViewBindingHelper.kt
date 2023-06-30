package com.maruchan.notes.data.helper

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.maruchan.notes.R
import java.io.File

class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl", "imageFile"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrl: String?, imageFile: File?) {

            view.setImageDrawable(null)

            if (imageUrl.isNullOrEmpty()) {
                Glide
                    .with(view.context)
                    .load(imageUrl)
//                    .placeholder(R.drawable.img_loading)
                    .apply(RequestOptions.centerCropTransform())
                    .error(R.drawable.img_placeholder)
                    .into(view)

            } else {
                imageUrl.let {
                    Glide
                        .with(view.context)
                        .load(imageUrl)
//                        .placeholder(R.drawable.img_loading)
                        .into(view)

                }

            }
            if(imageFile != null ){
                Glide
                    .with(view.context)
                    .load(imageFile)
                        .placeholder(R.drawable.img_loading)
                    .error(R.drawable.img_placeholder)

                    .into(view)
            }

        }
        fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100) {
            outputStream().use { out ->
                bitmap.compress(format, quality, out)
                out.flush()
            }
        }

    }

}