package hzkj.cc.base.weight.tabLayout

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil

/**

 * @Author chencheng
 * @Date 2019-12-02-15:09
 */
class MyTabIndicator(context: Context, attributeSet: AttributeSet) : View(context, attributeSet)
{
    var isScrolling: Boolean = false
    var isClick: Boolean = false
    // 按下的横坐标
    var downX = 0F
    var downY = 0F
    // 松开的横坐标
    var lastScrollX = 0F
    // 滑动的横坐标
    var moveX = 0F
    var max: Float = 0f
    var viewPagerCallBack: ((Int) -> Unit)? = null
    var tabs: List<String> = ArrayList()
    var d = 0f
    var distance = 0f
    var selectIndex = 0
    var textPaint = Paint().apply {
        isAntiAlias = true
        textSize = ViewUtil.spToPx(context, 17)
        color = ContextCompat.getColor(context, R.color.base_gray)
        isFakeBoldText = true
    }
    var selectedTextPaint = Paint().apply {
        isAntiAlias = true
        textSize = ViewUtil.spToPx(context, 17)
        color = ContextCompat.getColor(context, R.color.base_white)
        isFakeBoldText = true
    }
    var selectedLinePaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = ViewUtil.dipToPx(context, 2)
        color = ContextCompat.getColor(context, R.color.base_white)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        when (event?.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE ->
            {
                isScrolling = true

                moveX = event.x
                if (downX - moveX + lastScrollX < 0)
                {
                    scrollTo(0, 0)
                }
                else if (downX - moveX + lastScrollX > max * tabs.size - width && max * tabs.size > width)
                {
                    scrollTo((max * tabs.size - width).toInt(), 0)
                }
                else
                {
                    if (max * tabs.size > width)
                    {
                        scrollTo((downX - moveX + lastScrollX).toInt(), 0)
                    }
                }
            }
            MotionEvent.ACTION_UP ->
            {
                if (downY == event.y && downX == event.x)
                {
                    viewPagerCallBack?.invoke(((scrollX + event.x) / max).toInt())
                }
                else if (downX - moveX + lastScrollX < 0)
                {
                    lastScrollX = 0f
                }
                else if (downX - moveX + lastScrollX > max * tabs.size - width)
                {
                    lastScrollX = max * tabs.size - width
                }
                else
                {
                    lastScrollX = downX - moveX + lastScrollX
                }
            }
        }
        return true
    }

    fun move()
    {
        ValueAnimator.ofInt(scrollX, if (max * tabs.size > width && !isScrolling)
        {
            if (max * tabs.size - (distance * max + max / 2) < width / 2)
            {
                (max * tabs.size - width).toInt()
            }
            else
            {
                if ((distance * max + max / 2 - width / 2) < 0)
                {
                    0
                }
                else
                {
                    (distance * max + max / 2 - width / 2).toInt()
                }
            }
        }
        else scrollX)
            .apply {
                duration = 150
                addUpdateListener {
                    scrollTo(it.animatedValue as Int, 0)
                }
                addListener(object : Animator.AnimatorListener
                            {
                                override fun onAnimationRepeat(animation: Animator?)
                                {
                                }

                                override fun onAnimationEnd(animation: Animator?)
                                {
                                    lastScrollX = scrollX.toFloat()
                                }

                                override fun onAnimationCancel(animation: Animator?)
                                {
                                }

                                override fun onAnimationStart(animation: Animator?)
                                {
                                }
                            })
            }
            .start()
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        d = 0f
        max = 0f
        for (tab in tabs)
        {
            if (textPaint.measureText(tab) > max)
            {
                max = textPaint.measureText(tab) + ViewUtil.dipToPx(context, 17)
            }
        }
        for (i in 0 until tabs.size)
        {
            canvas?.drawText(tabs[i], d + (max - textPaint.measureText(tabs[i])) / 2, (height / 2 + (textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent) / 2) - textPaint.getFontMetrics().descent, if (i == selectIndex) selectedTextPaint else textPaint)
            if (i == selectIndex)
            {
                canvas?.drawLine(distance * max, (height - ViewUtil.dipToPx(context, 2)), distance * max + max, (height - ViewUtil.dipToPx(context, 2)), selectedLinePaint)
                if (max * tabs.size > width && !isScrolling && !isClick)
                {
                    if (max * tabs.size - (distance * max + max / 2) < width / 2)
                    {
                        scrollTo((max * tabs.size - width).toInt(), 0)
                    }
                    else
                    {
                        scrollTo(if ((distance * max + max / 2 - width / 2) < 0)
                                 {
                                     0
                                 }
                                 else (distance * max + max / 2 - width / 2).toInt(), 0)
                    }
                }
            }
            d += max
        }
    }
}