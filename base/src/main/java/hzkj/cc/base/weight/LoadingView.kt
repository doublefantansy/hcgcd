package hzkj.cc.base.weight

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil

class LoadingView(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet)
{
    var paint: Paint? = null
    var redPaint: Paint? = null
    var myHeight: Float = ViewUtil.dipToPx(context, 24)
    var lineWidth: Float
    var startWidth: Float
    var move: Boolean = false
    var current: Float = 0f
    lateinit var animator: ValueAnimator

    init
    {
        paint = Paint().also {
            it.setAntiAlias(true)
            it.color = context.resources.getColor(R.color.base_load_view_gray)
            it.style = Paint.Style.FILL
            it.strokeWidth = ViewUtil.dipToPx(context, 2)
        }
        redPaint = Paint().also {
            it.setAntiAlias(true)
            it.color = context.resources.getColor(R.color.base_load_view_black)
            it.style = Paint.Style.FILL
            it.strokeWidth = ViewUtil.dipToPx(context, 1)
        }
        //        System.out.println(textPaint?.isAntiAlias)
        lineWidth = (height / 2).toFloat()
        startWidth = (height / 4).toFloat()
        animator = ValueAnimator.ofInt(0, 12)
            .also {
                it.addUpdateListener {
                    //                System.out.println(it.animatedValue)
                    current = (it.animatedValue as Int).toFloat()
                    invalidate()
                }
                it.duration = 1000
                it.interpolator = LinearInterpolator()
                it.repeatCount = ValueAnimator.INFINITE
                it.addListener(object : Animator.AnimatorListener
                               {
                                   override fun onAnimationRepeat(animation: Animator?)
                                   {
                                   }

                                   override fun onAnimationEnd(animation: Animator?)
                                   {
                                   }

                                   override fun onAnimationCancel(animation: Animator?)
                                   {
                                   }

                                   override fun onAnimationStart(animation: Animator?)
                                   {
                                       move = true
                                   }
                               })
            }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        lineWidth = (myHeight / 2).toFloat()
        startWidth = (myHeight / 4).toFloat()
        setMeasuredDimension(myHeight.toInt(), myHeight.toInt())
    }

    fun stop()
    {
        animator.end()
    }

    fun move()
    {
        animator.start()
    }

    fun end()
    {
        animator.cancel()
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        for (i in 0..11)
        {
            canvas?.drawLine((width / 2).toFloat() + startWidth * Math.sin((Math.PI / 6) * i).toFloat(), (height / 2).toFloat() + startWidth * Math.cos((Math.PI / 6) * i + Math.PI).toFloat(), ((width / 2).toFloat() + lineWidth * Math.sin((Math.PI / 6) * i).toFloat()), ((height / 2).toFloat() + lineWidth * Math.cos((Math.PI / 6) * i + Math.PI).toFloat()), paint)
        }
        if (move)
        {
            canvas?.drawLine((width / 2).toFloat() + startWidth * Math.sin((Math.PI / 6) * current).toFloat(), (height / 2).toFloat() + startWidth * Math.cos((Math.PI / 6) * current + Math.PI).toFloat(), ((width / 2).toFloat() + lineWidth * Math.sin((Math.PI / 6) * current).toFloat()), ((height / 2).toFloat() + lineWidth * Math.cos((Math.PI / 6) * current + Math.PI).toFloat()), redPaint)
        }
        //        canvas?.drawLine(
        //            (width / 2).toFloat()+startWidth*0,
        //            (height / 2).toFloat() - startWidth,
        //            (width / 2).toFloat(),
        //            (height / 2 - lineWidth), textPaint
        //        )
        //        canvas?.drawLine(
        //            (width / 2).toFloat() + startWidth / 2,
        //            ((height / 2).toFloat() - startWidth * Math.sqrt(3.0) / 2).toFloat(),
        //            ((width / 2).toFloat() + lineWidth / 2).toFloat(),
        //            ((height / 2 - lineWidth * Math.sqrt(3.0) / 2).toFloat()), textPaint
        //        )
        //        canvas?.drawLine(
        //            ((width / 2).toFloat() + startWidth * Math.sqrt(3.0) / 2).toFloat(),
        //            (height / 2).toFloat() - startWidth / 2,
        //            ((width / 2).toFloat() + lineWidth * Math.sqrt(3.0) / 2).toFloat(),
        //            ((height / 2 - lineWidth / 2).toFloat()), textPaint
        //        )
        //        canvas?.drawLine(
        //            (width / 2).toFloat() + startWidth,
        //            (height / 2).toFloat(),
        //            ((width / 2).toFloat() + lineWidth),
        //            (height / 2).toFloat(), textPaint
        //        )
        //        canvas?.drawLine(
        //            (width / 2 + startWidth * Math.sqrt(3.0) / 2).toFloat(),
        //            (height / 2).toFloat() + startWidth / 2,
        //            (width / 2 + lineWidth * Math.sqrt(3.0) / 2).toFloat(),
        //            (height / 2 + lineWidth / 2), textPaint
        //        )
        ////        }
        //        }
    }
}