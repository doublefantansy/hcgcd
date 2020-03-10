package hzkj.cc.base

import android.content.Context
import android.graphics.Paint
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

object ViewUtil {
  fun toast(
    context: Context,
    str: String?
  ) {
    Toast.makeText(context, str, Toast.LENGTH_SHORT)
        .show()
  }

  fun dipToPx(
    context: Context,
    dipValue: Int
  ): Float {

    var scale = context.getResources()
        .getDisplayMetrics()
        .density
    return (dipValue * scale + 0.5f);
  }

  fun px2sp(
    context: Context,
    pxValue: Float
  ): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
  }

  fun spToPx(
    context: Context,
    spValue: Int
  ): Float {
    var scale = context.getResources()
        .getDisplayMetrics()
        .scaledDensity
    return (spValue * scale + 0.5f)
  }

  fun getTextMiddleOfHeightY(paint: Paint): Float {
    return (paint.fontMetrics.descent - paint.fontMetrics.ascent) / 2 - paint.fontMetrics.descent
  }

  fun getTextWidth(
    paint: Paint,
    str: String?
  ): Int {
    var iRet = 0
    if (str != null && str.length > 0) {
      val len = str.length
      val widths = FloatArray(len)
      paint.getTextWidths(str, widths)
      for (j in 0 until len) {
        iRet += Math.ceil(widths[j].toDouble())
            .toInt()
      }
    }
    return iRet
  }

  fun getScreenParams(context: Context): DisplayMetrics {
    return DisplayMetrics().also {
      (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(
          it
      )
    }
  }
}