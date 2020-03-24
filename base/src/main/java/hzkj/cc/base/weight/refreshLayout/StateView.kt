package hzkj.cc.base.weight.refreshLayout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil


/**

 * @Author chencheng
 * @Date 2020-03-24-09:44
 */
class StateView(context: Context) : View(context) {
    private val paint: Paint = Paint().apply {
        isFakeBoldText = true
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.base_black)
        textSize = ViewUtil.spToPx(context, 17)
    }
    var text = String()
    lateinit var bitmap: Bitmap
    var marginWithImage = ViewUtil.dipToPx(context, 20)

    companion object {
        var NETERROR = "网络错误"
        var NODATA = "暂无数据"
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        context.getDrawable(if (text == NETERROR) {
            R.drawable.net_error
        } else R.drawable.no_data).apply {
            bitmap = Bitmap.createBitmap(intrinsicWidth,
                    intrinsicHeight, Bitmap.Config.ARGB_8888)
            setBounds(width / 2 - bitmap.width / 2, height / 2 - bitmap.height / 2, width / 2 + bitmap.width / 2, height / 2 + bitmap.height / 2)
            draw(canvas)
        }
        canvas?.drawText(text, width / 2 - ViewUtil.getTextWidth(paint, text).toFloat() / 2, (height / 2 + bitmap.height / 2 + ViewUtil.getTextMiddleOfHeightY(paint) + marginWithImage).toFloat(), paint)
    }
}