package hzkj.cc.base

import android.content.Context
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object ImgUtil {
    fun createImgByBase64(context: Context, src: String, view: ImageView) {
        Glide.with(context)
            .load(Base64.decode(src, Base64.DEFAULT))
            .placeholder(R.drawable.load_image_placeholder)
            .error(R.drawable.load_image_fail)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }
}