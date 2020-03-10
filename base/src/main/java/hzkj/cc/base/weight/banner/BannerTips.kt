package hzkj.cc.base.weight.banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import hzkj.cc.base.ViewUtil

/**

 * @Author chencheng
 * @Date 2019-12-06-10:42
 */
class BannerTips(context: Context, attributeSet: AttributeSet) : View(context, attributeSet)
{
//    var listener: (() -> Int)? = null
    var count = 0
    var position = 0
    var circlePaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        setStyle(Paint.Style.FILL)
        //        setStrokeWidth(ViewUtil.dipToPx(context, 2))
    }
    var selectCirclePaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        setStyle(Paint.Style.FILL)
        //        setStrokeWidth(ViewUtil.dipToPx(context, 2))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
        //                             ViewUtil.dipToPx(context,
        //                                              200).toInt())
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        for (i in 0 until count)
        {
            canvas?.drawCircle((width - ViewUtil.dipToPx(context, 5) - (i + 1) * ViewUtil.dipToPx(context, 15)).toFloat(), (height - ViewUtil.dipToPx(context, 5) - ViewUtil.dipToPx(context, 10)).toFloat(), ViewUtil.dipToPx(context, 5), if (i == count - 1 - position) selectCirclePaint else circlePaint)
        }
    }
}