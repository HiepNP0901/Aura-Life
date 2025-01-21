package com.drs.auralife.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.drs.auralife.R

@GlideModule
object MyAppGlideModule : AppGlideModule() {
    private fun isValidContextForGlide(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            val activity = context
            if (activity.isDestroyed || activity.isFinishing) {
                return false
            }
        }
        return true
    }

    fun loadImage(
        context: Context,
        image: Any?,
        imageView: ImageView,
        transformation: Transformation<Bitmap>? = null
    ) {
        if (isValidContextForGlide(context)) {
            val glide = Glide.with(context).load(image).encodeQuality(1)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).skipMemoryCache(false)
            transformation?.let {
                glide.apply(RequestOptions.bitmapTransform(transformation))
            }
            glide.placeholder(R.drawable.rounded_shadow).error(R.drawable.bg_logo).into(imageView)
        }
    }
}
